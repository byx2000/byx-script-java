package byx.script.core;

import byx.script.core.interpreter.ASTEvaluator;
import byx.script.core.interpreter.Scope;
import byx.script.core.interpreter.value.builtin.Console;
import byx.script.core.interpreter.value.builtin.Math;
import byx.script.core.interpreter.value.builtin.Reader;
import byx.script.core.interpreter.value.builtin.Reflect;
import byx.script.core.interpreter.exception.*;
import byx.script.core.interpreter.value.Value;
import byx.script.core.parser.ByxScriptParser;
import byx.script.core.parser.ast.Program;
import byx.script.core.parser.exception.ByxScriptParseException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ByxScript执行器
 * 执行ByxScript脚本
 */
public class ByxScriptRunner {
    private static final String SCRIPT_SUFFIX = ".bs";
    private final List<Path> importPaths = new ArrayList<>();
    private final Map<String, Value> builtins = new HashMap<>();

    public ByxScriptRunner() {
        this(new Scanner(System.in), System.out);
    }

    public ByxScriptRunner(Scanner scanner, PrintStream printStream) {
        try {
            // 添加默认导入路径（当前路径）
            addImportPath(Path.of("").toAbsolutePath());
            addImportPath(Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("lib")).toURI()));

            // 添加内建对象
            addBuiltin("Console", new Console(printStream));
            addBuiltin("Reader", new Reader(scanner));
            addBuiltin("Reflect", Reflect.INSTANCE);
            addBuiltin("Math", Math.INSTANCE);
        } catch (URISyntaxException e) {
            throw new ByxScriptRuntimeException("load lib path failed", e);
        }
    }

    /**
     * 添加导入路径
     * @param path 路径
     */
    void addImportPath(Path path) {
        importPaths.add(path);
    }

    /**
     * 添加多个导入路径
     * @param paths 路径集合
     */
    void addImportPaths(Collection<Path> paths) {
        importPaths.addAll(paths);
    }

    /**
     * 添加内建变量
     * @param name 变量名
     * @param value 变量值
     */
    void addBuiltin(String name, Value value) {
        builtins.put(name, value);
    }

    // 读取并解析导入名称
    private Program parseImportName(String importName) {
        for (Path p : importPaths) {
            try {
                String script = Files.readString(p.resolve(importName + SCRIPT_SUFFIX));
                return ByxScriptParser.parse(script);
            } catch (IOException ignored) {}
        }

        throw new ByxScriptRuntimeException("cannot resolve import name: " + importName);
    }

    // 解析所有导入
    private Map<String, Program> parseImports(List<String> imports) {
        Map<String, Program> result = new HashMap<>();
        Queue<String> namesToParse = new LinkedList<>(imports);
        while (!namesToParse.isEmpty()) {
            int cnt = namesToParse.size();
            for (int i = 0; i < cnt; ++i) {
                String importName = namesToParse.remove();
                Program p = parseImportName(importName);
                result.put(importName, p);
                for (String name : p.getImports()) {
                    if (!result.containsKey(name)) {
                        namesToParse.add(name);
                    }
                }
            }
        }
        return result;
    }

    // 计算导入的加载顺序
    private List<String> getLoadOrder(Map<String, Program> imports) {
        // 计算依赖关系
        Map<String, Set<String>> dependOn = imports.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new HashSet<>(e.getValue().getImports())));

        // 计算反向依赖关系
        Map<String, Set<String>> dependBy = new HashMap<>();
        dependOn.forEach((k, v) -> {
            for (String n : v) {
                Set<String> set = dependBy.getOrDefault(n, new HashSet<>());
                set.add(k);
                dependBy.put(n, set);
            }
        });

        // 计算出度
        Map<String, Integer> out = dependOn.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

        // 保存加载顺序
        List<String> loadOrder = new ArrayList<>();

        Set<String> ready = new HashSet<>();
        out.forEach((n, o) -> {
            if (o == 0) {
                ready.add(n);
            }
        });

        // 拓扑排序
        while (!ready.isEmpty()) {
            String n = ready.iterator().next();
            ready.remove(n);
            loadOrder.add(n);
            if (dependBy.containsKey(n)) {
                for (String n2 : dependBy.get(n)) {
                    Integer o = out.get(n2);
                    out.put(n2, o - 1);
                    if (o == 1) {
                        ready.add(n2);
                    }
                }
            }
        }

        // 检测循环依赖
        if (loadOrder.size() != imports.size()) {
            throw new ByxScriptRuntimeException("circular dependency");
        }

        return loadOrder;
    }

    /**
     * 运行脚本
     * @param script 脚本字符串
     */
    public void run(String script) throws ByxScriptParseException, ByxScriptRuntimeException {
        // 解析脚本
        Program program = ByxScriptParser.parse(script);

        // 解析所有导入
        Map<String, Program> imports = parseImports(program.getImports());

        // 计算加载顺序
        List<String> loadOrder = getLoadOrder(imports);

        // 创建求值器
        ASTEvaluator evaluator = new ASTEvaluator();

        // 初始化作用域
        Scope scope = new Scope();

        // 添加内建变量
        builtins.forEach(scope::declareVar);

        // 按顺序加载依赖项
        for (String n : loadOrder) {
            evaluator.eval(imports.get(n), scope);
        }

        // 执行脚本
        try {
            evaluator.eval(program, scope);
        } catch (ByxScriptRuntimeException e) {
            throw e;
        } catch (BreakException e) {
            throw new ByxScriptRuntimeException("break statement only allow in loop");
        } catch (ContinueException e) {
            throw new ByxScriptRuntimeException("continue statement only allow in loop");
        } catch (ReturnException e) {
            throw new ByxScriptRuntimeException("return statement only allow in function");
        } catch (ThrowException e) {
            throw new ByxScriptRuntimeException("uncaught exception from script: " + e.getValue());
        } catch (Exception e) {
            throw new ByxScriptRuntimeException("unknown runtime exception: " + e.getMessage());
        }
    }
}
