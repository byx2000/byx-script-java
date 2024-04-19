package byx.script.core.parser.exception;

import byx.script.core.common.FastException;

public class InternalParseException extends FastException {
    private final String input;
    private final int index;

    public InternalParseException(String input, int index) {
        this.input = input;
        this.index = index;
    }

    public String getInput() {
        return input;
    }

    public int getIndex() {
        return index;
    }
}
