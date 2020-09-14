package graph;
/**
 * @verified
 * - https://atcoder.jp/contests/abc137/tasks/abc137_e
 * 
 * @param <Edg> type of edge
 */
class BellmanFord<Edg extends AbstractEdge> {
    public static final long UNREACHABLE = Long.MAX_VALUE;
    public static final long NEG_INF = Long.MIN_VALUE;

    private final int n;
    private final int s;
    private final long[] dist;
    private final java.util.ArrayList<Edg> prev;

    public BellmanFord(AbstractGraph<Edg> g, int s) {
        this.n = g.getV();
        this.s = s;
        this.dist = new long[n];
        this.prev = new java.util.ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            prev.add(null);
        }
        solve(g);
    }

    private void solve(AbstractGraph<Edg> g) {
        java.util.Arrays.fill(dist, UNREACHABLE);
        dist[s] = 0;
        for (int $ = 1; $ < n; $++) {
            for (int u = 0; u < n; u++) {
                if (dist[u] == UNREACHABLE) continue;
                for (Edg e : g.getEdges(u)) {
                    int v = e.to;
                    if (dist[u] + e.cost < dist[v]) {
                        dist[v] = dist[u] + e.cost;
                        prev.set(v, e);
                    }
                }
            }
        }
        for (int $ = 0; $ < n; $++) {
            for (int u = 0; u < n; u++) {
                if (dist[u] == UNREACHABLE) continue;
                for (Edg e : g.getEdges(u)) {
                    int v = e.to;
                    if (dist[u] == NEG_INF || dist[u] + e.cost < dist[v]) {
                        dist[u] = dist[v] = NEG_INF;
                    }
                }
            }
        }
    }

    public java.util.ArrayList<Edg> path(int t) {
        if (dist[t] == UNREACHABLE || dist[t] == NEG_INF) return null;
        java.util.ArrayList<Edg> path = new java.util.ArrayList<>();
        for (int v = t; v != s; v = prev.get(v).from) {
            path.add(prev.get(v));
        }
        int m = path.size();
        for (int l = 0, r = m - 1; l < r; l++, r--) {
            Edg tmp = path.get(l);
            path.set(l, path.get(r));
            path.set(r, tmp);
        }
        return path;
    }

    public long[] distances() {
        return dist;
    }

    public long distance(int i) {
        return dist[i];
    }

    public boolean isReachable(int t) {
        return dist[t] != UNREACHABLE;
    }

    public boolean isNegInf(int t) {
        return dist[t] == NEG_INF;
    }
}
