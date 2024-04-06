package byx.script.core.parser.ast.expr;

import java.util.List;

/**
 * 列表字面量
 */
public record ListLiteral(List<Expr> elems) implements Expr {}
