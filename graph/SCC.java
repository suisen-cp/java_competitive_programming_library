package graph;
/**
 * @verified
 * - https://atcoder.jp/contests/practice2/tasks/practice2_g
 */
class SCC {
    final int n;
    final int m;
    final int[] ids;
    final int[][] groups;

    public SCC(Digraph<? extends AbstractEdge> g) {
        this.n = g.getV();
        this.m = g.getE();
        this.ids = new int[n];
        this.groups = build(g);
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

    private int[][] build(Digraph<? extends AbstractEdge> g) {
        int nowOrd = 0;
        int groupNum = 0;
        int k = 0;
        int[] par = new int[n];
        int[] vis = new int[n];
        int[] low = new int[n];
        int[] ord = new int[n];
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
                    vis[k++] = u;
                }
                if (j < g.deg(u)) {
                    int to = g.getEdge(u, j).to;
                    stack[ptr++] += 1l << 32;
                    if (ord[to] == -1) {
                        stack[ptr++] = 0l << 32 | to;
                        par[to] = u;
                    } else {
                        low[u] = Math.min(low[u], ord[to]);
                    }
                } else {
                    while (j --> 0) {
                        int to = g.getEdge(u, j).to;
                        if (par[to] == u) low[u] = Math.min(low[u], low[to]);
                    }
                    if (low[u] == ord[u]) {
                        while (true) {
                            int v = vis[--k];
                            ord[v] = n;
                            ids[v] = groupNum;
                            if (v == u) break;
                        }
                        groupNum++;
                    }
                }
            }
        }
        int[] counts = new int[groupNum];
        for (int i = 0; i < n; i++) {
            counts[ids[i] = groupNum - 1 - ids[i]]++;
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
}
