package integer;

import java.util.HashMap;

/**
 * 離散対数問題を解くクラス．a, b に対して a^x = b (mod MOD) を満たす最小の x を求める．
 * 
 * verified:
 *  - https://atcoder.jp/contests/arc042/tasks/arc042_d
 * 
 * @author https://atcoder.jp/users/suisen
 */
public class DiscreteLogarithm {

    /**
     * 底
     */
    final long a;

    /**
     * 剰余の法
     */
    final long MOD;

    /**
     * A = a^(-sqrt(MOD)) として，b*A^i を i に写す map．
     */
    final HashMap<Long, Long> map;

    public DiscreteLogarithm(long a, long MOD) {
        this.a = a;
        this.MOD = MOD;
        this.map = buildMap(a, MOD);
    }

    /**
     * a, b に対して a^x = b (mod MOD) を満たす最小の x を求める．ただし，a はインスタンス化の際に固定した底である．
     * 式を満たす x が存在しない場合は，負の値を返す．O(sqrt MOD)
     * @param b 真数
     * @return a^x = b (mod MOD) を満たす最小の x．そのような値が存在しない場合は負値を返す．
     */
    public long log(long b) {
        return log(a, b, map, MOD);
    }

    /**
     * a, b に対して a^x = b (mod MOD) を満たす最小の x を求める．式を満たす x が存在しない場合は，負の値を返す．
     * 定数 a に対して何度も問題を解く場合はインスタンス化すると高速になる．O(sqrt MOD)
     * @param a 底
     * @param b 真数
     * @return a^x = b (mod MOD) を満たす最小の x．そのような値が存在しない場合は負値を返す．
     */
    public static long log(long a, long b, long MOD) {
        return log(a, b, buildMap(a, MOD), MOD);
    }

    /**
     * 固定した底に対して map を構築する
     * @param a 固定した底
     * @param MOD 剰余の法
     * @return 構築した map
     */
    static HashMap<Long, Long> buildMap(long a, long MOD) {
        a %= MOD;
        long sq = (long) (Math.sqrt(MOD) + 2);
        HashMap<Long, Long> map = new HashMap<>();
        for (long i = 0, ar = 1; i < sq; i++) {
            map.putIfAbsent(ar, i);
            ar = (ar * a) % MOD;
        }
        return map;
    }

    /**
     * 構築した map を用いて問題を解く．O(sqrt MOD)
     * @param a 底
     * @param b 真数
     * @param map A = a^(-sqrt(MOD)) として，b*A^i を i に写す map．
     * @param MOD 剰余の法
     * @return a^x = b (mod MOD) を満たす最小の x．そのような値が存在しない場合は負値を返す．
     */
    static long log(long a, long b, HashMap<Long, Long> map, long MOD) {
        a %= MOD;
        b %= MOD;
        long sq = (long) (Math.sqrt(MOD) + 2);
        long inv = ModArithmetic.pow(ModArithmetic.inv(a, MOD), sq, MOD);
        for (long p = 0, r = b; p <= sq; p++) {
            if (map.containsKey(r)) {
                long res = p * sq + map.get(r);
                if (res > 0) return res;
            }
            r = (r * inv) % MOD;
        }
        return -1;
    }
}