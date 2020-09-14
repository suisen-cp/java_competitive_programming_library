package graph;
/**
 * @verified
 * - https://atcoder.jp/contests/practice2/tasks/practice2_d
 */
class MaxFlow {
    private static final long INF = Long.MAX_VALUE;

    private final Digraph<? extends CapEdge> dig;
    private final int n;
    private final int[] count;
    private final CapEdge[][] g;

    public MaxFlow(Digraph<? extends CapEdge> capDiraph) {
        this.dig = capDiraph;
        this.n = capDiraph.getV();
        this.count = new int[n];
        this.g = new CapEdge[n][];
        buildGraph();
    }

    public long getFlow(CapEdge e) {
        return g[e.to][e.rev].cap;
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
                CapEdge r = new CapEdge(v, u, 0);
                r.rev = i;
                g[u][i] = e;
                g[v][e.rev] = r;
            }
        }
    }

    public long maxFlow(int s, int t) {
        return flow(s, t, INF);
    }

    public long flow(int s, int t, long flowLimit) {
        rangeCheck(s, 0, n);
        rangeCheck(t, 0, n);
        buildGraph();
        long flow = 0;
        int[] level = new int[n];
        int[] que = new int[n];
        int[] iter = new int[n];
        while (true) {
            java.util.Arrays.fill(level, -1);
            dinicBFS(s, t, level, que);
            if (level[t] < 0) return flow;
            java.util.Arrays.fill(iter, 0);
            while (true) {
                long d = dinicDFS(t, s, flowLimit - flow, iter, level);
                if (d <= 0) break;
                flow += d;
            }
        }
    }

    private void dinicBFS(int s, int t, int[] level, int[] que) {
        int hd = 0, tl = 0;
        que[tl++] = s;
        level[s] = 0;
        while (tl > hd) {
            int u = que[hd++];
            for (CapEdge e : g[u]) {
                int v = e.to;
                if (e.cap <= 0 || level[v] >= 0) continue;
                level[v] = level[u] + 1;
                if (v == t) return;
                que[tl++] = v;
            }
        }
    }

    private long dinicDFS(int cur, int s, long f, int[] iter, int[] level) {
        if (cur == s) return f;
        long res = 0;
        while (iter[cur] < count[cur]) {
            CapEdge er = g[cur][iter[cur]++];
            int u = er.to;
            CapEdge e = g[u][er.rev];
            if (level[u] >= level[cur] || e.cap <= 0) continue;
            long d = dinicDFS(u, s, Math.min(f - res, e.cap), iter, level);
            if (d <= 0) continue;
            e.cap -= d;
            er.cap += d;
            res += d;
            if (res == f) break;
        }
        return res;
    }

    public long fordFulkersonMaxFlow(int s, int t) {
        return fordFulkersonFlow(s, t, INF);
    }

    public long fordFulkersonFlow(int s, int t, long flowLimit) {
        rangeCheck(s, 0, n);
        rangeCheck(t, 0, n);
        buildGraph();
        boolean[] used = new boolean[n];
        long flow = 0;
        while (true) {
            java.util.Arrays.fill(used, false);
            long f = fordFulkersonDFS(s, t, flowLimit - flow, used);
            if (f <= 0) return flow;
            flow += f;
        }
    }

    private long fordFulkersonDFS(int cur, int t, long f, boolean[] used) {
        if (cur == t) return f;
        used[cur] = true;
        for (CapEdge e : g[cur]) {
            if (used[e.to] || e.cap <= 0) continue;
            long d = fordFulkersonDFS(e.to, t, Math.min(f, e.cap), used);
            if (d <= 0) continue;
            e.cap -= d;
            g[e.to][e.rev].cap += d;
            return d;
        }
        return 0;
    }

    public boolean[] minCut(int s) {
        rangeCheck(s, 0, n);
        boolean[] reachable = new boolean[n];
        int[] stack = new int[n];
        int ptr = 0;
        stack[ptr++] = s;
        reachable[s] = true;
        while (ptr > 0) {
            int u = stack[--ptr];
            for (CapEdge e : g[u]) {
                int v = e.to;
                if (reachable[v] || e.cap <= 0) continue;
                reachable[v] = true;
                stack[ptr++] = v;
            }
        }
        return reachable;
    }

    public void changeEdge(int u, int i, long newCap, long newFlow) {
        nonNegativeCheck(newCap, "Capacity");
        if (newFlow > newCap) {
            throw new IllegalArgumentException(
                String.format("Flow %d is greater than capacity %d.", newCap, newFlow)
            );
        }
        CapEdge e = dig.getEdge(u, i);
        CapEdge er = g[e.to][e.rev];
        e.cap = newCap - newFlow;
        er.cap = newFlow;
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
