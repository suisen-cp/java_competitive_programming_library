package tree;

/**
 * @verified
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_5_A
 */
class Diameter {
    public static int[] diameter(Tree t) {
        int[] dfs1 = maxDepAndVertex(t, t.getRoot());
        int u = dfs1[1];
        int[] dfs2 = maxDepAndVertex(t, u);
        int v = dfs2[1];
        int d = dfs2[0];
        return new int[]{u, v, d};
    }

    private static int[] maxDepAndVertex(Tree t, int root) {
        int maxDep = 0;
        int maxDepV = root;
        int n = t.getV();
        int[] dep = new int[n];
        java.util.Arrays.fill(dep, -1);
        dep[root] = 0;
        int[] stack = new int[n];
        int ptr = 0;
        stack[ptr++] = root;
        while (ptr > 0) {
            int u = stack[--ptr];
            for (int v : t.getEdges(u)) {
                if (dep[v] >= 0) continue;
                dep[v] = dep[u] + 1;
                if (dep[v] > maxDep) {
                    maxDep = dep[v];
                    maxDepV = v;
                }
                stack[ptr++] = v;
            }
        }
        return new int[]{maxDep, maxDepV};
    }

    public static long[] diameter(WeightedTree t) {
        long[] dfs1 = maxDepAndVertex(t, t.getRoot());
        int u = (int) dfs1[1];
        long[] dfs2 = maxDepAndVertex(t, u);
        int v = (int) dfs2[1];
        long d = dfs2[0];
        return new long[]{u, v, d};
    }

    private static long[] maxDepAndVertex(WeightedTree t, int root) {
        int[] par = t.parent();
        long[] cost = t.getWeights();
        long maxDep = 0;
        int maxDepV = root;
        int n = t.getV();
        long[] dist = new long[n];
        java.util.Arrays.fill(dist, -1);
        dist[root] = 0;
        int[] stack = new int[n];
        int ptr = 0;
        stack[ptr++] = root;
        while (ptr > 0) {
            int u = stack[--ptr];
            for (int v : t.getEdges(u)) {
                if (dist[v] >= 0) continue;
                long c = par[u] == v ? cost[u] : cost[v];
                dist[v] = dist[u] + c;
                if (dist[v] > maxDep) {
                    maxDep = dist[v];
                    maxDepV = v;
                }
                stack[ptr++] = v;
            }
        }
        return new long[]{maxDep, maxDepV};
    }
}