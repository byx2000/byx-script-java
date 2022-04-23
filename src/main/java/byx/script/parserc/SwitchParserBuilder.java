package byx.script.parserc;

import java.util.ArrayList;
import java.util.List;

import static byx.script.parserc.Parsers.*;

public class SwitchParserBuilder<R> {
    private final List<Pair<Parser<?>, Parser<R>>> cases = new ArrayList<>();
    private Parser<R> defaultCase = fail("no case reached");

    public SwitchParserBuilder<R> addCase(Parser<?> predicate, Parser<R> parser) {
        cases.add(new Pair<>(predicate, parser));
        return this;
    }

    public SwitchParserBuilder<R> setDefault(Parser<R> defaultCase) {
        this.defaultCase = defaultCase;
        return this;
    }

    public Parser<R> build() {
        return input -> {
            for (Pair<Parser<?>, Parser<R>> c : cases) {
                try {
                    c.getFirst().parse(input);
                } catch (ParseException e) {
                    continue;
                }
                return c.getSecond().parse(input);
            }
            return defaultCase.parse(input);
        };
    }
}
