package byx.script.runtime;

import java.util.List;
import java.util.Map;

public interface Value {
    Value UNDEFINED = new UndefinedValue();

    static Value of(int val) {
        return new IntegerValue(val);
    }

    static Value of (double val) {
        return new DoubleValue(val);
    }

    static Value of(boolean val) {
        return BoolValue.of(val);
    }

    static Value of (String val) {
        return new StringValue(val);
    }

    static Value of(Callable val) {
        return new CallableValue(val);
    }

    static Value of(Map<String, Value> val) {
        return new ObjectValue(val);
    }

    static Value of(List<Value> val) {
        return new ListValue(val);
    }

    default Value add(Value rhs) {
        throw new InterpretException(String.format("unsupported operator + between %s and %s", this, rhs));
    }

    default Value sub(Value rhs) {
        throw new InterpretException(String.format("unsupported operator - between %s and %s", this, rhs));
    }

    default Value mul(Value rhs) {
        throw new InterpretException(String.format("unsupported operator * between %s and %s", this, rhs));
    }

    default Value div(Value rhs) {
        throw new InterpretException(String.format("unsupported operator / between %s and %s", this, rhs));
    }

    default Value rem(Value rhs) {
        throw new InterpretException(String.format("unsupported operator %% between %s and %s", this, rhs));
    }

    default Value neg() {
        throw new InterpretException(String.format("unsupported operator - on %s", this));
    }

    default Value lessThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator < between %s and %s", this, rhs));
    }

    default Value lessEqualThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator <= between %s and %s", this, rhs));
    }

    default Value greaterThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator > between %s and %s", this, rhs));
    }

    default Value greaterEqualThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator >= between %s and %s", this, rhs));
    }

    default Value equal(Value rhs) {
        throw new InterpretException(String.format("unsupported operator == between %s and %s", this, rhs));
    }

    default Value notEqual(Value rhs) {
        Value v = equal(rhs);
        if (v instanceof BoolValue) {
            return Value.of(!((BoolValue) v).getValue());
        }
        throw new InterpretException(String.format("unsupported operator != between %s and %s", this, rhs));
    }

    default Value and(Value rhs) {
        throw new InterpretException(String.format("unsupported operator && between %s and %s", this, rhs));
    }

    default Value or(Value rhs) {
        throw new InterpretException(String.format("unsupported operator || between %s and %s", this, rhs));
    }

    default boolean toCondition() {
        throw new InterpretException(String.format("%s is not condition", this));
    }

    default Value not() {
        throw new InterpretException(String.format("unsupported operator ! on %s", this));
    }

    default Value call(List<Value> args) {
        throw new InterpretException(String.format("%s is not callable", this));
    }

    default Value getField(String field) {
        throw new InterpretException(String.format("field %s not exist", field));
    }

    default Value subscript(Value sub) {
        return UNDEFINED;
    }

    default void subscriptAssign(Value subscript, Value value) {
        throw new InterpretException(String.format("unsupported subscript assign: %s", this));
    }

    default void fieldAssign(String field, Value value) {
        throw new InterpretException(String.format("unsupported field assign: %s", this));
    }
}
