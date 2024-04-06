package byx.script.core.interpreter.value.builtin;

import byx.script.core.interpreter.value.*;

import java.util.Scanner;

/**
 * 内建对象，读取输入
 */
public class Reader extends ObjectValue {
    public Reader(Scanner scanner) {
        // 添加内建属性
        setCallableField("nextLine", args -> new StringValue(scanner.nextLine()));
        setCallableField("nextInt", args -> new IntegerValue(scanner.nextInt()));
        setCallableField("nextDouble", args -> new DoubleValue(scanner.nextDouble()));
        setCallableField("nextBool", args -> BoolValue.of(scanner.nextBoolean()));
        setCallableField("hasNext", args -> BoolValue.of(scanner.hasNext()));
    }
}
