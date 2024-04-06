package byx.script.core;

import byx.script.core.parser.ByxScriptParser;
import byx.script.core.parser.exception.ByxScriptParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ByxScriptParserTest {
    private void verifyException(Executable executable, String errMsg) {
        ByxScriptParseException e = assertThrowsExactly(ByxScriptParseException.class, executable);
        assertTrue(e.getMessage().contains(errMsg),
                String.format("actual msg: %s", e.getMessage()));
    }

    @Test
    public void testVarDeclareException() {
        verifyException(() -> ByxScriptParser.parse("var "), "expect identifier");
        verifyException(() -> ByxScriptParser.parse("var = 100"), "expect identifier");
        verifyException(() -> ByxScriptParser.parse("var a"), "expect '='");
        verifyException(() -> ByxScriptParser.parse("var a 100"), "expect '='");
        verifyException(() -> ByxScriptParser.parse("var a = @#@%#$"), "invalid expression");
        verifyException(() -> ByxScriptParser.parse("var a = +-"), "invalid expression");
        verifyException(() -> ByxScriptParser.parse("var a = 3+(6-)"), "invalid expression");
        verifyException(() -> ByxScriptParser.parse("var a = 3+*4"), "invalid expression");
    }

    @Test
    public void testFuncDeclareException() {
        verifyException(() -> ByxScriptParser.parse("func "), "expect identifier");
        verifyException(() -> ByxScriptParser.parse("func fun"), "expect '('");
        verifyException(() -> ByxScriptParser.parse("func fun(a, b"), "expect ')'");
        verifyException(() -> ByxScriptParser.parse("func fun(a, b) return 100"), "expect '{'");
        verifyException(() -> ByxScriptParser.parse("func fun(a, b) {return 100"), "expect '}'");
        verifyException(() -> ByxScriptParser.parse("func (a, b) {return 100}"), "expect identifier");
        verifyException(() -> ByxScriptParser.parse("func fun(a, , b)"), "expect identifier");
        verifyException(() -> ByxScriptParser.parse("func fun(, a, b)"), "expect identifier");
        verifyException(() -> ByxScriptParser.parse("func fun(+-)"), "expect identifier");
        verifyException(() -> ByxScriptParser.parse("func fun(a, b, )"), "expect identifier");
        verifyException(() -> ByxScriptParser.parse("func fun(a, 123, b)"), "expect identifier");
    }
}
