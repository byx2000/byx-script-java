package byx.script.parser.parserc;

/**
 * 封装解析器输入
 */
public class Input {
    private final String s;
    private final int index;
    private final int row, col;

    public Input(String s, int index) {
        this(s, index, 1, 1);
    }

    private Input(String s, int index, int row, int col) {
        this.s = s;
        this.index = index;
        this.row = row;
        this.col = col;
    }

    public Input next() {
        int row = this.row;
        int col = this.col + 1;
        if (s.charAt(index) == '\n') {
            row++;
            col = 1;
        }
        return new Input(s, index + 1, row, col);
    }

    public boolean end() {
        return index == s.length();
    }

    public char current() {
        return s.charAt(index);
    }

    public int index() {
        return index;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    @Override
    public String toString() {
        return s.substring(index);
    }
}
