package util;

/**
 * (Object, Object) のペア．両者とも値の変更が可能．
 * PriorityQueue などに入れたまま値を変更してしまうと比較が壊れてバグる可能性があるので注意．
 * @author https://atcoder.jp/users/suisen
 * @param <V> Value 側の型変数
 */
public class Pair<T, U> extends AbstractPair<T, U> {
    private static final long serialVersionUID = -2022089021501463539L;
    private T fst;
    private U snd;
    public Pair(T fst, U snd) {
        this.fst = fst;
        this.snd = snd;
    }
    @Override
    public T getFirst() {
        return fst;
    }
    @Override
    public U getSecond() {
        return snd;
    }
    @Override
    public T setFirst(T newValue) {
        T oldValue = fst;
        fst = newValue;
        return oldValue;
    }
    @Override
    public U setSecond(U newValue) {
        U oldValue = snd;
        snd = newValue;
        return oldValue;
    }
}