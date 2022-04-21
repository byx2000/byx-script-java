package byx.script.runtime;

import byx.script.runtime.builtin.Console;
import byx.script.runtime.builtin.MapValue;
import byx.script.runtime.builtin.Reflect;
import byx.script.runtime.builtin.SetValue;
import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装ByxScript运行时的作用域
 */
public class Scope {
    private final Map<String, Value> vars = new HashMap<>();
    private final Scope next;

    public Scope() {
        this(null);
        // 添加内建变量
        vars.put("Console", Console.INSTANCE);
        vars.put("Reflect", Reflect.INSTANCE);
        vars.put("Set", Value.of(args -> new SetValue()));
        vars.put("Map", Value.of(args -> new MapValue()));
    }

    public Scope(Scope next) {
        this.next = next;
    }

    public void declareVar(String varName, Value value) {
        if (vars.containsKey(varName)) {
            throw new InterpretException("var already exist: " + varName);
        }
        vars.put(varName, value);
    }

    public void setVar(String varName, Value value) {
        if (vars.containsKey(varName)) {
            vars.put(varName, value);
            return;
        }
        if (next == null) {
            throw new InterpretException("var not exist: " + varName);
        }
        next.setVar(varName, value);
    }

    public Value getVar(String varName) {
        if (vars.containsKey(varName)) {
            return vars.get(varName);
        }
        if (next == null) {
            throw new InterpretException("var not exist: " + varName);
        }
        return next.getVar(varName);
    }
}
