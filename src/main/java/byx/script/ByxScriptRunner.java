package byx.script;

import byx.script.ast.Program;

public class ByxScriptRunner {
    public static void run(String input) {
        Program program = ByxScriptParser.parse(input);
        program.run();
    }
}
