package byx.script;

import byx.script.ast.Program;
import byx.script.runtime.InterpretException;
import byx.script.runtime.Scope;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ByxScriptRunner {
    private static final String SCRIPT_SUFFIX = ".bs";

    // 读取并解析导入名称
    private static Program parseImportName(List<Path> importPaths, String importName) {
        for (Path p : importPaths) {
            try {
                String script = Files.readString(p.resolve(importName + SCRIPT_SUFFIX));
                return ByxScriptParser.parse(script);
            } catch (IOException ignored) {}
        }

        throw new InterpretException("cannot resolve import name: " + importName);
    }

    private static List<String> getLoadOrder(Map<String, Program> dependencies) {
        // 计算依赖关系
        Map<String, Set<String>> dependOn = dependencies.entrySet().stream()
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

        if (loadOrder.size() != dependencies.size()) {
            throw new InterpretException("circular dependency");
        }

        return loadOrder;
    }

    /**
     * 运行脚本
     * @param script 脚本字符串
     * @param importPaths 导入路径
     */
    public static void run(String script, List<Path> importPaths) {
        Program program = ByxScriptParser.parse(script);

        // 解析所有导入名称
        Map<String, Program> dependencies = new HashMap<>();
        Queue<String> namesToParse = new LinkedList<>(program.getImports());
        while (!namesToParse.isEmpty()) {
            int cnt = namesToParse.size();
            for (int i = 0; i < cnt; ++i) {
                String importName = namesToParse.remove();
                Program p = parseImportName(importPaths, importName);
                dependencies.put(importName, p);
                for (String name : p.getImports()) {
                    if (!dependencies.containsKey(name)) {
                        namesToParse.add(name);
                    }
                }
            }
        }

        // 计算加载顺序
        List<String> loadOrder = getLoadOrder(dependencies);

        Scope scope = new Scope();
        // 按顺序加载依赖项
        for (String n : loadOrder) {
            dependencies.get(n).run(scope);
        }
        program.run(scope);
    }

    public static void run(String script) {
        run(script, List.of(Path.of("").toAbsolutePath()));
    }
}
