package graph;
/**
 * @verified
 * - https://atcoder.jp/contests/arc039/tasks/arc039_d
 * 
 * @param <Edg> type of edge
 */
class BCC<Edg extends AbstractEdge> {
    final int n;
    final int m;
    final int[] ids;
    final int[][] groups;

    final int[] par;
    final int[] low;
    final int[] ord;

    public BCC(AbstractGraph<Edg> g) {
        this.n = g.getV();
        this.m = g.getE();
        this.ids = new int[n];
        this.par = new int[n];
        this.low = new int[n];
        this.ord = new int[n];
        this.groups = build(g);
    }

    public int getComponentsNum() {
        return groups.length;
    }

    public int[][] getComponents() {
        return groups;
    }

    public int[] getIds() {
        return ids;
    }

    public int getId(int i) {
        return ids[i];
    }

    private int[][] build(AbstractGraph<Edg> g) {
        lowLink(g);
        java.util.Arrays.fill(ids, -1);
        int groupNum = 0;
        int[] stack = new int[n];
        int ptr = 0;
        for (int i = 0; i < n; i++) {
            if (ids[i] >= 0) continue;
            ids[i] = groupNum++;
            stack[ptr++] = i;
            while (ptr > 0) {
                int u = stack[--ptr];
                for (Edg e : g.getEdges(u)) {
                    int v = e.to;
                    if (u != par[v]) continue;
                    stack[ptr++] = v;
                    if (ord[u] >= low[v]) {
                        ids[v] = ids[u];
                    } else {
                        ids[v] = groupNum++;
                    }
                }
            }
        }
        int[] counts = new int[groupNum];
        for (int i = 0; i < n; i++) {
            counts[ids[i]]++;
        }
        int[][] groups = new int[groupNum][];
        for (int i = 0; i < groupNum; i++) {
            groups[i] = new int[counts[i]];
        }
        for (int i = 0; i < n; i++) {
            int cmp = ids[i];
            groups[cmp][--counts[cmp]] = i;
        }
        return groups;
    }

    private void lowLink(AbstractGraph<Edg> g) {
        int nowOrd = 0;
        java.util.Arrays.fill(ord, -1);
        long[] stack = new long[n];
        int ptr = 0;
        for (int i = 0; i < n; i++) {
            if (ord[i] >= 0) continue;
            par[i] = -1;
            stack[ptr++] = 0l << 32 | i;
            while (ptr > 0) {
                long p = stack[--ptr];
                int u = (int) (p & 0xffff_ffffl);
                int j = (int) (p >>> 32);
                if (j == 0) {
                    low[u] = ord[u] = nowOrd++;
                }
                if (j < g.deg(u)) {
                    int to = g.getEdge(u, j).to;
                    stack[ptr++] += 1l << 32;
                    if (to == par[u]) continue;
                    if (ord[to] == -1) {
                        stack[ptr++] = 0l << 32 | to;
                        par[to] = u;
                    } else {
                        low[u] = Math.min(low[u], ord[to]);
                    }
                } else {
                    while (j --> 0) {
                        int to = g.getEdge(u, j).to;
                        if (par[to] == u) {
                            low[u] = Math.min(low[u], low[to]);
                        }
                    }
                }
            }
        }
    }
}