package byx.script;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class StandardLibraryTest {
    private static final Path LIB_PATH;

    static {
        try {
            LIB_PATH = Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("lib")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("load lib path failed", e);
        }
    }

    private void verify(String script, String expectedOutput) {
        TestUtils.verify(List.of(LIB_PATH), script, expectedOutput);
    }

    @Test
    public void testStack() {
        verify("""
                import stack
                
                var s = Stack()
                s.push(1)
                s.push(2)
                s.push(3)
                Console.println(s.size(), s.isEmpty())
                
                Console.println(s.pop())
                Console.println(s.pop())
                Console.println(s.size(), s.isEmpty())
                
                s.push(4)
                s.push(5)
                Console.println(s.top())
                Console.println(s.size())
                
                Console.println(s.pop())
                Console.println(s.pop())
                Console.println(s.pop())
                Console.println(s.size(), s.isEmpty())
                """, """
                3 false
                3
                2
                1 false
                5
                3
                5
                4
                1
                0 true
                """);
    }

    @Test
    public void testQueue() {
        verify("""
                import queue
                
                var q = Queue()
                q.enQueue(1)
                q.enQueue(2)
                q.enQueue(3)
                Console.println(q.size(), q.isEmpty())
                
                Console.println(q.deQueue())
                Console.println(q.deQueue())
                Console.println(q.size(), q.isEmpty())
                
                q.enQueue(4)
                q.enQueue(5)
                q.enQueue(6)
                Console.println(q.size())
                Console.println(q.front())
                Console.println(q.tail())
                
                Console.println(q.deQueue())
                Console.println(q.deQueue())
                Console.println(q.deQueue())
                q.deQueue()
                Console.println(q.size(), q.isEmpty())
                """, """
                3 false
                1
                2
                1 false
                4
                3
                6
                3
                4
                5
                0 true
                """);
    }
}
