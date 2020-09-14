package graph;
/**
 * @verified
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_2_A
 * 
 * @param <Edg> type of edge
 */
class Kruskal<Edg extends AbstractEdge> {
    private final int n;
    private final int[] dsu;
    private final long cost;
    private final java.util.ArrayList<Edg> mst;
    private final boolean isConnected;

    public Kruskal(AbstractGraph<Edg> g) {
        this.n = g.getV();
        this.dsu = new int[n];
        java.util.Arrays.fill(dsu, -1);
        this.mst = new java.util.ArrayList<Edg>(n - 1);
        this.cost = solve(g);
        this.isConnected = mst.size() == n - 1;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public java.util.OptionalLong cost() {
        return isConnected ? java.util.OptionalLong.of(cost) : java.util.OptionalLong.empty();
    }

    public java.util.ArrayList<Edg> getMST() {
        return isConnected ? mst : null;
    }

    private long solve(AbstractGraph<Edg> g) {
        if (g.getE() == 0) return 0;
        long cost = 0;
        java.util.PriorityQueue<Edg> pq = new java.util.PriorityQueue<>(g.getE());
        g.getEdges().forEach(pq::add);
        while (mst.size() < n - 1 && pq.size() > 0) {
            Edg e = pq.poll();
            if (merge(e.from, e.to)) {
                mst.add(e);
                cost += e.cost;
            }
        }
        return cost;
    }

    private boolean merge(int x, int y) {
        if ((x = leader(x)) == (y = leader(y))) return false;
        if (dsu[y] < dsu[x]) {
            dsu[y] += dsu[x];
            dsu[x] = y;
        } else {
            dsu[x] += dsu[y];
            dsu[y] = x;
        }
        return true;
    }

    private int leader(int x) {
        return dsu[x] < 0 ? x : (dsu[x] = leader(dsu[x]));
    }
}
