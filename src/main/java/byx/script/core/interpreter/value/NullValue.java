package byx.script.core.interpreter.value;

public class NullValue implements Value {
    public static final NullValue INSTANCE = new NullValue();

    private NullValue() {}

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public Value add(Value rhs) {
        if (rhs instanceof StringValue) {
            return new StringValue("null" + ((StringValue) rhs).getValue());
        }
        return Value.super.add(rhs);
    }
}
