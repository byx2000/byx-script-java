package byx.script.ast;

import byx.script.runtime.Scope;

/**
 * 语句
 */
public interface Statement {
    /**
     * 执行语句
     * @param scope 当前作用域
     */
    void execute(Scope scope);
}
