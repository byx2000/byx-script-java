package byx.script.core.parser.ast.stmt;

/**
 * try-catch-finally语句
 * try {
 *     stmts
 * } catch {
 *     stmts
 * } finally {
 *     stmts
 * }
 */
public record Try(Statement tryBranch, String catchVar, Statement catchBranch,
                  Statement finallyBranch) implements Statement {}
