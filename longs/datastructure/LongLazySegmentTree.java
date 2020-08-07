package longs.datastructure;

import java.util.Arrays;
import java.util.function.LongBinaryOperator;

/**
 * TODO 一点更新を実装する
 *
 * セグ木の進化版．長さ N の列に対して，区間作用および区間畳み込みをそれぞれ O(log N) で行うデータ構造．
 * ただ，定数倍はセグ木よりも重いので通常のセグ木で済むのであれば通常のセグ木を使う．
 * 
 * verified:
 *  - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=DSL_2_F
 *  - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=DSL_2_G
 *  - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=DSL_2_H
 * 
 * @author https://atcoder.jp/users/suisen
 */
public class LongLazySegmentTree {

    /**
     * これは通常のセグ木と同じ
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
     * クエリ側の二項演算の単位元
     */
    final long E0;

    /**
     * 作用側の二項演算の単位元
     */
    final long E1;

    /**
     * 畳み込みに用いる二項演算．(クエリ側の関数)
     */
    final LongBinaryOperator F;

    /**
     * 作用素 (作用側の関数)
     */
    final LongBinaryOperator G;

    /**
     * 作用素をマージする関数 ( = 作用素を畳み込む関数)
     */
    final LongBinaryOperator H;

    /**
     * {@code G} の複数回適用をまとめて行う関数．
     * 
     * 長さ L の区間に対する操作が要素数に比例して変化する場合に，
     * {@code G} を L 回作用させる操作を {@code P} を 1 回作用させることで代替する．(説明が下手)
     * 
     * 例えば，区間加算更新 / 区間和取得 の場合，長さ L の区間 (この和を S とする) に x を足す際に
     * 愚直にやると S + x + x + ... + x のように L 回作用させる必要があるが，作用素は L * x に書き換えられる．
     * この，作用素を高速に求める関数が P: long × Integer → long (この場合は p(x, L) = L * x)．
     */
    final LongBinaryOperator P;

    /**
     * ボトムアップに区間を列挙する際にこのスタックに区間を積み，
     * トップダウンに遅延配列から伝播する際にこのスタックから区間を pop していく．
     */
    final int[] Stack = new int[64];

    /**
     * 配列を用いない初期化．O(N)
     * @param n 必要な列のサイズ
     * @param e0 クエリ側の二項演算 {@code f} の単位元
     * @param e1 作用素をマージする二項演算 {@code h} の単位元
     * @param f クエリ側の二項演算
     * @param g データに作用素を作用させる関数
     * @param h 作用素をマージする二項演算
     * @param p {@code g} の複数回適用をまとめて行う関数
     */
    public LongLazySegmentTree(int n, long e0, long e1, LongBinaryOperator f, LongBinaryOperator g, LongBinaryOperator h, LongBinaryOperator p) {
        this.E0 = e0;
        this.E1 = e1;
        this.F = f; this.G = g; this.H = h; this.P = p;
        int k = 1;
        while (k < n) k <<= 1;
        this.Dat = new long[k << 1];
        this.Laz = new long[k << 1];
        this.N = k;
        Arrays.fill(Dat, E0);
        Arrays.fill(Laz, E1);
    }

    /**
     * 配列を用いた初期化．O(N)
     * @param src 列の初期化に用いる配列
     * @param e0 クエリ側の二項演算 {@code f} の単位元
     * @param e1 作用素をマージする二項演算 {@code h} の単位元
     * @param f クエリ側の二項演算
     * @param g データに作用素を作用させる関数
     * @param h 作用素をマージする二項演算
     * @param p {@code g} の複数回適用をまとめて行う関数
     */
    public LongLazySegmentTree(long[] src, long e0, long e1, LongBinaryOperator f, LongBinaryOperator g, LongBinaryOperator h, LongBinaryOperator p) {
        this(src.length, e0, e1, f, g, h, p);
        build(src);
    }

    /**
     * 通常のセグ木と同様にして配列を元に O(N) で Dat を構築する．
     * @param src
     */
    private void build(long[] src) {
        System.arraycopy(src, 0, Dat, N, src.length);
        for (int i = N - 1; i > 0; i--) Dat[i] = F.applyAsLong(Dat[i << 1 | 0], Dat[i << 1 | 1]);
    }

    /**
     * 半開区間 [l, r) に作用素 v を作用させる．O(log N)
     * @param l 半開区間の左端 (含まれる)
     * @param r 半開区間の右端 (含まれない)
     * @param v 作用素
     */
    public void apply(int l, int r, long v) {
        if (l >= r) return;
        int m = updown(l, r);
        l += N; r += N;
        for (; l < r; l >>= 1, r >>= 1) {
            if ((l & 1) != 0) {Laz[l] = H.applyAsLong(Laz[l], v); l++;}
            if ((r & 1) != 0) {r--; Laz[r] = H.applyAsLong(Laz[r], v);}
        }
        for (int i = 0; i < m; i++) {
            int k = Stack[i];
            Dat[k] = F.applyAsLong(calcDat(k << 1 | 0), calcDat(k << 1 | 1));
        }
    }

    /**
     * 一点取得．O(log N)
     * @param i index (0-indexed)
     * @return i 番目の値
     */
    public long get(int i) {
        int k = 1;
        int l = 0, r = N;
        while (k < N) {
            int kl = k << 1 | 0;
            int kr = k << 1 | 1;
            Dat[k] = F.applyAsLong(calcDat(kl), calcDat(kr));
            int m = (l + r) >> 1;
            if (m > i) {r = m; k = kl;} 
            else {l = m; k = kr;}
        }
        return Dat[k];
    }

    /**
     * 半開区間 [l, r) の畳み込み．O(log N)
     * @param l 半開区間の左端 (含まれる)
     * @param r 半開区間の右端 (含まれない)
     * @return 畳み込みの結果
     */
    public long fold(int l, int r) {
        if (l >= r) return E0;
        updown(l, r);
        long resL = E0, resR = E0;
        for (l += N, r += N; l < r; l >>= 1, r >>= 1) {
            if ((l & 1) != 0) resL = F.applyAsLong(resL, calcDat(l++));
            if ((r & 1) != 0) resR = F.applyAsLong(calcDat(--r), resR);
        }
        return F.applyAsLong(resL, resR);
    }

    /**
     * [l, r) と共通部分を持つ区間をボトムアップに列挙 (up) してから，
     * トップダウンに列挙区間の遅延値を伝播する (down)
     * @param l 半開区間の左端 (含まれる)
     * @param r 半開区間の右端 (含まれない)
     * @return 列挙した区間の数
     */
    private int updown(int l, int r) {
        if (l >= r) return 0;
        int i = 0;
        int kl = l + N, kr = r + N;
        for (int x = kl / (kl & -kl) >> 1, y = kr / (kr & -kr) >> 1; 0 < kl && kl < kr; kl >>= 1, kr >>= 1) {
            if (kl <= x) Stack[i++] = kl;
            if (kr <= y) Stack[i++] = kr;
        }
        for (; kl > 0; kl >>= 1) Stack[i++] = kl;
        int m = i;
        while (i > 0) calcDat(Stack[--i]);
        return m;
    }

    /**
     * Dat[k] の値を計算し，遅延値を子に伝播する
     * @param k 木上の index (1-indexed)
     * @return Dat[k]
     */
    private long calcDat(int k) {
        long lz = Laz[k];
        if (lz != E1) {
            int w = N / Integer.highestOneBit(k);
            Dat[k] = G.applyAsLong(Dat[k], P.applyAsLong(lz, w));
            if (k < N) {
                int l = k << 1 | 0, r = k << 1 | 1;
                Laz[l] = H.applyAsLong(Laz[l], lz);
                Laz[r] = H.applyAsLong(Laz[r], lz);
            }
            Laz[k] = E1;
        }
        return Dat[k];
    }

    /***************************** DEBUG *********************************/

    @Override
    public String toString() {
        return toString(1, 0);
    }

    private String toString(int k, int space) {
        String s = "";
        if (k < N) s += toString(k << 1 | 1, space + 6) + "\n";
        s += " ".repeat(space) + Dat[k] + "/" + Laz[k];
        if (k < N) s += "\n" + toString(k << 1 | 0, space + 6);
        return s;
    }

    /******* Usage *******/
    
    public static void main(String[] args) {
        long[] a = {0, 1, 2, 3, 4, 5};
        LongLazySegmentTree t1 = new LongLazySegmentTree(
            a, 0l, 0l, Long::sum, Long::sum, Long::sum, (v, l) -> v * l
        ); // クエリ : 和 / 作用 : 加算
        System.out.println(t1);
        System.out.printf("\nsum of [3, 5) = %d \n\n", t1.fold(3, 5));
        t1.apply(0, 5, 10);
        System.out.println(t1);
        System.out.printf("\nsum of [0, 4) = %d \n\n", t1.fold(0, 4));

        LongLazySegmentTree t2 = new LongLazySegmentTree(
            a, Long.MAX_VALUE, 0, Long::min, Long::sum, Long::sum, (v, l) -> v
        ); // クエリ : 最小値 / 作用 : 加算
        System.out.println(t2);
        System.out.printf("\nmin of [3, 5) = %d \n\n", t2.fold(3, 5));
        t2.apply(0, 5, 10);
        System.out.println(t2);
        System.out.printf("\nmin of [0, 4) = %d \n", t2.fold(0, 4));
    }
}