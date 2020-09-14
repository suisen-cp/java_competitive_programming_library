package util;

public class ImmutablePair<T, U> extends Pair<T, U> {
    private static final long serialVersionUID = 7731146188188679998L;
    public ImmutablePair(T fst, U snd) {
        super(fst, snd);
    }
    @Override
    public T setFirst(T newValue) {
        throw new UnsupportedOperationException();
    }
    @Override
    public U setSecond(U newValue) {
        throw new UnsupportedOperationException();
    }
}