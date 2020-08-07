package ints.collection;

import java.util.NoSuchElementException;
import java.util.OptionalInt;

/**
 * java.util.PriorityQueue を簡易的に {@code int} 特化にしたクラス．
 * 二分ヒープで実装された優先度付きキューで，以下の操作を行うことが出来る．
 * 
 * 1. 最小要素の取得．O(1)
 * 2. 最小要素の削除．O(log N)
 * 3. 任意要素の追加．O(log N)
 * 
 * verified:
 *  - https://atcoder.jp/contests/arc098/tasks/arc098_c
 *  - https://atcoder.jp/contests/aising2020/tasks/aising2020_e
 *  - https://atcoder.jp/contests/abc167/tasks/abc167_f
 */
public class IntPriorityQueue {

    /**
     * {@code int} 型の比較器．{@code Comparator<Integer>} は boxing が走るので遅い．
     */
    @FunctionalInterface
    public static interface IntComparator {public int compare(int a, int b);}

    /**
     * コンストラクタで初期容量を指定しなかった場合の初期容量
     */
    static final int DEFAULT_CAPACITY = 1 << 6;

    /**
     * 二分ヒープは配列で表現する．1-indexed なので，i の左の子は 2*i，右の子は 2*i+1．
     */
    int[] que;

    /**
     * 比較器．昇順か降順で優先度を付ける場合は {@code null} でよい．
     */
    final IntComparator comparator;

    /**
     * 降順で優先度を付けるなら {@code true}．
     * 昇順で優先度をつけるなら {@code false}．
     * ただし，{@code comparator != null} の場合は {@code comparator} が優先される．
     */
    final boolean descending;

    /**
     * 要素数
     */
    int size = 0;

    /**
     * 昇順または降順で比較を行う場合かつ必要な容量が予想できる場合のコンストラクタ．
     * 初期容量を適切に設定することで要素のコピー回数を減らすことが出来る．
     * @param capacity 初期容量
     * @param descending 降順なら {@code true}，昇順なら {@code false} を与える．
     */
    public IntPriorityQueue(int capacity, boolean descending) {
        int k = 1;
        while (k < capacity) k <<= 1;
        this.que = new int[k];
        this.comparator = null;
        this.descending = descending;
        this.size = 0;
    }

    /**
     * 昇順で比較を行う場合かつ必要な容量が予想できる場合のコンストラクタ．
     * 初期容量を適切に設定することで要素のコピー回数を減らすことが出来る．
     * @param capacity 初期容量
     */
    public IntPriorityQueue(int capacity) {
        this(capacity, false);
    }

    /**
     * 昇順または降順で比較を行う場合のコンストラクタ．初期容量はデフォルト値を用いる．
     * @param descending 降順なら {@code true}，昇順なら {@code false} を与える．
     */
    public IntPriorityQueue(boolean descending) {
        this(DEFAULT_CAPACITY, descending);
    }

    /**
     * 昇順で比較を行う場合のコンストラクタ．初期容量はデフォルト値を用いる．
     */
    public IntPriorityQueue() {
        this(DEFAULT_CAPACITY, false);
    }

    /**
     * 比較器を用いた比較を行う場合かつ必要な容量が予想できる場合のコンストラクタ．
     * 初期容量を適切に設定することで要素のコピー回数を減らすことが出来る．
     * @param capacity 初期容量
     * @param comparator 比較器
     */
    public IntPriorityQueue(int capacity, IntComparator comparator) {
        int k = 1;
        while (k < capacity) k <<= 1;
        this.que = new int[k];
        this.comparator = comparator;
        this.descending = false;
        this.size = 0;
    }

    /**
     * 比較器を用いた比較を行う場合のコンストラクタ．初期容量はデフォルト値を用いる．
     * @param comparator 比較器
     */
    public IntPriorityQueue(IntComparator comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    /**
     * 優先度付きキューに要素を追加する．
     * @param e 追加する要素
     */
    public void add(int e) {
        if (++size == que.length) grow();
        if (comparator != null) {
            addUsingComparator(e);
        } else if (descending) {
            addDescending(e);
        } else {
            addAscending(e);
        }
    }

    /**
     * {@code que} 配列に要素を収めきれない場合に呼ばれ，容量を 2 倍に増やす．
     */
    void grow() {
        int[] newQue = new int[que.length << 1];
        System.arraycopy(que, 0, newQue, 0, que.length);
        que = newQue;
    }

    /**
     * 比較器を用いて要素を追加する．
     * @param e 追加する要素
     */
    void addUsingComparator(int e) {
        int i = size;
        while (i > 1) {
            int p = i >> 1;
            if (comparator.compare(e, que[p]) >= 0) break;
            que[i] = que[i = p];
        }
        que[i] = e;
    }

    /**
     * 降順のヒープになるよう要素を追加する．
     * @param e 追加する要素
     */
    void addDescending(int e) {
        int i = size;
        while (i > 1) {
            int p = i >> 1;
            if (e <= que[p]) break;
            que[i] = que[i = p];
        }
        que[i] = (int) e;
    }

    /**
     * 昇順のヒープになるよう要素を追加する．
     * @param e 追加する要素
     */
    void addAscending(int e) {
        int i = size;
        while (i > 1) {
            int p = i >> 1;
            if (e >= que[p]) break;
            que[i] = que[i = p];
        }
        que[i] = (int) e;
    }

    /**
     * 先頭要素を削除し，削除した値を {@code OptionalInt} で wrap した値を返す．但し，キューが空の場合は空の {@code OptionalInt} を返す．
     * @return 削除された先頭要素を {@code OptionalInt} で wrap した値．ただし，キューが空の場合は空の {@code OptionalInt}
     */
    public OptionalInt poll() {
        if (size == 0) return OptionalInt.empty();
        if (comparator != null) {
            return OptionalInt.of(pollUsingComparator());
        } else if (descending) {
            return OptionalInt.of(pollDescending());
        } else {
            return OptionalInt.of(pollAscending());
        }
    }

    /**
     * 先頭要素を削除し，削除した値を返す．但し，キューが空の場合例外を投げる．
     * @return 削除された先頭要素
     * @throws NoSuchElementException キューが空の場合
     */
    public int removeFirst() {
        if (size == 0) throw new NoSuchElementException();
        if (comparator != null) {
            return pollUsingComparator();
        } else if (descending) {
            return pollDescending();
        } else {
            return pollAscending();
        }
    }

    /**
     * 先頭要素を削除し，比較器を用いて二分ヒープの条件を回復する．
     * @return 削除した要素
     */
    int pollUsingComparator() {
        int ret = que[1];
        int e = que[size--];
        int i = 1;
        int h = size >> 1;
        while (i <= h) {
            int l = i << 1 | 0, r = i << 1 | 1;
            if (r <= size) {
                if (comparator.compare(que[l], que[r]) > 0) {
                    if (comparator.compare(e, que[r]) <= 0) break;
                    que[i] = que[i = r];
                } else {
                    if (comparator.compare(e, que[l]) <= 0) break;
                    que[i] = que[i = l];
                }
            } else {
                if (comparator.compare(e, que[l]) <= 0) break;
                que[i] = que[i = l];
            }
        }
        que[i] = e;
        return ret;
    }

    /**
     * 先頭要素を削除し，降順ヒープの条件を回復する．
     * @return 削除した要素
     */
    int pollDescending() {
        int ret = que[1];
        int e = que[size--];
        int i = 1;
        int h = size >> 1;
        while (i <= h) {
            int l = i << 1 | 0, r = i << 1 | 1;
            if (r <= size) {
                if (que[l] < que[r]) {
                    if (e >= que[r]) break;
                    que[i] = que[i = r];
                } else {
                    if (e >= que[l]) break;
                    que[i] = que[i = l];
                }
            } else {
                if (e >= que[l]) break;
                que[i] = que[i = l];
            }
        }
        que[i] = (int) e;
        return ret;
    }

    /**
     * 先頭要素を削除し，昇順ヒープの条件を回復する．
     * @return 削除した要素
     */
    int pollAscending() {
        int ret = que[1];
        int e = que[size--];
        int i = 1;
        int h = size >> 1;
        while (i <= h) {
            int l = i << 1 | 0, r = i << 1 | 1;
            if (r <= size) {
                if (que[l] > que[r]) {
                    if (e <= que[r]) break;
                    que[i] = que[i = r];
                } else {
                    if (e <= que[l]) break;
                    que[i] = que[i = l];
                }
            } else {
                if (e <= que[l]) break;
                que[i] = que[i = l];
            }
        }
        que[i] = (int) e;
        return ret;
    }

    /**
     * 先頭要素を削除せずに取得し，{@code OptionalInt} で wrap した値を返す．但し，キューが空の場合は空の {@code OptionalInt} を返す．
     * @return 先頭要素を{@code OptionalInt} で wrap した値．キューが空の場合は空の {@code OptionalInt}
     */
    public OptionalInt peek() {
        return size == 0 ? OptionalInt.empty() : OptionalInt.of(que[1]);
    }

    /**
     * 先頭要素を削除せずに取得する．キューが空であれば例外を投げる．
     * @return 先頭要素
     * @throws NoSuchElementException キューが空の場合
     */
    public int getFirst() {
        if (size == 0) throw new NoSuchElementException();
        return que[1];
    }

    /**
     * キューの要素数を返す
     * @return キューの要素数
     */
    public int size() {
        return size;
    }

    /**
     * キューが空であるかを判定する
     * @return キューが空なら {@code true}，そうでなければ {@code false}
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * キューの要素をすべて削除する
     */
    public void clear() {
        size = 0;
    }

    /***************************** DEBUG *********************************/

    @Override
    public String toString() {
        return toString(1, 0);
    }

    private String toString(int k, int space) {
        String s = "";
        if ((k << 1 | 1) <= size) s += toString(k << 1 | 1, space + 3) + "\n";
        s += " ".repeat(space) + que[k];
        if ((k << 1 | 0) <= size) s += "\n" + toString(k << 1 | 0, space + 3);
        return s;
    }

    /******* Usage *******/

    public static void main(String[] args) {
        IntPriorityQueue pq = new IntPriorityQueue(1, true);
        pq.add(  3); pq.add(  4); pq.add(  1); pq.add(- 1);
        pq.add( 10); pq.add( 14); pq.add( 30); pq.add(- 3);
        pq.add(-13); pq.add( 32); pq.add( 13); pq.add(  7);
        pq.add(- 7); pq.add( 12); pq.add(-29); pq.add(- 2);
        pq.add(  0); pq.add(  1); pq.add( 10);
        while (pq.size() > 0) {
            System.out.print(pq.poll());
            if (pq.size() > 0) {
                System.out.print(", ");
            } else {
                System.out.println();
            }
        }
    }
}