package graph;
/**
 * @verified
 * - https://atcoder.jp/contests/practice2/tasks/practice2_e
 */
class MinCostFlow {
    private static final long INF = Long.MAX_VALUE;

    private final Digraph<? extends CapEdge> dig;
    private final int n;
    private final int[] count;
    private final CapEdge[][] g;
    private final long[] potential;

    private final long[] dist;
    private final CapEdge[] prev;

    public MinCostFlow(Digraph<? extends CapEdge> capDiraph) {
        this.dig = capDiraph;
        this.n = capDiraph.getV();
        this.count = new int[n];
        this.g = new CapEdge[n][];
        this.potential = new long[n];
        this.dist = new long[n];
        this.prev = new CapEdge[n];
        buildGraph();
    }

    private void buildGraph() {
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) {
            count[i] = idx[i] = dig.deg(i);
        }
        for (CapEdge e : dig.getEdges()) {
            int j = e.to;
            count[j]++;
        }
        for (int i = 0; i < n; i++) {
            g[i] = new CapEdge[count[i]];
        }
        for (int u = 0; u < n; u++) {
            int k = dig.deg(u);
            for (int i = 0; i < k; i++) {
                CapEdge e = dig.getEdge(u, i);
                int v = e.to;
                e.rev = idx[v]++;
                CapEdge r = new CapEdge(v, u, 0, -e.cost);
                r.rev = i;
                g[u][i] = e;
                g[v][e.rev] = r;
            }
        }
    }

    public long getFlow(CapEdge e) {
        return g[e.to][e.rev].cap;
    }

    private long addFlow;
    private long addCost;

    public long[] minCostMaxFlow(int s, int t) {
        return minCostFlow(s, t, INF);
    }

    public long[] minCostFlow(int s, int t, long flowLimit) {
        rangeCheck(s, 0, n);
        rangeCheck(t, 0, n);
        if (s == t) {
            throw new IllegalArgumentException(String.format("s = t = %d", s));
        }
        nonNegativeCheck(flowLimit, "Flow");
        buildGraph();
        long flow = 0;
        long cost = 0;
        while (true) {
            dijkstra(s, t, flowLimit - flow);
            if (addFlow == 0) break;
            flow += addFlow;
            cost += addFlow * addCost;
        }
        return new long[]{flow, cost};
    }

    public java.util.ArrayList<long[]> minCostSlope(int s, int t) {
        return minCostSlope(s, t, INF);
    }

    public java.util.ArrayList<long[]> minCostSlope(int s, int t, long flowLimit) {
        rangeCheck(s, 0, n);
        rangeCheck(t, 0, n);
        if (s == t) {
            throw new IllegalArgumentException(String.format("s = t = %d", s));
        }
        nonNegativeCheck(flowLimit, "Flow");
        buildGraph();
        java.util.ArrayList<long[]> slope = new java.util.ArrayList<>();
        long prevCost = -1;
        long flow = 0;
        long cost = 0;
        while (true) {
            slope.add(new long[]{flow, cost});
            dijkstra(s, t, flowLimit - flow);
            if (addFlow == 0) return slope;
            flow += addFlow;
            cost += addFlow * addCost;
            if (addCost == prevCost) {
                slope.remove(slope.size() - 1);
            }
            prevCost = addCost;
        }
    }

    private void dijkstra(int s, int t, long maxFlow) {
        final class State implements Comparable<State> {
            final int v;
            final long d;
            State(int v, long d) {this.v = v; this.d = d;}
            public int compareTo(State s) {return d == s.d ? v - s.v : d > s.d ? 1 : -1;}
        }
        java.util.Arrays.fill(dist, INF);
        dist[s] = 0;
        java.util.PriorityQueue<State> pq = new java.util.PriorityQueue<>();
        pq.add(new State(s, 0l));
        while (pq.size() > 0) {
            State st = pq.poll();
            int u = st.v;
            if (st.d != dist[u]) continue;
            for (CapEdge e : g[u]) {
                if (e.cap <= 0) continue;
                int v = e.to;
                long nextCost = dist[u] + e.cost + potential[u] - potential[v];
                if (nextCost < dist[v]) {
                    dist[v] = nextCost;
                    prev[v] = e;
                    pq.add(new State(v, dist[v]));
                }
            }
        }
        if (dist[t] == INF) {
            addFlow = 0;
            addCost = INF;
            return;
        }
        for (int i = 0; i < n; i++) {
            potential[i] += dist[i];
        }
        addCost = 0;
        addFlow = maxFlow;
        for (int v = t; v != s;) {
            CapEdge e = prev[v];
            addCost += e.cost;
            addFlow = java.lang.Math.min(addFlow, e.cap);
            v = e.from;
        }
        for (int v = t; v != s;) {
            CapEdge e = prev[v];
            e.cap -= addFlow;
            g[v][e.rev].cap += addFlow;
            v = e.from;
        }
    }

    public void clearFlow() {
        java.util.Arrays.fill(potential, 0);
        for (CapEdge e : dig.getEdges()) {
            long flow = getFlow(e);
            e.cap += flow;
            g[e.to][e.rev].cap -= flow;
        }
    }

    private void rangeCheck(int i, int minInlusive, int maxExclusive) {
        if (i < 0 || i >= maxExclusive) {
            throw new IndexOutOfBoundsException(
                String.format("Index %d out of bounds for length %d", i, maxExclusive)
            );
        }
    }

    private void nonNegativeCheck(long cap, java.lang.String attribute) {
        if (cap < 0) {
            throw new IllegalArgumentException(
                String.format("%s %d is negative.", attribute, cap)
            );
        }
    }
}

