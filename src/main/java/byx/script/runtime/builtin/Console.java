package byx.script.runtime.builtin;

import byx.script.runtime.*;
import byx.script.runtime.value.CallableValue;
import byx.script.runtime.value.ListValue;
import byx.script.runtime.value.ObjectValue;
import byx.script.runtime.value.UndefinedValue;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * 内建Console对象
 */
public class Console extends Value {
    public static final Console INSTANCE = new Console();
    private static final Scanner scanner = new Scanner(System.in);

    private Console() {
        setFields(Map.of(
                "println", Value.of(args -> {
                    System.out.println(args.stream().map(v -> valueToString(v, true)).collect(Collectors.joining(" ")));
                    return UndefinedValue.INSTANCE;
                }),
                "print", Value.of(args -> {
                    System.out.print(args.stream().map(v -> valueToString(v, true)).collect(Collectors.joining(" ")));
                    return UndefinedValue.INSTANCE;
                }),
                "readLine", Value.of(args -> Value.of(scanner.nextLine())),
                "readInt", Value.of(args -> Value.of(scanner.nextInt())),
                "readDouble", Value.of(args -> Value.of(scanner.nextDouble())),
                "readBool", Value.of(args -> Value.of(scanner.nextBoolean())),
                "hasNext", Value.of(args -> Value.of(scanner.hasNext()))
        ));
    }

    @Override
    public String toString() {
        return "Console";
    }

    private static String valueToString(Value value, boolean deep) {
        if (!deep) {
            return value.toString();
        }

        if (value instanceof ObjectValue) {
            Map<String, Value> fields = value.getFields();
            return "{" + fields.entrySet().stream()
                    .filter(e -> !(e.getValue() instanceof CallableValue))
                    .map(e -> e.getKey() + ": " + valueToString(e.getValue(), false))
                    .collect(Collectors.joining(", ")) + "}";
        } else if (value instanceof ListValue) {
            List<Value> values = ((ListValue) value).getValue();
            return "[" + values.stream().map(v -> valueToString(v, false)).collect(Collectors.joining(", ")) + "]";
        } else {
            return value.toString();
        }
    }
}
