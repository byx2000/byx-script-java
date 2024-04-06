package byx.script.core.parser.ast.stmt;

/**
 * 跳出循环
 */
public class Break implements Statement {
    public static final Break INSTANCE = new Break();

    private Break() {}
}
