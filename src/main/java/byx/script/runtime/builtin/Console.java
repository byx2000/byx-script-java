package byx.script.runtime.builtin;

import byx.script.runtime.value.*;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * 内建Console对象
 */
public class Console extends FieldReadableValue {
    public static final Console INSTANCE = new Console();
    private static final Scanner scanner = new Scanner(System.in);

    private Console() {
        setCallableFieldNoReturn("println", args -> System.out.println(args.stream().map(v -> valueToString(v, true)).collect(Collectors.joining(" "))));
        setCallableFieldNoReturn("print", args -> System.out.print(args.stream().map(v -> valueToString(v, true)).collect(Collectors.joining(" "))));
        setCallableField("readLine", () -> Value.of(scanner.nextLine()));
        setCallableField("readInt", () -> Value.of(scanner.nextInt()));
        setCallableField("readDouble", () -> Value.of(scanner.nextDouble()));
        setCallableField("readBool", () -> Value.of(scanner.nextBoolean()));
        setCallableField("hasNext", () -> Value.of(scanner.hasNext()));
    }

    @Override
    public String toString() {
        return "Console";
    }

    // 将Value转换成可打印的字符串
    private static String valueToString(Value value, boolean deep) {
        if (value instanceof IntegerValue) {
            return String.valueOf(((IntegerValue) value).getValue());
        } else if (value instanceof DoubleValue) {
            return String.valueOf(((DoubleValue) value).getValue());
        } else if (value instanceof BoolValue) {
            return String.valueOf(((BoolValue) value).getValue());
        } else if (value instanceof StringValue) {
            return ((StringValue) value).getValue();
        } else if (value instanceof ListValue) {
            if (!deep) {
                return "List";
            }
            List<Value> values = ((ListValue) value).getValue();
            return "[" + values.stream().map(v -> valueToString(v, false)).collect(Collectors.joining(", ")) + "]";
        } else if (value instanceof ObjectValue) {
            if (!deep) {
                return "Object";
            }
            Map<String, Value> fields = ((ObjectValue) value).getFields();
            return "{" + fields.entrySet().stream()
                    .map(e -> e.getKey() + ": " + valueToString(e.getValue(), false))
                    .collect(Collectors.joining(", ")) + "}";
        } else {
            return value.toString();
        }
    }
}
