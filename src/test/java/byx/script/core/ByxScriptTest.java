package byx.script.core;

import byx.script.core.interpreter.exception.*;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static byx.script.core.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ByxScriptTest {
    @Test
    public void testEmptyList() {
        verify("""
                var list = []
                println(list)
                println(list.length())
                """, """
                []
                0
                """);
    }

    @Test
    public void testEmptyObject() {
        verify("""
                var obj = {}
                """, """
                """);
    }

    @Test
    public void testArgsPass() {
        verify("""
                var f1 = (a, b) => 123 * 456
                println(f1())
                
                var f2 = (a, b) => a + b
                println(f2(100, 200, 400))
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

                println(arr[0])
                println(arr[1])
                println(arr[3][1])
                println(arr[4].c[0])
                println(arr[4].c[1])
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
                println(obj.a, obj.b, obj.c.d, obj.c.e[0].m)
                """, """
                101 150 400 100
                """);
        verify("""
                var obj = {}
                obj.a = 100
                obj.b = 3.14
                obj.c = 'hello'
                println(obj.a, obj.b, obj.c)
                """, """
                100 3.14 hello
                """);
    }

    @Test
    public void testfuncLiteral() {
        verify("""
                var f1 = () => 123
                println(f1())
                var f2 = () => 12.34
                println(f2())
                var f3 = () => 'hello'
                println(f3())
                var f4 = () => [100, 200, 300]
                println(f4())
                var f5 = () => (1 + 2) * 3
                println(f5())
                var f6 = () => 456
                var f7 = () => f6()
                println(f7())
                var f8 = () => {}
                println(f8())
                """, """
                123
                12.34
                hello
                [100, 200, 300]
                9
                456
                null
                """);
        verify("""
                var f1 = a => a + 1
                println(f1(10))
                var f2 = (a) => a + 1
                println(f2(20))
                var f3 = (a, b) => a + b
                println(f3(3, 5))
                var f4 = (a, b, c) => a * b * c
                println(f4(2, 4, 6))
                """, """
                11
                21
                8
                48
                """);
        verify("""
                var f1 = () => {return 100}
                println(f1())
                var f2 = () => {
                    var a = 10
                    var b = 20
                    return a + b
                }
                println(f2())
                var f3 = () => {
                    println('hello')
                }
                println(f3())
                
                var x = 1000
                var f4 = () => {x += 1}
                println(f4())
                println(x)
                """, """
                100
                30
                hello
                null
                null
                1001
                """);
    }

    @Test
    public void testfuncCallImmediately() {
        verify("""
                println((() => 12345)())
                println((m => m + 6)(10))
                println(((a, b) => a - b)(13, 7))
                
                var x = 10;
                ((m, n) => {x += m + n})(12, 13)
                println(x)
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
                println(add(2)(3))
                println(add(45)(67))
                var add5 = add(5)
                println(add5(7))
                println(add5(100))
                """, """
                5
                112
                12
                105
                """);
        verify("""
                var x = 100
                var fun = () => {x = x + 1}
                fun()
                fun()
                println(x);
                """, """
                102
                """);
        verify("""
                var x = 1000;
                (() => {x += 2})()
                println(x)
                """, """
                1002
                """);
        verify("""
                var compose = (n, f, g) => g(f(n))
                var f1 = n => n * 2
                var f2 = n => n + 1
                println(compose(100, f1, f2))
                """, """
                201
                """);
        verify("""
                var x = 123
                var outer = () => {
                    var x = 456
                    return () => x
                }
                println(x)
                println(outer()())
                println(x)
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
                println(x)
                println(outer()())
                println(x)
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
                println(x)
                println(outer()())
                println(x)
                """, """
                789
                456
                456
                """);
        verify("""
                var observer = callback => {
                    for (var i = 1; i <= 10; i = i + 1) {
                        callback(i)
                    }
                }
                var s = 0
                observer(n => {s = s + n})
                println(s)
                """, """
                55
                """);
        verify("""
                var observer = callback => {
                    for (var i = 1; i <= 10; i = i + 1) {
                        callback(i)
                    }
                }
                var s = 0
                observer(() => {s += 1})
                println(s)
                """, """
                10
                """);
        verify("""
                var Student = (name, age, score) => {
                    return {
                        getName: () => name,
                        setName: _name => {name = _name},
                        getAge: () => age,
                        setAge: _age => {age = _age},
                        getScore: () => score,
                        setScore: _score => {score = _score},
                        getDescription: () => '(' + name + ' ' + age + ' ' + score + ')'
                    }
                }

                var s1 = Student('Zhang San', 21, 87.5)
                var s2 = Student('Li Si', 23, 95)
                println(s1.getName())
                println(s2.getScore())
                println(s1.getDescription())
                println(s2.getDescription())
                s1.setName('Xiao Ming')
                s2.setScore(77.5)
                println(s1.getName())
                println(s2.getScore())
                println(s1.getDescription())
                println(s2.getDescription())
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
                println(123 + 456)
                println(123 + 3.14)
                println(12.34 + 555)
                println(12.34 + 56.78)
                println('hello ' + 'world!')
                println('hello ' + 123)
                println(123 + ' hello')
                println('world ' + 3.14)
                println(3.14 + ' world')
                println('abc ' + true)
                println(false + ' abc')
                println(null + ' xyz')
                println('xyz ' + null)
                """, getOutput(out -> {
            out.println(123 + 456);
            out.println(123 + 3.14);
            out.println(12.34 + 555);
            out.println(12.34 + 56.78);
            out.println("hello " + "world!");
            out.println("hello " + 123);
            out.println(123 + " hello");
            out.println("world " + 3.14);
            out.println(3.14 + " world");
            out.println("abc " + true);
            out.println(false + " abc");
            out.println("null xyz");
            out.println("xyz null");
        }));
    }

    @Test
    public void testSub() {
        verify("""
                println(532 - 34)
                println(3.14 - 12)
                println(12 - 7.78)
                println(56.78 - 12.34)
                """, getOutput(out -> {
            out.println(532 - 34);
            out.println(3.14 - 12);
            out.println(12 - 7.78);
            out.println(56.78 - 12.34);
        }));
    }

    @Test
    public void testMul() {
        verify("""
                println(12 * 34)
                println(12 * 3.4)
                println(0.12 * 34)
                println(12.34 * 56.78)
                """, getOutput(out -> {
            out.println(12 * 34);
            out.println(12 * 3.4);
            out.println(0.12 * 34);
            out.println(12.34 * 56.78);
        }));
    }

    @Test
    public void testDiv() {
        verify("""
                println(5 / 2)
                println(12 / 3.4)
                println(0.12 / 34)
                println(56.78 / 12.34)
                """, getOutput(out -> {
            out.println(5 / 2);
            out.println(12 / 3.4);
            out.println(0.12 / 34);
            out.println(56.78 / 12.34);
        }));
    }

    @Test
    public void testRem() {
        verify("""
                println(12 % 3)
                println(12 % 5)
                println(3 % 7)
                println(6 % 3)
                """, getOutput(out -> {
            out.println(12 % 3);
            out.println(12 % 5);
            out.println(3 % 7);
            out.println(6 % 3);
        }));
    }

    @Test
    public void testGreaterThan() {
        verify("""
                println(100 > 50)
                println(100 > 100)
                println(3.14 > 50)
                println(3.14 > 1)
                println(3.14 > 456.23)
                println(3.14 > 3.14)
                println(12 > 3.14)
                println(1 > 3.14)
                println('banana' > 'apple')
                println('apple' > 'banana')
                println('apple' > 'apple')
                """, """
                true
                false
                false
                true
                false
                false
                true
                false
                true
                false
                false
                """);
    }

    @Test
    public void testGreaterEqualThan() {
        verify("""
                println(100 >= 50)
                println(100 >= 100)
                println(3.14 >= 50)
                println(3.14 >= 1)
                println(3.14 >= 456.23)
                println(3.14 >= 3.14)
                println(12 >= 3.14)
                println(1 >= 3.14)
                println('banana' >= 'apple')
                println('apple' >= 'banana')
                println('apple' >= 'apple')
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
                false
                true
                """);
    }

    @Test
    public void testLessThan() {
        verify("""
                println(100 < 50)
                println(100 < 100)
                println(3.14 < 50)
                println(3.14 < 1)
                println(3.14 < 456.23)
                println(3.14 < 3.14)
                println(12 < 3.14)
                println(1 < 3.14)
                println('banana' < 'apple')
                println('apple' < 'banana')
                println('apple' < 'apple')
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
                true
                false
                """);
    }

    @Test
    public void testLessEqualThan() {
        verify("""
                println(100 <= 50)
                println(100 <= 100)
                println(3.14 <= 50)
                println(3.14 <= 1)
                println(3.14 <= 456.23)
                println(3.14 <= 3.14)
                println(12 <= 3.14)
                println(1 <= 3.14)
                println('banana' <= 'apple')
                println('apple' <= 'banana')
                println('apple' <= 'apple')
                """, """
                false
                true
                true
                false
                true
                true
                false
                true
                false
                true
                true
                """);
    }

    @Test
    public void testEqual() {
        verify("""
                println(123 == 123)
                println(12.34 == 12.34)
                println(true == true)
                println(false == false)
                println('apple' == 'apple')
                println(123 == 45)
                println(3.14 == 12.56)
                println(true == false)
                println('apple' == 'banana')
                println({a: 123, b: 'hello'} == {a: 123, b: 'hello'})
                
                var a = {a: 123, b: 'hello'}
                var b = a
                var c = {a: 123, b: 'hello'}
                println(a == b)
                println(a == c)
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
                false
                true
                false
                """);
    }

    @Test
    public void testNotEqual() {
        verify("""
                println(123 != 123)
                println(12.34 != 12.34)
                println(true != true)
                println(false != false)
                println('apple' != 'apple')
                println(123 != 45)
                println(3.14 != 12.56)
                println(true != false)
                println('apple' != 'banana')
                println({a: 123, b: 'hello'} != {a: 123, b: 'hello'})
                
                var a = {a: 123, b: 'hello'}
                var b = a
                var c = {a: 123, b: 'hello'}
                println(a != b)
                println(a != c)
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
                true
                false
                true
                """
        );
    }

    @Test
    public void testAnd() {
        verify("""
                println(true && true)
                println(true && false)
                println(false && true)
                println(false && false)
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
                println(true || true)
                println(true || false)
                println(false || true)
                println(false || false)
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
                println(!true)
                println(!false)
                """, """
                false
                true
                """);
    }

    @Test
    public void testNull() {
        verify("""
                println(null == null)
                println(123 == null)
                println(null == 123)
                println(3.14 == null)
                println(null == 3.14)
                println('hello' == null)
                println(null == 'hello')
                println([] == null)
                println(null == [])
                println([1, 2, 3] == null)
                println(null == [1, 2, 3])
                println({} == null)
                println(null == {})
                println({m: 100} == null)
                println(null == {m: 100})
                println((a => a + 1) == null)
                println(null == (a => a + 1))
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
                false
                false
                """);
    }

    @Test
    public void testExpr() {
        verify("""
                println(2 + 3*5)
                println((2+3) * 4 / (9-7))
                println(2.4 / 5.774 * (6 / 3.57 + 6.37 )-2 * 7 / 5.2 + 5)
                println(-2.4 / 5.774 * (6 / 3.57 + 6.37 )-2 * 7 / 5.2 + 5)
                println(77.58 * (6 / 3.14+55.2234) - 2 * 6.1 / (1.0 + 2 / (4.0 - 3.8*5)))
                println(77.58 * (6 / -3.14+55.2234) - 2 * (-6.1) / (1.0 + 2 / (4.0 - 3.8*5)))
                println(-100)
                println(-5 + 7)
                println(-(5 + 7))
                println(-3.14)
                println(-12.34-67.5)
                println(-(12.34-67.5))
                
                println(!false || true)
                println(!(false || true))
                println(!true && false)
                println(!(true && false))
                println(true && !false)
                println(false || !true)
                println(false && false || true)
                println(false && (false || true))
                println(true || true && false)
                println((true || true) && false)
                println(true && true && true)
                println(true && true && false)
                println(true || false || true)
                println(false || false || false)
                """, getOutput(out -> {
            out.println(2 + 3*5);
            out.println((2+3) * 4 / (9-7));
            out.println(2.4 / 5.774 * (6 / 3.57 + 6.37 )-2 * 7 / 5.2 + 5);
            out.println(-2.4 / 5.774 * (6 / 3.57 + 6.37 )-2 * 7 / 5.2 + 5);
            out.println(77.58 * (6 / 3.14+55.2234) - 2 * 6.1 / (1.0 + 2 / (4.0 - 3.8*5)));
            out.println(77.58 * (6 / -3.14+55.2234) - 2 * (-6.1) / (1.0 + 2 / (4.0 - 3.8*5)));
            out.println(-100);
            out.println(-5 + 7);
            out.println(-(5 + 7));
            out.println(-3.14);
            out.println(-12.34-67.5);
            out.println(-(12.34-67.5));

            out.println(true);
            out.println(false);
            out.println(false);
            out.println(true);
            out.println(true);
            out.println(false);
            out.println(true);
            out.println(false);
            out.println(true);
            out.println(false);
            out.println(true);
            out.println(false);
            out.println(true);
            out.println(false);
        }));
    }

    @Test
    public void testShortCircuit() {
        verify("""
                var x = 0
                var y = 0
                func f1() {
                    x = x + 1
                    return true
                }
                func f2() {
                    y = y + 1
                    return false
                }

                x = 0
                y = 0
                var b = f2() && f1()
                println(b)
                println(x)
                println(y)

                x = 0
                y = 0
                b = f1() || f2()
                println(b)
                println(x)
                println(y)
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
                println(i)
                println(j)
                """, """
                3
                124
                """);
    }

    @Test
    public void testIf() {
        verify("""
                if (true) {}
                if (true) {} else {}
                if (true) {} else if(false) {} else if(true) {} else {}
                """, """
                """);
        verify("""
                var i = 123
                if (i > 200) {
                    i = 456
                }
                println(i)""", """
                123
                """);
        verify("""
                var i = 123
                if (200 > i) {
                    i = 456
                }
                println(i)""", """
                456
                """);
        verify("""
                var i = 100
                if (i < 25 || !(i < 50)) {
                    i = 200
                }
                println(i)""", """
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
                println(i, j)""", """
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
                println(i, j)""", """
                1003 1004
                """);
        verify("""
                func getLevel(score) {
                    if (85 < score && score <= 100) {
                        return 'excellent'
                    } else if (75 < score && score <= 85) {
                        return 'good'
                    } else if (60 < score && score <= 75) {
                        return 'pass'
                    } else {
                        return 'failed'
                    }
                }
                
                println(getLevel(92))
                println(getLevel(73))
                println(getLevel(81))
                println(getLevel(50))
                """, """
                excellent
                pass
                good
                failed
                """);
    }

    @Test
    public void testFor() {
        verify("""
                for (var i = 1; i <= 100; i++) {}
                """, """
                """);
        verify("""
                var s = 0
                for (var i = 1; i <= 100; i++) {
                    s += i
                }
                println(s)""", """
                5050
                """);
        verify("""
                var s1 = 0
                var s2 = 0
                for (var i = 1; i <= 100; i++) {
                    if (i % 2 == 0) {
                        s1 = s1 + i
                    } else {
                        s2 = s2 + i
                    }
                }
                println(s1, s2)""", """
                2550 2500
                """);
        verify("""
                var s = 0
                for (var i = 0; i < 1000; i = i + 1) {
                    if (i % 6 == 1 && (i % 7 == 2 || i % 8 == 3)) {
                        s = s + i
                    }
                }
                println(s)""", """
                29441
                """);
        verify("""
                var s = 0
                for (var i = 0; i < 1000; ++i) {
                    if (i % 6 == 1 && i % 7 == 2 || i % 8 == 3) {
                        s += i
                    }
                }
                println(s)""", """
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
                println(s, i)""", """
                5050 101
                """);
        verify("""
                var s1 = 0
                var s2 = 0
                var i = 1
                while (i <= 100) {
                    if (i % 2 == 0) {
                        s1 += i
                    } else {
                        s2 += i
                    }
                    i = i + 1
                }
                println(s1, s2, i)""", """
                2550 2500 101
                """);
    }

    @Test
    public void testBreak() {
        verify("""
                var s = 0
                for (var i = 0; i < 10000; i += 1) {
                    if (i % 3242 == 837) {
                        break
                    }
                    s += i
                }
                println(s)""", """
                349866
                """);
        verify("""
                var s = 0
                var i = 0
                while (i < 10000) {
                    if (i % 3242 == 837) {
                        break
                    }
                    s = s + i
                    i = i + 1
                }
                println(s, i)""", """
                349866 837
                """);
    }

    @Test
    public void testNestedBreak() {
        verify("""
                for (var i = 0; i < 100; ++i) {
                    for (var j = 0; j < 100; ++j) {
                        if ((i * j) % 12 == 7 && (i * j) % 23 == 11) {
                            println(i, j)
                            break;
                        }
                    }
                }
                """, getOutput(out -> {
            for (int i = 0; i < 100; ++i) {
                for (int j = 0; j < 100; ++j) {
                    if ((i * j) % 12 == 7 && (i * j) % 23 == 11) {
                        out.println(i + " " + j);
                        break;
                    }
                }
            }
        }));
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
                println(s)""", """
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
                println(s, i)""", """
                277694 100
                """);
    }

    @Test
    public void testNestedContinue() {
        verify("""
                var s = 0
                for (var i = 0; i < 100; ++i) {
                    for (var j = 0; j < 100; ++j) {
                        if ((i * j) % 12 == 7 && (i * j) % 23 == 11) {
                            continue;
                        }
                        s += i * j
                    }
                }
                println(s)
                """, getOutput(out -> {
            int s = 0;
            for (int i = 0; i < 100; ++i) {
                for (int j = 0; j < 100; ++j) {
                    if ((i * j) % 12 == 7 && (i * j) % 23 == 11) {
                        continue;
                    }
                    s += i * j;
                }
            }
            out.println(s);
        }));
    }

    @Test
    public void testContinueAndBreak() {
        verify("""
                var i = 1
                while (i <= 10) {
                    if (i % 2 == 0) {
                        i++
                        continue
                    }
                    if (i == 7) {
                        break
                    }
                    println(i)
                    i++
                }
                """, getOutput(out -> {
            int i = 1;
            while (i <= 10) {
                if (i % 2 == 0) {
                    i++;
                    continue;
                }
                if (i == 7) {
                    break;
                }
                out.println(i);
                i++;
            }
        }));
        verify("""
                for (var i = 1; i <= 10; i++) {
                    if (i % 2 == 0) {
                        continue
                    }
                    if (i == 7) {
                        break
                    }
                    println(i)
                }
                """, getOutput(out -> {
            for (int i = 1; i <= 10; i++) {
                if (i % 2 == 0) {
                    continue;
                }
                if (i == 7) {
                    break;
                }
                out.println(i);
            }
        }));
    }

    @Test
    public void testStringConcat() {
        verify("""
                var s = ''
                for (var i = 1; i <= 10; i = i + 1) {
                    if (i != 10) {
                        s = s + i + ' '
                    } else {
                        s = s + i
                    }
                }
                println(s)""", """
                1 2 3 4 5 6 7 8 9 10
                """);
        verify("""
                var s = ''
                for (var i = 0; i < 100; i = i + 1) {
                    s = s + 'hello'
                }
                println(s)""", "hello".repeat(100));
    }

    @Test
    public void testHarmonicSeries() {
        verify("""
                var s = 0.0
                for (var i = 1; i <= 100; i++) {
                    s = s + 1.0/i
                }
                println(s)""", getOutput(out -> {
            double s = 0.0;
            for (int i = 1; i <= 100; ++i) {
                s += 1.0 / i;
            }
            out.println(s);
        }));
    }

    @Test
    public void testEmptyString() {
        verify("""
                var s = ''
                println(s)
                """, "");
    }

    @Test
    public void testStringLength() {
        verify("""
                println(''.length())
                println('abc'.length())
                println('hello，你好'.length())
                """, """
                0
                3
                8
                """);
    }

    @Test
    public void testStringSubstring() {
        verify("""
                var s = 'hello';
                println(s.substring(1, 4))
                println('你好世界'.substring(1, 3))
                """, """
                ell
                好世
                """);
    }

    @Test
    public void testStringConcatMethod() {
        verify("""
                println('abc'.concat('defg'))
                """, """
                abcdefg
                """);
    }

    @Test
    public void testStringCast() {
        verify("""
                println('123'.toInt())
                println('3.14'.toDouble())
                println('true'.toBool())
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
                println(s.charAt(0), s.charAt(1), s.charAt(2))
                println(s.charAt(1) == 'b')
                println('你好'[0] == '你')
                """, """
                a b c
                true
                true
                """);
    }

    @Test
    public void testStringCodeAt() {
        verify("""
                var s = 'abc'
                println(s.codeAt(0), s.codeAt(1), s.codeAt(2))
                """, """
                97 98 99
                """);
    }

    @Test
    public void testStringSubscript() {
        verify("""
                var s = 'abc'
                println(s[0], s[1], s[2])
                println(s[1] == 'b')
                """, """
                a b c
                true
                """);
    }

    @Test
    public void testListLength() {
        verify("""
                var arr1 = []
                println(arr1.length())
                var arr2 = [1, 2, 3, 4]
                println(arr2.length())
                println([1, 2, 3].length())
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
                println(arr.length())
                arr.addFirst(4)
                arr.addFirst(5)
                println(arr.length())
                arr.addFirst(3.14)
                arr.addFirst('hello')
                println(arr.length())
                println(arr)
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
                println(nums.removeFirst())
                println(nums)
                """, """
                1
                [2, 3, 4, 5]
                """);
    }

    @Test
    public void testListAddLast() {
        verify("""
                var arr = [1, 2, 3]
                println(arr.length())
                arr.addLast(4)
                arr.addLast(5)
                println(arr.length())
                arr.addLast(3.14)
                arr.addLast('hello')
                println(arr.length())
                println(arr)
                """, """
                3
                5
                7
                [1, 2, 3, 4, 5, 3.14, hello]
                """);
        verify("""
                var nums = []
                for (var i = 1; i <= 100; i = i + 1) {
                    nums.addLast(i * i)
                }
                var s = 0
                for (var i = 0; i < nums.length(); i = i + 1) {
                    s = s + nums[i]
                }
                println(s)
                """, """
                338350
                """);
    }

    @Test
    public void testListRemoveLast() {
        verify("""
                var nums = [1, 2, 3, 4, 5]
                println(nums.removeLast())
                println(nums)
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
                println(list)
                """, """
                [100, 1, 2, hello, 3, 4, 5, 3.14]
                """);
    }

    @Test
    public void testListRemove() {
        verify("""
                var list = [1, 2, 3, 4, 5]
                println(list.remove(2))
                println(list)
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
                println(list1)
                println(list2)
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
                        print(list[i][j] + ' ')
                    }
                    println()
                }
                """, """
                10 11 12 13
                20 21 22 23
                30 31 32 33
                """
        );
    }

    @Test
    public void testCallableEqual() {
        verify("""
                println((() => {}) == (() => {}))
                println((a => a + 1) == (a => a + 1))
                var f1 = (a, b) => a + b
                var f2 = f1
                var f3 = (a, b) => a + b
                println(f1 == f2)
                println(f2 == f3)
                """, """
                false
                false
                true
                false
                """);
    }

    @Test
    public void testListEqual() {
        verify("""
                println([] == [])
                println([1, 2, 3] == [1, 2, 3])
                println([1, 2, 3] == [1, 2, 3, 4])
                var a = [1, 2, 3]
                var b = a
                var c = [1, 2, 3]
                println(a == b)
                println(b == c)
                """, """
                true
                true
                false
                true
                true
                """);
    }

    @Test
    public void testObjectEqual() {
        verify("""
                println({} == {})
                println({a: 123, b: 'hello'} == {a: 123, b: 'hello'})
                var o1 = {a: 123, b: 'hello'}
                var o2 = o1
                var o3 = {a: 123, b: 'hello'}
                println(o1 == o2)
                println(o2 == o3)
                """, """
                false
                false
                true
                false
                """);
    }

    @Test
    public void testInc() {
        verify("""
                var i = 100
                var obj = {a: 1, b: {x: 20}, c: [1, 2, 3]}
                
                i++
                ++i
                obj.a++
                ++obj.a
                obj.c[0]++
                ++obj.c[1]
                
                println(i)
                println(obj.a)
                println(obj.c[0])
                println(obj.c[1])
                """, """
                102
                3
                2
                3
                """);
    }

    @Test
    public void testDec() {
        verify("""
                var i = 100
                var obj = {a: 1, b: {x: 20}, c: [1, 2, 3]}
                
                i--
                --i
                obj.a--
                --obj.a
                obj.c[0]--
                --obj.c[1]
                
                println(i)
                println(obj.a)
                println(obj.c[0])
                println(obj.c[1])
                """, """
                98
                -1
                0
                1
                """);
    }

    @Test
    public void testReturn() {
        verify("""
                func fun() {
                    println('hello')
                    return;
                    println('hi')
                }
                
                fun()
                """, """
                hello
                """);
    }

    @Test
    public void testTry() {
        verify("""
                func testException(f) {
                    try {
                        f()
                    } catch (e) {
                        println('catch', e)
                    }
                }
                
                testException(() => {
                    println('test1')
                })
                
                testException(() => {
                    println('test2')
                    throw 123
                })
                
                testException(() => {
                    println('test3-1')
                    throw 456
                    println('test3-2')
                })
                """, """
                test1
                test2
                catch 123
                test3-1
                catch 456
                """);
        verify("""
                try {
                    println(123)
                    throw 'hello'
                    println(456)
                } catch (err) {
                    println('catch', err)
                }
                """, """
                123
                catch hello
                """);
    }

    @Test
    public void testNestedTry() {
        verify("""
            println('begin')
            try {
                println('try1-1')
                try {
                    println('try2-1')
                    throw 123
                    println('try2-2')
                } catch (e) {
                    println('catch1-1', e)
                    throw 456
                    println('catch1-2', e)
                }
                println('try1-2')
            } catch (e) {
                println('catch2', e)
            }
            println('end')
            """, """
            begin
            try1-1
            try2-1
            catch1-1 123
            catch2 456
            end
            """);
    }

    @Test
    public void testNestedCatch() {
        verify("""
            println('begin')
            try {
                println('try1-1')
                throw 123
                println('try1-2')
            } catch (e) {
                println('catch1-1', e)
                try {
                    println('try2-1')
                    throw 456
                    println('try2-2')
                } catch (e) {
                    println('catch2', e)
                }
                println('catch1-2')
            }
            println('end')
            """, """
            begin
            try1-1
            catch1-1 123
            try2-1
            catch2 456
            catch1-2
            end
            """);
    }

    @Test
    public void testTryAndReturn() {
        verify("""
            func f(n) {
                if (n == 123) {
                    throw 'error'
                }
            }
            
            func g() {
                try {
                    f(123)
                    println('success')
                    return 'ok'
                } catch (e) {
                    println('catch')
                    return 'failed'
                }
            }
            
            println(g())
            """, """
            catch
            failed
            """);
        verify("""
            func f(n) {
                if (n == 123) {
                    throw 'error'
                }
            }
            
            func g() {
                try {
                    f(567)
                    println('success')
                    return 'ok'
                } catch (e) {
                    println('catch')
                    return 'failed'
                }
            }
            
            println(g())
            """, """
            success
            ok
            """);
    }

    @Test
    public void testTryScope() {
        verify("""
            var a = 123
            try {
                var a = 456
                throw 'error'
            } catch (e) {
                println(a)
            }
            """, """
            123
            """);
        verify("""
            var a = 123
            try {
                var a = 456
                println('hello'.charAt(10))
            } catch (e) {
                println(a)
            }
            """, """
            123
            """);
    }

    @Test
    public void testTryAndContinue() {
        verify("""
            for (var i = 1; i <= 5; i++) {
                try {
                    for (var j = i; j <= 5; j++) {
                        if (j == 3) {
                            throw 'error'
                        }
                    }
                } catch (e) {
                    continue
                }
                println(i)
            }
            """, """
            4
            5
            """);
    }

    @Test
    public void testTryAndBreak() {
        verify("""
            for (var i = 1; i <= 5; i++) {
                println(i)
                try {
                    for (var j = 1; j <= i; j++) {
                        if (j == 3) {
                            throw 'error'
                        }
                    }
                } catch (e) {
                    break
                }
            }
            """, """
            1
            2
            3
            """);
    }

    @Test
    public void testBuiltinThrow() {
        verify("""
            println('begin')
            try {
                println(max('hello', 'hi'))
            } catch (e) {
                println('catch')
            }
            println('end')
            """, """
            begin
            catch
            end
            """);
        verify("""
            println('begin')
            try {
                println(max(1, 2))
            } catch (e) {
                println('catch')
            }
            println('end')
            """, """
            begin
            2
            end
            """);
        verify("""
            println('begin')
            try {
                println('hello'.charAt(10))
            } catch (e) {
                println('catch')
            }
            println('end')
            """, """
            begin
            catch
            end
            """);
        verify("""
            println('begin')
            try {
                println('hello'.charAt(1))
            } catch (e) {
                println('catch')
            }
            println('end')
            """, """
            begin
            e
            end
            """);
    }

    @Test
    public void testDeepThrow() {
        verify("""
            func test(n) {
                if (n == 100) {
                    throw 'error'
                }
                test(n + 1)
            }
            
            println('begin')
            try {
                test(0)
            } catch (e) {
                println('catch', e)
            }
            println('end')
            """, """
            begin
            catch error
            end
            """);
    }

    @Test
    public void testImport() throws Exception {
        Path classPath = Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("./")).toURI());
        verify(List.of(classPath.resolve("p1"), classPath.resolve("p2")), """
                import a
                import b
                
                println('main')
                """, """
                d
                c
                b
                a
                main
                """
        );
        verifyException(ByxScriptRuntimeException.class, List.of(classPath.resolve("p3")), """
                import x
                
                println('main')
                """);
    }

    @Test
    public void testInterrupt1() throws InterruptedException {
        Thread t = new Thread(() -> {
            ByxScriptRunner runner = new ByxScriptRunner();
            assertThrows(InterruptException.class, () -> runner.run("while (true) {}"));
            System.out.println("testInterrupt1 finish");
        });
        t.start();
        TimeUnit.SECONDS.sleep(1);
        t.interrupt();
        t.join();
    }

    @Test
    public void testInterrupt2() throws InterruptedException {
        Thread t = new Thread(() -> {
            ByxScriptRunner runner = new ByxScriptRunner();
            assertThrows(InterruptException.class, () -> runner.run("for (var i = 1; i >= 0; i++) {}"));
            System.out.println("testInterrupt2 finish");
        });
        t.start();
        TimeUnit.SECONDS.sleep(1);
        t.interrupt();
        t.join();
    }

    @Test
    public void testInterrupt3() throws InterruptedException {
        Thread t = new Thread(() -> {
            ByxScriptRunner runner = new ByxScriptRunner();
            assertThrows(InterruptException.class, () -> runner.run("""
                func test() {
                    test()
                }
                test()
                """));
            System.out.println("testInterrupt3 finish");
        });
        t.start();
        TimeUnit.SECONDS.sleep(1);
        t.interrupt();
        t.join();
    }

    @Test
    public void testStackOverflow() {
        verify("""
            func sum(n) {
                if (n == 1) {
                    return 1
                }
                return sum(n - 1) + n
            }
            
            println(sum(10000))
            """, """
            50005000
            """);
    }

    @Test
    public void testLongList() {
        String script = String.format("""
            var list = [%s]
            println(list[100])
            """, "1" + ", 1".repeat(10000));
        verify(script, """
            1
            """);
    }

    @Test
    public void testBreakOutsideLoop() {
        verifyException(BreakOutsideLoopException.class, "break");
        verifyException(BreakOutsideLoopException.class, """
            func test() {
                break
            }
            test()
            """);
    }

    @Test
    public void testContinueOutsideLoop() {
        verifyException(ContinueOutsideLoopException.class, "continue");
        verifyException(ContinueOutsideLoopException.class, """
            func test() {
                continue
            }
            test()
            """);
    }

    @Test
    public void testReturnOutsideFunction() {
        verifyException(ByxScriptRuntimeException.class, "return");
        verifyException(ByxScriptRuntimeException.class, "return 123");
    }

    @Test
    public void testUncaughtException() {
        verifyException(UncaughtException.class, "throw 'error'");
        verifyException(UncaughtException.class, """
            func test() {
                throw 'error'
            }
            test()
            """);
    }

    @Test
    public void testUnaryOpException() {
        verifyException(UnaryOpException.class, "![]");
        verifyException(UnaryOpException.class, "-{}");
    }

    @Test
    public void testBinaryOpException() {
        verifyException(BinaryOpException.class, "[] + {}");
        verifyException(BinaryOpException.class, "[] - {}");
        verifyException(BinaryOpException.class, "[] * {}");
        verifyException(BinaryOpException.class, "[] / {}");
        verifyException(BinaryOpException.class, "[] % {}");
        verifyException(BinaryOpException.class, "[] < {}");
        verifyException(BinaryOpException.class, "[] <= {}");
        verifyException(BinaryOpException.class, "[] > {}");
        verifyException(BinaryOpException.class, "[] >= {}");
        verifyException(BinaryOpException.class, "[] && {}");
        verifyException(BinaryOpException.class, "[] || {}");
    }

    @Test
    public void testNotCallableException() {
        verifyException(NotCallableException.class, "123()");
    }

    @Test
    public void testFieldNotExistException() {
        verifyException(FieldNotExistException.class, """
            var obj = {
                a: 123,
                b: 'hello'
            }
            println(obj.c)
            """);
    }

    @Test
    public void testFieldAccessUnsupportedException() {
        verifyException(FieldAccessUnsupportedException.class, "123.a");
    }

    @Test
    public void testFieldAssignUnsupportedException() {
        verifyException(FieldAssignUnsupportedException.class, "123.a = 456");
    }

    @Test
    public void testInvalidSubscriptException() {
        verifyException(InvalidSubscriptException.class, "[1, 2, 3]['hello']");
        verifyException(InvalidSubscriptException.class, "'hello'[3.14]");
        verifyException(InvalidSubscriptException.class, "[1, 2, 3][3.14] = 'a'");
    }

    @Test
    public void testSSubscriptAccessUnsupportedException() {
        verifyException(SubscriptAccessUnsupportedException.class, "123[1]");
    }

    @Test
    public void testSubscriptAssignUnsupportedException() {
        verifyException(SubscriptAssignUnsupportedException.class, "123[1] = 456");
        verifyException(SubscriptAssignUnsupportedException.class, "'hello'[1] = 'a'");
    }

    @Test
    public void testInvalidLoopConditionException() {
        verifyException(InvalidLoopConditionException.class, """
            while (123) {
                println('hello')
            }
            """);
        verifyException(InvalidLoopConditionException.class, """
            for (var i = 0; 123; i++) {
                println('hello')
            }
            """);
    }
}
