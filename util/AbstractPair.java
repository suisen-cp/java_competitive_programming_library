package util;

public abstract class AbstractPair<T, U> implements java.io.Serializable {
    private static final long serialVersionUID = -8418290440169931227L;
    public abstract T getFirst();
    public abstract U getSecond();
    public abstract T setFirst(T newValue);
    public abstract U setSecond(U newValue);
    @Override
    public int hashCode() {
        int result = 1;
        T fst = getFirst();
        U snd = getSecond();
        result = 31 * result + (fst == null ? 0 : fst.hashCode());
        result = 31 * result + (snd == null ? 0 : snd.hashCode());
        return result;
    }
    @Override
    @SuppressWarnings("all")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof AbstractPair) {
            T thisFst = getFirst();
            U thisSnd = getSecond();
            AbstractPair<?,?> p = (AbstractPair<?,?>) o;
            Object thatFst = p.getFirst();
            Object thatSnd = p.getSecond();
            boolean equalsFirst = thisFst == null ? thatFst == null : thisFst.equals(thatFst);
            boolean equalsSecond = thisSnd == null ? thatSnd == null : thisSnd.equals(thatSnd);
            return equalsFirst && equalsSecond;
        }
        return false;
    }
    @Override
    public String toString() {
        T fst = getFirst();
        U snd = getSecond();
        return "(" + fst + ", " + snd + ")";
    }
}