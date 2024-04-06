package byx.script.core.interpreter.value;

public class NullValue implements Value {
    public static final NullValue INSTANCE = new NullValue();

    private NullValue() {}

    @Override
    public String typeId() {
        return "null";
    }
}
