package byx.script.core.parser.ast.expr;

/**
 * 二元表达式
 */
public record BinaryExpr(BinaryOp op, Expr lhs, Expr rhs) implements Expr {}
