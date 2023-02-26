package byx.script.parser.parserc.exception;

import byx.script.parser.parserc.Cursor;

/**
 * 输入结束异常
 */
public class EndOfInputException extends ParseException {
    public EndOfInputException(Cursor cursor) {
        super(cursor, "unexpected end of input");
    }
}
