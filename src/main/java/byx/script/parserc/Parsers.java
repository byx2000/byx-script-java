package byx.script.parserc;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 常用解析器的静态工厂
 */
public class Parsers {
    public static <R> Parser<R> fail() {
        return fail("no error message");
    }

    public static <R> Parser<R> fail(String msg) {
        return input -> {
            throw new ParseException(input, msg);
        };
    }

    public static <R> Parser<R> success() {
        return input -> new ParseResult<>(null, input);
    }

    public static <R> Parser<R> success(R result) {
        return input -> new ParseResult<>(result, input);
    }

    public static Parser<Character> satisfy(Predicate<Character> predicate, Function<Character, String> errMsg) {
        return input -> {
            if (input.end()) {
                throw new ParseException(input, errMsg.apply(null));
            }
            char c = input.current();
            if (!predicate.test(c)) {
                throw new ParseException(input, errMsg.apply(c));
            }
            return new ParseResult<>(c, input.next());
        };
    }

    public static Parser<Character> satisfy(Predicate<Character> predicate) {
        return satisfy(predicate, c -> "no error message");
    }

    public static Parser<Character> any() {
        return satisfy(c -> true);
    }

    public static Parser<Character> ch(char c) {
        return satisfy(ch -> c == ch, ch -> String.format("expected %c", c));
    }

    public static Parser<Character> range(char c1, char c2) {
        return satisfy(c -> (c - c1) * (c - c2) <= 0, c -> String.format("expected character in range [%c, %c]", c1, c2));
    }

    public static Parser<Character> chs(Character... chs) {
        Set<Character> set = Arrays.stream(chs).collect(Collectors.toSet());
        return satisfy(set::contains, c -> String.format("expected character in set %s", set));
    }

    public static Parser<Character> not(char c) {
        return satisfy(ch -> ch != c, ch -> String.format("unexpected character %c", ch));
    }

    public static Parser<String> string(String s) {
        return input -> {
            Input oldInput = input;
            for (int i = 0; i < s.length(); ++i) {
                if (input.end()) {
                    throw new ParseException(input, String.format("expected %s", s));
                }
                if (input.current() != s.charAt(i)) {
                    throw new ParseException(oldInput, String.format("expected %s", s));
                }
                input = input.next();
            }
            return new ParseResult<>(s, input);
        };
    }

    public static Parser<String> strings(String s1, String s2, String... ss) {
        return Arrays.stream(ss).reduce(string(s1).or(string(s2)), (p, s) -> p.or(string(s)), Parser::or);
    }

    public static <R1, R2> Parser<Pair<R1, R2>> and(Parser<R1> lhs, Parser<R2> rhs) {
        return input -> {
            ParseResult<R1> r1 = lhs.parse(input);
            ParseResult<R2> r2 = rhs.parse(r1.getRemain());
            return new ParseResult<>(new Pair<>(r1.getResult(), r2.getResult()), r2.getRemain());
        };
    }

    public static <R> Parser<R> or(Parser<R> lhs, Parser<R> rhs) {
        return input -> {
            try {
                return lhs.parse(input);
            } catch (ParseException e) {
                return rhs.parse(input);
            }
        };
    }

    @SafeVarargs
    public static <R> Parser<R> oneOf(Parser<R> p1, Parser<R> p2, Parser<R>... parsers) {
        return Arrays.stream(parsers).reduce(p1.or(p2), Parser::or);
    }

    public static Parser<Object> oneOf(Parser<?>... parsers) {
        return input -> {
            for (Parser<?> p : parsers) {
                try {
                    return p.mapTo(Object.class).parse(input);
                } catch (ParseException ignored) {}
            }
            throw new ParseException(input, "no parser available");
        };

    }

    public static <R1, R2> Parser<R2> map(Parser<R1> p, Function<R1, R2> mapper) {
        return input -> {
            ParseResult<R1> r = p.parse(input);
            return new ParseResult<>(mapper.apply(r.getResult()), r.getRemain());
        };
    }

    public static Parser<List<Object>> seq(Parser<?>... parsers) {
        return input -> {
            List<Object> result = new ArrayList<>();
            for (Parser<?> p : parsers) {
                ParseResult<?> r = p.parse(input);
                result.add(r.getResult());
                input = r.getRemain();
            }
            return new ParseResult<>(result, input);
        };
    }

    public static <R> Parser<List<R>> many(Parser<R> p) {
        return input -> {
            List<R> result = new ArrayList<>();
            try {
                while (true) {
                    ParseResult<R> r = p.parse(input);
                    result.add(r.getResult());
                    input = r.getRemain();
                }
            } catch (ParseException e) {
                return new ParseResult<>(result, input);
            }
        };
    }

    public static <R> Parser<List<R>> many1(Parser<R> p) {
        return p.and(p.many()).map(r -> {
            List<R> result = new ArrayList<>();
            result.add(r.getFirst());
            result.addAll(r.getSecond());
            return result;
        });
    }

    public static <R> Parser<R> optional(Parser<R> p) {
        return optional(p, null);
    }

    public static <R> Parser<R> optional(Parser<R> p, R defaultResult) {
        return input -> {
            try {
                return p.parse(input);
            } catch (ParseException e) {
                return new ParseResult<>(defaultResult, input);
            }
        };
    }

    public static <R> Parser<R> lazy(Supplier<Parser<R>> supplier) {
        return input -> supplier.get().parse(input);
    }

    public interface SeparateParser<D, R> extends Parser<Pair<R, List<Pair<D, R>>>> {
        Parser<List<R>> ignoreDelimiter();
    }

    public static <D, R> SeparateParser<D, R> separateBy(Parser<D> delimiter, Parser<R> parser) {
        Parser<Pair<R, List<Pair<D, R>>>> p1 = parser.and(delimiter.and(parser).many());
        Parser<List<R>> p2 = parser.and(skip(delimiter).and(parser).many()).map(p -> {
            List<R> result = new ArrayList<>();
            result.add(p.getFirst());
            result.addAll(p.getSecond());
            return result;
        });
        return new SeparateParser<>() {
            @Override
            public Parser<List<R>> ignoreDelimiter() {
                return p2;
            }

            @Override
            public ParseResult<Pair<R, List<Pair<D, R>>>> parse(Input input) throws ParseException {
                return p1.parse(input);
            }
        };
    }

    public static <R1, R2> Parser<R2> skipFirst(Parser<R1> lhs, Parser<R2> rhs) {
        return lhs.and(rhs).map(Pair::getSecond);
    }

    public static <R1, R2> Parser<R1> skipSecond(Parser<R1> lhs, Parser<R2> rhs) {
        return lhs.and(rhs).map(Pair::getFirst);
    }

    public static class SkipWrapper<R> {
        private final Parser<R> lhs;

        public SkipWrapper(Parser<R> lhs) {
            this.lhs = lhs;
        }

        public <R2> Parser<R2> and(Parser<R2> rhs) {
            return skipFirst(lhs, rhs);
        }
    }

    public static <R> SkipWrapper<R> skip(Parser<R> lhs) {
        return new SkipWrapper<>(lhs);
    }

    public static <R> Parser<R> peek(Parser<?> probe, Parser<R> success, Parser<R> failed) {
        return input -> {
            try {
                probe.parse(input);
            } catch (ParseException e) {
                return failed.parse(input);
            }
            return success.parse(input);
        };
    }

    public static <R> Parser<List<R>> manyUntil(Parser<R> p, Parser<?> until) {
        return peek(until, fail(), p).many();
    }

    public static <R1, R2> Parser<R2> then(Parser<R1> p, Function<R1, Parser<R2>> flatMap) {
        return input -> {
            ParseResult<R1> r = p.parse(input);
            return flatMap.apply(r.getResult()).parse(r.getRemain());
        };
    }

    public static <R> Parser<R> fatal(Parser<R> p) {
        return input -> {
            try {
                return p.parse(input);
            } catch (ParseException e) {
                throw new FatalParseException(e.getInput(), e.getMsg());
            }
        };
    }
}
