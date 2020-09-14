package graph;
/**
 * @verified
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_2_A
 * 
 * @param <Edg> type of edge
 */
class Prim<Edg extends AbstractEdge> {
    private final int n;
    private final long cost;
    private final java.util.ArrayList<Edg> mst;
    private final boolean isConnected;

    public Prim(AbstractGraph<Edg> g) {
        this.n = g.getV();
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
        boolean[] s = new boolean[n];
        s[0] = true;
        java.util.PriorityQueue<Edg> pq = new java.util.PriorityQueue<>(g.getE());
        g.getEdges(0).forEach(pq::add);
        while (pq.size() > 0 && mst.size() < n - 1) {
            Edg e = pq.poll();
            int u = e.from;
            int v = e.to;
            if (s[u] && s[v]) continue;
            if (!s[u]) {
                int tmp = u; u = v; v = tmp;
            }
            cost += e.cost;
            mst.add(e);
            s[v] = true;
            for (Edg edge : g.getEdges(v)) {
                if (s[edge.from] && s[edge.to]) continue;
                pq.add(edge);
            }
        }
        return cost;
    }
}
