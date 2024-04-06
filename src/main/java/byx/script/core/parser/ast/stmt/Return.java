package byx.script.core.parser.ast.stmt;

import byx.script.core.parser.ast.expr.Expr;

/**
 * 函数返回语句
 */
public record Return(Expr retVal) implements Statement {}
