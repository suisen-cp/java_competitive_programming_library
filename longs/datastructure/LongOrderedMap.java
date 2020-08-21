package longs.datastructure;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.Set;

import util.MutablePair;
import util.primitive.pair.IntObjEntry;
import util.primitive.pair.LongObjEntry;

/**
 * Randomized Binary Search Tree (RBST) による平衡二分探索木の実装．
 * 要素の追加，削除，取得などの各種操作を要素数 N に対して expected O(logN) で行うことが出来る．
 * 標準ライブラリにおける平衡二分探索木は java.util.TreeMap として実装されているが，
 * k 番目の要素を取得したり，木を split したりはできないのでそのような操作が必要な場合に用いると良い．
 * 
 * verified:
 *  - https://atcoder.jp/contests/dp/tasks/dp_z
 * 
 * @author https://atcoder.jp/users/suisen
 */
public class LongOrderedMap<V> implements Iterable<LongObjEntry<V>> {

    /**
     * merge に用いる乱数生成器．速度がかなり重要になるので，java.util.Random 
     * を使わず XorShift による乱数生成器を実装している．
     */
    private static Random rnd = new Random();

    /**
     * 根ノード
     */
    RBST<V> root;

    /**
     * コンストラクタ．何も渡さなくてよい．
     */
    public LongOrderedMap() {}

    /**
     * クラス内部で用いるコンストラクタ．根ノードを与えて初期化する．
     * @param root 根ノード
     */
    private LongOrderedMap(RBST<V> root) {
        this.root = root;
    }

    /**
     * ∀x∈L，∀y∈R. x <= y を満たす二つの集合 L，R をマージする．
     * @param <V> Value の型
     * @param l 左側の木 (小さいほう)
     * @param r 右側の木 (大きいほう)
     * @return 二つの木を merge した木を返す
     */
    public static <V> LongOrderedMap<V> merge(LongOrderedMap<V> l, LongOrderedMap<V> r) {
        return l.mergeRight(r);
    }

    /**
     * ∀x∈L，∀y∈R. x <= y を満たす二つの集合 L，R をマージする．
     * この場合，引数の木が L に相当する．
     * @param l 左側の木 (小さいほう)
     * @return 自分に対して左側から木をマージし，その結果を返す．
     */
    public LongOrderedMap<V> mergeLeft(LongOrderedMap<V> l) {
        return new LongOrderedMap<>(RBST.merge(l.root, root));
    }

    /**
     * ∀x∈L，∀y∈R. x <= y を満たす二つの集合 L，R をマージする．
     * この場合，引数の木が R に相当する．
     * @param r 右側の木 (大きいほう)
     * @return 自分に対して右側から木をマージし，その結果を返す．
     */
    public LongOrderedMap<V> mergeRight(LongOrderedMap<V> r) {
        return new LongOrderedMap<>(RBST.merge(root, r.root));
    }

    /**
     * index を基準に L=[0, k)，R=[k, N) に木を分解する．
     * @param k 分解する境目となる index
     * @return 右側の木を切り離して返り値とする．(自分は左側の木となる)
     * @throws IndexOutOfBoundsException {@code k < 0 || k > N} の場合
     */
    public LongOrderedMap<V> splitRightUsingIndex(int k) {
        MutablePair<RBST<V>, RBST<V>> p = RBST.splitUsingIndex(root, k);
        LongOrderedMap<V> fst = new LongOrderedMap<>(p.getFirst());
        root = fst.root;
        LongOrderedMap<V> snd = new LongOrderedMap<>(p.getSecond());
        return snd;
    }

    /**
     * index を基準に L=[0, k)，R=[k, N) に木を分解する．
     * @param k 分解する境目となる index
     * @return 左側の木を切り離して返り値とする．(自分は右側の木となる)
     * @throws IndexOutOfBoundsException {@code k < 0 || k > N} の場合
     */
    public LongOrderedMap<V> splitLeftUsingIndex(int k) {
        MutablePair<RBST<V>, RBST<V>> p = RBST.splitUsingIndex(root, k);
        LongOrderedMap<V> fst = new LongOrderedMap<>(p.getFirst());
        LongOrderedMap<V> snd = new LongOrderedMap<>(p.getSecond());
        root = snd.root;
        return fst;
    }

    /**
     * key の値を基準に L=(-∞, key)，R=[key, +∞) に気を分解する．
     * @param key 分解する境目となる key の値
     * @return 右側の木を切り離して返り値とする．(自分は左側の木となる)
     */
    public LongOrderedMap<V> splitRightUsingKey(long key) {
        MutablePair<RBST<V>, RBST<V>> p = RBST.splitUsingKey(root, key);
        LongOrderedMap<V> fst = new LongOrderedMap<>(p.getFirst());
        root = fst.root;
        LongOrderedMap<V> snd = new LongOrderedMap<>(p.getSecond());
        return snd;
    }

    /**
     * key の値を基準に L=(-∞, key)，R=[key, +∞) に気を分解する．
     * @param key 分解する境目となる key の値
     * @return 左側の木を切り離して返り値とする．(自分は右側の木となる)
     */
    public LongOrderedMap<V> splitLeftUsingKey(long key) {
        MutablePair<RBST<V>, RBST<V>> p = RBST.splitUsingKey(root, key);
        LongOrderedMap<V> fst = new LongOrderedMap<>(p.getFirst());
        LongOrderedMap<V> snd = new LongOrderedMap<>(p.getSecond());
        root = snd.root;
        return fst;
    }

    /**
     * k 番目のエントリを取得する (0-indexed)．存在しない場合は {@code null} を返す．
     * @param k index
     * @return k 番目のエントリ．存在しない場合は {@code null}
     */
    public LongObjEntry<V> kthEntry(int k) {
        if (k < 0 || k >= size()) return null;
        return RBST.kthEntry(root, k);
    }

    /**
     * 0 番目のエントリを取得する．存在しない場合は {@code null} を返す．
     * @return 0 番目のエントリ．存在しない場合は {@code null}
     */
    public LongObjEntry<V> firstEntry() {
        return kthEntry(0);
    }

    /**
     * N-1 番目のエントリを取得する．存在しない場合は {@code null} を返す．
     * @return N-1 番目のエントリ．存在しない場合は {@code null}
     */
    public LongObjEntry<V> lastEntry() {
        return kthEntry(size() - 1);
    }

    /**
     * 与えられた key よりも真に小さなキーを持つエントリのうち，key の値が最大のエントリを返す．
     * 存在しない場合は {@code null} を返す．
     * @param key 基準となる key
     * @return 与えられた key よりも真に小さなキーを持つエントリのうち，key の値が最大のエントリ．存在しない場合は {@code null}
     */
    public LongObjEntry<V> lowerEntry(long key) {
        return kthEntry(RBST.ltCount(root, key) - 1);
    }

    /**
     * 与えられた key 以下のキーを持つエントリのうち，key の値が最大のエントリを返す．
     * 存在しない場合は {@code null} を返す．
     * @param key 基準となる key
     * @return 与えられた key 以下のキーを持つエントリのうち，key の値が最大のエントリ．存在しない場合は {@code null}
     */
    public LongObjEntry<V> floorEntry(long key) {
        return kthEntry(RBST.leqCount(root, key) - 1);
    }

    /**
     * 与えられた key よりも真に大きなキーを持つエントリのうち，key の値が最小のエントリを返す．
     * 存在しない場合は {@code null} を返す．
     * @param key 基準となる key
     * @return 与えられた key よりも真に大きなキーを持つエントリのうち，key の値が最小のエントリ．存在しない場合は {@code null}
     */
    public LongObjEntry<V> higherEntry(long key) {
        return kthEntry(RBST.leqCount(root, key));
    }

    /**
     * 与えられた key 以上のキーを持つエントリのうち，key の値が最小のエントリを返す．
     * 存在しない場合は {@code null} を返す．
     * @param key 基準となる key
     * @return 与えられた key 以上のキーを持つエントリのうち，key の値が最小のエントリ．存在しない場合は {@code null}
     */
    public LongObjEntry<V> ceilingEntry(long key) {
        return kthEntry(RBST.ltCount(root, key));
    }

    /**
     * 与えられた key に対応付けられた Value を返す．存在しない場合は {@code null} を返す．
     * @param key 探索キー
     * @return 与えられた key に対応付けられた Value．存在しない場合は {@code null} を返す．
     */
    public V get(long key) {
        return RBST.get(root, key);
    }

    /**
     * 与えられた key に対応付けられた Value を返す．存在しない場合は第二引数で与えられた値を返す．
     * @param key 探索キー
     * @param defaultValue 該当するエントリが見つからなかった場合の返り値
     * @return 与えられた key に対応付けられた Value．存在しない場合は第二引数で与えられた値．
     */
    public V getOrDefault(long key, V defaultValue) {
        V res = RBST.get(root, key);
        return res != null ? res : defaultValue;
    }

    /**
     * 与えられた key に第二引数の val を対応付ける．
     * 既に同じキーを持つエントリが登録されていた場合は，Value の値を書き換えて，元の値を返り値とする．
     * 登録されていなかった場合は，新たにエントリを挿入し，{@code null} を返す．
     * @param key 探索キー
     * @param val 対応付けられる値
     * @return 既にキーが存在していた場合は，元々の Value を返す．存在していなければ {@code null} を返す．
     */
    public V put(long key, V val) {
        if (RBST.contains(root, key)) {
            LongObjEntry<V> e = RBST.getEntry(root, key);
            V oldValue = e.getValue();
            e.setValue(val);
            return oldValue;
        }
        root = RBST.insert(root, key, val);
        return null;
    }

    /**
     * まだキーが登録されていない場合に限り，key に第二引数の値を対応付けて {@code null} を返す．
     * 既にキーが存在していた場合は書き換えず，既に登録されている値を返す．
     * @param key 探索キー
     * @param val キーが存在しない場合に限り，対応付けを行いたい値
     * @return 既にキーが存在していた場合は既に登録されている値．存在していなければ {@code null}
     */
    public V putIfAbsent(long key, V val) {
        LongObjEntry<V> e = RBST.getEntry(root, key);
        if (e != null) return e.getValue();
        put(key, val);
        return null;
    }

    /**
     * k 番目のエントリを削除する (0-indexed)．
     * 削除対象のエントリが存在していた場合は，そのエントリを返す．
     * 存在していなかった場合は，{@code null} を返す．
     * @param k index (0-indexed)
     * @return 削除したエントリ．削除するエントリが存在しなかった場合は {@code null}
     */
    public LongObjEntry<V> removeKthEntry(int k) {
        if (k < 0 || k >= size()) return null;
        MutablePair<RBST<V>, LongObjEntry<V>> nodeAndEntry = RBST.eraseUsingIndex(root, k);
        root = nodeAndEntry.getFirst();
        return nodeAndEntry.getSecond();
    }

    /**
     * 与えられた key を持つエントリを削除する．
     * 削除対象のエントリが存在していた場合は，そのエントリを返す．
     * 存在していなかった場合は，{@code null} を返す．
     * @param key 削除したいエントリのキー
     * @return 削除したエントリ．削除するエントリが存在しなかった場合は {@code null}
     */
    public LongObjEntry<V> remove(long key) {
        if (!containsKey(key)) return null;
        MutablePair<RBST<V>, LongObjEntry<V>> nodeAndEntry = RBST.eraseUsingKey(root, key);
        root = nodeAndEntry.getFirst();
        return nodeAndEntry.getSecond();
    }

    /**
     * 与えられた key を持つエントリが，第二引数の値に対応付けられている場合に限り，削除する．
     * 削除対象のエントリが存在していた場合は，そのエントリを返す．
     * 存在していなかった場合は，{@code null} を返す．
     * @param key 削除したいエントリのキー
     * @param value 削除したいエントリの値
     * @return 削除したエントリ．削除するエントリが存在しなかった場合は {@code null}
     */
    public boolean remove(long key, V value) {
        LongObjEntry<V> e = RBST.getEntry(root, key);
        if (e == null) return false;
        if (Objects.equals(value, e.getValue())) {
            MutablePair<RBST<V>, LongObjEntry<V>> nodeAndEntry = RBST.eraseUsingKey(root, key);
            root = nodeAndEntry.getFirst();
            return true;
        }
        return false;
    }

    /**
     * 与えられた key を持つエントリに対応付ける値を第二引数の値に変更し，書き換え前の値を返す．
     * ただし，与えられた key を持つエントリが存在しなかった場合は何もせず，{@code null} を返す．
     * @param key 値を書き換えたいエントリのキー
     * @param newValue 書き換える値
     * @return 該当のエントリが存在すれば，書き換え前の値．存在しなければ {@code null}
     */
    public V replace(long key, V newValue) {
        LongObjEntry<V> e = RBST.getEntry(root, key);
        if (e == null) return null;
        V oldValue = e.getValue();
        e.setValue(newValue);
        return oldValue;
    }

    /**
     * 与えられた key を持つエントリに対応付ける値が第二引数の値と等しい場合に限り，
     * 第三引数の値に書き換えて，書き換え前の値を返す．
     * 与えられた key を持つエントリが存在していても，値が一致しない場合は何もせず {@code null} を返す．
     * 元々与えられた key を持つエントリが存在しなかった場合も，何もせず {@code null} を返す．
     * @param key 値を書き換えたいエントリのキー
     * @param oldValue 該当のエントリの value がこれに一致した場合にのみ書き換えを行う．
     * @param newValue 書き換える値
     * @return 該当のエントリが存在すれば，書き換え前の値．存在しなければ {@code null}
     */
    public boolean replace(long key, V oldValue, V newValue) {
        LongObjEntry<V> e = RBST.getEntry(root, key);
        if (e == null) return false;
        V value = e.getValue();
        if (Objects.equals(value, oldValue)) {
            e.setValue(newValue);
            return true;
        }
        return false;
    }

    /**
     * 与えられた key を持つエントリが存在するかを判定する．
     * @param key 探索キー
     * @return 存在すれば {@code true}，存在しなければ {@code false}
     */
    public boolean containsKey(long key) {
        return RBST.contains(root, key);
    }

    /**
     * エントリ数を返す
     * @return エントリ数
     */
    public int size() {
        return RBST.size(root);
    }

    /**
     * 木が空か (=サイズが 0 であるか) を判定する．
     * @return 木が空であれば {@code true}，空でなければ {@code false}
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * 全てのエントリを削除する
     */
    public void clear() {
        root = null;
    }

    /**
     * 全てのエントリをキーの昇順に格納した Set を返す．
     * @return 全てのエントリをキーの昇順に格納した Set
     */
    public Set<LongObjEntry<V>> entrySet() {
        return RBST.entrySet(root);
    }

    /**
     * 全てのエントリをキーの降順に格納した Set を返す．
     * @return 全てのエントリをキーの降順に格納した Set
     */
    public Set<LongObjEntry<V>> descendingEntrySet() {
        return RBST.descendingEntrySet(root);
    }

    /**
     * 全てのエントリを昇順に走査するイテレータ．
     * @return 全てのエントリを昇順に走査するイテレータ
     */
    public Iterator<LongObjEntry<V>> iterator() {
        return RBST.iterator(root);
    }

    /**
     * 全てのエントリを降順に走査するイテレータ
     * @return 全てのエントリを降順に走査するイテレータ
     */
    public Iterator<LongObjEntry<V>> descendingIterator() {
        return RBST.descendingIterator(root);
    }

    /**
     * 全てのキーを昇順に走査するイテレータ．
     * @return 全てのキーを昇順に走査するイテレータ
     */
    public PrimitiveIterator.OfLong keyIterator() {
        return RBST.keyIterator(root);
    }

    /**
     * 全てのキーを降順に走査するイテレータ．
     * @return 全てのキーを降順に走査するイテレータ
     */
    public PrimitiveIterator.OfLong descendingKeyIterator() {
        return RBST.descendingKeyIterator(root);
    }

    /**
     * 全てのキーを昇順に格納した Set を返す．
     * @return 全てのキーを昇順に格納した Set
     */
    public Set<Long> keySet() {
        return RBST.keySet(root);
    }

    /**
     * 全てのキーを降順に格納した Set を返す．
     * @return 全てのキーを降順に格納した Set
     */
    public Set<Long> descendingKeySet() {
        return RBST.descendingKeySet(root);
    }

    /**
     * 全ての Value を，対応付けられていたキーの昇順に格納した Collection を返す
     * @return 全ての Value を，対応付けられていたキーの昇順に格納した Collection
     */
    public Collection<V> values() {
        return RBST.values(root);
    }

    /** Optional */

    /**
     * get の結果を Optional 型で包んだ結果を返す．
     * @param key 探索キー
     * @return get の結果を Optional 型で包んだ結果
     */
    public Optional<V> safeGet(long key) {
        V res = get(key);
        return res != null ? Optional.of(res) : Optional.empty();
    }

    /**
     * firstEntry の結果を Optional 型で包んだ結果を返す．
     * @return firstEntry の結果を Optional 型で包んだ結果
     */
    public Optional<LongObjEntry<V>> safeGetFirstEntry() {
        return size() > 0 ? Optional.of(kthEntry(0)) : Optional.empty();
    }

    /**
     * lastEntry の結果を Optional 型で包んだ結果を返す．
     * @return lastEntry の結果を Optional 型で包んだ結果
     */
    public Optional<LongObjEntry<V>> safeGetLastEntry() {
        return size() > 0 ? Optional.of(kthEntry(size() - 1)) : Optional.empty();
    }

    /**
     * lowerEntry の結果を Optional 型で包んだ結果を返す．
     * @param key 探索キー
     * @return lowerEntry の結果を Optional 型で包んだ結果
     */
    public Optional<LongObjEntry<V>> safeGetLowerEntry(long key) {
        int k = RBST.ltCount(root, key) - 1;
        return k >= 0 ? Optional.of(kthEntry(k)) : Optional.empty();
    }

    /**
     * floorEntry の結果を Optional 型で包んだ結果を返す．
     * @param key 探索キー
     * @return floorEntry の結果を Optional 型で包んだ結果
     */
    public Optional<LongObjEntry<V>> safeGetFloorEntry(long key) {
        int k = RBST.leqCount(root, key) - 1;
        return k >= 0 ? Optional.of(kthEntry(k)) : Optional.empty();
    }

    /**
     * higherEntry の結果を Optional 型で包んだ結果を返す．
     * @param key 探索キー
     * @return higherEntry の結果を Optional 型で包んだ結果
     */
    public Optional<LongObjEntry<V>> safeGetHigherEntry(long key) {
        int k = RBST.leqCount(root, key);
        return k < size() ? Optional.of(kthEntry(k)) : Optional.empty();
    }

    /**
     * ceilingEntry の結果を Optional 型で包んだ結果を返す．
     * @param key 探索キー
     * @return ceilingEntry の結果を Optional 型で包んだ結果
     */
    public Optional<LongObjEntry<V>> safeGetCeilingEntry(long key) {
        int k = RBST.ltCount(root, key);
        return k < size() ? Optional.of(kthEntry(k)) : Optional.empty();
    }

    /**
     * Randomized Binary Search Tree (RBST) の実装
     * @param <V> Value の型
     */
    static final class RBST<V> extends LongObjEntry<V> {

        /**
         * 左右の子ノード
         */
        private RBST<V> l, r;

        /**
         * 自らを根とする部分木のサイズ
         */
        private int size;

        /**
         * コンストラクタ．key と val を与えて初期化する．
         * @param key 探索に用いるキー
         * @param val キーに対応付ける値
         */
        private RBST(long key, V val) {
            super(key, val);
            this.size = 1;
        }

        /**
         * 左右の子が更新されたときに部分木のサイズを更新する関数
         * @return 更新後の自ノード
         */
        private RBST<V> update() {
            size = size(l) + size(r) + 1;
            return this;
        }

        /** 以下，Node が null である可能性を踏まえて全て static メソッドとして実装している */

        /**
         * 与えられたキーを持つエントリを取得する
         * @param <V> Value の型
         * @param t 探索する部分木の根
         * @param key 探索キー
         * @return 与えられたキーを持つエントリ．存在しなければ {@code null}
         */
        static <V> LongObjEntry<V> getEntry(RBST<V> t, long key) {
            while (t != null) {
                if (t.getKeyAsLong() == key) return t;
                t = t.getKeyAsLong() < key ? t.r : t.l;
            }
            return null;
        }

        /**
         * 与えられたキーを持つエントリの Value を取得する
         * @param <V> Value の型
         * @param t 探索する部分木の根
         * @param key 探索キー
         * @return 与えられたキーを持つエントリの Value．存在しなければ {@code null}
         */
        static <V> V get(RBST<V> t, long key) {
            while (t != null) {
                if (t.getKeyAsLong() == key) return t.getValue();
                t = t.getKeyAsLong() < key ? t.r : t.l;
            }
            return null;
        }

        /**
         * k 番目 (0-indexed) のエントリを取得
         * @param <V> Value の型
         * @param t 探索する部分木の根
         * @param k index (0-indexed)
         * @return k 番目 (0-indexed) のエントリ．存在しなければ {@code null}
         */
        static <V> LongObjEntry<V> kthEntry(RBST<V> t, int k) {
            int c = size(t.l);
            if (k < c) return kthEntry(t.l, k);
            if (k == c) return t;
            return kthEntry(t.r, k - c - 1);
        }

        /**
         * 与えられた key 以下の値を key として持つエントリの個数を数える．
         * @param <V> Value の型
         * @param t 探索する部分木の根
         * @param key キーの境界値
         * @return 与えられた key 以下の値を key として持つエントリの個数
         */
        static <V> int leqCount(RBST<V> t, long key) {
            if (t == null) return 0;
            if (key < t.getKeyAsLong()) return leqCount(t.l, key);
            return leqCount(t.r, key) + size(t.l) + 1;
        }

        /**
         * 与えられた key より真に小さい値を key として持つエントリの個数を数える．
         * @param <V> Value の型
         * @param t 探索する部分木の根
         * @param key キーの境界値
         * @return 与えられた key より真に小さい値を key として持つエントリの個数
         */
        static <V> int ltCount(RBST<V> t, long key) {
            if (t == null) return 0;
            if (key <= t.getKeyAsLong()) return ltCount(t.l, key);
            return ltCount(t.r, key) + size(t.l) + 1;
        }

        /**
         * 二本の木 L，R を merge する．但し，∀x∈L，∀y∈R. x <= y を満たす．
         * @param <V> Value の型
         * @param l 左の木 (小さいほう)
         * @param r 右の木 (大きいほう)
         * @return 二本の木 L，R を merge してできる木
         */
        static <V> RBST<V> merge(RBST<V> l, RBST<V> r) {
            if (l == null) return r;
            if (r == null) return l;
            if (rnd.nextInt() % (l.size + r.size) < l.size) {
                l.r = merge(l.r, r);
                return l.update();
            } else {
                r.l = merge(l, r.l);
                return r.update();
            }
        }

        /**
         * index を基準に L=[0, k)，R=[k, N) の二本の木に分解する．
         * @param <V> Value の型
         * @param x 分解する木
         * @param k index (0-indexed)
         * @return 分解してできる二本の木のペア
         * @throws IndexOutOfBoundsException {@code k < 0 || k > size(x)} の場合
         */
        static <V> MutablePair<RBST<V>, RBST<V>> splitUsingIndex(RBST<V> x, int k) {
            if (k < 0 || k > size(x)) {
                throw new IndexOutOfBoundsException(
                    String.format("index %d is out of bounds for the length of %d", k, size(x))
                );
            }
            if (x == null) {
                return new MutablePair<RBST<V>, RBST<V>>(null, null);
            } else if (k <= size(x.l)) {
                MutablePair<RBST<V>, RBST<V>> p = splitUsingIndex(x.l, k);
                x.l = p.getSecond();
                p.setSecond(x.update());
                return p;
            } else {
                MutablePair<RBST<V>, RBST<V>> p = splitUsingIndex(x.r, k - size(x.l) - 1);
                x.r = p.getFirst();
                p.setFirst(x.update());
                return p;
            }
        }

        /**
         * key を基準に L=(-∞, key)，R=[k, +∞) の二本の木に分解する．
         * @param <V> Value の型
         * @param x 分解する木
         * @param key 境界となるキー
         * @return 分解してできる二本の木のペア
         */
        static <V> MutablePair<RBST<V>, RBST<V>> splitUsingKey(RBST<V> x, long key) {
            if (x == null) {
                return new MutablePair<RBST<V>, RBST<V>>(null, null);
            } else if (key <= x.getKeyAsLong()) {
                MutablePair<RBST<V>, RBST<V>> p = splitUsingKey(x.l, key);
                x.l = p.getSecond();
                p.setSecond(x.update());
                return p;
            } else {
                MutablePair<RBST<V>, RBST<V>> p = splitUsingKey(x.r, key);
                x.r = p.getFirst();
                p.setFirst(x.update());
                return p;
            }
        }

        /**
         * 木にエントリを挿入する．
         * @param <V> Value の型
         * @param t 挿入される木
         * @param key 挿入するエントリのキー
         * @param val 挿入するエントリの値
         * @return 挿入して更新された {@code t}
         */
        static <V> RBST<V> insert(RBST<V> t, long key, V val) {
            MutablePair<RBST<V>, RBST<V>> p = splitUsingKey(t, key);
            return RBST.merge(RBST.merge(p.getFirst(), new RBST<>(key, val)), p.getSecond());
        }

        /**
         * k 番目のエントリを削除する (0-indexed)．
         * @param <V> Value の型
         * @param t エントリを削除する木
         * @param k index (0-indexed)
         * @return 削除して更新された {@code t}
         * @throws IndexOutOfBoundsException {@code k < 0 || k > size(t)} の場合
         */
        static <V> MutablePair<RBST<V>, LongObjEntry<V>> eraseUsingIndex(RBST<V> t, int k) {
            MutablePair<RBST<V>, RBST<V>> p = splitUsingIndex(t, k);
            MutablePair<RBST<V>, RBST<V>> q = splitUsingIndex(p.getSecond(), 1);
            return new MutablePair<>(RBST.merge(p.getFirst(), q.getSecond()), q.getFirst());
        }

        /**
         * 与えられた key を持つエントリを削除する．
         * @param <V> Value の型
         * @param t エントリを削除する木
         * @param key 削除するエントリのキー
         * @return 削除して更新された {@code t}
         */
        static <V> MutablePair<RBST<V>, LongObjEntry<V>> eraseUsingKey(RBST<V> t, long key) {
            MutablePair<RBST<V>, RBST<V>> p = splitUsingKey(t, key);
            MutablePair<RBST<V>, RBST<V>> q = splitUsingIndex(p.getSecond(), 1);
            return new MutablePair<>(RBST.merge(p.getFirst(), q.getSecond()), q.getFirst());
        }

        /**
         * 与えられた key を持つエントリが存在するかを判定する
         * @param <V> Value の型
         * @param t 探索対象の木
         * @param key 探索キー
         * @return 与えられた key を持つエントリが存在するなら {@code true}，存在しないなら {@code false}
         */
        static <V> boolean contains(RBST<V> t, long key) {
            while (t != null) {
                if (t.getKeyAsLong() == key) return true;
                else if (t.getKeyAsLong() < key) t = t.r;
                else t = t.l;
            }
            return false;
        }

        /**
         * 部分木のサイズを返す
         * @param <V> Value の型
         * @param nd 部分木
         * @return 部分木のサイズ
         */
        static <V> int size(RBST<V> nd) {
            return nd == null ? 0 : nd.size;
        }

        /**
         * キーが昇順となるようにエントリを列挙する (= DFS の inorder)．
         * @param <V> Value の型
         * @param t 部分木
         * @return 部分木に含まれる全てのエントリをキーの昇順に格納した Set．
         */
        static <V> Set<LongObjEntry<V>> entrySet(RBST<V> t) {
            LinkedHashSet<LongObjEntry<V>> set = new LinkedHashSet<>();
            if (t == null) return set;
            ArrayDeque<IntObjEntry<RBST<V>>> stack = new ArrayDeque<>();
            if (t.r != null) stack.addLast(new IntObjEntry<>(0, t.r));
            stack.addLast(new IntObjEntry<>(1, t));
            if (t.l != null) stack.addLast(new IntObjEntry<>(0, t.l));
            while (stack.size() > 0) {
                IntObjEntry<RBST<V>> p = stack.pollLast();
                RBST<V> u = p.getValue();
                if (p.getKeyAsInt() == 1) {
                    set.add(u);
                } else {
                    if (u.r != null) stack.addLast(new IntObjEntry<>(0, u.r));
                    stack.addLast(new IntObjEntry<>(1, u));
                    if (u.l != null) stack.addLast(new IntObjEntry<>(0, u.l));
                }
            }
            return set;
        }

        /**
         * キーが降順となるようにエントリを列挙する (= DFS の reversed inorder)．
         * @param <V> Value の型
         * @param t 部分木
         * @return 部分木に含まれる全てのエントリをキーの降順に格納した Set．
         */
        static <V> Set<LongObjEntry<V>> descendingEntrySet(RBST<V> t) {
            LinkedHashSet<LongObjEntry<V>> set = new LinkedHashSet<>();
            if (t == null) return set;
            ArrayDeque<IntObjEntry<RBST<V>>> stack = new ArrayDeque<>();
            if (t.l != null) stack.addLast(new IntObjEntry<>(0, t.l));
            stack.addLast(new IntObjEntry<>(1, t));
            if (t.r != null) stack.addLast(new IntObjEntry<>(0, t.r));
            while (stack.size() > 0) {
                IntObjEntry<RBST<V>> p = stack.pollLast();
                RBST<V> u = p.getValue();
                if (p.getKeyAsInt() == 1) {
                    set.add(u);
                } else {
                    if (u.l != null) stack.addLast(new IntObjEntry<>(0, u.l));
                    stack.addLast(new IntObjEntry<>(1, u));
                    if (u.r != null) stack.addLast(new IntObjEntry<>(0, u.r));
                }
            }
            return set;
        }

        /**
         * 部分木に含まれる全てのキーの昇順に格納した Set を返す
         * @param <V> Value の型
         * @param t 部分木
         * @return 部分木に含まれる全てのキーの昇順に格納した Set
         */
        static <V> Set<Long> keySet(RBST<V> t) {
            Set<Long> set = new LinkedHashSet<>();
            for (LongObjEntry<V> e : entrySet(t)) set.add(e.getKeyAsLong());
            return set;
        }

        /**
         * 部分木に含まれる全てのキーの降順に格納した Set を返す
         * @param <V> Value の型
         * @param t 部分木
         * @return 部分木に含まれる全てのキーの降順に格納した Set
         */
        static <V> Set<Long> descendingKeySet(RBST<V> t) {
            Set<Long> set = new LinkedHashSet<>();
            for (LongObjEntry<V> e : descendingEntrySet(t)) set.add(e.getKeyAsLong());
            return set;
        }

        /**
         * 全ての Value を，対応付けられていたキーの昇順に格納した Collection を返す
         * @param <V> Value の型
         * @param t 部分木
         * @return 全ての Value を，対応付けられていたキーの昇順に格納した Collection
         */
        static <V> Collection<V> values(RBST<V> t) {
            Collection<V> col = new ArrayList<>();
            for (LongObjEntry<V> e : entrySet(t)) col.add(e.getValue());
            return col;
        }

        /**
         * 部分木の全てのエントリを昇順に走査するイテレータ．
         * @param <V> Value の型
         * @param t 部分木
         * @return 部分木の全てのエントリを昇順に走査するイテレータ
         */
        static <V> Iterator<LongObjEntry<V>> iterator(RBST<V> t) {
            return entrySet(t).iterator();
        }

        /**
         * 部分木の全てのエントリを降順に走査するイテレータ．
         * @param <V> Value の型
         * @param t 部分木
         * @return 部分木の全てのエントリを降順に走査するイテレータ
         */
        static <V> Iterator<LongObjEntry<V>> descendingIterator(RBST<V> t) {
            return descendingEntrySet(t).iterator();
        }

        /**
         * 部分木の全てのキーを昇順に走査するイテレータ．
         * @param <V> Value の型
         * @param t 部分木
         * @return 部分木の全てのキーを昇順に走査するイテレータ
         */
        static <V> PrimitiveIterator.OfLong keyIterator(RBST<V> t) {
            return new PrimitiveIterator.OfLong(){
                Iterator<LongObjEntry<V>> it = iterator(t);
                public boolean hasNext() {return it.hasNext();}
                public long nextLong() {return it.next().getKeyAsLong();}
            };
        }

        /**
         * 部分木の全てのキーを降順に走査するイテレータ．
         * @param <V> Value の型
         * @param t 部分木
         * @return 部分木の全てのキーを降順に走査するイテレータ
         */
        static <V> PrimitiveIterator.OfLong descendingKeyIterator(RBST<V> t) {
            return new PrimitiveIterator.OfLong(){
                Iterator<LongObjEntry<V>> it = descendingIterator(t);
                public boolean hasNext() {return it.hasNext();}
                public long nextLong() {return it.next().getKeyAsLong();}
            };
        }

        public String toString() {
            return "(" + getKey() + " => " + getValue() + ")";
        }
    }

    /**
     * Xor Shift による乱数生成器
     */
    static final class Random {
        int x = 123456789, y = 362436069, z = 521288629, w = 88675123;
        int nextInt() {
            int t = x ^ (x << 11);
            x = y; y = z; z = w;
            return w = (w ^ (w >> 19)) ^ (t ^ (t >> 8));
        }
    }

    /******* Usage *******/
    
    public static void main(String[] args) {
        LongOrderedMap<String> map = new LongOrderedMap<>();
        System.out.println("PUT");
        map.put(0, "a");
        map.put(4, "e");
        map.put(6, "g");
        map.put(4, "ee"); // replace
        map.put(-1, "z");
        for (LongObjEntry<String> e : map) {
            System.out.println(e);
        }
        System.out.println("REMOVE");
        map.removeKthEntry(2); // removing (4, "ee")
        map.removeKthEntry(1); // removing (0, "a")
        for (LongObjEntry<String> e : map) {
            System.out.println(e);
        }
        System.out.println("ADD");
        map.putIfAbsent(-1, "zzz"); // has no effect
        map.replace(0, "a", "aa");  // has no effect
        map.put(2, "c");
        map.replace(2, "cc");       // replace
        map.put(3, "d");
        map.replace(3, "dd", "ddd");// has no effect
        for (LongObjEntry<String> e : map) {
            System.out.println(e);
        }
        System.out.println("GET");
        System.out.println(map.kthEntry(0));
        System.out.println(map.kthEntry(1));
        System.out.println(map.kthEntry(2));
        System.out.println(map.kthEntry(3));
        System.out.println(map.kthEntry(4)); // null
        System.out.println("KEYS");
        for (long key : map.keySet()) {
            System.out.println(key);
        }
        System.out.println("VALUES");
        for (String val : map.values()) {
            System.out.println(val);
        }
        System.out.println("DESC ENTRIES");
        for (LongObjEntry<String> e : map.descendingEntrySet()) {
            System.out.println(e);
        }
        System.out.println("DESC KEYS");
        for (long key : map.descendingKeySet()) {
            System.out.println(key);
        }
        System.out.println("HIGHER/LOWER/CEILING/FLOOR");
        System.out.println(map.higherEntry(3));
        System.out.println(map.lowerEntry(3));
        System.out.println(map.ceilingEntry(3));
        System.out.println(map.floorEntry(3));
        System.out.println(map.lowerEntry(2));
        System.out.println(map.floorEntry(2));
        System.out.println(map.higherEntry(6)); // null
        System.out.println(map.ceilingEntry(7));// null
        System.out.println(map.floorEntry(-2)); // null
        System.out.println(map.lowerEntry(-1)); // null
        System.out.println("L");
        LongOrderedMap<String> l = map.splitLeftUsingIndex(2);
        for (LongObjEntry<String> e : l.descendingEntrySet()) {
            System.out.println(e);
        }
        System.out.println("R");
        for (LongObjEntry<String> e : map.descendingEntrySet()) {
            System.out.println(e);
        }
        map = map.mergeLeft(l);
        LongOrderedMap<String> r = map.splitRightUsingKey(3);
        System.out.println("L");
        for (LongObjEntry<String> e : map.descendingEntrySet()) {
            System.out.println(e);
        }
        System.out.println("R");
        for (LongObjEntry<String> e : r.descendingEntrySet()) {
            System.out.println(e);
        }
    }
}