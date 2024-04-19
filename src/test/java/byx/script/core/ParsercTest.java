package byx.script.core;

import byx.script.core.common.Pair;
import byx.script.core.parser.exception.InternalParseException;
import byx.script.core.parser.parserc.Parser;
import byx.script.core.parser.parserc.Parsers;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static byx.script.core.parser.parserc.Parsers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParsercTest {
    private static class MyException extends RuntimeException {
        private final String input;
        private final int index;
        private final String msg;

        private MyException(String input, int index, String msg) {
            this.input = input;
            this.index = index;
            this.msg = msg;
        }

        public String getInput() {
            return input;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String getMessage() {
            return msg;
        }
    }

    @Test
    public void testEmpty1() {
        Parser<Integer> p = empty(123);
        assertEquals(123, p.parse("abc"));
        assertEquals(123, p.parse(""));
    }

    @Test
    public void testFail4() {
        Parser<?> p = Parsers.fail();
        assertThrows(InternalParseException.class, () -> p.parse("abc"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testEnd1() {
        Parser<String> p = end("byx");
        String r = p.parse("");
        assertEquals("byx", r);
        assertThrows(InternalParseException.class, () -> p.parse("abc"));
    }

    @Test
    public void testEnd2() {
        Parser<?> p = end();
        Object r = p.parse("");
        assertNull(r);
        assertThrows(InternalParseException.class, () -> p.parse("abc"));
    }

    @Test
    public void testCh1() {
        Parser<Character> p = ch(c -> c == 'a');
        Character r = p.parse("abc");
        assertEquals((Character) 'a', r);
        assertThrows(InternalParseException.class, () -> p.parse("def"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testCh2() {
        Parser<Character> p = ch('a');
        Character r = p.parse("abc");
        assertEquals((Character) 'a', r);
        assertThrows(InternalParseException.class, () -> p.parse("def"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testAny() {
        Parser<Character> p = any();
        Character r1 = p.parse("abc");
        assertEquals((Character) 'a', r1);
        Character r = p.parse("bcd");
        assertEquals((Character) 'b', r);
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testRange1() {
        Parser<Character> p = range('d', 'f');
        Character r = p.parse("dog");
        assertEquals((Character) 'd', r);
        assertEquals('e', p.parse("egg"));
        assertEquals('f', p.parse("father"));
        assertThrows(InternalParseException.class, () -> p.parse("apple"));
        assertThrows(InternalParseException.class, () -> p.parse("high"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testRange2() {
        Parser<Character> p = range('f', 'd');
        Character r = p.parse("dog");
        assertEquals((Character) 'd', r);
        assertEquals('e', p.parse("egg"));
        assertEquals('f', p.parse("father"));
        assertThrows(InternalParseException.class, () -> p.parse("apple"));
        assertThrows(InternalParseException.class, () -> p.parse("high"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testChs1() {
        Parser<Character> p = chs('f', 'o', 'h');
        Character r = p.parse("far");
        assertEquals((Character) 'f', r);
        assertEquals('o', p.parse("ohh"));
        assertEquals('h', p.parse("high"));
        assertThrows(InternalParseException.class, () -> p.parse("byx"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testChs2() {
        Parser<Character> p = chs('f');
        Character r = p.parse("far");
        assertEquals((Character) 'f', r);
        assertThrows(InternalParseException.class, () -> p.parse("byx"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testChs3() {
        Parser<Character> p = chs();
        assertThrows(InternalParseException.class, () -> p.parse("byx"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testNot1() {
        Parser<Character> p = not('f', 'o', 'h');
        Character r = p.parse("byx");
        assertEquals((Character) 'b', r);
        assertThrows(InternalParseException.class, () -> p.parse("fog"));
        assertThrows(InternalParseException.class, () -> p.parse("ohhh"));
        assertThrows(InternalParseException.class, () -> p.parse("high"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testNot2() {
        Parser<Character> p = not('f');
        Character r = p.parse("byx");
        assertEquals((Character) 'b', r);
        assertThrows(InternalParseException.class, () -> p.parse("fog"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testNot3() {
        Parser<Character> p = not();
        Character r = p.parse("byx");
        assertEquals((Character) 'b', r);
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStr() {
        Parser<String> p = str("byx");
        String r1 = p.parse("byxabcd");
        assertEquals("byx", r1);
        String r = p.parse("byx");
        assertEquals("byx", r);
        assertThrows(InternalParseException.class, () -> p.parse("by"));
        assertThrows(InternalParseException.class, () -> p.parse("bytb"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStrs1() {
        Parser<String> p = strs("apple", "amend", "byx");
        String r1 = p.parse("applemen");
        assertEquals("apple", r1);
        String r = p.parse("byxm");
        assertEquals("byx", r);
        assertThrows(InternalParseException.class, () -> p.parse("app"));
        assertThrows(InternalParseException.class, () -> p.parse("bycd"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStrs2() {
        Parser<String> p = strs("byx");
        String r1 = p.parse("byxabcd");
        assertEquals("byx", r1);
        String r = p.parse("byx");
        assertEquals("byx", r);
        assertThrows(InternalParseException.class, () -> p.parse("by"));
        assertThrows(InternalParseException.class, () -> p.parse("bytb"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStrs3() {
        Parser<String> p = strs();
        assertThrows(InternalParseException.class, () -> p.parse("abc"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testAnd() {
        Parser<Pair<String, Character>> p = str("hello").and(ch('a'));
        Pair<String, Character> r = p.parse("helloabc");
        assertEquals(new Pair<>("hello", 'a'), r);
        assertThrows(InternalParseException.class, () -> p.parse("hello world"));
        assertThrows(InternalParseException.class, () -> p.parse("byx"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSeq1() {
        Parser<List<Object>> p = seq(ch('a'), str("bcd"), ch('e'));
        List<?> r = p.parse("abcdefgh");
        assertEquals(List.of('a', "bcd", 'e'), r);
        assertThrows(InternalParseException.class, () -> p.parse("abcdk"));
        assertThrows(InternalParseException.class, () -> p.parse("amnpuk"));
        assertThrows(InternalParseException.class, () -> p.parse("byx"));
        assertThrows(InternalParseException.class, () -> p.parse("a"));
        assertThrows(InternalParseException.class, () -> p.parse("abc"));
        assertThrows(InternalParseException.class, () -> p.parse("abcd"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSeq2() {
        Parser<List<Object>> p = seq(str("abc"));
        List<?> r = p.parse("abcde");
        assertEquals(List.of("abc"), r);
        assertThrows(InternalParseException.class, () -> p.parse("axy"));
        assertThrows(InternalParseException.class, () -> p.parse("ab"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSeq3() {
        Parser<List<Object>> p = seq();
        List<Object> r = p.parse("abcde");
        assertEquals(Collections.emptyList(), r);
        assertEquals(Collections.emptyList(), p.parse(""));
    }

    @Test
    public void testOr() {
        Parser<Character> p = ch('a').or(ch('b'));
        Character r1 = p.parse("a");
        assertEquals((Character) 'a', r1);
        Character r = p.parse("b");
        assertEquals((Character) 'b', r);
        assertThrows(InternalParseException.class, () -> p.parse("x"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOneOf1() {
        Parser<Character> p = oneOf(ch('a'), ch('b'), ch('c'));
        Character r2 = p.parse("a");
        assertEquals((Character) 'a', r2);
        Character r1 = p.parse("b");
        assertEquals((Character) 'b', r1);
        Character r = p.parse("c");
        assertEquals((Character) 'c', r);
        assertThrows(InternalParseException.class, () -> p.parse("d"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOneOf2() {
        Parser<Character> p = oneOf(ch('a'));
        Character r = p.parse("a");
        assertEquals((Character) 'a', r);
        assertThrows(InternalParseException.class, () -> p.parse("d"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOneOf3() {
        Parser<Object> p = oneOf();
        assertThrows(InternalParseException.class, () -> p.parse("a"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testMap() {
        Parser<Integer> p = str("hello").map(String::length);
        Integer r = p.parse("hello");
        assertEquals((Integer) 5, r);
        assertThrows(InternalParseException.class, () -> p.parse("hi"));
    }

    @Test
    public void testMany() {
        Parser<List<Character>> p = ch('a').many();
        List<?> r3 = p.parse("");
        assertEquals(Collections.emptyList(), r3);
        List<?> r2 = p.parse("bbb");
        assertEquals(Collections.emptyList(), r2);
        List<Character> r1 = p.parse("a");
        assertEquals(List.of('a'), r1);
        List<Character> r = p.parse("aaa");
        assertEquals(List.of('a', 'a', 'a'), r);
    }

    @Test
    public void testMany1() {
        Parser<List<Character>> p = ch('a').many1();
        List<Character> r1 = p.parse("a");
        assertEquals(List.of('a'), r1);
        List<Character> r = p.parse("aaa");
        assertEquals(List.of('a', 'a', 'a'), r);
        assertThrows(InternalParseException.class, () -> p.parse("bbb"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOpt1() {
        Parser<Character> p = ch('a').opt('x');
        Character r2 = p.parse("a");
        assertEquals((Character) 'a', r2);
        Character r1 = p.parse("byx");
        assertEquals((Character) 'x', r1);
        Character r = p.parse("");
        assertEquals((Character) 'x', r);
    }

    @Test
    public void testOpt2() {
        Parser<Character> p = ch('a').opt();
        Character r2 = p.parse("a");
        assertEquals((Character) 'a', r2);
        Character r1 = p.parse("byx");
        assertNull(r1);
        Character r = p.parse("");
        assertNull(r);
    }

    @Test
    public void testLazy() {
        int[] val = {0};
        Parser<Character> p = lazy(() -> {
            val[0] = 1;
            return ch('a');
        });
        assertEquals(0, val[0]);
        Character r = p.parse("a");
        assertEquals((Character) 'a', r);
        assertEquals(1, val[0]);
    }

    @Test
    public void testSurround1() {
        Parser<Character> p = ch('a').surround(ch('('), ch(')'));
        Character r = p.parse("(a)");
        assertEquals((Character) 'a', r);
        assertThrows(InternalParseException.class, () -> p.parse("(a"));
        assertThrows(InternalParseException.class, () -> p.parse("a)"));
        assertThrows(InternalParseException.class, () -> p.parse("a"));
        assertThrows(InternalParseException.class, () -> p.parse("(b)"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSurround2() {
        Parser<Character> p = ch('a').surround(str("***"));
        Character r = p.parse("***a***");
        assertEquals((Character) 'a', r);
        assertThrows(InternalParseException.class, () -> p.parse("***a**"));
        assertThrows(InternalParseException.class, () -> p.parse("*a***"));
        assertThrows(InternalParseException.class, () -> p.parse("*a**"));
        assertThrows(InternalParseException.class, () -> p.parse("***b***"));
        assertThrows(InternalParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSkipFirst() {
        Parser<String> p = skip(ch('a')).and(str("bc"));
        String r = p.parse("abc");
        assertEquals("bc", r);
    }

    @Test
    public void testSkipSecond() {
        Parser<Character> p = ch('a').skip(str("bc"));
        Character r = p.parse("abc");
        assertEquals((Character) 'a', r);
    }

    @Test
    public void testSkip() {
        Parser<String> p = skip(ch('a')).and(str("bc"));
        String r = p.parse("abc");
        assertEquals("bc", r);
    }

    @Test
    public void testExpect() {
        Parser<Integer> p = expect(str("xy"), 123);
        Integer r = p.parse("xyz");
        assertEquals((Integer) 123, r);
        assertThrows(InternalParseException.class, () -> p.parse("xz"));
    }

    @Test
    public void testNot() {
        Parser<?> p = not(str("xy"));
        Object r = p.parse("abc");
        assertNull(r);
        assertThrows(InternalParseException.class, () -> p.parse("xyz"));
    }

    @Test
    public void testNotFollow() {
        Parser<String> p = str("abc").notFollow(ch('d'));
        String r1 = p.parse("abcx");
        assertEquals("abc", r1);
        String r = p.parse("abc");
        assertEquals("abc", r);
        assertThrows(InternalParseException.class, () -> p.parse("abcd"));
    }

    @Test
    public void testFatal() {
        Parser<Character> p = ch('a').fatal(e -> new MyException(e.getInput(), e.getIndex(), "msg"));
        MyException e = assertThrows(MyException.class, () -> p.parse("bcd"));
        assertEquals("bcd", e.getInput());
        assertEquals(0, e.getIndex());
        assertEquals("msg", e.getMessage());
    }
}
