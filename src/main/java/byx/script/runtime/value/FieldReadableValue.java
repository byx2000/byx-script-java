package byx.script.runtime.value;

import java.util.HashMap;
import java.util.Map;

public abstract class FieldReadableValue implements Value {
    private final Map<String, Value> fields = new HashMap<>();

    public Map<String, Value> getFields() {
        return fields;
    }

    protected void setField(String field, Value value) {
        fields.put(field, value);
    }

    protected void setFields(Map<String, Value> fieldsToAdd) {
        fields.putAll(fieldsToAdd);
    }

    public Value getField(String field) {
        if (fields.containsKey(field)) {
            return fields.get(field);
        }
        return Value.super.getField(field);
    }
}
