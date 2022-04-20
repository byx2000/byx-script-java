package byx.script.runtime.value;

import byx.script.runtime.exception.InterpretException;

import java.util.Map;

public class ObjectValue extends FieldWritableValue {
    public ObjectValue(Map<String, Value> fields) {
        setFields(Map.of(
                "getField", Value.of(args -> {
                    if (args.size() != 1 || !(args.get(0) instanceof StringValue)) {
                        throw new InterpretException("getField method require 1 string argument");
                    }
                    String field = ((StringValue) args.get(0)).getValue();
                    return getField(field);
                }),
                "setField", Value.of(args -> {
                    if (args.size() != 2 || !(args.get(0) instanceof StringValue)) {
                        throw new InterpretException("setField method require 2 arguments");
                    }
                    String field = ((StringValue) args.get(0)).getValue();
                    setField(field, args.get(1));
                    return UndefinedValue.INSTANCE;
                })
        ));
        setFields(fields);
    }

    @Override
    public Value equal(Value rhs) {
        return Value.of(this == rhs);
    }

    @Override
    public String toString() {
        return "Object";
    }
}
