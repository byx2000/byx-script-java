package byx.script.core;

import org.junit.jupiter.api.Test;

import static byx.script.core.TestUtils.getOutput;
import static byx.script.core.TestUtils.verify;

public class StandardLibraryTest {
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
                
                func Student(name, age, score) {
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

    @Test
    public void testSet() {
        verify("""
                import set
                
                var set = Set()
                set.add(1)
                set.add(2)
                set.add(3)
                set.add(2)
                Console.println(set.toList())
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
                [1, 2, 3]
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
                import set
                
                var s = Set()
                for (var i = 0; i < 100; ++i) {
                    s.add(i % 10)
                }
                Console.println(s.size())
                """, """
                10
                """);
    }

    @Test
    public void testMap() {
        verify("""
                import map
                
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
                null
                true
                true
                true
                false
                456
                12345
                null
                123
                2
                """);
        verify("""
                import map
                
                func twoSum(nums, target) {
                    var map = Map()
                    for (var i = 0; i < nums.length(); ++i) {
                        if (map.containsKey(target - nums[i])) {
                            return [map.get(target - nums[i]), i]
                        }
                        map.put(nums[i], i)
                    }
                    return null
                }
                
                Console.println(twoSum([2, 7, 11, 15], 9))
                Console.println(twoSum([3, 2, 4], 6))
                Console.println(twoSum([3, 3], 6))
                Console.println(twoSum([23, 16, 76, 97, 240, 224, 5, 78, 443, 25], 103))
                """, """
                [0, 1]
                [1, 2]
                [0, 1]
                [7, 9]
                """);
    }

    @Test
    public void testMath() {
        verify("""
                import math
                                
                Console.println(Math.abs(15))
                Console.println(Math.abs(-3.14))
                Console.println(Math.sin(10))
                Console.println(Math.sin(12.34))
                Console.println(Math.cos(10))
                Console.println(Math.cos(12.34))
                Console.println(Math.tan(10))
                Console.println(Math.tan(12.34))
                Console.println(Math.pow(2, 3))
                Console.println(Math.pow(2, 3.5))
                Console.println(Math.pow(2.5, 3))
                Console.println(Math.pow(2.5, 3.5))
                Console.println(Math.exp(2))
                Console.println(Math.exp(3.14))
                Console.println(Math.ln(25))
                Console.println(Math.ln(12.56))
                Console.println(Math.log10(25))
                Console.println(Math.log10(12.56))
                Console.println(Math.sqrt(2))
                Console.println(Math.sqrt(31.5))
                Console.println(Math.round(7))
                Console.println(Math.round(8.3))
                Console.println(Math.round(12.9))
                Console.println(Math.ceil(7))
                Console.println(Math.ceil(8.3))
                Console.println(Math.ceil(12.9))
                Console.println(Math.floor(7))
                Console.println(Math.floor(8.3))
                Console.println(Math.floor(12.9))
                """, getOutput(out -> {
            out.println(Math.abs(15));
            out.println(Math.abs(-3.14));
            out.println(Math.sin(10));
            out.println(Math.sin(12.34));
            out.println(Math.cos(10));
            out.println(Math.cos(12.34));
            out.println(Math.tan(10));
            out.println(Math.tan(12.34));
            out.println(Math.pow(2, 3));
            out.println(Math.pow(2, 3.5));
            out.println(Math.pow(2.5, 3));
            out.println(Math.pow(2.5, 3.5));
            out.println(Math.exp(2));
            out.println(Math.exp(3.14));
            out.println(Math.log(25));
            out.println(Math.log(12.56));
            out.println(Math.log10(25));
            out.println(Math.log10(12.56));
            out.println(Math.sqrt(2));
            out.println(Math.sqrt(31.5));
            out.println(Math.round(7));
            out.println((int) Math.round(8.3));
            out.println((int) Math.round(12.9));
            out.println((int) Math.ceil(7));
            out.println((int) Math.ceil(8.3));
            out.println((int) Math.ceil(12.9));
            out.println((int) Math.floor(7));
            out.println((int) Math.floor(8.3));
            out.println((int) Math.floor(12.9));
        }));
    }
}
