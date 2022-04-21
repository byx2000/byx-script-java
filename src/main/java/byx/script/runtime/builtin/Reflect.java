package byx.script.runtime.builtin;

import byx.script.runtime.value.*;

public class Reflect extends FieldReadableValue {
    public static final Reflect INSTANCE = new Reflect();

    private Reflect() {
        setCallableField("isInt", Value.class, v -> Value.of(v instanceof IntegerValue));
        setCallableField("isDouble", Value.class, v -> Value.of(v instanceof DoubleValue));
        setCallableField("isBool", Value.class, v -> Value.of(v instanceof BoolValue));
        setCallableField("isString", Value.class, v -> Value.of(v instanceof StringValue));
        setCallableField("isList", Value.class, v -> Value.of(v instanceof ListValue));
        setCallableField("isObject", Value.class, v -> Value.of(v instanceof ObjectValue));
        setCallableField("isUndefined", Value.class, v -> Value.of(v instanceof UndefinedValue));
        setCallableField("hashCode", Value.class, v -> Value.of(v.hashCode()));
    }
}
