package byx.script.core.parser.ast.expr;

/**
 * 字段访问
 */
public record FieldAccess(Expr expr, String fieldName) implements Expr {}
