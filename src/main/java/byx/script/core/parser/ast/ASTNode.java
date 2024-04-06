package byx.script.core.parser.ast;

import byx.script.core.interpreter.ASTVisitor;
import byx.script.core.interpreter.exception.InterruptException;

/**
 * 抽象语法树节点基类
 */
public abstract class ASTNode {
    public final  <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        // 检测线程中断状态
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException();
        }
        return doVisit(visitor, ctx);
    }

    /**
     * 子类实现visitor模式的分派函数
     */
    protected abstract <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx);
}
