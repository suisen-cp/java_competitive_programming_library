package util.primitive.pair;

import java.util.Map;

/**
 * (long, Object) のエントリ．
 * @author https://atcoder.jp/users/suisen
 * @param <V> Value 側の型変数
 */
public class LongObjEntry<V> implements Map.Entry<Long, V> {
    private long key;
    private V val;
    public LongObjEntry(long key, V val) {
        this.key = key;
        this.val = val;
    }
    public Long getKey() {
        return key;
    }
    public long getKeyAsLong() {
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
}