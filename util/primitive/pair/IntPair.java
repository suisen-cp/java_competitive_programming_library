package util.primitive.pair;

import util.AbstractPair;

/**
 * (int, int) のペア．
 * @author https://atcoder.jp/users/suisen
 */
public class IntPair extends AbstractPair<Integer, Integer> {
	private static final long serialVersionUID = 5193785898235502024L;
	private int fst;
    private int snd;
    public IntPair(int fst, int snd) {
        this.fst = fst;
        this.snd = snd;
    }
    public int getFirstAsInt() {
        return fst;
    }
    public int getSecondAsInt() {
        return snd;
    }
    public int setFirstAsInt(int newValue) {
        int oldValue = fst;
        fst = newValue;
        return oldValue;
    }
    public int setSecondAsInt(int newValue) {
        int oldValue = snd;
        snd = newValue;
        return oldValue;
    }
    @Override
    public Integer getFirst() {
        return getFirstAsInt();
    }
    @Override
    public Integer getSecond() {
        return getSecondAsInt();
    }
    @Override
    public Integer setFirst(Integer newValue) {
        return setFirstAsInt(newValue);
    }
    @Override
    public Integer setSecond(Integer newValue) {
        return setSecondAsInt(newValue);
    }
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + fst;
        result = 31 * result + snd;
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof IntPair) {
            IntPair p = (IntPair) o;
            return fst == p.fst && snd == p.snd;
        }
        return super.equals(o);
    }
}