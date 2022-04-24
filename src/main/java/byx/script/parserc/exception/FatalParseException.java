package byx.script.parserc.exception;

import byx.script.parserc.Input;

public class FatalParseException extends RuntimeException {
    private final Input input;
    private final String msg;

    public FatalParseException(Input input, String msg) {
        super(null, null, false, false);
        this.input = input;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return String.format("at row %d, col %d: \n\t%s", input.row(), input.col(), msg);
    }
}
