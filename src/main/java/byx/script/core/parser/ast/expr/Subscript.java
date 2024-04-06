package byx.script.core.parser.ast.expr;

/**
 * 下标访问
 */
public record Subscript(Expr expr, Expr subscript) implements Expr {}
