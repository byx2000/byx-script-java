package byx.script.runtime;

import java.util.HashMap;
import java.util.Map;

public class ObjectValue implements Value {
    private final Map<String, Value> fields;

    public ObjectValue(Map<String, Value> fields) {
        this.fields = new HashMap<>(fields);
        this.fields.put("fields", Value.of(args -> Value.of(this.fields.keySet().stream().map(Value::of).toList())));
        this.fields.put("getField", Value.of(args -> {
            if (args.size() != 1 || !(args.get(0) instanceof StringValue)) {
                throw new InterpretException("getField method require 1 string argument");
            }
            String field = ((StringValue) args.get(0)).getValue();
            if (!this.fields.containsKey(field)) {
                throw new InterpretException(String.format("field %s not exist", field));
            }
            return this.fields.get(field);
        }));
        this.fields.put("setField", Value.of(args -> {
            if (args.size() != 2 || !(args.get(0) instanceof StringValue)) {
                throw new InterpretException("setField method require 2 arguments");
            }
            String field = ((StringValue) args.get(0)).getValue();
            this.fields.put(field, args.get(1));
            return UNDEFINED;
        }));
    }

    public Map<String, Value> getFields() {
        return fields;
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof UndefinedValue) {
            return Value.of(false);
        }
        return Value.super.equal(rhs);
    }

    @Override
    public String toString() {
        return "Object";
    }

    @Override
    public Value getField(String field) {
        if (fields.containsKey(field)) {
            return fields.get(field);
        }
        return Value.super.getField(field);
    }

    @Override
    public void fieldAssign(String field, Value value) {
        fields.put(field, value);
    }
}
