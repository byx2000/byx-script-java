package byx.script.core.parser.ast.expr;

import java.util.Map;

/**
 * 对象字面量
 */
public record ObjectLiteral(Map<String, Expr> fields) implements Expr {}
