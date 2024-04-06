package byx.script.core;

import byx.script.core.interpreter.exception.ByxScriptRuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ByxScriptRunnerTest {
    @Test
    public void test() {
        ByxScriptRunner runner = new ByxScriptRunner();
        assertThrows(ByxScriptRuntimeException.class, () -> runner.run("break"));
        assertThrows(ByxScriptRuntimeException.class, () -> runner.run("continue"));
        assertThrows(ByxScriptRuntimeException.class, () -> runner.run("return"));
        assertThrows(ByxScriptRuntimeException.class, () -> runner.run("return 123"));
        assertThrows(ByxScriptRuntimeException.class, () -> runner.run("throw 'hello'"));
    }
}
