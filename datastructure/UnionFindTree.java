package datastructure;

import java.util.Arrays;

/**
 * 素集合に対して，次の二つの操作を amortized O(α(N)) で行うことが出来るデータ構造．
 * 
 *  1. unite(x, y): 元 x が属する集合と元 y が属する集合を merge する
 *  2. root(x): 元 x が属する集合の代表元を答える
 * 
 * ただし，α はアッカーマン関数の逆関数であり，オーダーとしては極めて小さいので実用上は無視してもおそらく問題ない．
 * 
 * verified:
 *  - https://judge.yosupo.jp/problem/unionfind
 * 
 * @author https://atcoder.jp/users/suisen
 */
public class UnionFindTree {

    /**
     * 各要素に関するデータをこの配列一つで管理する．
     * もし Dat[i] < 0 であれば，i　が属する集合の大きさは -Dat[i] であり，i が属する集合の代表 (root) は i である．
     * Dat[i] >= 0 であれば，i の親は Dat[i] である．
     */
    final int[] Dat;

    /**
     * 全部で n 要素ある素集合を管理する UnionFindTree を構築する．
     * @param n 要素の種類数
     */
    public UnionFindTree(int n) {
        this.Dat = new int[n];
        Arrays.fill(Dat, -1);
    }

    /**
     * 元 {@code x} が属する集合の代表を amortized O(α(N)) で答える．
     * @param x 代表を求めたい元
     * @return {@code x} が属する集合の代表
     */
    public int root(int x) {
        if (Dat[x] < 0) return x;
        Dat[x] = root(Dat[x]);
        return Dat[x];
    }

    /**
     * 元 {@code x} が属する集合と元 {@code y} が属する集合を amortized O(α(N)) で merge する．
     * @param x
     * @param y
     * @return 元々同じ集合に属していれば {@code false}，そうでなければ {@code true}
     */
    public boolean unite(int x, int y) {
        int xr = root(x);
        int yr = root(y);
        if (xr == yr) return false;
        if (Dat[xr] > Dat[yr]) {
            int tmp = xr; xr = yr; yr = tmp;
        }
        Dat[xr] += Dat[yr];
        Dat[yr] = xr;
        return true;
    }

    /**
     * 元 {@code x} と元 {@code y} が属する集合が同じであるかを amortized O(α(N)) で判定する
     * @param x
     * @param y
     * @return 同じ集合に属していれば {@code true}，そうでなければ {@code false}
     */
    public boolean isSame(int x, int y) {
        return root(x) == root(y);
    }

    /**
     * 元 {@code x} が属する集合のサイズを amortized O(α(N)) で求める．
     * @param x
     * @return 元 {@code x} が属する集合のサイズ
     */
    public int size(int x) {
        return -Dat[root(x)];
    }

    /***************************** DEBUG *********************************/

    @Override
    public String toString() {
        final int N = Dat.length;
        StringBuilder sb = new StringBuilder();
        boolean exi = false;
        for (int i = 0; i < N; i++) {
            if (Dat[i] < 0) {
                if (exi) sb.append('\n');
                sb.append(String.format("Size = %d Root = %d : ", -Dat[i], i));
                boolean exj = false;
                for (int j = 0; j < N; j++) {
                    if (Dat[j] == i) {
                        if (exj) sb.append(", ");
                        sb.append(j);
                        exj = true;
                    }
                }
                exi = true;
            }
        }
        return sb.toString();
    }

    /******* Usage *******/
    
    public static void main(String[] args) {
        UnionFindTree t = new UnionFindTree(7);
        System.out.println("Before");
        System.out.println(t);
        t.unite(2, 4);
        t.unite(1, 6);
        t.unite(2, 0);
        System.out.println("\nAfter");
        System.out.println(t);
        System.out.println();
        for (int i = 0; i < 7; i++) {
            System.out.printf("size(%d) = %d\n", i, t.size(i));
        }
    }
}