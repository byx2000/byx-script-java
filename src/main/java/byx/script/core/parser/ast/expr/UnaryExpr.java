package byx.script.core.parser.ast.expr;

/**
 * 一元表达式
 */
public record UnaryExpr(UnaryOp op, Expr expr) implements Expr {}
