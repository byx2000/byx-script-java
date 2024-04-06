package byx.script.core.parser.ast.expr;

import java.util.List;

/**
 * 函数调用
 */
public record Call(Expr expr, List<Expr> args) implements Expr {}
