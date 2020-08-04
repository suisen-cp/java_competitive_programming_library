package collection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * java.util.ArrayDeque<T> の簡易版．次の操作を行うことが出来る
 * 
 *  - 先頭/末尾の追加: amortized O(1)
 *  - 先頭/末尾の取得: O(1)
 *  - 先頭/末尾の削除: O(1)
 *  - ランダムアクセス: O(1) (ArrayDeque では O(N))
 * 
 * 実装は Ring Buffer による．
 * 
 * verified:
 *  - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=ITP2_1_B
 *  - https://atcoder.jp/contests/arc005/tasks/arc005_3
 * 
 * @author https://atcoder.jp/users/suisen
 * @param <T> Deque に格納するデータの型
 */
@SuppressWarnings("unchecked")
public class Deque<T> implements Iterable<T>, RandomAccess {

    /**
     * コンストラクタで初期容量を指定しなかった場合の初期容量
     */
    static final int DEFAULT_CAPACITY = 1 << 6;

    /**
     * Ring Buffer．剰余算を高速化するためにサイズは 2 冪になるようにする．
     */
    T[] buf;

    /**
     * buf のサイズ．
     */
    int len = 1;

    /**
     * 剰余算の代わりに行う論理積演算に用いる mask．
     */
    int mask;

    /**
     * Deque の先頭要素の index．
     * 0 <= head < len は保証されていないので，Deque には mask を通してアクセスする．
     */
    int head = 0;

    /**
     * Deque の末尾要素の index + 1．つまり，[head, tail) の半開区間に要素が入っている．
     * 0 <= tail-1 < len は保証されていないので，Deque には mask を通してアクセスする．
     */
    int tail = 0;

    /**
     * 初期容量を与えて初期化する．
     * 予め必要な容量が分かっている場合はその値を用いて初期化するとメモリ使用量が減る．
     * また，最大容量を与えた場合は追加操作が償却ではなく真に定数時間で行える．
     * @param capacity 初期容量
     */
    public Deque(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                String.format("Capacity %d is negative.", capacity)
            );
        }
        while (this.len < capacity) {
            this.len <<= 1;
        }
        this.mask = this.len - 1;
        this.buf = (T[]) new Object[len];
    }

    /**
     * 初期容量をデフォルト値 {@code DEFAULT_CAPACITY = 64} で初期化する．
     * 必要容量の見積もりがつかない場合はこれを使う．
     */
    public Deque() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Deque の末尾要素を取得する．O(1)
     * @return 末尾要素
     * @throws NoSuchElementException 要素数が 0 の場合
     */
    public T getLast() {
        if (size() == 0) throw new NoSuchElementException();
        return buf[(tail - 1) & mask];
    }

    /**
     * Deque の先頭要素を取得する．O(1)
     * @return 先頭要素
     * @throws NoSuchElementException 要素数が 0 の場合
     */
    public T getFirst() {
        if (size() == 0) throw new NoSuchElementException();
        return buf[head];
    }

    /**
     * Deque へのランダムアクセス．O(1)
     * @param index 先頭から何番目の要素を取得するか (0-indexed)
     * @return 先頭から {@code index} 番目の要素 (0-indexed)
     * @throws IndexOutOfBoundsException {@code index} が負であるか，または要素数以上である場合
     */
    public T get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(
                String.format("Index %d out of bounds for length %d.", index, size())
            );
        }
        return buf[(head + index) & mask];
    }

    /**
     * Deque の末尾に要素を追加する．amortized O(1)
     * @param v 追加する要素．{@code null} を許容する．
     */
    public void addLast(T v) {
        if (size() == len) grow();
        buf[tail++ & mask] = v;
    }

    /**
     * Deque の先頭に要素を追加する．amortized O(1)
     * @param v 追加する要素．{@code null} を許容する．
     */
    public void addFirst(T v) {
        if (size() == len) grow();
        buf[--head & mask] = v;
    }

    /**
     * Deque の末尾要素を削除する．O(1)
     * @return 削除された要素
     * @throws NoSuchElementException 要素数が 0 の場合
     */
    public T removeLast() {
        if (size() == 0) throw new NoSuchElementException();
        return buf[--tail & mask];
    }

    /**
     * Deque の先頭要素を削除する．O(1)
     * @return 削除された要素
     * @throws NoSuchElementException 要素数が 0 の場合
     */
    public T removeFirst() {
        if (size() == 0) throw new NoSuchElementException();
        return buf[head++ & mask];
    }

    /**
     * Deque の末尾要素を削除する．O(1)
     * @return 末尾要素が存在した場合は削除された要素を返し，存在しない場合は {@code null} を返す．
     */
    public T pollLast() {
        if (size() == 0) return null;
        return removeLast();
    }

    /**
     * Deque の先頭要素を削除する．O(1)
     * @return 先頭要素が存在した場合は削除された要素を返し，存在しない場合は {@code null} を返す．
     */
    public T pollFirst() {
        if (size() == 0) return null;
        return removeFirst();
    }

    /**
     * Deque の要素数を返す．O(1)
     * @return 要素数
     */
    public int size() {
        return tail - head;
    }

    /**
     * Deque が空であるかを判定する．O(1)
     * @return 空であれば {@code true}，そうでなければ {@code false}
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Deque の要素を全て削除する．容量は変化しない．
     */
    public void removeAll() {
        head = tail = 0;
    }

    /**
     * Deque の要素を全て削除し，容量を変更する．
     * @param capaacity 変更後の容量
     */
    public void clear(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                String.format("Capacity %d is negative.", capacity)
            );
        }
        head = tail = 0;
        this.len = 1;
        while (this.len < capacity) {
            this.len <<= 1;
        }
        this.mask = this.len - 1;
        this.buf = (T[]) new Object[len];
    }

    /**
     * Deque の要素を全て削除し，容量をデフォルトの容量 {@code DEFAULT_CAPACITY = 64} に変更する．
     */
    public void clear() {
        clear(DEFAULT_CAPACITY);
    }

    /**
     * Deque の要素を先頭から順に格納した配列を生成する．
     * @param clazz Deque に格納しているデータの {@code class}
     * @return Deque の要素を先頭から順に格納した配列
     */
    public T[] toArray(Class<T> clazz) {
        T[] ret = (T[]) Array.newInstance(clazz, size());
        Iterator<T> it = iterator();
        Arrays.setAll(ret, i -> it.next());
        return ret;
    }

    /**
     * Ring Buffer の容量を 2 倍にする．
     */
    private void grow() {
        T[] newBuf = (T[]) new Object[len << 1];
        head &= mask;
        tail &= mask;
        int len1 = len - head;
        int len2 = head;
        System.arraycopy(buf, head, newBuf, 0, len1);
        System.arraycopy(buf, 0, newBuf, len1, len2);
        this.head = 0;
        this.tail = this.len;
        this.len <<= 1;
        this.mask = this.len - 1;
        this.buf = newBuf;
    }

    /**
     * @return 先頭要素から末尾要素までの順方向イテレータ
     */
    public Iterator<T> iterator() {
        return new Iterator<T>(){
            int it = head;
            public boolean hasNext() {return it < tail;}
            public T next() {return buf[it++ & mask];}
        };
    }

    /**
     * @return 末尾要素から先頭要素までの逆方向イテレータ
     */
    public Iterator<T> descendingIterator() {
        return new Iterator<T>(){
            int it = tail;
            public boolean hasNext() {return it > head;}
            public T next() {return buf[--it & mask];}
        };
    }

    /***************************** DEBUG *********************************/

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            if (it.hasNext()) sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    /******* Usage *******/

    public static void main(String[] args) {
        Deque<Integer> dq = new Deque<>();
        dq.addLast(2);
        dq.addLast(3);
        dq.addLast(4);
        System.out.println(dq);
        dq.removeFirst();
        dq.removeLast();
        System.out.println(dq);
        dq.addFirst(1);
        dq.addFirst(0);
        dq.addFirst(-1);
        System.out.println(dq);
        dq.removeAll();
        System.out.println(dq);
        System.out.println(dq.pollFirst()); // => null
        // System.out.println(dq.removeFirst()); => NoSuchElementException
    }
}