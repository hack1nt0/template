package template.string;

import template.collection.sequence.ArrayUtils;
import template.collection.tuple.Tuple2;
import template.debug.Stopwatch;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dy on 17-1-13.
 *
 * Usage proposed: [start, end)
 */
public abstract class StringSearch {

    public abstract List<Tuple2<Integer, Integer>> exploit(String text);

    public Iterator<Tuple2<Integer, Integer>> exploitLazy(String text) {throw new UnsupportedOperationException();}

    public List<Tuple2<Integer, Integer>> search(String pattern) {throw new UnsupportedOperationException();}

    public Iterator<Tuple2<Integer, Integer>> searchLazy(String pattern) {throw new UnsupportedOperationException();}


}
