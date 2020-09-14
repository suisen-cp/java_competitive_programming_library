package tree;
/**
 * @verified
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_5_D (queryForEdge)
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_5_E (queryForEdge)
 */
class HLD {
    @FunctionalInterface
    public static interface IntBiConsumer {
        public void accept(int u, int v);
    }

    private final Tree t;
    private final int n;
    private final int[] par;
    public  final int[] ids;
    public  final int[] inv;
    private final int[] head;

    public HLD(final Tree t) {
        this.t = t;
        this.n = t.getV();
        this.par = t.parent();
        this.ids = new int[n];
        this.inv = new int[n];
        this.head = new int[n];
        bfs(dfs());
    }

    public void queryForVertex(int u, int v, IntBiConsumer f) {
        while (true) {
            if (ids[u] > ids[v]) {int tmp = u; u = v; v = tmp;}
            f.accept(Math.max(ids[head[v]], ids[u]), ids[v]);
            if (head[u] == head[v]) return;
            v = par[head[v]];
        }
    }

    public void queryForEdge(int u, int v, IntBiConsumer f) {
        while (true) {
            if (ids[u] > ids[v]) {int tmp = u; u = v; v = tmp;}
            if (head[u] == head[v]) {
                f.accept(ids[u] + 1, ids[v]);
                return;
            }
            f.accept(ids[head[v]], ids[v]);
            v = par[head[v]];
        }
    }

    private int[] dfs() {
        int[] nxt = new int[n];
        int[] sub = new int[n];
        java.util.Arrays.fill(nxt, -1);
        java.util.Arrays.fill(sub, 1);
        for (int u : t.postOrder()) {
            int subMax = 0;
            for (int v : t.getEdges(u)) {
                if (v == par[u]) continue;
                sub[u] += sub[v];
                if (subMax < sub[v]) subMax = sub[nxt[u] = v];
            }
        }
        return nxt;
    }

    private void bfs(int[] nxt) {
        int[] que = new int[n];
        int qh = 0, qt = 0;
        que[qt++] = t.getRoot();
        int k = 0;
        while (qh < qt) {
            int h = que[qh++];
            for (int u = h; u >= 0; u = nxt[u]) {
                inv[ids[u] = k++] = u;
                head[u] = h;
                for (int v : t.getEdges(u)) {
                    if (v == par[u] || v == nxt[u]) continue;
                    que[qt++] = v;
                }
            }
        }
    }

    public int lca(int u, int v) {
        while (true) {
            if (ids[u] > ids[v]) {int tmp = u; u = v; v = tmp;}
            if (head[u] == head[v]) return u;
            v = par[head[v]];
        }
    }
}
