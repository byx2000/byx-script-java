package byx.script.runtime;

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

    public Scope(Map<String, Value> builtins) {
        next = null;
        vars.putAll(builtins);
    }

    public Scope(Scope next) {
        this.next = next;
    }

    /**
     * 定义变量
     * @param varName 变量名
     * @param value 变量值
     */
    public void declareVar(String varName, Value value) {
        if (vars.containsKey(varName)) {
            throw new InterpretException("var already exist: " + varName);
        }
        vars.put(varName, value);
    }

    /**
     * 设置变量的值
     * @param varName 变量名
     * @param value 变量值
     */
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

    /**
     * 获取变量的值
     * @param varName 变量名
     * @return 变量值
     */
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
