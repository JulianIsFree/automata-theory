package lab1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class EmptyGrammarChecker {
    public static class BadLeftLiteralException extends Exception {
        public BadLeftLiteralException(Rule r) {
            super("Left-literal " + r.left + " of rule " + r + " isn't from non-terminal list");
        }
    }

    public static class BadRightLiteralException extends Exception {
        public BadRightLiteralException(Rule r) {
            super("Right-literal " + r.right + " of rule " + r + "isn't produced by this grammar");
        }
    }

    public record Rule(String left, String right) {
        @Override
        public String toString() {
            return "(" + left + " -> " + right + ")";
        }
    }

    public record Grammar(HashSet<Rule> rules, HashSet<String> nonTerminals,
                   HashSet<String> terminals, String startSymbol) {
        @Override
        public String toString() {
            String N = "N=" + nonTerminals.toString();
            String SIGMA = "E=" + terminals.toString();
            String P = "P=" + rules.toString();
            String S = "S=" + startSymbol;

            return "Grammar{" + N + "; " + SIGMA + "; " + P + "; " + S + "}";
        }
    }

    public static boolean nonEmpty(Grammar g) throws Exception {
        boolean changed = true;
        final Predicate<String> grammarMatcher = getGrammarMatcher(g);
        final String terminalSet = genSetPattern(g.terminals);
        final Set<String> curr = new HashSet<>();

        while (changed) {
            String iterationPattern = wrapString(genSetPattern(curr) + terminalSet);
            Predicate<String> matcher = Pattern.compile(iterationPattern).asMatchPredicate();
            for (Rule r : g.rules) {
                if (!g.nonTerminals.contains(r.left)) throw new BadLeftLiteralException(r);
                if (!grammarMatcher.test(r.right)) throw new BadRightLiteralException(r);
                if (matcher.test(r.right)) changed &= curr.add(r.left);
            }
        }

        return curr.contains(g.startSymbol);
    }

    public static Predicate<String> getGrammarMatcher(Grammar g) {
        return Pattern.compile(wrapString(genSetPattern(g.terminals) + genSetPattern(g.nonTerminals))).asMatchPredicate();
    }

    private static String genSetPattern(Set<String> words) {
        StringBuilder pattern = new StringBuilder();
        words.forEach(word -> pattern.append(word).append("\b"));
        return pattern.toString();
    }

    private static String wrapString(String str) {
        return "[" + str +"]*";
    }

    private static Grammar example1() {
        HashSet<String> terminals = new HashSet<>(Arrays.asList(
                "a",
                "b"
        ));
        HashSet<String> nonTerminals = new HashSet<>(Arrays.asList(
                "A",
                "B",
                "S"
        ));
        HashSet<Rule> rules = new HashSet<>(Arrays.asList(
                new Rule("S", "AB"),
                new Rule("A", "aA"),
                new Rule("A", "a"),
                new Rule("B", "b")
        ));
        String S = "S";
        return new Grammar(rules, nonTerminals, terminals, S);
    }

    private static Grammar example2() {
        HashSet<String> terminals = new HashSet<>(Arrays.asList(
                "a",
                "b"
        ));
        HashSet<String> nonTerminals = new HashSet<>(Arrays.asList(
                "A",
                "B",
                "S"
        ));
        HashSet<Rule> rules = new HashSet<>(Arrays.asList(
                new Rule("S", "AB"),
                new Rule("A", "aA"),
                new Rule("B", "b")
        ));
        String S = "S";
        return new Grammar(rules, nonTerminals, terminals, S);
    }

    private static Grammar example3() {
        HashSet<String> terminals = new HashSet<>(Arrays.asList(
                "a",
                "b"
        ));
        HashSet<String> nonTerminals = new HashSet<>(Arrays.asList(
                "A",
                "B",
                "S"
        ));
        HashSet<Rule> rules = new HashSet<>(Arrays.asList(
                new Rule("S", "AB"),
                new Rule("A", "aA"),
                new Rule("A", "a")
        ));
        String S = "S";
        return new Grammar(rules, nonTerminals, terminals, S);
    }

    private static void printResult(Grammar grammar) {
        try {
            System.out.println(grammar);
            if (EmptyGrammarChecker.nonEmpty(grammar)) System.out.println("Grammar creates non-empty language");
            else System.out.println("Grammar doesn't creates non-empty language");
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        printResult(example1());
        printResult(example2());
        printResult(example3());
    }
}
