package byx.script;

import byx.script.interpreter.InterpretException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static byx.script.TestUtils.*;

public class ImportTest {
    @Test
    public void test() throws Exception {
        Path classPath = Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("./")).toURI());
        verify(List.of(classPath.resolve("p1"), classPath.resolve("p2")), """
                import a
                import b
                
                Console.println('main')
                """, """
                d
                c
                b
                a
                main
                """
        );
        verifyException(InterpretException.class, List.of(classPath.resolve("p3")), """
                import x
                
                Console.println('main')
                """);
    }
}
