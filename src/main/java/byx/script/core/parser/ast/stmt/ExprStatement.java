package byx.script.core.parser.ast.stmt;

import byx.script.core.parser.ast.expr.Expr;

/**
 * 表达式语句
 * 对表达式求值，然后直接丢弃求值结果
 */
public record ExprStatement(Expr expr) implements Statement {}
