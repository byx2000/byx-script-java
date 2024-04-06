package byx.script.core.interpreter.value;

import byx.script.core.interpreter.exception.ByxScriptRuntimeException;
import byx.script.core.interpreter.exception.ThrowException;

import java.util.*;
import java.util.stream.Collectors;

public class ObjectValue implements Value {
    private static final Map<Class<? extends Value>, String> TYPE_ID_MAP = Map.of(
        Value.class, "any",
        NullValue.class, "null",
        IntegerValue.class, "integer",
        DoubleValue.class, "double",
        BoolValue.class, "bool",
        StringValue.class, "string",
        ListValue.class, "list",
        CallableValue.class, "callable",
        ObjectValue.class, "object"
    );

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
        return getFields().toString();
    }

    protected void setCallableField(String field, CallableValue callable) {
        setField(field, callable);
    }

    /**
     * 实现内建方法时，对调用方法时传的参数进行检查
     *
     * @param method 方法名
     * @param args 实参列表
     * @param paramsTypes 参数类型列表
     */
    @SafeVarargs
    protected final void checkArgument(String method, List<Value> args, Class<? extends Value>... paramsTypes) {
        // 检查传参个数是否一致
        if (args.size() != paramsTypes.length) {
            throw new ThrowException(new StringValue(buildArgumentExceptionMsg(method, args, paramsTypes)));
        }

        // 检查传参类型是否匹配
        for (int i = 0; i < args.size(); i++) {
            if (!paramsTypes[i].isAssignableFrom(args.get(i).getClass())) {
                throw new ThrowException(new StringValue(buildArgumentExceptionMsg(method, args, paramsTypes)));
            }
        }
    }

    // 构造错误消息
    @SafeVarargs
    private String buildArgumentExceptionMsg(String method, List<Value> args, Class<? extends Value>... paramsTypes) {
        String paramsTypeList = Arrays.stream(paramsTypes)
                .map(t -> TYPE_ID_MAP.getOrDefault(t, "any"))
                .collect(Collectors.joining(", "));
        String argsTypeList = args.stream().map(Value::typeId).collect(Collectors.joining(", "));
        return String.format("method %s expect parameters (%s) but receive (%s)",
                method, paramsTypeList, argsTypeList);
    }

    @Override
    public String typeId() {
        return "object";
    }
}
