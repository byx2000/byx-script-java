package byx.script.core.interpreter.builtin;

import byx.script.core.interpreter.value.*;
import byx.script.core.util.ValueUtils;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * 输入输出内建函数
 */
public class IOFunctions {
    public static BuiltinFunction println(PrintStream printStream) {
        return new BuiltinFunction() {
            @Override
            public String name() {
                return "println";
            }

            @Override
            public Value onCall(List<Value> args) {
                printStream.println(args.stream().map(ValueUtils::valueToString).collect(Collectors.joining(" ")));
                return NullValue.INSTANCE;
            }
        };
    }

    public static BuiltinFunction print(PrintStream printStream) {
        return new BuiltinFunction() {
            @Override
            public String name() {
                return "print";
            }

            @Override
            public Value onCall(List<Value> args) {
                printStream.print(args.stream().map(ValueUtils::valueToString).collect(Collectors.joining(" ")));
                return NullValue.INSTANCE;
            }
        };
    }

    public static BuiltinFunction readLine(Scanner scanner) {
        return new BuiltinFunction() {
            @Override
            public String name() {
                return "readLine";
            }

            @Override
            public Value onCall(List<Value> args) {
                return new StringValue(scanner.nextLine());
            }
        };
    }

    public static BuiltinFunction readInt(Scanner scanner) {
        return new BuiltinFunction() {
            @Override
            public String name() {
                return "readInt";
            }

            @Override
            public Value onCall(List<Value> args) {
                return new IntegerValue(scanner.nextInt());
            }
        };
    }

    public static BuiltinFunction readDouble(Scanner scanner) {
        return new BuiltinFunction() {
            @Override
            public String name() {
                return "readDouble";
            }

            @Override
            public Value onCall(List<Value> args) {
                return new DoubleValue(scanner.nextDouble());
            }
        };
    }

    public static BuiltinFunction readBool(Scanner scanner) {
        return new BuiltinFunction() {
            @Override
            public String name() {
                return "readBool";
            }

            @Override
            public Value onCall(List<Value> args) {
                return BoolValue.of(scanner.nextBoolean());
            }
        };
    }

    public static BuiltinFunction hasNext(Scanner scanner) {
        return new BuiltinFunction() {
            @Override
            public String name() {
                return "hasNext";
            }

            @Override
            public Value onCall(List<Value> args) {
                return BoolValue.of(scanner.hasNext());
            }
        };
    }
}
