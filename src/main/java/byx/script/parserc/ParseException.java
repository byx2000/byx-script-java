package byx.script.parserc;

/**
 * 解析异常
 */
public class ParseException extends RuntimeException {
    private final Input input;
    private final String msg;

    public ParseException(Input input, String msg) {
        super(null, null, false, false);
        this.input = input;
        this.msg = msg;
    }

    public Input getInput() {
        return input;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String getMessage() {
        return String.format("at row %d, col %d: \n\t%s", input.row(), input.col(), msg);
    }
}
