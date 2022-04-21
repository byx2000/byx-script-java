package byx.script.runtime.value;

import java.util.Map;

public class ObjectValue extends FieldWritableValue {
    public ObjectValue(Map<String, Value> fields) {
        setFields(fields);
    }

    @Override
    public String toString() {
        return "Object";
    }
}
