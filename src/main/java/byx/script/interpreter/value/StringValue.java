package byx.script.interpreter.value;

import java.util.Objects;

public class StringValue extends AbstractValue {
    private final String value;

    public StringValue(String value) {
        this.value = value;
        setCallableField("length", () -> Value.of(value.length()));
        setCallableField("substring", IntegerValue.class, IntegerValue.class, (start, end) -> Value.of(value.substring(start.getValue(), end.getValue())));
        setCallableField("concat", StringValue.class, s -> Value.of(value.concat(s.getValue())));
        setCallableField("charAt", IntegerValue.class, index -> Value.of(String.valueOf(value.charAt(index.getValue()))));
        setCallableField("codeAt", IntegerValue.class, index -> Value.of(value.charAt(index.getValue())));
        setCallableField("compareTo", StringValue.class, s -> Value.of(value.compareTo(s.getValue())));
        setCallableField("toInt", () -> Value.of(Integer.parseInt(value)));
        setCallableField("toDouble", () -> Value.of(Double.parseDouble(value)));
        setCallableField("toBool", () -> Value.of(Boolean.parseBoolean(value)));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringValue that = (StringValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "String";
    }

    @Override
    public Value add(Value rhs) {
        if (rhs instanceof StringValue) {
            return new StringValue(value + ((StringValue) rhs).getValue());
        } else if (rhs instanceof IntegerValue) {
            return new StringValue(value + ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new StringValue(value + ((DoubleValue) rhs).getValue());
        } else if (rhs instanceof BoolValue) {
            return new StringValue(value + ((BoolValue) rhs).getValue());
        } else if (rhs instanceof UndefinedValue) {
            return new StringValue(value + "undefined");
        }
        return super.add(rhs);
    }

    @Override
    public Value lessThan(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) < 0);
        }
        return super.lessThan(rhs);
    }

    @Override
    public Value lessEqualThan(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) <= 0);
        }
        return super.lessEqualThan(rhs);
    }

    @Override
    public Value greaterThan(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) > 0);
        }
        return super.greaterThan(rhs);
    }

    @Override
    public Value greaterEqualThan(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) >= 0);
        }
        return super.greaterEqualThan(rhs);
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) == 0);
        }
        return BoolValue.of(false);
    }

    @Override
    public Value notEqual(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) != 0);
        }
        return BoolValue.of(true);
    }

    @Override
    public Value subscript(Value sub) {
        if (sub instanceof IntegerValue) {
            int index = ((IntegerValue) sub).getValue();
            return Value.of(String.valueOf(value.charAt(index)));
        }
        return super.subscript(sub);
    }
}
