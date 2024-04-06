package byx.script.core.interpreter.value;

import byx.script.core.interpreter.exception.ThrowException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public Value getField(String field) {
        if (fields.containsKey(field)) {
            return fields.get(field);
        }
        return Value.super.getField(field);
    }

    @Override
    public void setField(String field, Value rhs) {
        fields.put(field, rhs);
    }

    @Override
    public String toString() {
        return getFields().toString();
    }

    @Override
    public Value equal(Value rhs) {
        if (hasField("_equal")) {
            return getField("_equal").call(List.of(rhs));
        }
        return Value.super.equal(rhs);
    }

    @Override
    public Value add(Value rhs) {
        if (hasField("_add")) {
            return getField("_add").call(List.of(rhs));
        }
        return Value.super.add(rhs);
    }

    @Override
    public Value sub(Value rhs) {
        if (hasField("_sub")) {
            return getField("_sub").call(List.of(rhs));
        }
        return Value.super.add(rhs);
    }

    @Override
    public Value mul(Value rhs) {
        if (hasField("_mul")) {
            return getField("_mul").call(List.of(rhs));
        }
        return Value.super.add(rhs);
    }

    @Override
    public Value div(Value rhs) {
        if (hasField("_div")) {
            return getField("_div").call(List.of(rhs));
        }
        return Value.super.add(rhs);
    }

    protected void setCallableField(String field, Function<List<Value>, Value> callable) {
        setField(field, new CallableValue(callable));
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
}
