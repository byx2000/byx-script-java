package byx.script.core.parser.exception;

import byx.script.core.common.FastException;
import byx.script.core.parser.parserc.Cursor;

/**
 * ByxScript解析异常
 */
public class ByxScriptParseException extends FastException {
    private final Cursor cursor;
    private final String msg;

    public ByxScriptParseException(Cursor cursor, String msg) {
        this.cursor = cursor;
        this.msg = msg;
    }

    public ByxScriptParseException(String msg) {
        this(null, msg);
    }

    @Override
    public String getMessage() {
        return (cursor != null)
                ? String.format("syntax error: \nrow: %d, col: %d: %s", cursor.row(), cursor.col(), msg)
                : String.format("syntax: \n%s", msg);
    }
}
