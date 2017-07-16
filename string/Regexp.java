package template.string;

import template.collection.tuple.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dy on 17-1-12.
 *
 * Caution!! To match '\' in text, you need '\\\\' in the regexp.
 */
public class Regexp extends StringSearch {
    private Pattern javaPattern;
    private Matcher javaMatcher;

    public Regexp(String pattern) {
        pattern = adjust(pattern);
        javaPattern = Pattern.compile(pattern);
    }

    public Regexp(String[] patterns) {
        StringBuilder pattern = new StringBuilder();
        for (int i = 0; i < patterns.length; ++i) {
            patterns[i] = adjust(patterns[i]);
            if (i > 0) pattern.append("|");
            pattern.append("(").append(patterns[i]).append(")");
        }
    }

    public boolean subMatches(String text) {
        javaMatcher = javaPattern.matcher(text);
        return javaMatcher.lookingAt();
    }

    public boolean matches(String text) {
        javaMatcher = javaPattern.matcher(text);
        return javaMatcher.matches();
    }

    public List<Tuple2<Integer, Integer>> exploit(String text) {
        List<Tuple2<Integer, Integer>> res = new ArrayList<>();
        javaMatcher = javaPattern.matcher(text);
        while (javaMatcher.find()) {
            res.add(new Tuple2<Integer, Integer>(javaMatcher.start(), javaMatcher.end()));
        }
        return res;
    }

    public Iterator<Tuple2<Integer, Integer>> searchItr(String text) {
        javaMatcher = javaPattern.matcher(text);
        return new Iterator<Tuple2<Integer, Integer>>() {
            @Override
            public boolean hasNext() {
                return javaMatcher.find();
            }

            @Override
            public Tuple2<Integer, Integer> next() {
                return new Tuple2<Integer, Integer>(javaMatcher.start(), javaMatcher.end());
            }
        };
    }

    public String adjust(String pattern) {
        return pattern;
    }


    public static void main(String[] args) {
        Regexp regexp = new Regexp("\\\\d+");
        System.out.println(regexp.matches("\\ddd"));
    }
}


