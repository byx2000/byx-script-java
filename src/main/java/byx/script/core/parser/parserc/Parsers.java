package byx.script.core.parser.parserc;

import byx.script.core.common.Pair;
import byx.script.core.parser.exception.InternalParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 常用解析器的静态工厂
 */
public class Parsers {
    /**
     * 在当前位置抛出ParseException，错误消息为空
     */
    public static <R> Parser<R> fail() {
        return (s, index) -> {throw new InternalParseException(s, index);};
    }

    /**
     * 不执行任何操作的解析器，并返回result作为解析结果
     * @param result 解析结果
     */
    public static <R> Parser<R> empty(R result) {
        return (s, index) -> new ParseResult<>(result, index);
    }

    /**
     * 如果当前位置到达输入末尾，则返回result作为解析结果，否则抛出ParseException
     * @param result 解析结果
     */
    public static <R> Parser<R> end(R result) {
        return (s, index) -> {
            if (index < s.length()) {
                throw new InternalParseException(s, index);
            }
            return new ParseResult<>(result, index);
        };
    }

    /**
     * 如果当前位置到达输入末尾，则返回null作为解析结果，否则抛出ParseException
     */
    public static <T> Parser<T> end() {
        return end(null);
    }

    /**
     * <p>如果当前位置的字符满足predicate指定的条件，则解析成功并将字符作为解析结果</p>
     * <p>如果predicate不满足或到达输入末尾，则抛出ParseException</p>
     * @param predicate 匹配条件
     */
    public static Parser<Character> ch(Predicate<Character> predicate) {
        return (s, index) -> {
            if (index >= s.length()) {
                throw new InternalParseException(s, index);
            }

            char c = s.charAt(index);
            if (predicate.test(c)) {
                return new ParseResult<>(c, index + 1);
            }

            throw new InternalParseException(s, index);
        };
    }

    /**
     * <p>匹配当前位置的任何字符，并将当前位置的字符作为解析结果返回</p>
     * <p>如果到达输入末尾，则抛出ParseException</p>
     */
    public static Parser<Character> any() {
        return ch(c -> true);
    }

    /**
     * <p>如果当前位置的字符等于指定字符c，则解析成功，并返回c作为解析结果</p>
     * <p>如果当前位置的字符不等于c或到达输入末尾，则抛出ParseException</p>
     * @param c c
     */
    public static Parser<Character> ch(char c) {
        return ch(ch -> c == ch);
    }

    /**
     * <p>如果当前位置的字符在区间[c1, c2]内，则解析成功，并返回当前位置的字符作为解析结果</p>
     * <p>如果当前位置的字符不在区间[c1, c2]内或到达输入末尾，则抛出ParseException</p>
     * @param c1 c1
     * @param c2 c2
     */
    public static Parser<Character> range(char c1, char c2) {
        return ch(c -> (c - c1) * (c - c2) <= 0);
    }

    /**
     * <p>如果当前位置的字符在字符集chs内，则解析成功，并返回当前位置的字符作为解析结果</p>
     * <p>如果当前位置的字符不在字符集chs内或到达输入末尾，则抛出ParseException</p>
     * @param chs 字符集
     */
    public static Parser<Character> chs(Character... chs) {
        Set<Character> set = Arrays.stream(chs).collect(Collectors.toSet());
        return ch(set::contains);
    }

    /**
     * <p>如果当前位置的字符不在字符集chs内，则解析成功，并返回当前位置的字符作为解析结果</p>
     * <p>如果当前位置的字符在字符集chs内或到达输入末尾，则抛出ParseException</p>
     * @param chs 字符集
     */
    public static Parser<Character> not(Character... chs) {
        Set<Character> set = Arrays.stream(chs).collect(Collectors.toSet());
        return ch(c -> !set.contains(c));
    }

    /**
     * <p>如果当前位置以字符串s为前缀，则解析成功，并返回该字符串作为解析结果</p>
     * <p>如果前缀不匹配或在匹配过程中遇到输入结尾，则抛出ParseException</p>
     * @param str 字符串
     */
    public static Parser<String> str(String str) {
        return (s, index) -> {
            if (s.startsWith(str, index)) {
                return new ParseResult<>(str, index + str.length());
            }
            throw new InternalParseException(s, index);
        };
    }

    /**
     * <p>如果当前位置匹配ss中的任何字符串前缀，则解析成功，并返回该字符串作为解析结果</p>
     * <p>如果不匹配ss中的任何字符串或在匹配过程中遇到输入结尾，则抛出ParseException</p>
     * @param ss 字符串集合
     */
    public static Parser<String> strs(String... ss) {
        return Arrays.stream(ss).reduce(fail(), (p, s) -> p.or(str(s)), Parser::or);
    }

    /**
     * <p>连续应用多个解析器，并组合所有解析器的解析结果</p>
     * <p>如果任意一个解析器解析失败，则解析失败</p>
     * @param parsers 解析器数组
     */
    public static Parser<List<Object>> seq(Parser<?>... parsers) {
        return (s, index) -> {
            int i = index;
            List<Object> result = new ArrayList<>();
            for (Parser<?> p : parsers) {
                ParseResult<?> r = p.parse(s, i);
                result.add(r.result());
                i = r.index();
            }
            return new ParseResult<>(result, i);
        };
    }

    /**
     * <p>依次尝试应用parsers中的解析器，如果成功则返回其解析结果</p>
     * <p>如果所有解析器都解析失败，则解析失败</p>
     * @param parsers 解析器数组
     */
    @SafeVarargs
    public static <R> Parser<R> oneOf(Parser<R>... parsers) {
        return (s, index) -> {
            for (Parser<R> p : parsers) {
                try {
                    return p.parse(s, index);
                } catch (InternalParseException ignored) {}
            }
            throw new InternalParseException(s, index);
        };
    }

    /**
     * <p>依次尝试应用parsers中的解析器，如果成功则返回其解析结果</p>
     * <p>parsers中的解析器可以是不同的类型</p>
     * <p>如果所有解析器都解析失败，则解析失败</p>
     * @param parsers 解析器数组
     */
    public static Parser<Object> alt(Parser<?>... parsers) {
        return (s, index) -> {
            for (Parser<?> p : parsers) {
                try {
                    return p.mapTo(Object.class).parse(s, index);
                } catch (InternalParseException ignored) {}
            }
            throw new InternalParseException(s, index);
        };
    }

    /**
     * <p>延迟解析器，解析动作发生时才调用parserSupplier获取解析器并调用其解析方法</p>
     * <p>该方法一般被用于解决解析器之间循环引用的问题</p>
     * @param parserSupplier 解析器生成器
     */
    public static <R> Parser<R> lazy(Supplier<Parser<R>> parserSupplier) {
        return (s, index) -> parserSupplier.get().parse(s, index);
    }

    public static class SkipWrapper<R> {
        private final Parser<R> lhs;

        public SkipWrapper(Parser<R> lhs) {
            this.lhs = lhs;
        }

        public <R2> Parser<R2> and(Parser<R2> rhs) {
            return lhs.and(rhs).map(Pair::second);
        }
    }

    /**
     * 连接两个解析器，并丢弃第一个解析器的结果
     * @param lhs 第一个解析器
     */
    public static <R> SkipWrapper<R> skip(Parser<R> lhs) {
        return new SkipWrapper<>(lhs);
    }

    /**
     * 跳过连续多个解析器
     * @param parsers parsers
     */
    public static SkipWrapper<?> skip(Parser<?>... parsers) {
        return new SkipWrapper<>(seq(parsers));
    }

    /**
     * 在当前位置应用解析器predicate，解析成功不消耗任何输入，并返回value，解析失败抛出ParseException
     *
     * @param value value
     * @param predicate predicate
     */
    public static <R> Parser<R> expect(Parser<?> predicate, R value) {
        return (s, index) -> {
            predicate.parse(s, index);
            return new ParseResult<>(value, index);
        };
    }

    /**
     * 在当前位置应用解析器predicate，解析成功抛出ParseException，解析失败不消耗任何输入
     * @param predicate predicate
     */
    public static <R> Parser<R> not(Parser<?> predicate) {
        return (s, index) -> {
            try {
                predicate.parse(s, index);
            } catch (InternalParseException e) {
                return new ParseResult<>(null, index);
            }
            throw new InternalParseException(s, index);
        };
    }
}