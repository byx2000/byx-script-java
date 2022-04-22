package byx.script.runtime.host;

import byx.script.runtime.value.FieldReadableValue;
import byx.script.runtime.value.Value;

import java.util.Scanner;

public class Reader extends FieldReadableValue {
    public final Scanner scanner = new Scanner(System.in);

    public Reader() {
        setCallableField("nextLine", () -> Value.of(scanner.nextLine()));
        setCallableField("nextInt", () -> Value.of(scanner.nextInt()));
        setCallableField("nextDouble", () -> Value.of(scanner.nextDouble()));
        setCallableField("nextBool", () -> Value.of(scanner.nextBoolean()));
        setCallableField("hasNext", () -> Value.of(scanner.hasNext()));
    }
}
