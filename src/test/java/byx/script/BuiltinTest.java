package byx.script;

import org.junit.jupiter.api.Test;

import static byx.script.TestUtils.*;

public class BuiltinTest {
    @Test
    public void testSet() {
        verify("""
                var set = Set()
                set.add(1)
                set.add(2)
                set.add(3)
                set.add(2)
                Console.println(set.size())
                Console.println(set.contains(1))
                Console.println(set.contains(2))
                Console.println(set.contains(3))
                Console.println(set.contains(4))
                Console.println(set.remove(1))
                Console.println(set.remove(5))
                Console.println(set.size())
                Console.println(set.contains(1))
                Console.println(set.contains(2))
                Console.println(set.contains(3))
                Console.println(set.contains(4))
                Console.println(set.isEmpty())
                set.remove(2)
                set.remove(3)
                Console.println(set.isEmpty())
                """, """
                3
                true
                true
                true
                false
                true
                false
                2
                false
                true
                true
                false
                false
                true
                """);
        verify("""
                var set = Set()
                set.add('hello')
                set.add('hello')
                set.add('hi')
                set.add('world')
                Console.println(set.size())
                Console.println(set.contains('hello'))
                Console.println(set.contains('hello1'))
                Console.println(set.contains('hi'))
                Console.println(set.contains('world'))
                set.remove('hello')
                Console.println(set.size())
                Console.println(set.contains('hello'))
                Console.println(set.contains('hello1'))
                Console.println(set.contains('hi'))
                Console.println(set.contains('world'))
                """, """
                3
                true
                false
                true
                true
                2
                false
                false
                true
                true
                """);
        verify("""
                var set = Set()
                set.add([1, 2, 3])
                set.add([1, 2])
                set.add([1, 2, 4])
                set.add([1, 2, 3])
                set.add([2, 3])
                set.add([1, 2])
                Console.println(set.size())
                Console.println(set.contains([1, 2, 3]))
                Console.println(set.contains([1]))
                Console.println(set.contains([1, 2, 4]))
                set.remove([1, 2, 3])
                set.remove([1, 2, 4])
                Console.println(set.size())
                Console.println(set.contains([1, 2, 3]))
                Console.println(set.contains([1, 2]))
                Console.println(set.contains([2, 3]))
                """, """
                4
                true
                false
                true
                2
                false
                true
                true
                """);
        verify("""
                var set = Set()
                var a = {a: 123, b: 'hello'}
                set.add(a)
                set.add({a: 123, b: 'hello'})
                Console.println(set.size())
                set.add(a)
                Console.println(set.size())
                Console.println(set.contains(a))
                Console.println(set.contains({a: 123, b: 'hello'}))
                set.remove(a)
                Console.println(set.size())
                """, """
                2
                2
                true
                false
                1
                """);
        verify("""
                var set = Set()
                set.add(1)
                set.add(2)
                set.add(3)
                var list = set.toList()
                Console.println(list)
                """, """
                [1, 2, 3]
                """);
    }

    @Test
    public void testMap() {
        verify("""
                var map = Map()
                map.put('k1', 123)
                map.put('k2', 456)
                map.put('k3', 789)
                Console.println(map.size())
                Console.println(map.get('k1'))
                Console.println(map.get('k2'))
                Console.println(map.get('k3'))
                Console.println(map.get('k4'))
                Console.println(map.containsKey('k1'))
                Console.println(map.containsKey('k2'))
                Console.println(map.containsKey('k3'))
                Console.println(map.containsKey('k4'))
                Console.println(map.put('k2', 12345))
                Console.println(map.get('k2'))
                Console.println(map.remove('k4'))
                Console.println(map.remove('k1'))
                Console.println(map.size())
                """, """
                3
                123
                456
                789
                undefined
                true
                true
                true
                false
                456
                12345
                undefined
                123
                2
                """);
    }
}
