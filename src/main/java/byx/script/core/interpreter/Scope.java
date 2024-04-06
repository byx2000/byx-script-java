package byx.script.core.interpreter;

import byx.script.core.interpreter.exception.ByxScriptRuntimeException;
import byx.script.core.interpreter.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装ByxScript运行时的作用域
 */
public class Scope {
    private final Map<String, Value> vars = new HashMap<>();
    private final Scope next;

    /**
     * 创建空作用域
     */
    public Scope() {
        next = null;
    }

    /**
     * 创建作用域并指定父级
     * @param next 父级作用域
     */
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
            throw new ByxScriptRuntimeException("var already exist: " + varName);
        }
        vars.put(varName, value);
    }

    /**
     * 设置变量的值
     * @param varName 变量名
     * @param value 变量值
     */
    public void setVar(String varName, Value value) {
        Scope cur = this;
        while (cur != null) {
            if (cur.vars.containsKey(varName)) {
                cur.vars.put(varName, value);
                return;
            }
            cur = cur.next;
        }
        throw new ByxScriptRuntimeException("var not exist: " + varName);
    }

    /**
     * 获取变量的值
     * @param varName 变量名
     * @return 变量值
     */
    public Value getVar(String varName) {
        Scope cur = this;
        while (cur != null) {
            if (cur.vars.containsKey(varName)) {
                return cur.vars.get(varName);
            }
            cur = cur.next;
        }
        throw new ByxScriptRuntimeException("var not exist: " + varName);
    }
}
