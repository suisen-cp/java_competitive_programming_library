package datastructure;

import collection.Deque;

/**
 * 直線の集合 S={f_i=a_i*x+b_i|1<=i<=N} と座標 x に対して，min{f_i(x)|f_i∈S} を求めることが出来る．
 * 通常の CHT と異なるのは，追加する直線の傾きが単調であるという制約が課される点である．
 * この制約をうまく生かすことで，合計 N 本の直線を追加し，クエリに Q 回答えるとき，計算量を全体で O(N+QlogN) にできる．
 * (通常版よりも log が一つ落ちている)
 * 
 * verified:
 *  - https://atcoder.jp/contests/dp/tasks/dp_z
 * 
 * @author https://atcoder.jp/users/suisen
 */
public class MonotoneConvexHullTrick {

    /**
     * 単調性より，直線を Deque で管理してよい．これにより log を一つ落とすことが出来る．
     */
    final Deque<LinearFunction> Lines = new Deque<>();

    /**
     * {@code true} なら最小値クエリ．{@code false} なら最大値クエリ．
     */
    final boolean MinQuery;

    /**
     * 直線の傾きが広義単調増加であれば {@code true}，広義単調減少であれば {@code false}．
     */
    final boolean Increasing;

    /**
     * コンストラクタ．最小値クエリか最大値クエリかの情報と，直線の傾きの単調性の向き情報を渡す．
     * @param minQuery 最小値クエリなら {@code true}，最大値クエリなら {@code false}
     * @param increasing 直線の傾きが広義単調増加であれば {@code true}，広義単調減少であれば {@code false}
     */
    public MonotoneConvexHullTrick(boolean minQuery, boolean increasing) {
        this.MinQuery = minQuery;
        this.Increasing = minQuery ? increasing : !increasing;
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
        checkMonotonic(a, b);
        if (Increasing) {
            if (Lines.isEmpty()) {
                Lines.addLast(new LinearFunction(a, b));
            } else if (Lines.size() == 1) {
                LinearFunction f = Lines.getFirst();
                if (f.a == a) {
                    f.b = Math.min(f.b, b);
                } else {
                    Lines.addLast(new LinearFunction(a, b));
                }
            } else {
                LinearFunction add = new LinearFunction(a, b);
                LinearFunction last = Lines.getLast();
                if (last.a == a && last.b > b) {
                    last.b = b;
                    add = Lines.pollLast();
                }
                long ar = add.a;
                long br = add.b;
                while (Lines.size() > 1) {
                    int size = Lines.size();
                    long am = Lines.get(size - 1).a;
                    long bm = Lines.get(size - 1).b;
                    long al = Lines.get(size - 2).a;
                    long bl = Lines.get(size - 2).b;
                    if (isNecessary(al, bl, am, bm, ar, br)) break;
                    Lines.pollLast();
                }
                Lines.addLast(add);
            }
        } else {
            if (Lines.isEmpty()) {
                Lines.addLast(new LinearFunction(a, b));
            } else if (Lines.size() == 1) {
                LinearFunction f = Lines.getFirst();
                if (f.a == a) {
                    f.b = Math.min(f.b, b);
                } else {
                    Lines.addFirst(new LinearFunction(a, b));
                }
            } else {
                LinearFunction add = new LinearFunction(a, b);
                LinearFunction first = Lines.getFirst();
                if (first.a == a && first.b > b) {
                    first.b = b;
                    add = Lines.pollFirst();
                }
                long al = add.a;
                long bl = add.b;
                while (Lines.size() > 1) {
                    long am = Lines.get(0).a;
                    long bm = Lines.get(0).b;
                    long ar = Lines.get(1).a;
                    long br = Lines.get(1).b;
                    if (isNecessary(al, bl, am, bm, ar, br)) break;
                    Lines.pollFirst();
                }
                Lines.addFirst(add);
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
            if (Lines.get(m - 1).apply(x) >= Lines.get(m).apply(x)) {
                l = m;
            } else {
                r = m;
            }
        }
        long y = Lines.get(l).apply(x);
        return MinQuery ? y : -y;
    }

    /**
     * 単調性の条件に違反していないかをチェックする．
     * @param a 追加する直線の傾き
     * @param b 追加する直線の切片
     */
    void checkMonotonic(long a, long b) {
        if (Lines.isEmpty()) return;
        if (Increasing) {
            if (Lines.getLast().a > a) throw new IllegalArgumentException(
                "Lines are not monotonic."
            );
        } else {
            if (Lines.getFirst().a < a) throw new IllegalArgumentException(
                "Lines are not monotonic."
            );
        }
    }

    /**
     * 関数を適用する
     * @param f 一次関数
     * @param x x 座標
     * @return f(x)
     */
    static final class LinearFunction {
        long a, b;
        LinearFunction(long a, long b) {
            this.a = a;
            this.b = b;
        }
        long apply(long x) {
            return a * x + b;
        }
    }
}