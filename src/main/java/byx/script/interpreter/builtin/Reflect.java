package byx.script.interpreter.builtin;

import byx.script.interpreter.value.*;

/**
 * Native.Reflect
 */
public class Reflect extends AbstractValue {
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
        setCallableField("fields", AbstractValue.class, v -> Value.of(v.getFields().keySet().stream().map(Value::of).toList()));
        setCallableFieldNoReturn("setField", AbstractValue.class, StringValue.class, Value.class, (obj, field, value) -> obj.setField(field.getValue(), value));
        setCallableField("getField", AbstractValue.class, StringValue.class, (obj, field) -> obj.getField(field.getValue()));
        setCallableField("hasField", AbstractValue.class, StringValue.class, (obj, field) -> Value.of(obj.hasField(field.getValue())));
    }
}
