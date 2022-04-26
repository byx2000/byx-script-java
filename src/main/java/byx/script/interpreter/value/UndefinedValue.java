package byx.script.interpreter.value;

public class UndefinedValue implements Value {
    public static UndefinedValue INSTANCE = new UndefinedValue();

    private UndefinedValue() {}

    @Override
    public String toString() {
        return "undefined";
    }

    @Override
    public Value add(Value rhs) {
        if (rhs instanceof StringValue) {
            return new StringValue("undefined" + ((StringValue) rhs).getValue());
        }
        return Value.super.add(rhs);
    }
}
