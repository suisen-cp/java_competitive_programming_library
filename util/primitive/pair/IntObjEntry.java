package util.primitive.pair;

import java.util.Map;

/**
 * (int, Object) のエントリ．
 * @author https://atcoder.jp/users/suisen
 * @param <V> Value 側の型変数
 */
public class IntObjEntry<V> implements Map.Entry<Integer, V> {
    private int key;
    private V val;
    public IntObjEntry(int key, V val) {
        this.key = key;
        this.val = val;
    }
    public Integer getKey() {
        return key;
    }
    public int getKeyAsInt() {
        return key;
    }
    public V getValue() {
        return val;
    }
    public V setValue(V newValue) {
        V oldValue = val;
        val = newValue;
        return oldValue;
    }
    public String toString() {
        return key + " => " + val;
    }
}