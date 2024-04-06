package byx.script.core.parser.ast.expr;

/**
 * 变量引用
 */
public record Var(String varName) implements Expr {}
