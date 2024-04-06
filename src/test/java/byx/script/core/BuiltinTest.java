package byx.script.core;

import org.junit.jupiter.api.Test;

import static byx.script.core.TestUtils.verify;

public class BuiltinTest {
    @Test
    public void testReader() {
        verify("""                
                while (Reader.hasNext()) {
                    var line = Reader.nextLine()
                    Console.println(line)
                }
                """, """
                hello
                world!
                this is the example
                """, """
                hello
                world!
                this is the example
                """);
        verify("""                
                while (Reader.hasNext()) {
                    var a = Reader.nextInt()
                    var b = Reader.nextInt()
                    Console.println(a + b)
                }
                """, """
                1 2 45 77
                400
                500
                """, """
                3
                122
                900
                """);
        verify("""                
                var nums = []
                while (Reader.hasNext()) {
                    nums.addLast(Reader.nextInt())
                }
                
                var sum = 0
                for (var i = 0; i < nums.length(); ++i) {
                    sum += nums[i]
                }
                
                Console.println(sum)
                """, """
                23 17 56 124 4 85
                """, """
                309
                """);
    }

    @Test
    public void testReflect() {
        verify("""
                Console.println(Reflect.typeId(123));
                Console.println(Reflect.typeId(3.14));
                Console.println(Reflect.typeId('hello'));
                Console.println(Reflect.typeId(true));
                Console.println(Reflect.typeId([1, 2, 3]));
                Console.println(Reflect.typeId({a: 100, b: 'hi'}));
                Console.println(Reflect.typeId(null));
                """, """
                integer
                double
                string
                bool
                list
                object
                null
                """);
        verify("""                
                var objs = [123, 3.14, true, 'hello', [1, 2, 3], {a: 100, b: 'hi'}, null]
                for (var i = 0; i < objs.length(); ++i) {
                    for (var j = 0; j < objs.length(); ++j) {
                        Console.print((Reflect.hashCode(objs[i]) == Reflect.hashCode(objs[j])) + ' ')
                    }
                    Console.println()
                }
                """, """
                true false false false false false false
                false true false false false false false
                false false true false false false false
                false false false true false false false
                false false false false true false false
                false false false false false true false
                false false false false false false true
                """);
        verify("""                
                var obj = {a: 123, b: 'hello'}
                var fields = Reflect.fields(obj)
                Console.println(fields == ['a', 'b'] || fields == ['b', 'a'])
                
                Reflect.setField(obj, 'a', 456)
                Console.println(obj.a)
                
                Console.println(Reflect.getField(obj, 'b'))
                
                Console.println(Reflect.hasField(obj, 'a'))
                Console.println(Reflect.hasField(obj, 'b'))
                Console.println(Reflect.hasField(obj, 'c'))
                """, """
                true
                456
                hello
                true
                true
                false
                """);
    }
}
