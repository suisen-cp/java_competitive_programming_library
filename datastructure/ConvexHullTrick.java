package datastructure;

import longs.datastructure.LongOrderedMap;
import util.primitive.pair.LongObjEntry;

/**
 * 直線の集合 S={f_i=a_i*x+b_i|1<=i<=N} と座標 x に対して，min{f_i(x)|f_i∈S} を求めることが出来る．
 * 合計 N 本の直線を追加し，クエリに Q 回答えるとき，計算量は全体で O(NlogN+Q(logN)^2)．
 * 実装を上手くやれば O((N+Q)logN) に落ちそう．
 * 
 * verified:
 *  - https://atcoder.jp/contests/dp/tasks/dp_z
 * 
 * @author https://atcoder.jp/users/suisen
 */
public class ConvexHullTrick {

    /**
     * 直線を管理する平衡二分探索木．(傾き，切片) のペアを傾きの昇順で管理する．
     */
    final LongOrderedMap<Long> Lines = new LongOrderedMap<>();

    /**
     * {@code true} なら最小値クエリ．{@code false} なら最大値クエリ．
     */
    final boolean MinQuery;

    /**
     * コンストラクタ．最小値クエリか最大値クエリかの情報を渡す．
     * @param minQuery 最小値クエリなら {@code true}，最大値クエリなら {@code false}
     */
    public ConvexHullTrick(boolean minQuery) {
        this.MinQuery = minQuery;
    }

    /**
     * 直線 a*x+b を追加する．
     * @param a 傾き
     * @param b 切片
     */
    public void addLine(long a, long b) {
        if (!MinQuery) {
            a = -a; b = -b;
        }
        if (Lines.isEmpty()) {
            Lines.put(a, b);
        } else if (Lines.size() == 1) {
            if (Lines.getOrDefault(a, Long.MAX_VALUE) > b) Lines.put(a, b);
        } else {
            LongObjEntry<Long> hi = Lines.higherEntry(a);
            LongObjEntry<Long> lo = Lines.lowerEntry(a);
            long al = lo == null ? 0 : lo.getKey();
            long bl = lo == null ? 0 : lo.getValue();
            long am = a;
            long bm = b;
            long ar = hi == null ? 0 : hi.getKey();
            long br = hi == null ? 0 : hi.getValue();
            if (lo != null && hi != null && !isNecessary(al, bl, am, bm, ar, br)) {
                return;
            }
            Lines.put(a, Math.min(b, Lines.getOrDefault(a, Long.MAX_VALUE)));
            if (hi != null) {
                al = am;
                bl = bm;
                while (true) {
                    LongObjEntry<Long> md = hi;
                    am = md.getKey();
                    bm = md.getValue();
                    hi = Lines.higherEntry(md.getKey());
                    if (hi == null) break;
                    ar = hi.getKey();
                    br = hi.getValue();
                    if (isNecessary(al, bl, am, bm, ar, br)) break;
                    Lines.remove(am);
                }
            }
            if (lo != null) {
                ar = am; br = bm;
                while (true) {
                    LongObjEntry<Long> md = lo;
                    am = md.getKey();
                    bm = md.getValue();
                    lo = Lines.lowerEntry(md.getKey());
                    if (lo == null) break;
                    al = lo.getKey();
                    bl = lo.getValue();
                    if (isNecessary(al, bl, am, bm, ar, br)) break;
                    Lines.remove(am);
                }
            }
        }
    }

    /**
     * 3 直線 L_l=al*x+bl, L_m=am*x+bm, L_r=ar*x+br に関して，al < am < ar を満たすとする．
     * この時，L_m が最小値(/最大値)を求めるうえで必要かを判定する．
     * @param al L_l の傾き
     * @param bl L_l の切片
     * @param am L_m の傾き
     * @param bm L_m の切片
     * @param ar L_r の傾き
     * @param br L_r の切片
     * @return L_m が必要なら {@code true}，不必要なら {@code false}
     */
    boolean isNecessary(long al, long bl, long am, long bm, long ar, long br) {
        long l = (ar - am) * (bl - bm);
        long r = (am - al) * (bm - br);
        if (l == r) return false;
        return l > r;
    }

    /**
     * min{f(x)=a*x+b|f∈S} あるいは max{f(x)=a*x+b|f∈S} を求める．
     * @param x x 座標
     * @return 最小値クエリなら min{f(x)=a*x+b|f∈S}，最大値クエリなら max{f(x)=a*x+b|f∈S}
     */
    public long query(long x) {
        int l = 0, r = Lines.size();
        while (r - l > 1) {
            int m = (l + r) >> 1;
            if (apply(Lines.kthEntry(m - 1), x) >= apply(Lines.kthEntry(m), x)) {
                l = m;
            } else {
                r = m;
            }
        }
        long y = apply(Lines.kthEntry(l), x);
        return MinQuery ? y : -y;
    }

    /**
     * 関数を適用する
     * @param f 一次関数
     * @param x x 座標
     * @return f(x)
     */
    long apply(LongObjEntry<Long> f, long x) {
        return f.getKey() * x + f.getValue();
    }
}
