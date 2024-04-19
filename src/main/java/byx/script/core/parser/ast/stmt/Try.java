package byx.script.core.parser.ast.stmt;

/**
 * try-catch语句
 * try {
 *     tryBody
 * } catch (catchVar) {
 *     catchBody
 * }
 */
public record Try(Statement tryBody, String catchVar, Statement catchBody) implements Statement {}

