package byx.script.core.parser.exception;

import byx.script.core.common.FastException;

/**
 * ByxScript解析异常
 */
public class ByxScriptParseException extends FastException {
    private final int row;
    private final int col;
    private final String msg;

    public ByxScriptParseException(int row, int col, String msg) {
        this.row = row;
        this.col = col;
        this.msg = msg;
    }

    public ByxScriptParseException(String msg) {
        this(0, 0, msg);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String getMessage() {
        return (row > 0 && col > 0)
            ? String.format("parse error at row %d, col %d: %s", row, col, msg)
            : String.format("parse error: \n%s", msg);
    }
}
