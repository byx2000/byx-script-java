package byx.script.ast;

/**
 * 抽象语法树节点基类
 */
public interface ASTNode {
    <R, C> R visit(ASTVisitor<R, C> visitor, C ctx);
}
