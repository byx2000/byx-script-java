package byx.script.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 测试工具类
 */
public class TestUtils {
    public static void verify(String script, String expectedOutput) {
        verify(Collections.emptyList(), script, "", expectedOutput);
    }

    public static void verify(String script, String input, String expectedOutput) {
        verify(Collections.emptyList(), script, input, expectedOutput);
    }

    public static void verify(List<Path> importPaths, String script, String expectedOutput) {
        verify(importPaths, script, "", expectedOutput);
    }

    public static void verify(List<Path> importPaths, String script, String input, String expectedOutput) {
        String output = getScriptOutput(importPaths, script, input);
        assertEquals(replaceBlank(expectedOutput), replaceBlank(output), "实际输出与期望输出不符");
    }

    public static void verifyException(Class<? extends Exception> type, List<Path> importPaths, String script) {
        assertThrows(type, () -> {
            ByxScriptRunner runner = new ByxScriptRunner();
            runner.addImportPaths(importPaths);
            runner.run(script);
        });
    }

    private static String getScriptOutput(List<Path> importPaths, String script, String input) {
        try (
                ByteArrayInputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Scanner scanner = new Scanner(is);
                PrintStream printStream = new PrintStream(os)
        ) {
            ByxScriptRunner runner = new ByxScriptRunner(scanner, printStream);
            runner.addImportPaths(importPaths);
            runner.run(script);
            return os.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getOutput(Consumer<PrintStream> consumer) {
        try (
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(os)
        ) {
            consumer.accept(printStream);
            return os.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String replaceBlank(String s) {
        s = s.replaceAll("\\r\\n", "\n");
        s = s.replaceAll("\\s+\\n", "\n");
        s = s.replaceAll("\\s+$", "");
        return s;
    }
}
