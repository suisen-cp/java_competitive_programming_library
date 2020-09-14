package graph;
/**
 * @verified
 * - https://judge.yosupo.jp/problem/shortest_path
 * 
 * @param <Edg> type of edge
 */
class Dijkstra<Edg extends AbstractEdge> {
    public static final long UNREACHABLE = Long.MAX_VALUE;

    private static final class State implements Comparable<State> {
        final int v;
        final long d;
        State(int v, long d) {this.v = v; this.d = d;}
        public int compareTo(State s) {return d == s.d ? v - s.v : d > s.d ? 1 : -1;}
    }

    private final int n;
    private final int s;
    private final long[] dist;
    private final java.util.ArrayList<Edg> prev;

    public Dijkstra(AbstractGraph<Edg> g, int s) {
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
        java.util.PriorityQueue<State> pq = new java.util.PriorityQueue<>();
        pq.add(new State(s, 0l));
        while (pq.size() > 0) {
            State st = pq.poll();
            int u = st.v;
            if (st.d != dist[u]) continue;
            for (Edg e : g.getEdges(u)) {
                int v = e.to;
                long c = e.cost;
                if (e.cost < 0) throw new AssertionError("Negative cost.");
                if (dist[u] + c < dist[v]) {
                    dist[v] = dist[u] + c;
                    prev.set(v, e);
                    pq.add(new State(v, dist[v]));
                }
            }
        }
    }

    public java.util.ArrayList<Edg> path(int t) {
        if (dist[t] == UNREACHABLE) return null;
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
}