package byx.script.core.util;

import byx.script.core.interpreter.exception.BuiltinFunctionException;
import byx.script.core.interpreter.value.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValueUtils {
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

    /**
     * 将Value转换成可打印的字符串
     * @param value value
     * @return value对应的字符串
     */
    public static String valueToString(Value value) {
        return valueToString(value, true);
    }

    private static String valueToString(Value value, boolean deep) {
        if (value instanceof IntegerValue) {
            return String.valueOf(((IntegerValue) value).value());
        } else if (value instanceof DoubleValue) {
            return String.valueOf(((DoubleValue) value).value());
        } else if (value instanceof BoolValue) {
            return String.valueOf(((BoolValue) value).getValue());
        } else if (value instanceof StringValue) {
            return ((StringValue) value).value();
        } else if (value instanceof NullValue) {
            return "null";
        } else if (value instanceof ListValue) {
            if (!deep) {
                return "[...]";
            }
            List<Value> values = ((ListValue) value).getElems();
            return "[" + values.stream().map(v -> valueToString(v, false)).collect(Collectors.joining(", ")) + "]";
        } else if (value instanceof CallableValue) {
            return "f(...)";
        } else if (value instanceof ObjectValue) {
            if (!deep) {
                return "{...}";
            }
            Map<String, Value> fields = ((ObjectValue) value).getFields();
            return "{" + fields.entrySet().stream()
                .map(e -> e.getKey() + ": " + valueToString(e.getValue(), false))
                .collect(Collectors.joining(", ")) + "}";
        } else {
            return value.toString();
        }
    }

    /**
     * 针对一个参数只有一种可选类型的情况
     * @param funcName 函数名
     * @param args 实参列表
     * @param paramsTypes 参数期望类型列表
     */
    @SafeVarargs
    public static void checkArgument(String funcName, List<Value> args, Class<? extends Value>... paramsTypes) {
        // 检查传参个数是否一致
        if (args.size() != paramsTypes.length) {
            throw new BuiltinFunctionException(buildArgumentExceptionMsg(funcName, args, paramsTypes));
        }

        // 检查传参类型是否匹配
        for (int i = 0; i < args.size(); i++) {
            if (!paramsTypes[i].isAssignableFrom(args.get(i).getClass())) {
                throw new BuiltinFunctionException(buildArgumentExceptionMsg(funcName, args, paramsTypes));
            }
        }
    }

    /**
     * 参数检查，针对一个参数有多种可选类型的情况
     * @param funcName 函数名
     * @param args 实参列表
     * @param paramOptionalTypes 参数可选类型列表
     */
    @SafeVarargs
    public static void checkArgument(String funcName, List<Value> args, List<Class<? extends Value>>... paramOptionalTypes) {
        // 检查传参个数是否一致
        if (args.size() != paramOptionalTypes.length) {
            throw new BuiltinFunctionException(buildArgumentExceptionMsg(funcName, args, paramOptionalTypes));
        }

        // 检查传参类型是否匹配
        for (int i = 0; i < args.size(); i++) {
            int finalI = i;
            if (paramOptionalTypes[i].stream().noneMatch(t -> t.isAssignableFrom(args.get(finalI).getClass()))) {
                throw new BuiltinFunctionException(buildArgumentExceptionMsg(funcName, args, paramOptionalTypes));
            }
        }
    }

    /**
     * 构造参数错误消息，针对一个参数只有一种可选类型的情况
     * @param funcName 函数名
     * @param args 实参列表
     * @param paramsTypes 参数期望类型列表
     * @return 错误消息
     */
    @SafeVarargs
    public static String buildArgumentExceptionMsg(String funcName, List<Value> args, Class<? extends Value>... paramsTypes) {
        String paramsTypeList = Arrays.stream(paramsTypes)
            .map(t -> TYPE_ID_MAP.getOrDefault(t, "any"))
            .collect(Collectors.joining(", "));
        String argsTypeList = args.stream().map(Value::typeId).collect(Collectors.joining(", "));
        return String.format("ArgumentError: function %s expect parameters (%s) but receive (%s)",
            funcName, paramsTypeList, argsTypeList);
    }

    /**
     * 构造参数错误消息，针对一个参数有多种可选类型的情况
     * @param funcName 函数名
     * @param args 实参列表
     * @param paramsOptionalTypes 参数可选类型列表
     * @return 错误消息
     */
    @SafeVarargs
    public static String buildArgumentExceptionMsg(String funcName, List<Value> args, List<Class<? extends Value>>... paramsOptionalTypes) {
        String paramsTypeList = Arrays.stream(paramsOptionalTypes)
            .map(ts -> ts.stream()
                .map(t -> TYPE_ID_MAP.getOrDefault(t, "any"))
                .collect(Collectors.joining("|", "[", "]")))
            .collect(Collectors.joining(", "));
        String argsTypeList = args.stream()
            .map(Value::typeId)
            .collect(Collectors.joining(", "));
        return String.format("ArgumentError: function %s expect parameters (%s) but receive (%s)",
            funcName, paramsTypeList, argsTypeList);
    }
}
