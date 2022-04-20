package byx.script.ast.stmt;

import byx.script.ast.Statement;
import byx.script.runtime.Scope;

/**
 * 空语句
 */
public class EmptyStatement implements Statement {
    /**
     * 所有空语句共享同一个实例
     */
    public static EmptyStatement INSTANCE = new EmptyStatement();

    private EmptyStatement() {}

    @Override
    public void execute(Scope scope) {

    }
}
