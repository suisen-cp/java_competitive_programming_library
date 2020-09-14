package tree;
/**
 * @verified
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_5_C
 * - https://atcoder.jp/contests/abc014/tasks/abc014_4
 */
class EulerTourLCA {
    private final int n;
    private final int[] tour;
    private final int[] tbeg;
    private final int[] dep;
    private final int m;
    private final int[] seg;
    public EulerTourLCA(Tree t) {
        this.n = t.getV();
        this.tour = new int[2 * n];
        this.tbeg = new int[n];
        this.dep = new int[n];
        dfs(t);
        int k = 1;
        while (k < n << 1) k <<= 1;
        this.m = k;
        this.seg = new int[m << 1];
        buildSegTree();
    }
    private void dfs(Tree t) {
        int k = 0;
        int[] par = t.parent();
        int[] stack = new int[n << 1];
        int ptr = 0;
        int root = t.getRoot();
        stack[ptr++] = ~root;
        stack[ptr++] =  root;
        while (ptr > 0) {
            int u = stack[--ptr];
            if (u >= 0) {
                tour[tbeg[u] = k++] = u;
                for (int v : t.getEdges(u)) {
                    if (v == par[u]) continue;
                    dep[v] = dep[u] + 1;
                    stack[ptr++] = ~v;
                    stack[ptr++] =  v;
                }
            } else {
                tour[k++] = par[~u];
            }
        }
    }
    private void buildSegTree() {
        for (int i = 0; i < 2 * n - 1; i++) {
            seg[i + m] = dep[tour[i]];
        }
        java.util.Arrays.fill(seg, m + 2 * n - 1, 2 * m, n);
        for (int i = m - 1; i > 0; i--) {
            seg[i] = Math.min(seg[i << 1 | 0], seg[i << 1 | 1]);
        }
    }
    public int query(int u, int v) {
        if (tbeg[u] > tbeg[v]) return query(v, u);
        int mink = -1, min = n;
        for (int l = tbeg[u] + m, r = tbeg[v] + m + 1; l < r; l >>= 1, r >>= 1) {
            if ((l & 1) == 1) {
                if (seg[l] < min) min = seg[mink = l];
                l++;
            }
            if ((r & 1) == 1) {
                r--;
                if (seg[r] < min) min = seg[mink = r];
            }
        }
        while (mink < m) {
            mink <<= 1;
            if (min != seg[mink]) mink |= 1;
        }
        return tour[mink - m];
    }
    public int dist(int u, int v) {
        return dep[u] + dep[v] - 2 * dep[query(u, v)];
    }
}
