package byx.script;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static byx.script.TestUtils.*;

public class SampleTest {
    private Path getPathFromClasspath(String path) throws Exception {
        return Paths.get(Objects.requireNonNull(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(path)).toURI()));
    }

    private String readFileFromClasspath(String filename) throws Exception {
        Path path = getPathFromClasspath(filename);
        return Files.readString(path);
    }

    private void verifyCase(String caseName) {
        try {
            System.out.println("case " + caseName + " begin");
            String script = readFileFromClasspath(caseName + ".bs");
            String expectedOutput = readFileFromClasspath(caseName + ".out");
            verify(script, expectedOutput);
            System.out.println("case " + caseName + " finish");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testExample() throws Exception {
        try (Stream<Path> files = Files.list(getPathFromClasspath("./"))) {
            files.map(path -> path.getFileName().toString())
                    .filter(filename -> filename.endsWith(".bs"))
                    .map(filename -> filename.replace(".bs", ""))
                    .forEach(this::verifyCase);
        }
    }
}
