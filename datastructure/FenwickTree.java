package datastructure;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * 長さ N の列に対して，一点更新および二項演算による区間畳み込みをそれぞれ O(log N) で行うデータ構造．別名 Binary Indexed Tree．
 * 一般的に定数倍が Segment Tree よりも軽い.
 * この実装では群であることを要求しているが，単位元は Segment Tree と同様 null でよしなにやってあげればよい．
 * 
 * verified:
 *  - https://judge.yosupo.jp/problem/point_add_range_sum
 * 
 * @author https://atcoder.jp/users/suisen
 * @param <T> Fenwick Tree に載せるデータの型
 */
@SuppressWarnings("unchecked")
public class FenwickTree<T> {

    /**
     * データを格納する配列．1-indexed．
     */
    final T[] Dat;

    /**
     * 単位元．
     */
    final T E;

    /**
     * 二項演算
     */
    final BinaryOperator<T> F;

    /**
     * 逆元を求める関数．即ち，F(t, Inv(t)) = F(Inv(t), t) = E なる Inv(t) を求める関数．
     */
    final UnaryOperator<T> Inv;

    /**
     * 配列のサイズ
     */
    final int N;

    /**
     * 単位元で列を初期化する．
     * @param n 配列のサイズ
     * @param e 単位元
     * @param f 二項演算
     * @param inverse 逆元を求める関数
     */
    public FenwickTree(int n, T e, BinaryOperator<T> f, UnaryOperator<T> inverse) {
        this.N = n;
        this.E = e;
        this.F = f;
        this.Inv = inverse;
        this.Dat = (T[]) new Object[N + 1];
        Arrays.fill(Dat, E);
    }
    
    /**
     * 配列で列を初期化する．
     * @param src 配列
     * @param e 単位元
     * @param f 二項演算
     * @param inverse 逆元を求める関数
     */
    public FenwickTree(T[] src, T e, BinaryOperator<T> f, UnaryOperator<T> inverse) {
        this(src.length, e, f, inverse);
        build(src);
    }

    /**
     * 配列による初期化処理の本体
     * @param src 配列
     */
    private void build(T[] src) {
        for (int i = 1; i <= N; i++) {
            Dat[i] = F.apply(Dat[i], src[i - 1]);
            int par = i + (-i & i);
            if (par <= N) Dat[par] = F.apply(Dat[par], Dat[i]);
        }
    }

    /**
     * 半開区間 [l, r) の畳み込みを O(log N) で計算する．
     * @param l 区間の左端 (含まれる)
     * @param r 区間の右端 (含まれない)
     * @return [l, r) の畳み込み
     */
    public T fold(int l, int r) {
        return F.apply(Inv.apply(fold(l)), fold(r));
    }

    /**
     * 半開区間 [0, r) の畳み込みを O(log N) で計算する．一般の [l, r) の計算よりも高速．
     * @param r 区間の右端 (含まれない)
     * @return [0, r) の畳み込み
     */
    public T fold(int r) {
        T res = E; 
        for (; r > 0; r -= -r & r) res = F.apply(Dat[r], res);
        return res;
    }

    /**
     * 一点取得．O(log N) で計算．
     * @param i index
     */
    public T get(int i) {
        return fold(i, i + 1);
    }

    /**
     * 列を {@code a} として，{@code a[i] = F(a[i], e)} に O(log N) で更新する．一般の update よりも高速．
     * @param i index
     * @param v {@code a[i]} に作用させる値
     */
    public void apply(int i, T e) {
        i++; 
        for (; i <= N; i += -i & i) Dat[i] = F.apply(Dat[i], e);
    }

    /**
     * 列を {@code a} として，{@code a[i] = g(a[i])} に O(log N) で更新する．
     * @param i index of a
     * @param v adding value
     */
    public void update(int i, UnaryOperator<T> g) {
        T v = get(i);
        apply(i, F.apply(g.apply(v), Inv.apply(v)));
    }

    /***************************** DEBUG *********************************/

    @Override
    public String toString() {
        int msb = 31 - Integer.numberOfLeadingZeros(N);
        char[][] c = new char[msb + 1][1 << (2 * msb + 1)];
        int[] idx = new int[msb + 1];
        for (int i = 1; i <= N; i++) {
            int j = Integer.numberOfTrailingZeros(i);
            char[] ic = Dat[i].toString().toCharArray();
            c[j][idx[j]] = '['; c[j][idx[j] + (1 << (j + 2)) - 1] = ']';
            int st = idx[j] + (1 << (j + 1)) - ic.length / 2 - 1;
            for (int l = 0; l < ic.length; l++) c[j][st + l] = ic[l];
            idx[j] += 1 << (j + 3);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = msb; i >= 0; i--) sb.append(c[i]).append('\n');
        return sb.toString();
    }

    /******* Usage *******/

    public static void main(String[] args) {
        Integer[] a = {0, 1, 2, 3, 4, 5};
        FenwickTree<Integer> t = new FenwickTree<>(a, 0, Integer::sum, e -> -e);
        System.out.println(t);
        System.out.printf("\nsum of [3, 5) = %d \n\n", t.fold(3, 5));
        t.update(2, e -> e * 2);
        System.out.println(t);
        System.out.printf("\nsum of [0, 4) = %d \n", t.fold(0, 4));
    }
}
