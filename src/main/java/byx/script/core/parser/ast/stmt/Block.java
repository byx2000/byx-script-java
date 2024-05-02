package byx.script.core.parser.ast.stmt;

import java.util.Collections;
import java.util.List;

/**
 * 语句块
 */
public record Block(List<Statement> stmts) implements Statement {
    public static final Block EMPTY = new Block(Collections.emptyList());
}
