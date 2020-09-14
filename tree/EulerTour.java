package tree;
/**
 * @verified
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_5_D
 */
class EulerTour {
    private final int n;
    public final int[] tour;
    public final int[] tourBeg;
    public final int[] tourEnd;
    public final int[] subtBeg;
    public final int[] subtEnd;

    public EulerTour(Tree t) {
        this.n = t.getV();
        this.tour = new int[n << 1];
        this.tourBeg = new int[n];
        this.tourEnd = new int[n];
        this.subtBeg = new int[n];
        this.subtEnd = new int[n];
        build(t);
    }

    private void build(Tree t) {
        java.util.Arrays.fill(tourBeg, -1);
        int[] pre = t.preOrder();
        int[] pst = t.postOrder();
        for (int i = 0, j = 0; j != n;) {
            int u = pst[j], v;
            do {
                v = pre[i];
                tour[i + j] = v;
                tourBeg[v] = i + j;
                subtBeg[v] = i;
                i++;
            } while (v != u);
            do {
                tour[i + j] = ~u;
                tourEnd[u] = i + j;
                subtEnd[u] = i;
                j++;
            } while (j < n && tourBeg[u = pst[j]] >= 0);
        }
    }
}