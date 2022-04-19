package byx.script;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static byx.script.TestUtils.*;

public class ByxScriptTest {
    @Test
    public void testEmptyList() {
        verify("""
                var list = []
                Console.println(list)
                Console.println(list.length())
                """, """
                []
                0
                """);
    }

    @Test
    public void testEmptyObject() {
        verify("""
                var obj = {}
                Console.println(obj)
                """, """
                {}
                """);
    }

    @Test
    public void testArgsPass() {
        verify("""
                var f1 = (a, b) => 123 * 456
                Console.println(f1())
                                
                var f2 = (a, b) => a + b
                Console.println(f2(100, 200, 400))
                """, """
                56088
                300
                """);
    }

    @Test
    public void testSubscriptAssign() {
        verify("""
                var arr = [1, 2, 3, [4, 5], {a: 100, b: 200, c: [100, 200]}]
                arr[0] = 5
                arr[1] += 10
                arr[3][1] *= 100
                arr[4].c[0] = 12
                arr[4].c[1] -= 8
                                
                Console.println(arr[0])
                Console.println(arr[1])
                Console.println(arr[3][1])
                Console.println(arr[4].c[0])
                Console.println(arr[4].c[1])
                """, """
                5
                12
                500
                12
                192
                """);
    }

    @Test
    public void testFieldAssign() {
        verify("""
                var obj = {a: 100, b: 200, c: {d: 300, e: [{m: 10}, 2, 3]}}
                obj.a = 101
                obj.b -= 50
                obj.c.d += 100
                obj.c.e[0].m *= 10
                                
                Console.println(obj.a, obj.b, obj.c.d, obj.c.e[0].m)
                """, """
                101 150 400 100
                """);
    }

    @Test
    public void testFunctionExpr() {
        verify("""
                var f1 = () => 123
                Console.println(f1())
                
                var f2 = () => return 123
                Console.println(f2())
                
                var f3 = () => {return 123}
                Console.println(f3())
                
                var f4 = (a) => a + 1
                Console.println(f4(100))
                
                var f5 = (a) => return a + 1
                Console.println(f5(100))
                
                var f6 = (a) => {return a + 1}
                Console.println(f6(100))
                
                var f7 = a => a * 2
                Console.println(f7(200))
                
                var f8 = a => return a * 2
                Console.println(f8(200))
                
                var f9 = a => {return a * 2}
                Console.println(f9(200))
                
                var f10 = (a, b) => a + b
                Console.println(f10(12, 13))
                
                var f11 = (a, b) => return a + b
                Console.println(f11(12, 13))
                
                var f12 = (a, b) => {return a + b}
                Console.println(f12(12, 13))
                                
                var x = 10
                
                var f13 = () => x += 1
                f13()
                Console.println(x)
                
                var f14 = () => {x += 1}
                f14()
                Console.println(x)
                
                var f15 = () => {}
                Console.println(f15())
                """, """
                123
                123
                123
                101
                101
                101
                400
                400
                400
                25
                25
                25
                11
                12
                undefined
                """);
    }

    @Test
    public void testFunctionCallImmediately() {
        verify("""
                Console.println((() => 12345)())
                Console.println((m => m + 6)(10))
                Console.println(((a, b) => a - b)(13, 7))
                
                var x = 10;
                ((m, n) => x += m + n)(12, 13)
                Console.println(x)
                """, """
                12345
                16
                6
                35
                """);
    }

    @Test
    public void testClosure() {
        verify("""
                var add = a => b => a + b
                Console.println(add(2)(3))
                Console.println(add(45)(67))
                var add5 = add(5)
                Console.println(add5(7))
                Console.println(add5(100))
                """, """
                5
                112
                12
                105
                """);
        verify("""
                var x = 100
                var fun = () => x = x + 1
                fun()
                fun()
                Console.println(x);
                """, """
                102
                """);
        verify("""
                var x = 1000;
                (() => x += 2)()
                Console.println(x)
                """, """
                1002
                """);
        verify("""
                Console.println(((a, b) => a + b)(12, 13))
                """, """
                25
                """);
        verify("""
                var compose = (n, f, g) => g(f(n))
                var f1 = n => n * 2
                var f2 = n => n + 1
                Console.println(compose(100, f1, f2))
                """, """
                201
                """);
        verify("""
                var x = 123
                var outer = () => {
                    var x = 456
                    return () => x
                }
                Console.println(x)
                Console.println(outer()())
                Console.println(x)
                """, """
                123
                456
                123
                """);
        verify("""
                var x = 123
                var outer = () => {
                    x = 456
                    return () => x
                }
                Console.println(x)
                Console.println(outer()())
                Console.println(x)
                """, """
                123
                456
                456
                """);
        verify("""
                var x = 123
                var outer = () => {
                    x = 456
                    return () => x
                }
                x = 789
                Console.println(x)
                Console.println(outer()())
                Console.println(x)
                """, """
                789
                456
                456
                """);
        verify("""
                var observer = callback => {
                    for (var i = 1; i <= 10; i = i + 1)
                        callback(i)
                }
                var s = 0
                observer(n => s = s + n)
                Console.println(s)
                """, """
                55
                """);
        verify("""
                var observer = callback => {
                    for (var i = 1; i <= 10; i = i + 1)
                        callback(i)
                }
                var s = 0
                observer(() => s += 1)
                Console.println(s)
                """, """
                10
                """);
        verify("""
                var Student = (name, age, score) => {
                    return {
                        getName: () => name,
                        setName: _name => name = _name,
                        getAge: () => age,
                        setAge: _age => age = _age,
                        getScore: () => score,
                        setScore: _score => score = _score,
                        getDescription: () => '(' + name + ' ' + age + ' ' + score + ')'
                    }
                }
                                
                var s1 = Student('Zhang San', 21, 87.5)
                var s2 = Student('Li Si', 23, 95)
                Console.println(s1.getName())
                Console.println(s2.getScore())
                Console.println(s1.getDescription())
                Console.println(s2.getDescription())
                s1.setName('Xiao Ming')
                s2.setScore(77.5)
                Console.println(s1.getName())
                Console.println(s2.getScore())
                Console.println(s1.getDescription())
                Console.println(s2.getDescription())
                """, """
                Zhang San
                95
                (Zhang San 21 87.5)
                (Li Si 23 95)
                Xiao Ming
                77.5
                (Xiao Ming 21 87.5)
                (Li Si 23 77.5)
                """);
    }

    @Test
    public void testAdd() {
        verify("""
                Console.println(123 + 456)
                Console.println(123 + 3.14)
                Console.println(12.34 + 555)
                Console.println(12.34 + 56.78)
                Console.println('hello ' + 'world!')
                Console.println('hello ' + 123)
                Console.println(123 + ' hello')
                Console.println('world ' + 3.14)
                Console.println(3.14 + ' world')
                Console.println('abc ' + true)
                Console.println(false + ' abc')
                Console.println(undefined + ' xyz')
                Console.println('xyz ' + undefined)
                """, getOutput(() -> {
                System.out.println(123 + 456);
                System.out.println(123 + 3.14);
                System.out.println(12.34 + 555);
                System.out.println(12.34 + 56.78);
                System.out.println("hello " + "world!");
                System.out.println("hello " + 123);
                System.out.println(123 + " hello");
                System.out.println("world " + 3.14);
                System.out.println(3.14 + " world");
                System.out.println("abc " + true);
                System.out.println(false + " abc");
                System.out.println("undefined xyz");
                System.out.println("xyz undefined");
        }));
    }

    @Test
    public void testSub() {
        verify("""
                Console.println(532 - 34)
                Console.println(3.14 - 12)
                Console.println(12 - 7.78)
                Console.println(56.78 - 12.34)
                """, getOutput(() -> {
                System.out.println(532 - 34);
                System.out.println(3.14 - 12);
                System.out.println(12 - 7.78);
                System.out.println(56.78 - 12.34);
        }));
    }

    @Test
    public void testMul() {
        verify("""
                Console.println(12 * 34)
                Console.println(12 * 3.4)
                Console.println(0.12 * 34)
                Console.println(12.34 * 56.78)
                """, getOutput(() -> {
                System.out.println(12 * 34);
                System.out.println(12 * 3.4);
                System.out.println(0.12 * 34);
                System.out.println(12.34 * 56.78);
        }));
    }

    @Test
    public void testDiv() {
        verify("""
                Console.println(5 / 2)
                Console.println(12 / 3.4)
                Console.println(0.12 / 34)
                Console.println(56.78 / 12.34)
                """, getOutput(() -> {
                System.out.println(5 / 2);
                System.out.println(12 / 3.4);
                System.out.println(0.12 / 34);
                System.out.println(56.78 / 12.34);
        }));
    }

    @Test
    public void testRem() {
        verify("""
                Console.println(12 % 3)
                Console.println(12 % 5)
                Console.println(3 % 7)
                Console.println(6 % 3)
                """, getOutput(() -> {
                System.out.println(12 % 3);
                System.out.println(12 % 5);
                System.out.println(3 % 7);
                System.out.println(6 % 3);
        }));
    }

    @Test
    public void testGreaterThan() {
        verify("""
                Console.println(100 > 50)
                Console.println(100 > 100)
                Console.println(3.14 > 50)
                Console.println(3.14 > 1)
                Console.println(3.14 > 456.23)
                Console.println(3.14 > 3.14)
                Console.println('banana' > 'apple')
                Console.println('apple' > 'banana')
                Console.println('apple' > 'apple')
                """, """
                true
                false
                false
                true
                false
                false
                true
                false
                false
                """);
    }

    @Test
    public void testGreaterEqualThan() {
        verify("""
                Console.println(100 >= 50)
                Console.println(100 >= 100)
                Console.println(3.14 >= 50)
                Console.println(3.14 >= 1)
                Console.println(3.14 >= 456.23)
                Console.println(3.14 >= 3.14)
                Console.println('banana' >= 'apple')
                Console.println('apple' >= 'banana')
                Console.println('apple' >= 'apple')
                """, """
                true
                true
                false
                true
                false
                true
                true
                false
                true
                """);
    }

    @Test
    public void testLessThan() {
        verify("""
                Console.println(100 < 50)
                Console.println(100 < 100)
                Console.println(3.14 < 50)
                Console.println(3.14 < 1)
                Console.println(3.14 < 456.23)
                Console.println(3.14 < 3.14)
                Console.println('banana' < 'apple')
                Console.println('apple' < 'banana')
                Console.println('apple' < 'apple')
                """, """
                false
                false
                true
                false
                true
                false
                false
                true
                false
                """);
    }

    @Test
    public void testLessEqualThan() {
        verify("""
                Console.println(100 <= 50)
                Console.println(100 <= 100)
                Console.println(3.14 <= 50)
                Console.println(3.14 <= 1)
                Console.println(3.14 <= 456.23)
                Console.println(3.14 <= 3.14)
                Console.println('banana' <= 'apple')
                Console.println('apple' <= 'banana')
                Console.println('apple' <= 'apple')
                """, """
                false
                true
                true
                false
                true
                true
                false
                true
                true
                """);
    }

    @Test
    public void testEqual() {
        verify("""
                Console.println(123 == 123)
                Console.println(12.34 == 12.34)
                Console.println(true == true)
                Console.println(false == false)
                Console.println('apple' == 'apple')
                Console.println(123 == 45)
                Console.println(3.14 == 12.56)
                Console.println(true == false)
                Console.println('apple' == 'banana')
                """, """
                true
                true
                true
                true
                true
                false
                false
                false
                false
                """);
    }

    @Test
    public void testNotEqual() {
        verify("""
                Console.println(123 != 123)
                Console.println(12.34 != 12.34)
                Console.println(true != true)
                Console.println(false != false)
                Console.println('apple' != 'apple')
                Console.println(123 != 45)
                Console.println(3.14 != 12.56)
                Console.println(true != false)
                Console.println('apple' != 'banana')
                """, """
                false
                false
                false
                false
                false
                true
                true
                true
                true
                """
        );
    }

    @Test
    public void testAnd() {
        verify("""
                Console.println(true && true)
                Console.println(true && false)
                Console.println(false && true)
                Console.println(false && false)
                """, """
                true
                false
                false
                false
                """);
    }

    @Test
    public void testOr() {
        verify("""
                Console.println(true || true)
                Console.println(true || false)
                Console.println(false || true)
                Console.println(false || false)
                """, """
                true
                true
                true
                false
                """);
    }

    @Test
    public void testNot() {
        verify("""
                Console.println(!true)
                Console.println(!false)
                """, """
                false
                true
                """);
    }

    @Test
    public void testUndefined() {
        verify("""
                Console.println(undefined == undefined)
                Console.println(123 == undefined)
                Console.println(undefined == 123)
                Console.println(3.14 == undefined)
                Console.println(undefined == 3.14)
                Console.println('hello' == undefined)
                Console.println(undefined == 'hello')
                Console.println([] == undefined)
                Console.println(undefined == [])
                Console.println([1, 2, 3] == undefined)
                Console.println(undefined == [1, 2, 3])
                Console.println({} == undefined)
                Console.println(undefined == {})
                Console.println({m: 100} == undefined)
                Console.println(undefined == {m: 100})
                """, """
                true
                false
                false
                false
                false
                false
                false
                false
                false
                false
                false
                false
                false
                false
                false
                """);
    }

    @Test
    public void testExpr() {
        verify("""
                Console.println(2 + 3*5)
                Console.println((2+3) * 4 / (9-7))
                Console.println(2.4 / 5.774 * (6 / 3.57 + 6.37 )-2 * 7 / 5.2 + 5)
                Console.println(-2.4 / 5.774 * (6 / 3.57 + 6.37 )-2 * 7 / 5.2 + 5)
                Console.println(77.58 * (6 / 3.14+55.2234) - 2 * 6.1 / (1.0 + 2 / (4.0 - 3.8*5)))
                Console.println(77.58 * (6 / -3.14+55.2234) - 2 * (-6.1) / (1.0 + 2 / (4.0 - 3.8*5)))
                Console.println(-100)
                Console.println(-5 + 7)
                Console.println(-(5 + 7))
                Console.println(-3.14)
                Console.println(-12.34-67.5)
                Console.println(-(12.34-67.5))
                
                Console.println(!false || true)
                Console.println(!(false || true))
                Console.println(!true && false)
                Console.println(!(true && false))
                Console.println(true && !false)
                Console.println(false || !true)
                Console.println(false && false || true)
                Console.println(false && (false || true))
                Console.println(true || true && false)
                Console.println((true || true) && false)
                Console.println(true && true && true)
                Console.println(true && true && false)
                Console.println(true || false || true)
                Console.println(false || false || false)
                """, getOutput(() -> {
                System.out.println(2 + 3*5);
                System.out.println((2+3) * 4 / (9-7));
                System.out.println(2.4 / 5.774 * (6 / 3.57 + 6.37 )-2 * 7 / 5.2 + 5);
                System.out.println(-2.4 / 5.774 * (6 / 3.57 + 6.37 )-2 * 7 / 5.2 + 5);
                System.out.println(77.58 * (6 / 3.14+55.2234) - 2 * 6.1 / (1.0 + 2 / (4.0 - 3.8*5)));
                System.out.println(77.58 * (6 / -3.14+55.2234) - 2 * (-6.1) / (1.0 + 2 / (4.0 - 3.8*5)));
                System.out.println(-100);
                System.out.println(-5 + 7);
                System.out.println(-(5 + 7));
                System.out.println(-3.14);
                System.out.println(-12.34-67.5);
                System.out.println(-(12.34-67.5));

                System.out.println(true);
                System.out.println(false);
                System.out.println(false);
                System.out.println(true);
                System.out.println(true);
                System.out.println(false);
                System.out.println(true);
                System.out.println(false);
                System.out.println(true);
                System.out.println(false);
                System.out.println(true);
                System.out.println(false);
                System.out.println(true);
                System.out.println(false);
        }));
    }

    @Test
    public void testShortCircuit() {
        verify("""
                var x = 0
                var y = 0
                function f1() {
                    x = x + 1
                    return true
                }
                function f2() {
                    y = y + 1
                    return false
                }
                                
                x = 0
                y = 0
                var b = f2() && f1()
                Console.println(b)
                Console.println(x)
                Console.println(y)
                                
                x = 0
                y = 0
                b = f1() || f2()
                Console.println(b)
                Console.println(x)
                Console.println(y)
                """, """
                false
                0
                1
                true
                1
                0
                """);
    }

    @Test
    public void testScope() {
        verify("""
                var i = 100 + 2*3;
                i = 123;
                var j = i + 1;
                {
                    var k = 3 ;
                    i = k;
                }
                Console.println(i)
                Console.println(j)
                """, """
                3
                124
                """);
    }

    @Test
    public void testIf() {
        verify("""
                var i = 123
                if (i > 200)
                    i = 456
                Console.println(i)""", """
                123
                """);
        verify("""
                var i = 123
                if (200 > i)
                    i = 456
                Console.println(i)""", """
                456
                """);
        verify("""
                var i = 100
                if (i < 25 || !(i < 50)) {
                    i = 200
                }
                Console.println(i)""", """
                200
                """);
        verify("""
                var i = 123
                var j = 456
                if (i < 200 && j > 300) {
                    i = 1001
                    j = 1002
                } else {
                    i = 1003
                    j = 1004
                }
                Console.println(i, j)""", """
                1001 1002
                """);
        verify("""
                var i = 123
                var j = 456
                if (i > 200 && j > 300) {
                    i = 1001
                    j = 1002
                } else {
                    i = 1003
                    j = 1004
                }
                Console.println(i, j)""", """
                1003 1004
                """);
    }

    @Test
    public void testFor() {
        verify("""
                var s = 0
                for (var i = 1; i <= 100; i++)
                    s += i
                Console.println(s)""", """
                5050
                """);
        verify("""
                var s1 = 0
                var s2 = 0
                for (var i = 1; i <= 100; i++) {
                    if (i % 2 == 0)
                        s1 = s1 + i
                    else
                        s2 = s2 + i
                }
                Console.println(s1, s2)""", """
                2550 2500
                """);
        verify("""
                var s = 0
                for (var i = 0; i < 1000; i = i + 1)
                    if (i % 6 == 1 && (i % 7 == 2 || i % 8 == 3))
                        s = s + i
                Console.println(s)""", """
                29441
                """);
        verify("""
                var s = 0
                for (var i = 0; i < 1000; ++i)
                    if (i % 6 == 1 && i % 7 == 2 || i % 8 == 3)
                        s += i
                Console.println(s)""", """
                71357
                """);
    }

    @Test
    public void testWhile() {
        verify("""
                var s = 0
                var i = 1
                while (i <= 100) {
                    s = s + i
                    i++
                }
                Console.println(s, i)""", """
                5050 101
                """);
        verify("""
                var s1 = 0
                var s2 = 0
                var i = 1
                while (i <= 100) {
                    if (i % 2 == 0)
                        s1 += i
                    else
                        s2 += i
                    i = i + 1
                }
                Console.println(s1, s2, i)""", """
                2550 2500 101
                """);
    }

    @Test
    public void testBreak() {
        verify("""
                var s = 0
                for (var i = 0; i < 10000; i += 1) {
                    if (i % 3242 == 837)
                        break
                    s += i
                }
                Console.println(s)""", """
                349866
                """);
        verify("""
                var s = 0
                var i = 0
                while (i < 10000) {
                    if (i % 3242 == 837)
                        break
                    s = s + i
                    i = i + 1
                }
                Console.println(s, i)""", """
                349866 837
                """);
    }

    @Test
    public void testContinue() {
        verify("""
                 var s = 0
                for (var i = 0; i < 100; i = i + 1){
                    if (i % 6 == 4) {
                        continue
                    }
                    s += i * i
                }
                Console.println(s)""", """
                277694
                """);
        verify("""
                var s = 0
                var i = 0
                while (i < 100) {
                    if (i % 6 == 4) {
                        ++i
                        continue
                    }
                    s = s + i * i
                    ++i
                }
                Console.println(s, i)""", """
                277694 100
                """);
    }

    @Test
    public void testStringConcat() {
        verify("""
                var s = ''
                for (var i = 1; i <= 10; i = i + 1) {
                    if (i != 10)
                        s = s + i + ' '
                    else
                        s = s + i
                }
                Console.println(s)""", """
                1 2 3 4 5 6 7 8 9 10
                """);
        verify("""
                var s = ''
                for (var i = 0; i < 100; i = i + 1)
                    s = s + 'hello'
                Console.println(s)""", "hello".repeat(100));
    }

    @Test
    public void testHarmonicSeries() {
        verify("""
                var s = 0.0
                for (var i = 1; i <= 100; i++) {
                    s = s + 1.0/i
                }
                Console.println(s)""", getOutput(() -> {
                double s = 0.0;
                for (int i = 1; i <= 100; ++i) {
                    s += 1.0 / i;
                }
                System.out.println(s);
        }));
    }

    @Test
    public void testEmptyString() {
        verify("""
                var s = ''
                Console.println(s)
                """, "");
    }

    @Test
    public void testStringLength() {
        verify("""
                Console.println(''.length())
                Console.println('abc'.length())
                """, """
                0
                3
                """);
    }

    @Test
    public void testStringConcatMethod() {
        verify("""
                Console.println('abc'.concat('defg'))
                """, """
                abcdefg
                """);
    }

    @Test
    public void testStringCast() {
        verify("""
                Console.println('123'.toInt())
                Console.println('3.14'.toDouble())
                Console.println('true'.toBool())
                """, """
                123
                3.14
                true
                """);
    }

    @Test
    public void testStringCharAt() {
        verify("""
                var s = 'abc'
                Console.println(s.charAt(0), s.charAt(1), s.charAt(2))
                Console.println(s.charAt(1) == 'b')
                """, """
                a b c
                true
                """);
    }

    @Test
    public void testStringCodeAt() {
        verify("""
                var s = 'abc'
                Console.println(s.codeAt(0), s.codeAt(1), s.codeAt(2))
                """, """
                97 98 99
                """);
    }

    @Test
    public void testStringSubscript() {
        verify("""
                var s = 'abc'
                Console.println(s[0], s[1], s[2])
                Console.println(s[1] == 'b')
                """, """
                a b c
                true
                """);
    }

    @Test
    public void testListLength() {
        verify("""
                var arr1 = []
                Console.println(arr1.length())
                var arr2 = [1, 2, 3, 4]
                Console.println(arr2.length())
                Console.println([1, 2, 3].length())
                """, """
                0
                4
                3
                """);
    }

    @Test
    public void testListAddFirst() {
        verify("""
                var arr = [1, 2, 3]
                Console.println(arr.length())
                arr.addFirst(4)
                arr.addFirst(5)
                Console.println(arr.length())
                arr.addFirst(3.14)
                arr.addFirst('hello')
                Console.println(arr.length())
                Console.println(arr)
                """, """
                3
                5
                7
                [hello, 3.14, 5, 4, 1, 2, 3]
                """);
    }

    @Test
    public void testListRemoveFirst() {
        verify("""
                var nums = [1, 2, 3, 4, 5]
                Console.println(nums.removeFirst())
                Console.println(nums)
                """, """
                1
                [2, 3, 4, 5]
                """);
    }

    @Test
    public void testListAddLast() {
        verify("""
                var arr = [1, 2, 3]
                Console.println(arr.length())
                arr.addLast(4)
                arr.addLast(5)
                Console.println(arr.length())
                arr.addLast(3.14)
                arr.addLast('hello')
                Console.println(arr.length())
                Console.println(arr)
                """, """
                3
                5
                7
                [1, 2, 3, 4, 5, 3.14, hello]
                """);
        verify("""
                var nums = []
                for (var i = 1; i <= 100; i = i + 1)
                    nums.addLast(i * i)
                var s = 0
                for (var i = 0; i < nums.length(); i = i + 1)
                    s = s + nums[i]
                Console.println(s)
                """, """
                338350
                """);
    }

    @Test
    public void testListRemoveLast() {
        verify("""
                var nums = [1, 2, 3, 4, 5]
                Console.println(nums.removeLast())
                Console.println(nums)
                """, """
                5
                [1, 2, 3, 4]
                """);
    }

    @Test
    public void testListInsert() {
        verify("""
                var list = [1, 2, 3, 4, 5]
                list.insert(0, 100)
                list.insert(3, 'hello')
                list.insert(7, 3.14)
                Console.println(list)
                """, """
                [100, 1, 2, hello, 3, 4, 5, 3.14]
                """);
    }

    @Test
    public void testListRemove() {
        verify("""
                var list = [1, 2, 3, 4, 5]
                Console.println(list.remove(2))
                Console.println(list)
                """, """
                3
                [1, 2, 4, 5]
                """);
    }

    @Test
    public void testListCopy() {
        verify("""
                var list1 = [1, 2, 3, 4, 5]
                var list2 = list1.copy()
                list1[2] = 100
                list2[3] = 200
                Console.println(list1)
                Console.println(list2)
                """, """
                [1, 2, 100, 4, 5]
                [1, 2, 3, 200, 5]
                """);
    }

    @Test
    public void testNestedList() {
        verify("""
                var list = []
                for (var i = 10; i <= 30; i += 10) {
                    list.addLast([])
                    for (var j = 0; j < 4; j = j + 1) {
                        list[i / 10 - 1].addLast(i + j)
                    }
                }
                
                for (var i = 0; i < list.length(); ++i) {
                    for (var j = 0; j < list[i].length(); ++j) {
                        Console.print(list[i][j] + ' ')
                    }
                    Console.println()
                }
                """, """
                10 11 12 13
                20 21 22 23
                30 31 32 33
                """
        );
    }

    @Test
    public void testObjectGetField() {
        verify("""
                var obj = {a: 123, b: 3.14, str: 'hello', c: {x: 100, y: 200}}
                Console.println(obj.getField('a'))
                Console.println(obj.getField('b'))
                Console.println(obj.getField('str'))
                """, """
                123
                3.14
                hello
                """);
    }

    @Test
    public void testObjectSetField() {
        verify("""
                var obj = {a: 123, b: 3.14, str: 'hello', c: {x: 100, y: 200}}
                obj.setField('a', 12345)
                obj.setField('b', 'hi')
                obj.c.setField('y', 101.5)
                Console.println(obj.a)
                Console.println(obj.b)
                Console.println(obj.c.y)
                """, """
                12345
                hi
                101.5
                """);
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
}
