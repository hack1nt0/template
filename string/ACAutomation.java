package template.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ACAutomation {

    class Node {
        char c;
        int type;// 1: end, 0: else
        int depth;
        Map<Character, Node> childs;
        Node fa, fail, lastPattern;
        int patternId;

        Node(int type, char c, Node fa) {
            this.type = type;
            this.depth = -1;
            this.c = c;
            this.childs = new HashMap<Character, Node>();
            this.fa = fa == null ? this : fa;
            this.fail = this;
            this.lastPattern = this;
        }

        public boolean isRoot() {
            return fa == this;
        }
    }

    Node root;

    public ACAutomation() {

    }

    public ACAutomation(List<String> ss) {
        root = new Node(0, '\0', root);
        for (int i = 0; i < ss.size(); ++i) insert(ss.get(i), i);
        Queue<Node> Q = new LinkedList<Node>();
        Q.add(root);
        while (!Q.isEmpty()) {
            Node cur = Q.poll();
            cur.depth = cur.fa.depth + 1;
            //fail
            if (cur == root || cur.fa == root) {
                cur.fail = root;
            } else {
                cur.fail = cur.fa.fail;
                while (cur.fail != root && !cur.fail.childs.containsKey(cur.c)) cur.fail = cur.fail.fail;
                if (cur.fail.childs.containsKey(cur.c)) cur.fail = cur.fail.childs.get(cur.c);
            }
            //lastPattern
            cur.lastPattern = cur.fail.type == 1 ? cur.fail : cur.fail.lastPattern;
            for (Node chd : cur.childs.values()) Q.add(chd);
        }
    }

    private void insert(String s, int index) {
        Node cur = root;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (!cur.childs.containsKey(c))
                cur.childs.put(c, new Node(0, c, cur));
            cur = cur.childs.get(c);
        }
        cur.type = 1;
        cur.patternId = index;
    }

    public List<int[]> endWith(String text, int endIndex) {
        List<int[]> temp=find(text);
        if(endIndex>=text.length()){
            endIndex=text.length()-1;
        }
        List<int[]> ret = new ArrayList<int[]>();
        for (int[] is : temp) {
            if(is[2]==endIndex){
                ret.add(is);
            }
        }
        return ret;
    }

    public List<int[]> startWith(String text, int startIndex) {
        Node cur = root;
        List<int[]> ret = new ArrayList<int[]>();
        for (int i = startIndex; i < text.length(); ++i) {
            if (!cur.childs.containsKey(text.charAt(i))) break;
            cur = cur.childs.get(text.charAt(i));
            if (cur.type == 1) ret.add(new int[]{cur.patternId, startIndex, startIndex + i});
        }
        return ret;
    }

    public List<int[]> match(String text, int startIndex,int endIndex) {
        List<int[]> ret = new ArrayList<int[]>();
        if (text.length() <= startIndex || startIndex < 0) {
            return ret;
        }

        Node cur = root;

        for (int i = startIndex; i <= endIndex; ++i) {
            char c = text.charAt(i);
            while (!cur.isRoot() && !cur.childs.containsKey(c)) cur = cur.fail;
            if (cur.childs.containsKey(c)) cur = cur.childs.get(c);
            else continue;
            for (Node lastPattern = cur; lastPattern != root; lastPattern = lastPattern.lastPattern) {
                if (lastPattern.type == 0) continue; // make sense of the cur node
                ret.add(new int[]{lastPattern.patternId, i - lastPattern.depth + 1, i});
            }
        }
        return ret;
    }

    public List<int[]> match(String text, int startIndex) {
        List<int[]> ret = new ArrayList<int[]>();
        if (text.length() <= startIndex || startIndex < 0) {
            return ret;
        }

        Node cur = root;

        for (int i = startIndex; i < text.length(); ++i) {
            char c = text.charAt(i);
            while (!cur.isRoot() && !cur.childs.containsKey(c)) cur = cur.fail;
            if (cur.childs.containsKey(c)) cur = cur.childs.get(c);
            else continue;
            for (Node lastPattern = cur; lastPattern != root; lastPattern = lastPattern.lastPattern) {
                if (lastPattern.type == 0) continue; // make sense of the cur node
                ret.add(new int[]{lastPattern.patternId, i - lastPattern.depth + 1, i});
            }
        }
        return ret;
    }

    //to left only the ones without both children and parent
    public List<int[]> filterNoOverlay(List<int[]> tri) {
        //if (patterns.capacity() == 1) return findSingle(text, patterns.get(0));
        List<int[]> res = new ArrayList<int[]>();
        if (tri.size() == 0) return res;

        Collections.sort(tri, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[1] != o2[1]) return o1[1] - o2[1];
                return o2[2] - o1[2];
            }
        });
        for (int i = 0; i < tri.size(); ++i) {
            if (0 < res.size() && tri.get(i)[1] <= res.get(res.size() - 1)[2])
                continue;
            res.add(tri.get(i));
        }
        return res;
    }

    //to left the left-most ones
    protected List<int[]> filterLeftMost(List<int[]> tri) {
        //if (patterns.capacity() == 1) return findSingle(text, patterns.get(0));
        List<int[]> res = new ArrayList<int[]>();
        if (tri.size() == 0) return res;

        for (int i = 0, LM = Integer.MAX_VALUE; i < tri.size(); ++i) {
            int curL = tri.get(i)[1];
            if (LM < curL) continue;
            if (curL < LM) res.clear();
            LM = Math.min(curL, LM);
            res.add(tri.get(i));
        }
        return res;
    }

    protected int[] filterLongest(List<int[]> tri) {
        if (tri.size() == 0) return null;
        int longestI = 0;
        for (int i = 0; i < tri.size(); ++i) {
            int curL = tri.get(i)[2] - tri.get(i)[1] + 1;
            int maxL = tri.get(longestI)[2] - tri.get(longestI)[1] + 1;
            if (curL <= maxL) continue;
            curL = maxL;
            longestI = i;
        }
        return tri.get(longestI);
    }


    public List<int[]> find(String text) {
        return filterNoOverlay(match(text, 0));
    }

    //跟原先的功能一样，但是是从index位置开始找
    public List<int[]> find(String text, int startIndex) {
        return filterNoOverlay(match(text, startIndex));
    }

    //跟原先的功能一样，但是是从index位置开始找
    public List<int[]> find(String text, int startIndex,int endIndex) {
        return filterNoOverlay(match(text, startIndex,endIndex));
    }

    //返回最早出现在词典中的词（最长。例如Dic={"ab","bc","bcd","cde"}；​findFirst("abcdefg",1)​返回{"bcd"}）
    public int[] findFirst(String text, int startIndex) {
        return filterLongest(findFirstAll(text, startIndex));
    }

    //返回最早出现在词典中的词（最长。例如Dic={"ab","bc","bcd","cde"}；​findFirst("abcdefg",1)​返回{"bcd"}）
    public int[] findFirst(String text, int startIndex,int endIndex) {
        return filterLongest(findFirstAll(text, startIndex,endIndex));
    }

    //返回最早出现在词典中的词（所有。例如Dic={"ab","bc","bcd","cde"}；findFirstAll("abcdefg",1)返回{"bc","bcd"}）
    public List<int[]> findFirstAll(String text, int startIndex) {//TODO
        List<int[]> ret = filterLeftMost(match(text, startIndex));
        Collections.sort(ret, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o2[2] - o1[2];
            }
        });
        return ret;
    }

    //返回最早出现在词典中的词（所有。例如Dic={"ab","bc","bcd","cde"}；findFirstAll("abcdefg",1)返回{"bc","bcd"}）
    public List<int[]> findFirstAll(String text, int startIndex,int endIndex) {//TODO
        List<int[]> ret = filterLeftMost(match(text, startIndex,endIndex));
        Collections.sort(ret, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o2[2] - o1[2];
            }
        });
        return ret;
    }

    //判断word是否是词典中的词（例如Dic={"ab","bc","bcd","cde"}；contain("abcdefg")返回false；contain("cde"​)返回true）
    public int contains(String word) {
        Node cur = root;
        for (int i = 0; i < word.length(); ++i) {
            char c = word.charAt(i);
            if (!cur.childs.containsKey(c)) return -1;
            cur = cur.childs.get(c);
        }
        return cur.type == 1 ? cur.patternId : -1;
    }

    public ACAutomation(String[] ss) {
        root = new Node(0, '\0', root);
        for (int i = 0; i < ss.length; ++i) insert(ss[i], i);
        Queue<Node> Q = new LinkedList<Node>();
        Q.add(root);
        while (!Q.isEmpty()) {
            Node cur = Q.poll();
            cur.depth = cur.fa.depth + 1;
            //fail
            if (cur == root || cur.fa == root) {
                cur.fail = root;
            } else {
                cur.fail = cur.fa.fail;
                while (cur.fail != root && !cur.fail.childs.containsKey(cur.c)) cur.fail = cur.fail.fail;
                if (cur.fail.childs.containsKey(cur.c)) cur.fail = cur.fail.childs.get(cur.c);
            }
            //lastPattern
            cur.lastPattern = cur.fail.type == 1 ? cur.fail : cur.fail.lastPattern;
            for (Node chd : cur.childs.values()) Q.add(chd);
        }
    }

    public int contains(String word,int startIndexInword,int endIndexInword){
        Node cur = root;
        for (int i =startIndexInword; i < word.length()&&i<endIndexInword; ++i) {
            char c = word.charAt(i);
            if (!cur.childs.containsKey(c)) return -1;
            cur = cur.childs.get(c);
        }
        return cur.type == 1 ? cur.patternId : -1;
    }
}
