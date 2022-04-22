package byx.script;

import org.junit.jupiter.api.Test;

import static byx.script.TestUtils.*;

public class BuiltinTest {
    @Test
    public void testReflect() {
        verify("""
                var objs = [123, 3.14, true, 'hello', [1, 2, 3], {a: 100, b: 'hi'}, undefined]
                
                for (var i = 0; i < objs.length(); ++i) {
                    Console.print(Reflect.isInt(objs[i]) + ' ')
                }
                Console.println()
                
                for (var i = 0; i < objs.length(); ++i) {
                    Console.print(Reflect.isDouble(objs[i]) + ' ')
                }
                Console.println()
                
                for (var i = 0; i < objs.length(); ++i) {
                    Console.print(Reflect.isBool(objs[i]) + ' ')
                }
                Console.println()
                
                for (var i = 0; i < objs.length(); ++i) {
                    Console.print(Reflect.isString(objs[i]) + ' ')
                }
                Console.println()
                
                for (var i = 0; i < objs.length(); ++i) {
                    Console.print(Reflect.isList(objs[i]) + ' ')
                }
                Console.println()
                
                for (var i = 0; i < objs.length(); ++i) {
                    Console.print(Reflect.isObject(objs[i]) + ' ')
                }
                Console.println()
                
                for (var i = 0; i < objs.length(); ++i) {
                    Console.print(Reflect.isUndefined(objs[i]) + ' ')
                }
                Console.println()
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
                var objs = [123, 3.14, true, 'hello', [1, 2, 3], {a: 100, b: 'hi'}, undefined]
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
    }
}
