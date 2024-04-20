package byx.script.core;

import org.junit.jupiter.api.Test;

import static byx.script.core.TestUtils.getOutput;
import static byx.script.core.TestUtils.verify;

public class BuiltinTest {
    @Test
    public void testOutput() {
        verify("""
            println(123)
            println(3.14)
            println('hello')
            println(true)
            println(false)
            println([1, 'hello', false])
            println([1, 'hello', [1, 2, 3], false])
            println({a: 123, b: 'hello'})
            println({a: 123, b: 'hello', c: {x: 10, y: 20}})
            println((a, b) => a + b)
            println(null)
            """, """
            123
            3.14
            hello
            true
            false
            [1, hello, false]
            [1, hello, [...], false]
            {a: 123, b: hello}
            {a: 123, b: hello, c: {...}}
            f(...)
            null
            """);
    }

    @Test
    public void testInput() {
        verify("""
                while (hasNext()) {
                    var line = readLine()
                    println(line)
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
                while (hasNext()) {
                    var a = readInt()
                    var b = readInt()
                    println(a + b)
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
                while (hasNext()) {
                    nums.addLast(readInt())
                }
                
                var sum = 0
                for (var i = 0; i < nums.length(); ++i) {
                    sum += nums[i]
                }
                
                println(sum)
                """, """
                23 17 56 124 4 85
                """, """
                309
                """);
        verify("""
            var a = readDouble()
            var b = readDouble()
            println(a, b)
            """, """
            1.2 0.4
            """, """
            1.2 0.4
            """);
        verify("""
            var a = readBool()
            var b = readBool()
            println(a, b)
            """, """
            true
            false
            """, """
            true false
            """);
    }

    @Test
    public void testReflect() {
        verify("""
                println(typeId(123));
                println(typeId(3.14));
                println(typeId('hello'));
                println(typeId(true));
                println(typeId([1, 2, 3]));
                println(typeId({a: 100, b: 'hi'}));
                println(typeId(null));
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
                        print((hashCode(objs[i]) == hashCode(objs[j])) + ' ')
                    }
                    println()
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
                var fs = fields(obj)
                println(fs == ['a', 'b'] || fs == ['b', 'a'])
                
                setField(obj, 'a', 456)
                println(obj.a)
                
                println(getField(obj, 'b'))
                
                println(hasField(obj, 'a'))
                println(hasField(obj, 'b'))
                println(hasField(obj, 'c'))
                """, """
                true
                456
                hello
                true
                true
                false
                """);
    }

    @Test
    public void testMath() {
        verify("""
                println(abs(15))
                println(abs(-3.14))
                println(sin(10))
                println(sin(12.34))
                println(cos(10))
                println(cos(12.34))
                println(tan(10))
                println(tan(12.34))
                println(pow(2, 3))
                println(pow(2, 3.5))
                println(pow(2.5, 3))
                println(pow(2.5, 3.5))
                println(exp(2))
                println(exp(3.14))
                println(ln(25))
                println(ln(12.56))
                println(log10(25))
                println(log10(12.56))
                println(sqrt(2))
                println(sqrt(31.5))
                println(round(7))
                println(round(8.3))
                println(round(12.9))
                println(ceil(7))
                println(ceil(8.3))
                println(ceil(12.9))
                println(floor(7))
                println(floor(8.3))
                println(floor(12.9))
                println(max(1, 2))
                println(max(5.6, 3.4))
                println(max(5.6, 3))
                println(max(6, 34.5))
                println(min(1, 2))
                println(min(5.6, 3.4))
                println(min(5.6, 3))
                println(min(6, 34.5))
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
            out.println(Math.max(1, 2));
            out.println(Math.max(5.6, 3.4));
            out.println(Math.max(5.6, 3));
            out.println(Math.max(6, 34.5));
            out.println(Math.min(1, 2));
            out.println(Math.min(5.6, 3.4));
            out.println(Math.min(5.6, 3));
            out.println(Math.min(6, 34.5));
        }));
    }
}
