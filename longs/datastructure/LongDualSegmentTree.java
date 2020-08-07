package longs.datastructure;

import java.util.Arrays;
import java.util.function.LongBinaryOperator;

/**
 * 双対セグ木．長さ N の列に対して区間作用と一点取得がそれぞれ O(log N) で可能．
 * 遅延セグ木から不必要な処理を除いて簡略化したものなので，定数倍はこっちの方が軽い．
 * 
 * verified:
 *  - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=DSL_2_D
 *  - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=DSL_2_E
 */
public class LongDualSegmentTree {
    
    /**
     * データを格納する配列．一点取得のみなので，葉部分しか持たなくてよい．
     */
    final long[] Dat;

    /**
     * 遅延配列
     */
    final long[] Laz;

    /**
     * 葉の個数
     */
    final int N;

    /**
     * 元々の列のサイズ．配列外参照を検知するために用いる．
     */
    final int L;

    /**
     * 作用素をマージする二項演算 H の単位元
     */
    final long E1;

    /**
     * 作用関数
     */
    final LongBinaryOperator G;

    /**
     * 作用素をマージする二項演算
     */
    final LongBinaryOperator H;

    /**
     * ボトムアップに区間を列挙する際にこのスタックに区間を積み，
     * トップダウンに遅延配列から伝播する際にこのスタックから区間を pop していく．
     */
    final int[] Stack = new int[64];
    
    /**
     * 配列を用いない初期化．O(N)
     * @param n 列の長さ
     * @param initialValue 列の初期値
     * @param e1 作用素をマージする二項演算の単位元
     * @param g 作用
     * @param h 作用素をマージする二項演算
     */
    public LongDualSegmentTree(int n, long initialValue, long e1, LongBinaryOperator g, LongBinaryOperator h) {
        this.E1 = e1;
        this.G = g;
        this.H = h;
        int k = 1;
        while (k < n) k <<= 1;
        this.N = k;
        this.L = n;
        this.Dat = new long[k];
        this.Laz = new long[k << 1];
        Arrays.fill(Dat, initialValue);
        Arrays.fill(Laz, e1);
    }

    /**
     * 配列を用いて初期化．O(N)
     * @param src 列 (初期データ)
     * @param e1 作用素をマージする二項演算の単位元
     * @param g 作用
     * @param h 作用素をマージする二項演算
     */
    public LongDualSegmentTree(long[] src, long e1, LongBinaryOperator g, LongBinaryOperator h) {
        this.E1 = e1;
        this.G = g;
        this.H = h;
        int n = src.length;
        int k = 1;
        while (k < n) k <<= 1;
        this.N = k;
        this.L = n;
        this.Dat = new long[k];
        this.Laz = new long[k << 1];
        System.arraycopy(src, 0, Dat, 0, src.length);
        Arrays.fill(Laz, e1);
    }

    /**
     * 半開区間 [l, r) に作用素 v を作用させる．O(log N)
     * @param l 半開区間の左端 (含まれる)
     * @param r 半開区間の右端 (含まれない)
     * @param v 作用素
     */
    public void apply(int l, int r, long v) {
        rangeCheck(l, r);
        if (l >= r) return;
        updown(l, r);
        l += N; r += N;
        for (; l < r; l >>= 1, r >>= 1) {
            if ((l & 1) != 0) {Laz[l] = H.applyAsLong(Laz[l], v); l++;}
            if ((r & 1) != 0) {r--; Laz[r] = H.applyAsLong(Laz[r], v);}
        }
    }

    /**
     * 一点取得．O(log N)
     * @param i index (0-indexed)
     * @return i 番目の値
     */
    public long get(int i) {
        rangeCheck(i);
        int k = 1;
        int l = 0, r = N;
        while (k < N) {
            propagate(k);
            int kl = k << 1 | 0;
            int kr = k << 1 | 1;
            int m = (l + r) >> 1;
            if (m > i) {r = m; k = kl;} 
            else {l = m; k = kr;}
        }
        propagate(k);
        return Dat[k - N];
    }

    /**
     * [l, r) と共通部分を持つ区間をボトムアップに列挙 (up) してから，
     * トップダウンに列挙区間の遅延値を伝播する (down)
     * @param l 半開区間の左端 (含まれる)
     * @param r 半開区間の右端 (含まれない)
     * @return 列挙した区間の数
     */
    void updown(int l, int r) {
        int i = 0;
        int kl = l + N, kr = r + N;
        for (int x = kl / (kl & -kl) >> 1, y = kr / (kr & -kr) >> 1; 0 < kl && kl < kr; kl >>= 1, kr >>= 1) {
            if (kl <= x) Stack[i++] = kl;
            if (kr <= y) Stack[i++] = kr;
        }
        for (; kl > 0; kl >>= 1) Stack[i++] = kl;
        while (i > 0) propagate(Stack[--i]);
    }

    /**
     * 遅延値を子に伝播する
     * @param k 木上の index (1-indexed)
     */
    void propagate(int k) {
        long lz = Laz[k];
        if (lz != E1) {
            if (k < N) {
                int l = k << 1 | 0, r = k << 1 | 1;
                Laz[l] = H.applyAsLong(Laz[l], lz);
                Laz[r] = H.applyAsLong(Laz[r], lz);
            } else {
                Dat[k - N] = G.applyAsLong(Dat[k - N], lz);
            }
            Laz[k] = E1;
        }
    }

    void rangeCheck(int i) {
        if (i < 0 || i >= L) {
            throw new IndexOutOfBoundsException(
                String.format("Index %d out of bounds for length %d", i, L)
            );
        }
    }

    void rangeCheck(int l, int r) {
        if (l < 0 || l > L || r < 0 || r > L) {
            throw new IndexOutOfBoundsException(
                String.format("Segment [%d, %d) is not in [%d, %d)", l, r, 0, L)
            );
        }
    }

    /***************************** DEBUG *********************************/

    @Override
    public String toString() {
        return toString(1, 0);
    }

    String toString(int k, int space) {
        String s = "";
        if (k < N) s += toString(k << 1 | 1, space + 6) + "\n";
        if (k < N) {
            s += " ".repeat(space) + Laz[k];
        } else {
            s += " ".repeat(space) + Dat[k - N] + "/" + Laz[k];
        }
        if (k < N) s += "\n" + toString(k << 1 | 0, space + 6);
        return s;
    }

    /******* Usage *******/
    
    public static void main(String[] args) {
        long[] a = {0, 1, 2, 3, 4, 5};
        LongDualSegmentTree t1 = new LongDualSegmentTree(a, 0, (u, v) -> u + v, (u, v) -> u + v); // 区間加算クエリ
        System.out.println(t1);
        t1.apply(0, 3, 10);
        System.out.println(t1);
        System.out.printf("\na[2] = %d \n\n", t1.get(2));
        t1.apply(2, 4, 10);
        System.out.println(t1);
        System.out.printf("\na[2] = %d \n\n", t1.get(2));
    }
}