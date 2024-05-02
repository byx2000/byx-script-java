package byx.script.core.parser.ast.stmt;

import byx.script.core.parser.ast.expr.Expr;

/**
 * 变量声明
 * var varName = expr
 */
public record VarDeclare(String varName, Expr expr) implements Statement {}
