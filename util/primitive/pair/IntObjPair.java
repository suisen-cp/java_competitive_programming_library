package util.primitive.pair;

import util.AbstractPair;

/**
 * (int, Object) のペア．
 * @author https://atcoder.jp/users/suisen
 * @param <T> Object 側の型変数
 */
public class IntObjPair<T> extends AbstractPair<Integer, T> {
	private static final long serialVersionUID = 8919332078330327072L;
	private int fst;
    private T snd;
    public IntObjPair(int fst, T snd) {
        this.fst = fst;
        this.snd = snd;
    }
    public int getFirstAsInt() {
        return fst;
    }
    @Override
    public T getSecond() {
        return snd;
    }
    public int setFirstAsInt(int newValue) {
        int oldValue = fst;
        fst = newValue;
        return oldValue;
    }
    @Override
    public T setSecond(T newValue) {
        T oldValue = snd;
        snd = newValue;
        return oldValue;
    }
    @Override
    public Integer getFirst() {
        return getFirstAsInt();
    }
    @Override
    public Integer setFirst(Integer newValue) {
        return setFirstAsInt(newValue);
    }
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + fst;
        result = 31 * result + (snd == null ? 0 : snd.hashCode());
        return result;
    }
    @Override
    @SuppressWarnings("all")
    public boolean equals(Object o) {
        if (o instanceof IntObjPair) {
            IntObjPair p = (IntObjPair) o;
            return fst == p.fst && (snd == null ? p.snd == null : snd.equals(p.snd));
        }
        return super.equals(o);
    }
}