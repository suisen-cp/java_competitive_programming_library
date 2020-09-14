package tree;
/**
 * @verified
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_5_C
 * - https://atcoder.jp/contests/abc014/tasks/abc014_4
 */
class DoublingLCA {
    private final Tree t;
    private final int n;
    private final int log;
    private final int root;
    private final int[][] ancestor;
    private final int[] dep;
    private final int[] par;
    public DoublingLCA(Tree t) {
        this.t = t;
        this.n = t.getV();
        int k = 1;
        while ((1 << k) < n) k++;
        this.log = k;
        this.root = t.getRoot();
        this.ancestor = new int[log][n];
        buildTable();
        this.par = ancestor[0];
        this.dep = new int[n];
        dfs();
    }
    private void buildTable() {
        System.arraycopy(t.parent(), 0, ancestor[0], 0, n);
        ancestor[0][root] = root;
        for (int i = 1; i < log; i++) {
            for (int v = 0; v < n; v++) {
                ancestor[i][v] = ancestor[i - 1][ancestor[i - 1][v]];
            }
        }
    }
    private void dfs() {
        dep[root] = -1;
        for (int u : t.preOrder()) {
            dep[u] = dep[par[u]] + 1;
        }
    }
    public int query(int u, int v) {
        if (dep[u] > dep[v]) return query(v, u);
        int d = dep[v] - dep[u];
        for (int p = 0; d != 0; v = ancestor[p][v]) {
            int lsb = -d & d;
            while ((1 << p) != lsb) p++;
            d ^= lsb;
        }
        if (u == v) return u;
        for (int i = log - 1; i >= 0; i--) {
            if (ancestor[i][u] == ancestor[i][v]) continue;
            u = ancestor[i][u];
            v = ancestor[i][v];
        }
        return par[u];
    }
    public int dist(int u, int v) {
        return dep[u] + dep[v] - 2 * dep[query(u, v)];
    }
}
