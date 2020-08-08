package integer;

/**
 * 剰余関連の演算をまとめたクラス．
 * 
 * verified:
 *  - https://atcoder.jp/contests/abc172/tasks/abc172_e (factorial, factorialInv, arrayInv)
 *  - https://atcoder.jp/contests/abc171/tasks/abc171_f (factorial, factorialInv, rangePower, arrayInv)
 *  - https://atcoder.jp/contests/arc067/submissions/me (rangeInv, arrayInv, pow, inv)
 * 
 * @author https://atcoder.jp/users/suisen
 */
public class ModArithmetic {

    /**
     * 剰余の法
     */
    final long MOD;

    /**
     * 剰余の法を指定して，指定された法の下で各種演算を行うインスタンスを生成
     * @param mod 剰余の法
     */
    public ModArithmetic(long mod) {
        this.MOD = mod;
    }

    /************** instance methods ***************/

    /**
     * MOD の getter method
     * @return MOD
     */
    public long getMod() {
        return MOD;
    }

    /**
     * [0, MOD) に含まれない可能性のある整数の剰余を計算する．
     * @param a [0, MOD) に含まれない可能性のある整数
     * @return 剰余
     */
    public long mod(long a) {
        a %= MOD;
        return a < 0 ? a + MOD : a;
    }

    /**
     * a + b の剰余を計算する．
     * {@code 0 <= a < MOD && 0 <= b < MOD && a + b <= Long.MAX_VALUE} を満たさなければならない．
     * @param a {@code 0 <= a < MOD && 0 <= b < MOD && a + b <= Long.MAX_VALUE} なる整数
     * @param b {@code 0 <= a < MOD && 0 <= b < MOD && a + b <= Long.MAX_VALUE} なる整数
     * @return a + b の剰余．条件を満たしていれば結果は 0 以上 MOD 未満となることが保証される．
     */
    public long add(long a, long b) {
        long c = a + b;
        return c >= MOD ? c - MOD : c;
    }

    /**
     * a - b の剰余を計算する．
     * {@code 0 <= a < MOD && 0 <= b < MOD && a - b >= Long.MIN_VALUE} を満たさなければならない．
     * @param a {@code 0 <= a < MOD && 0 <= b < MOD && a - b >= Long.MIN_VALUE} なる整数
     * @param b {@code 0 <= a < MOD && 0 <= b < MOD && a - b >= Long.MIN_VALUE} なる整数
     * @return a - b の剰余．条件を満たしていれば結果は 0 以上 MOD 未満となることが保証される．
     */
    public long sub(long a, long b) {
        long c = a - b;
        return c < 0 ? c + MOD : c;
    }
    
    /**
     * a * b の剰余を計算する．{@code 0 <= a * b <= Long.MAX_VALUE} を満たさなければならない．
     * @param a {@code 0 <= a * b <= Long.MAX_VALUE} なる整数
     * @param b {@code 0 <= a * b <= Long.MAX_VALUE} なる整数
     * @return a * b の剰余．条件を満たしていれば結果は 0 以上 MOD 未満となることが保証される．
     */
    public long mul(long a, long b) {
        long c = a * b;
        return c % MOD;
    }

    /**
     * a = x * b (mod MOD) && 0 <= x < MOD を満たす唯一の整数 x を計算する (x = a * b^(-1))．
     * {@code b} と {@code MOD} は互いに素でなければならない．
     * また，{@code 0 <= a * b^(-1) <= Long.MAX_VALUE} を満たさなければならない．
     * @param a {@code 0 <= a * b^(-1) <= Long.MAX_VALUE} なる整数
     * @param b {@code 0 <= a * b^(-1) <= Long.MAX_VALUE} かつ MOD と互いに素な整数
     * @return a = x * b (mod MOD) && 0 <= x < MOD を満たす唯一の整数 x．
     */
    public long div(long a, long b) {
        long c = a * inv(b, MOD);
        return c % MOD;
    }

    /**
     * a * x = 1 (mod MOD) && 0 <= x < MOD を満たす唯一の整数 x を計算する (x = a^(-1))．
     * {@code a} と {@code MOD} は互いに素でなければならない．
     * @param a mod MOD での乗法逆元を求めたい整数．MOD とは互いに素である必要がある
     * @return a * x = 1 (mod MOD) && 0 <= x < MOD を満たす唯一の整数 x．
     */
    public long inv(long a) {
        return inv(a, MOD);
    }

    /**
     * a ^ b (累乗) の剰余を計算する．O(log b)．
     * @param a 底
     * @param b 指数 (a と MOD が互いに素な場合は mod (MOD - 1) しておくと定数倍が高速になる)
     * @return a ^ b (累乗) の剰余．0 以上 MOD 未満が保証される．
     */
    public long pow(long a, long b) {
        return pow(a, b, MOD);
    }

    /**
     * add の可変長引数版．3 項以上の加算にはこれを用いると記述が楽．
     * ただし，パフォーマンスは悪くなるので注意．
     * @param vals 総和を取りたい列
     * @return 列の総和の剰余
     */
    public long sum(long... vals) {
        long ret = 0;
        for (long v : vals) {
            ret += v;
            if (ret >= MOD) ret -= MOD;
        }
        return ret;
    }

    /**
     * mul の可変長引数版．3 項以上の乗算にはこれを用いると記述が楽．
     * ただし，パフォーマンスは悪くなるので注意．
     * @param vals 総積を取りたい列
     * @return 列の総積の剰余
     */
    public long prod(long... vals) {
        long ret = 1;
        for (long v : vals) {
            ret = (ret * v) % MOD;
        }
        return ret;
    }

    /**
     * 1, 2, ..., n に対する mod MOD での乗法逆元を計算する．O(n)．
     * 配列の i 番目に i^(-1) の計算結果が入っている．0 番目は使わない．
     * @param n 最大値
     * @return 長さ {@code n + 1} の配列．i 番目に i^(-1) の計算結果が入っている．0 番目は使わない．
     */
    public long[] rangeInv(int n) {
        return rangeInv(n, MOD);
    }

    /**
     * 長さ N の配列 {@code a} の各要素に対する mod MOD での乗法逆元を計算する．O(N)．
     * 配列の i 番目に a[i]^(-1) の計算結果が入っている．但し，{@code a} の各要素は非零でなければならない．
     * @param a 逆元を求めたい配列．各要素は非零．
     * @return {@code a} の各要素に対する mod MOD での乗法逆元を格納した配列
     */
    public long[] arrayInv(long[] a) {
        return arrayInv(a, MOD);
    }

    /**
     * 0!, 1!, ..., n! の剰余を計算する．O(n)．
     * 配列の i 番目に i! の計算結果が入っている．
     * @param n 最大値
     * @return 長さ {@code n + 1} の配列．i 番目に i! の計算結果が入っている．
     */
    public long[] factorial(int n) {
        return factorial(n, MOD);
    }

    /**
     * 0!, 1!, ..., n! に対する mod MOD での乗法逆元を計算する．O(n)．
     * 配列の i 番目に i!^(-1) の計算結果が入っている．
     * @param n 最大値
     * @return 長さ {@code n + 1} の配列．i 番目に i!^(-1) の計算結果が入っている．
     */
    public long[] factorialInv(int n) {
        return factorialInv(n, MOD);
    }

    /**
     * a^0, a^1, ..., a^n に対する mod MOD での乗法逆元を計算する．O(n)．
     * 配列の i 番目に a^i の計算結果が入っている．
     * @param a 底
     * @param n 指数の最大値
     * @return 長さ {@code n + 1} の配列．i 番目に a^i の計算結果が入っている．
     */
    public long[] rangePower(long a, int n) {
        return rangePower(a, n, MOD);
    }

    /*************** static methods ****************/


    /**
     * [0, MOD) に含まれない可能性のある整数の剰余を計算する．
     * @param a [0, MOD) に含まれない可能性のある整数
     * @param mod MOD
     * @return 剰余
     */
    public static long mod(long a, long mod) {
        a %= mod;
        return a < 0 ? a + mod : a;
    }

    /**
     * a + b の剰余を計算する．
     * {@code 0 <= a < MOD && 0 <= b < MOD && a + b <= Long.MAX_VALUE} を満たさなければならない．
     * @param a {@code 0 <= a < MOD && 0 <= b < MOD && a + b <= Long.MAX_VALUE} なる整数
     * @param b {@code 0 <= a < MOD && 0 <= b < MOD && a + b <= Long.MAX_VALUE} なる整数
     * @param mod MOD
     * @return a + b の剰余．条件を満たしていれば結果は 0 以上 MOD 未満となることが保証される．
     */
    public static long add(long a, long b, long mod) {
        long c = a + b;
        return c >= mod ? c - mod : c;
    }

    /**
     * a - b の剰余を計算する．
     * {@code 0 <= a < MOD && 0 <= b < MOD && a - b >= Long.MIN_VALUE} を満たさなければならない．
     * @param a {@code 0 <= a < MOD && 0 <= b < MOD && a - b >= Long.MIN_VALUE} なる整数
     * @param b {@code 0 <= a < MOD && 0 <= b < MOD && a - b >= Long.MIN_VALUE} なる整数
     * @param mod MOD
     * @return a - b の剰余．条件を満たしていれば結果は 0 以上 MOD 未満となることが保証される．
     */
    public static long sub(long a, long b, long mod) {
        long c = a - b;
        return c < 0 ? c + mod : c;
    }

    /**
     * a * b の剰余を計算する．{@code 0 <= a * b <= Long.MAX_VALUE} を満たさなければならない．
     * @param a {@code 0 <= a * b <= Long.MAX_VALUE} なる整数
     * @param b {@code 0 <= a * b <= Long.MAX_VALUE} なる整数
     * @param mod MOD
     * @return a * b の剰余．条件を満たしていれば結果は 0 以上 MOD 未満となることが保証される．
     */
    public static long mul(long a, long b, long mod) {
        long c = a * b;
        return c % mod;
    }

    /**
     * a = x * b (mod MOD) && 0 <= x < MOD を満たす唯一の整数 x を計算する (x = a * b^(-1))．
     * {@code b} と {@code MOD} は互いに素でなければならない．
     * また，{@code 0 <= a * b^(-1) <= Long.MAX_VALUE} を満たさなければならない．
     * @param a {@code 0 <= a * b^(-1) <= Long.MAX_VALUE} なる整数
     * @param b {@code 0 <= a * b^(-1) <= Long.MAX_VALUE} かつ MOD と互いに素な整数
     * @param mod MOD
     * @return a = x * b (mod MOD) && 0 <= x < MOD を満たす唯一の整数 x．
     */
    public static long div(long a, long b, long mod) {
        long c = a * inv(b, mod);
        return c % mod;
    }

    /**
     * a * x = 1 (mod MOD) && 0 <= x < MOD を満たす唯一の整数 x を計算する (x = a^(-1))．
     * {@code a} と {@code MOD} は互いに素でなければならない．
     * @param a mod MOD での乗法逆元を求めたい整数．MOD とは互いに素である必要がある
     * @param mod MOD
     * @return a * x = 1 (mod MOD) && 0 <= x < MOD を満たす唯一の整数 x．
     */
    public static long inv(long a, long mod) {
        long b = mod;
        long u = 1, v = 0;
        while (b >= 1) {
            long t = a / b;
            a -= t * b;
            long tmp;
            tmp = a; a = b; b = tmp;
            u -= t * v;
            tmp = u; u = v; v = tmp;
        }
        u %= mod;
        return u < 0 ? u + mod : u;
    }

    /**
     * a ^ b (累乗) の剰余を計算する．O(log b)．
     * @param a 底
     * @param b 指数 (a と MOD が互いに素な場合は mod (MOD - 1) しておくと定数倍が高速になる)
     * @param mod MOD
     * @return a ^ b (累乗) の剰余．0 以上 MOD 未満が保証される．
     */
    public static long pow(long a, long b, long mod) {
        long pow = 1;
        for (long p = a, c = 1; b > 0;) { // p = a^c
            long lsb = b & -b;
            while (lsb != c) {
                c <<= 1;
                p = (p * p) % mod;
            }
            pow = (pow * p) % mod;
            b ^= lsb;
        }
        return pow;
    }

    /**
     * 1, 2, ..., n に対する mod MOD での乗法逆元を計算する．O(n)．
     * 配列の i 番目に i^(-1) の計算結果が入っている．0 番目は使わない．
     * @param n 最大値
     * @param mod MOD
     * @return 長さ {@code n + 1} の配列．i 番目に i^(-1) の計算結果が入っている．0 番目は使わない．
     */
    public static long[] rangeInv(int n, long mod) {
        long[] invs = new long[n + 1];
        invs[1] = 1;
        for (int i = 2; i <= n; i++) {
            long q = mod - mod / i;
            long r = invs[(int) (mod % i)];
            invs[i] = (q * r) % mod;
        }
        return invs;
    }

    /**
     * 長さ N の配列 {@code a} の各要素に対する mod MOD での乗法逆元を計算する．O(N)．
     * 配列の i 番目に a[i]^(-1) の計算結果が入っている．但し，{@code a} の各要素は非零でなければならない．
     * @param a 逆元を求めたい配列．各要素は非零．
     * @param mod MOD
     * @return {@code a} の各要素に対する mod MOD での乗法逆元を格納した配列
     */
    public static long[] arrayInv(long[] a, long mod) {
        int n = a.length;
        long[] dp = new long[n + 1];
        long[] pd = new long[n + 1];
        dp[0] = pd[n] = 1;
        for (int i = 0; i < n; i++) dp[i + 1] = (dp[i] * a[i    ]) % mod;
        for (int i = n; i > 0; i--) pd[i - 1] = (pd[i] * a[i - 1]) % mod;
        long inv = inv(dp[n], mod);
        long[] invs = new long[n];
        for (int i = 0; i < n; i++) {
            long lr = (dp[i] * pd[i + 1]) % mod;
            invs[i] = (lr * inv) % mod;
        }
        return invs;
    }

    /**
     * 0!, 1!, ..., n! の剰余を計算する．O(n)．
     * 配列の i 番目に i! の計算結果が入っている．
     * @param n 最大値
     * @param mod MOD
     * @return 長さ {@code n + 1} の配列．i 番目に i! の計算結果が入っている．
     */
    public static long[] factorial(int n, long mod) {
        long[] ret = new long[n + 1];
        ret[0] = 1;
        for (int i = 1; i <= n; i++) ret[i] = (ret[i - 1] * i) % mod;
        return ret;
    }

    /**
     * 0!, 1!, ..., n! に対する mod MOD での乗法逆元を計算する．O(n)．
     * 配列の i 番目に i!^(-1) の計算結果が入っている．
     * @param n 最大値
     * @param mod MOD
     * @return 長さ {@code n + 1} の配列．i 番目に i!^(-1) の計算結果が入っている．
     */
    public static long[] factorialInv(int n, long mod) {
        long facN = 1;
        for (int i = 2; i <= n; i++) facN = (facN * i) % mod;
        long[] invs = new long[n + 1];
        invs[n] = inv(facN, mod);
        for (int i = n; i > 0; i--) invs[i - 1] = (invs[i] * i) % mod;
        return invs;
    }

    /**
     * a^0, a^1, ..., a^n に対する mod MOD での乗法逆元を計算する．O(n)．
     * 配列の i 番目に a^i の計算結果が入っている．
     * @param a 底
     * @param n 指数の最大値
     * @param mod MOD
     * @return 長さ {@code n + 1} の配列．i 番目に a^i の計算結果が入っている．
     */
    public static long[] rangePower(long a, int n, long mod) {
        long[] pows = new long[n + 1];
        pows[0] = 1;
        for (int i = 1; i <= n; i++) pows[i] = (pows[i - 1] * a) % mod;
        return pows;
    }

    /******* Usage *******/

    public static void main(String[] args) {
        // using instance methods
        ModArithmetic ma = new ModArithmetic(998244353);
        long inv7 = ma.inv(7);
        System.out.println(ma.mul(7, inv7));
        // using static methods
        long inv7_2 = ModArithmetic.inv(7, 1000000007);
        System.out.println(ModArithmetic.mul(7, inv7_2, 1000000007));
    }
}