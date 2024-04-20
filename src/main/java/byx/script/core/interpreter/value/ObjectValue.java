package byx.script.core.interpreter.value;

import byx.script.core.interpreter.Cont;
import byx.script.core.interpreter.exception.ByxScriptRuntimeException;
import byx.script.core.util.ValueUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ObjectValue implements Value {
    private final Map<String, Value> fields = new HashMap<>();

    public ObjectValue() {
        this(Collections.emptyMap());
    }

    public ObjectValue(Map<String, Value> initialFields) {
        fields.putAll(initialFields);
    }

    public Map<String, Value> getFields() {
        return fields;
    }

    public boolean hasField(String field) {
        return fields.containsKey(field);
    }

    public Value getField(String field) {
        if (fields.containsKey(field)) {
            return fields.get(field);
        }
        throw new ByxScriptRuntimeException(String.format("field %s not exist", field));
    }

    public void setField(String field, Value rhs) {
        fields.put(field, rhs);
    }

    @Override
    public String toString() {
        return ValueUtils.valueToString(this);
    }

    /**
     * 添加内建属性方法
     * @param fieldName 属性名
     * @param callable 方法执行逻辑
     */
    protected void setCallableField(String fieldName, Function<List<Value>, Value> callable) {
        setField(fieldName, (CallableValue) args -> Cont.value(callable.apply(args)));
    }

    @Override
    public String typeId() {
        return "object";
    }
}
