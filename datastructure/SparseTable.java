package datastructure;

import java.util.Arrays;
import java.util.function.BinaryOperator;

/**
 * 長さ N の「静的な」列に対して，「冪等律」および「結合律」を満たす二項演算による区間畳み込みを
 * 前計算 O(N*logN)，クエリ毎 O(1) で行うデータ構造．空間計算量は，前計算の結果を記憶するので O(N*logN)．
 * 
 * 単位元は本来要求されないが，区間が空の場合の返り値として便利なので単位元を要求する．
 * 単位元がない場合は null を使うなどしてよしなに二項演算を定義すると良い．
 * 
 * 冪等律および結合律が成り立つ演算は max, min, bitwise-and, bitwise-or, gcd, lcm など．
 * 加算乗算および bitwise-xor など，セグ木に載るが SparseTable には載らない演算も多いので注意．
 * 
 * verified:
 *  - https://judge.yosupo.jp/problem/staticrmq
 * 
 * @author https://atcoder.jp/users/suisen
 * @param <T> 載せるデータの型
 */
@SuppressWarnings("unchecked")
public class SparseTable<T> {

    /**
     * 列のサイズ
     */
    final int N;

    /**
     * 2^l > N を満たす最小の l
     */
    final int L;
    
    /**
     * 前計算を保持するテーブル．
     * Dat[l][i] は区間 [i, i+2^l) の区間畳み込みの結果を記憶する．
     */
    final T[][] Dat;

    /**
     * 「冪等律」および「結合律」を満たす二項演算
     */
    final BinaryOperator<T> F;

    /**
     * 二項演算の単位元
     */
    final T E;

    /**
     * FloorLog[i] := 2^k <= i を満たす最大の k
     */
    final int[] FloorLog;

    /**
     * 列と二項演算と単位元で初期化．二項演算は「冪等律」および「結合律」を満たさなければならない．
     * @param src クエリ対象の列
     * @param f 「冪等律」および「結合律」を満たす二項演算
     * @param e 二項演算の単位元
     */
    public SparseTable(T[] src, BinaryOperator<T> f, T e) {
        this.N = src.length;
        int l = 0;
        for (int m = 1; m <= N; m <<= 1) l++;
        this.L = l;
        this.Dat = (T[][]) new Object[L][];
        this.F = f;
        this.E = e;
        this.FloorLog = new int[N + 1];
        Arrays.setAll(FloorLog, i -> Integer.SIZE - Integer.numberOfLeadingZeros(i) - 1);
        build(src);
    }

    /**
     * [i, i+2^l) = [i, i+2^(l-1)) + [i+2^(l-1)), i+2^l) を利用してテーブルを構築する (結合律)．
     * @param src 列
     */
    private void build(T[] src) {
        Dat[0] = (T[]) new Object[N];
        System.arraycopy(src, 0, Dat[0], 0, N);
        for (int l = 1; l < L; l++) {
            final int B = 1 << (l - 1);
            Dat[l] = (T[]) new Object[N - (B << 1) + 1];
            for (int i = 0; i <= N - (B << 1); i++) {
                Dat[l][i] = F.apply(Dat[l - 1][i], Dat[l - 1][i + B]);
            }
        }
    }

    /**
     * 区間 [l, r) を畳み込む．半開区間に注意．区間が空であれば単位元を返す．
     * 
     * 2^k <= r-l を満たす k を求め，[l, l+2^k) と [r-2^k, r) の結果を併合する (結合律)．
     * このとき，この 2 つの区間は共通部分を持つので二項演算は冪等律を満たす必要がある．
     * 
     * @param l 区間の左端 (含まれる)
     * @param r 区間の左端 (含まれない)
     * @return 区間 [l, r) を畳み込んだ結果．r >= l なら単位元を返す．
     */
    public T fold(int l, int r) {
        if (r <= l) return E;
        int logWidth = FloorLog[r - l];
        return F.apply(Dat[logWidth][l], Dat[logWidth][r - (1 << logWidth)]);
    }

    /***************************** DEBUG *********************************/

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int l = 0; l < L; l++) {
            sb.append(String.format("Length = %2d: [", 1 << l));
            for (int i = 0; i < Dat[l].length; i++) {
                sb.append(String.format("%3d", Dat[l][i]));
                if (i < Dat[l].length - 1) sb.append(", ");
            }
            sb.append(']');
            if (l < L - 1) sb.append('\n');
        }
        return sb.toString();
    }

    /******* Usage *******/
    
    public static void main(String[] args) {
        Integer[] a = {2, 4, 6, -1, 43, 21, -4, 4};
        SparseTable<Integer> t = new SparseTable<>(a, Integer::min, Integer.MAX_VALUE);
        System.out.println(t);
        System.out.println();
        System.out.printf("min of [3, 5) = %d \n", t.fold(3, 5));
        System.out.printf("min of [0, 3) = %d \n", t.fold(0, 3));
        System.out.printf("min of [0, 8) = %d \n", t.fold(0, 8));
    }
}