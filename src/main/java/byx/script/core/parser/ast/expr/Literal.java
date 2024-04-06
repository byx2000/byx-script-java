package byx.script.core.parser.ast.expr;

import byx.script.core.interpreter.value.Value;

/**
 * 字面量
 */
public record Literal(Value value) implements Expr {}
