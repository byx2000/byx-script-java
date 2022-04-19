package byx.script;

import org.junit.jupiter.api.function.Executable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试工具类
 */
public class TestUtils {
    public static void verify(String script, String expectedOutput) {
        String output = getScriptOutput(script);
        assertEquals(replaceBlank(expectedOutput), replaceBlank(output), "实际输出与期望输出不符");
    }

    public static String getOutput(Executable executable) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PrintStream out = System.out;
            System.setOut(new PrintStream(os));
            executable.execute();
            System.setOut(new PrintStream(out));
            return os.toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static String getScriptOutput(String script) {
        return getOutput(() -> ByxScriptRunner.run(script));
    }

    private static String replaceBlank(String s) {
        s = s.replaceAll("\\r\\n", "\n");
        s = s.replaceAll("\\s+\\n", "\n");
        s = s.replaceAll("\\s+$", "");
        return s;
    }
}
