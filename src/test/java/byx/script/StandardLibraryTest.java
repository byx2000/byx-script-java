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

    @Test
    public void testList() {
        verify("""
                import list
                
                var list1 = [1, 2, 3, 4, 5]
                List.reverse(list1)
                Console.println(list1)
                
                var list2 = [1, 2, 3, 4, 5, 6]
                List.reverse(list2)
                Console.println(list2)
                
                var list3 = [1]
                List.reverse(list3)
                Console.println(list3)
                
                var list4 = [1, 2]
                List.reverse(list4)
                Console.println(list4)
                """, """
                [5, 4, 3, 2, 1]
                [6, 5, 4, 3, 2, 1]
                [1]
                [2, 1]
                """);
        verify("""
                import list
                
                var list1 = [5, 2, 1, 3, 4, 7, 6]
                List.sort(list1)
                Console.println(list1)
                
                var list2 = [5, 2, 1, 3, 4, 7, 8, 6]
                List.sort(list2, (a, b) => b - a)
                Console.println(list2)
                
                function Student(name, age, score) {
                    return {
                        name: name,
                        age: age,
                        score: score,
                        toString: () => '(' + name + ', ' + age + ', ' + score + ')'
                    }
                }
                
                var students = [
                    Student('Marry', 17, 87.5),
                    Student('John', 21, 67.0),
                    Student('Tom', 18, 95.5)
                ]
                
                List.sort(students, (s1, s2) => s1.name.compareTo(s2.name))
                for (var i = 0; i < students.length(); ++i) {
                    Console.print(students[i].toString() + ' ')
                }
                Console.println()
                
                List.sort(students, (s1, s2) => s1.age - s2.age)
                for (var i = 0; i < students.length(); ++i) {
                    Console.print(students[i].toString() + ' ')
                }
                Console.println()
                
                List.sort(students, (s1, s2) => s2.score - s1.score)
                for (var i = 0; i < students.length(); ++i) {
                    Console.print(students[i].toString() + ' ')
                }
                """, """
                [1, 2, 3, 4, 5, 6, 7]
                [8, 7, 6, 5, 4, 3, 2, 1]
                (John, 21, 67.0) (Marry, 17, 87.5) (Tom, 18, 95.5)
                (Marry, 17, 87.5) (Tom, 18, 95.5) (John, 21, 67.0)
                (Tom, 18, 95.5) (Marry, 17, 87.5) (John, 21, 67.0)
                """);
    }

    @Test
    public void testStream() {
        verify("""
                import stream
                
                Stream.of([1, 2, 3, 4, 5])
                    .map(n => n + 1)
                    .filter(n => n % 2 == 0)
                    .forEach(Console.println)
                """, """
                2
                4
                6
                """);
        verify("""
                import stream
                
                var s = Stream.of([1, 2, 3, 4, 5])
                    .map(n => n + 1)
                    .filter(n => n % 2 == 1)
                    .toList()
                Console.println(s)
                """, """
                [3, 5]
                """);
    }
}
