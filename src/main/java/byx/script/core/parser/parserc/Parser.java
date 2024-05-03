package byx.script.core.parser.parserc;

import byx.script.core.common.Pair;
import byx.script.core.parser.exception.InternalParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 解析器
 * @param <R> 解析结果类型
 */
public interface Parser<R> {
    /**
     * 解析输入
     * @return 解析结果
     */
    ParseResult<R> parse(String s, int index);

    /**
     * 解析字符串
     * @param s 输入字符串
     * @return 解析结果
     */
    default R parse(String s) {
        return parse(s, 0).result();
    }

    /**
     * <p>依次应用两个解析器，并组合两个解析器的解析结构</p>
     * <p>如果任意一个解析器解析失败，则解析失败</p>
     * @param rhs 解析器2
     */
    default <R2> Parser<Pair<R, R2>> and(Parser<R2> rhs) {
        return (s, index) -> {
            ParseResult<R> r1 = this.parse(s, index);
            ParseResult<R2> r2 = rhs.parse(s, r1.index());
            return new ParseResult<>(new Pair<>(r1.result(), r2.result()), r2.index());
        };
    }

    /**
     * 在当前解析器后连接字符c
     * @param c c
     */
    default Parser<Pair<R, Character>> and(char c) {
        return this.and(Parsers.ch(c));
    }

    /**
     * 在当前解析器后连接字符串s
     * @param s s
     */
    default Parser<Pair<R, String>> and(String s) {
        return this.and(Parsers.str(s));
    }

    /**
     * <p>依次尝试应用两个解析器，如果成功则返回其解析结果</p>
     * <p>如果两个解析器都失败，则解析失败</p>
     * @param rhs 解析器2
     */
    default Parser<R> or(Parser<R> rhs) {
        return (s, index) -> {
            try {
                return this.parse(s, index);
            } catch (InternalParseException e) {
                return rhs.parse(s, index);
            }
        };
    }

    /**
     * 应用指定解析器，并转换解析结果
     * @param mapper 结果转换器
     */
    default <R2> Parser<R2> map(Function<R, R2> mapper) {
        return (s, index) -> {
            ParseResult<R> r = this.parse(s, index);
            return new ParseResult<>(mapper.apply(r.result()), r.index());
        };
    }

    /**
     * 丢弃当前解析器的结果，并返回另一个结果
     * @param result 结果
     */
    default <R2> Parser<R2> value(R2 result) {
        return this.map(r -> result);
    }

    /**
     * 将当前解析器的结果强制转换为指定类型
     * @param type 类型
     */
    default <R2> Parser<R2> mapTo(Class<R2> type) {
        return this.map(type::cast);
    }

    /**
     * 连续应用当前解析器最少minTimes次
     * @param minTimes minTimes
     */
    default Parser<List<R>> many(int minTimes) {
        return (s, index) -> {
            List<R> result = new ArrayList<>();
            int i = index;

            for (int c = 0; c < minTimes; c++) {
                ParseResult<R> r = this.parse(s, i);
                result.add(r.result());
                i = r.index();
            }

            while (true) {
                try {
                    ParseResult<R> r = this.parse(s, i);
                    result.add(r.result());
                    i = r.index();
                } catch (InternalParseException e) {
                    break;
                }
            }
            return new ParseResult<>(result, i);
        };
    }

    /**
     * 连续应用当前解析器零次或多次，直到失败
     */
    default Parser<List<R>> many() {
        return many(0);
    }

    /**
     * 连续应用当前解析器一次或多次，直到失败
     */
    default Parser<List<R>> many1() {
        return many(1);
    }

    /**
     * 连接另一个解析器并跳过解析结果
     * @param rhs rhs
     */
    default <R2> Parser<R> skip(Parser<R2> rhs) {
        return this.and(rhs).map(Pair::first);
    }

    /**
     * 跳过连续多个解析器
     * @param parsers parsers
     */
    default Parser<R> skip(Parser<?>... parsers) {
        return this.and(Parsers.seq(parsers)).map(Pair::first);
    }

    /**
     * 在解析器p前后连接prefix和suffix
     * @param prefix 前缀
     * @param suffix 后缀
     */
    default Parser<R> surround(Parser<?> prefix, Parser<?> suffix) {
        return Parsers.skip(prefix).and(this).skip(suffix);
    }

    /**
     * 在解析器p前后连接s
     * @param s s
     */
    default Parser<R> surround(Parser<?> s) {
        return this.surround(s, s);
    }

    /**
     * 解析器p解析成功时返回其解析结果，否则解析成功并返回defaultResult
     * @param defaultResult 默认值
     */
    default Parser<R> opt(R defaultResult) {
        return (s, index) -> {
            try {
                return this.parse(s, index);
            } catch (InternalParseException e) {
                return new ParseResult<>(defaultResult, index);
            }
        };
    }

    /**
     * 解析器p解析成功时返回其解析结果，否则解析成功并返回null
     */
    default Parser<R> opt() {
        return this.opt(null);
    }

    /**
     * 当前解析器抛出ParseException时，使用exceptionMapper转换异常并重新抛出
     * @param exceptionMapper 异常转换器
     */
    default Parser<R> fatal(Function<InternalParseException, RuntimeException> exceptionMapper) {
        return (s, index) -> {
            try {
                return parse(s, index);
            } catch (InternalParseException e) {
                throw exceptionMapper.apply(e);
            }
        };
    }

    /**
     * 如果当前位置到达输入末尾，则返回null作为解析结果，否则抛出ParseException
     */
    default Parser<R> end() {
        return this.skip(Parsers.end());
    }

    /**
     * 当前解析器应用后执行断言predicate，断言成功抛出异常
     * @param predicate predicate
     */
    default Parser<R> notFollow(Parser<?> predicate) {
        return this.skip(Parsers.not(predicate));
    }
}