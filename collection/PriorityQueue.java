package collection;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * java.util.PriorityQueue の簡易版．
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
 * 
 * @author https://atcoder.jp/users/suisen
 * @param <T> 優先度付きキューに格納するデータの型
 */
@SuppressWarnings("unchecked")
public class PriorityQueue<T> {

    /**
     * コンストラクタで初期容量を指定しなかった場合の初期容量
     */
    static final int DEFAULT_CAPACITY = 1 << 6;

    /**
     * 二分ヒープは配列で表現する．1-indexed なので，i の左の子は 2*i，右の子は 2*i+1．
     */
    T[] que;

    /**
     * 比較器．{@code T} が {@code Comparable<? super T>} 型であれば {@code null} でよい．
     */
    final Comparator<? super T> comparator;

    /**
     * 要素数
     */
    int size = 0;

    /**
     * 比較器を用いた比較を行う場合かつ必要な容量が予想できる場合のコンストラクタ．
     * 初期容量を適切に設定することで要素のコピー回数を減らすことが出来る．
     * @param capacity 初期容量
     * @param comparator 比較器
     */
    public PriorityQueue(int capacity, Comparator<? super T> comparator) {
        int k = 1;
        while (k < capacity) k <<= 1;
        this.que = (T[]) new Object[k];
        this.comparator = comparator;
        this.size = 0;
    }

    /**
     * 比較に比較器を用いない場合かつ必要な容量が予想できる場合のコンストラクタ．
     * {@code T} が {@code Comparable<? super T>} 型で無ければ実行時エラーとなるので注意．
     * 初期容量を適切に設定することで要素のコピー回数を減らすことが出来る．
     * @param capacity 初期容量
     */
    public PriorityQueue(int capacity) {
        this(capacity, null);
    }

    /**
     * 比較器を用いた比較を行う場合のコンストラクタ．初期容量はデフォルト値を用いる．
     * @param comparator 比較器
     */
    public PriorityQueue(Comparator<? super T> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    /**
     * 比較器を用いない場合のコンストラクタ．初期容量はデフォルト値を用いる．
     * {@code T} が {@code Comparable<? super T>} 型で無ければ実行時エラーとなるので注意．
     */
    public PriorityQueue() {
        this(DEFAULT_CAPACITY, null);
    }

    /**
     * 優先度付きキューに要素を追加する．
     * @param e 追加する要素
     */
    public void add(T e) {
        if (++size == que.length) grow();
        if (comparator != null) {
            addUsingComparator(e);
        } else {
            addComparable((Comparable<? super T>) e);
        }
    }

    /**
     * {@code que} 配列に要素を収めきれない場合に呼ばれ，容量を 2 倍に増やす．
     */
    void grow() {
        T[] newQue = (T[]) new Object[que.length << 1];
        System.arraycopy(que, 0, newQue, 0, que.length);
        que = newQue;
    }

    /**
     * 比較器を用いて要素を追加する．
     * @param e 追加する要素
     */
    void addUsingComparator(T e) {
        int i = size;
        while (i > 1) {
            int p = i >> 1;
            if (comparator.compare(e, que[p]) >= 0) break;
            que[i] = que[i = p];
        }
        que[i] = e;
    }

    /**
     * 比較器を用いないで要素を追加する．
     * @param e 追加する要素
     */
    void addComparable(Comparable<? super T> e) {
        int i = size;
        while (i > 1) {
            int p = i >> 1;
            if (e.compareTo(que[p]) >= 0) break;
            que[i] = que[i = p];
        }
        que[i] = (T) e;
    }

    /**
     * 先頭要素を削除し，削除した値を返す．但し，キューが空の場合は {@code null} を返す．
     * @return 削除された先頭要素．ただし，キューが空の場合は {@code null}
     */
    public T poll() {
        if (size == 0) return null;
        if (comparator != null) {
            return pollUsingComparator();
        } else {
            return pollComparable();
        }
    }

    /**
     * 先頭要素を削除し，削除した値を返す．但し，キューが空の場合例外を投げる．
     * @return 削除された先頭要素
     * @throws NoSuchElementException キューが空の場合
     */
    public T removeFirst() {
        if (size == 0) throw new NoSuchElementException();
        if (comparator != null) {
            return pollUsingComparator();
        } else {
            return pollComparable();
        }
    }

    /**
     * 先頭要素を削除し，比較器を用いて二分ヒープの条件を回復する．
     * @return 削除した要素
     */
    T pollUsingComparator() {
        T ret = que[1];
        T e = que[size--];
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
     * 先頭要素を削除し，比較器を用いずに二分ヒープの条件を満たすように復帰する．
     * @return 削除した要素
     */
    T pollComparable() {
        T ret = que[1];
        Comparable<? super T> e = (Comparable<? super T>) que[size--];
        int i = 1;
        int h = size >> 1;
        while (i <= h) {
            int l = i << 1 | 0, r = i << 1 | 1;
            if (r <= size) {
                if (((Comparable<? super T>) que[l]).compareTo(que[r]) > 0) {
                    if (e.compareTo(que[r]) <= 0) break;
                    que[i] = que[i = r];
                } else {
                    if (e.compareTo(que[l]) <= 0) break;
                    que[i] = que[i = l];
                }
            } else {
                if (e.compareTo(que[l]) <= 0) break;
                que[i] = que[i = l];
            }
        }
        que[i] = (T) e;
        return ret;
    }

    /**
     * 先頭要素を削除せずに取得する．キューが空であれば {@code null} を返す．
     * @return 先頭要素．キューが空の場合は {@code null}
     */
    public T peek() {
        return size == 0 ? null : que[1];
    }

    /**
     * 先頭要素を削除せずに取得する．キューが空であれば例外を投げる．
     * @return 先頭要素
     * @throws NoSuchElementException キューが空の場合
     */
    public T getFirst() {
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
        PriorityQueue<Integer> pq = new PriorityQueue<>(1);
        pq.add(  3); pq.add(  4); pq.add(  1); pq.add(- 1);
        pq.add( 10); pq.add( 14); pq.add( 30); pq.add(- 3);
        pq.add(-13); pq.add( 32); pq.add( 13); pq.add(  7);
        pq.add(- 7); pq.add( 12); pq.add(-29); pq.add(- 2);
        pq.add(  0); pq.add(  1); pq.add( 10);
        System.out.println(pq);
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