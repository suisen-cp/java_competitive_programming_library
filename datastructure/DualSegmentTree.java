package datastructure;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * 双対セグ木．長さ N の列に対して区間作用と一点取得がそれぞれ O(log N) で可能．
 * 遅延セグ木から不必要な処理を除いて簡略化したものなので，定数倍はこっちの方が軽い．
 * 
 * verified:
 *  - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=DSL_2_D
 *  - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=DSL_2_E
 * 
 * @param <T> データの型
 * @param <U> 作用素の型
 */
@SuppressWarnings("unchecked")
public class DualSegmentTree<T, U> {
    
    /**
     * データを格納する配列．一点取得のみなので，葉部分しか持たなくてよい．
     */
    final T[] Dat;

    /**
     * 遅延配列
     */
    final U[] Laz;

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
    final U E1;

    /**
     * 作用関数
     */
    final BiFunction<T, U, T> G;

    /**
     * 作用素をマージする二項演算
     */
    final BinaryOperator<U> H;

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
    public DualSegmentTree(int n, T initialValue, U e1, BiFunction<T, U, T> g, BinaryOperator<U> h) {
        this.E1 = e1;
        this.G = g;
        this.H = h;
        int k = 1;
        while (k < n) k <<= 1;
        this.N = k;
        this.L = n;
        this.Dat = (T[]) new Object[k];
        this.Laz = (U[]) new Object[k << 1];
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
    public DualSegmentTree(T[] src, U e1, BiFunction<T, U, T> g, BinaryOperator<U> h) {
        this.E1 = e1;
        this.G = g;
        this.H = h;
        int n = src.length;
        int k = 1;
        while (k < n) k <<= 1;
        this.N = k;
        this.L = n;
        this.Dat = (T[]) new Object[k];
        this.Laz = (U[]) new Object[k << 1];
        System.arraycopy(src, 0, Dat, 0, src.length);
        Arrays.fill(Laz, e1);
    }

    /**
     * 半開区間 [l, r) に作用素 v を作用させる．O(log N)
     * @param l 半開区間の左端 (含まれる)
     * @param r 半開区間の右端 (含まれない)
     * @param v 作用素
     */
    public void apply(int l, int r, U v) {
        if (l < 0 || l > L || r < 0 || r > L) {
            throw new IndexOutOfBoundsException(
                String.format("Segment [%d, %d) is not in [%d, %d)", l, r, 0, L)
            );
        }
        if (l >= r) return;
        updown(l, r);
        l += N; r += N;
        for (; l < r; l >>= 1, r >>= 1) {
            if ((l & 1) != 0) {Laz[l] = H.apply(Laz[l], v); l++;}
            if ((r & 1) != 0) {r--; Laz[r] = H.apply(Laz[r], v);}
        }
    }

    /**
     * 一点取得．O(log N)
     * @param i index (0-indexed)
     * @return i 番目の値
     */
    public T get(int i) {
        if (i < 0 || i >= L) {
            throw new IndexOutOfBoundsException(
                String.format("Index %d is not in [%d, %d)", i, 0, L)
            );
        }
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
        U lz = Laz[k];
        if (lz != E1) {
            if (k < N) {
                int l = k << 1 | 0, r = k << 1 | 1;
                Laz[l] = H.apply(Laz[l], lz);
                Laz[r] = H.apply(Laz[r], lz);
            } else {
                Dat[k - N] = G.apply(Dat[k - N], lz);
            }
            Laz[k] = E1;
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
        Integer[] a = {0, 1, 2, 3, 4, 5};
        DualSegmentTree<Integer, Integer> t1 = new DualSegmentTree<>(a, 0, (u, v) -> u + v, (u, v) -> u + v); // 区間加算クエリ
        System.out.println(t1);
        t1.apply(0, 3, 10);
        System.out.println(t1);
        System.out.printf("\na[2] = %d \n\n", t1.get(2));
        t1.apply(2, 4, 10);
        System.out.println(t1);
        System.out.printf("\na[2] = %d \n\n", t1.get(2));
    }
}