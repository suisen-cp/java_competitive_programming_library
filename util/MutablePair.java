package util;

/**
 * (Object, Object) のペア．両者とも値の変更が可能．
 * PriorityQueue などに入れたまま値を変更してしまうと比較が壊れてバグる可能性があるので注意．
 * @author https://atcoder.jp/users/suisen
 * @param <V> Value 側の型変数
 */
public class MutablePair<T, U> implements java.io.Serializable {
    private static final long serialVersionUID = -2022089021501463539L;
    private T fst;
    private U snd;
    public MutablePair(T fst, U snd) {
        this.fst = fst;
        this.snd = snd;
    }
    public T getFirst() {
        return fst;
    }
    public U getSecond() {
        return snd;
    }
    public T setFirst(T newValue) {
        T oldValue = fst;
        fst = newValue;
        return oldValue;
    }
    public U setSecond(U newValue) {
        U oldValue = snd;
        snd = newValue;
        return oldValue;
    }
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (fst == null ? 0 : fst.hashCode());
        result = 31 * result + (snd == null ? 0 : snd.hashCode());
        return result;
    }
    @Override
    @SuppressWarnings("all")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof MutablePair) {
            MutablePair p = (MutablePair) o;
            boolean equalsFirst = fst == null ? p.fst == null : fst.equals(p.fst);
            boolean equalsSecond = snd == null ? p.snd == null : snd.equals(p.snd);
            return equalsFirst && equalsSecond;
        }
        return false;
    }
    @Override
    public String toString() {
        return "(" + fst + ", " + snd + ")";
    }
}