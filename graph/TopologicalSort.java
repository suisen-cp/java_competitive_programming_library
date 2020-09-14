package graph;
/**
 * @verified
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_4_B
 */
class TopologicalSort {
    private final int n;
    private final int[] ord;
    private final int[] inv;
    private final boolean isDAG;
    public TopologicalSort(Digraph<? extends AbstractEdge> g) {
        this.n = g.getV();
        this.ord = new int[n];
        this.inv = new int[n];
        this.isDAG = build(g);
    }
    public boolean isDAG() {
        return isDAG;
    }
    public int[] topologicalOrder() {
        return isDAG ? ord : null;
    }
    public int[] topologicalOrderInv() {
        return isDAG ? inv : null;
    }
    private boolean build(Digraph<? extends AbstractEdge> g) {
        for (AbstractEdge e : g.getEdges()) {
            inv[e.to]++;
        }
        int hd = 0, tl = 0;
        for (int u = 0; u < n; u++) {
            if (inv[u] == 0) {
                ord[inv[u] = tl++] = u;
            }
        }
        while (tl > hd) {
            int u = ord[hd++];
            for (AbstractEdge e : g.getEdges(u)) {
                int v = e.to;
                if (--inv[v] == 0) {
                    ord[inv[v] = tl++] = v;
                }
            }
        }
        return tl == n;
    }
}
