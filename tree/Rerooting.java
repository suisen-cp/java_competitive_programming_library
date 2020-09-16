package tree;
/**
 * @verified
 * - https://atcoder.jp/contests/abc160/tasks/abc160_f
 * - https://atcoder.jp/contests/dp/tasks/dp_v
 * 
 * @param <DP> type of DP array
 */
@SuppressWarnings("unchecked")
class Rerooting<DP> {
    @FunctionalInterface
    public static interface AddChild<DP> {
        public DP add(DP dpSum, DP dp, int child, int parent);
    }
    @FunctionalInterface
    public static interface AddSubtreeRoot<DP> {
        public DP add(DP dpSum, int root, int parent);
    }
    @FunctionalInterface
    public static interface MergeChildren<DP> {
        public DP merge(DP dpSumL, DP dpSumR);
    }
    @FunctionalInterface
    public static interface AddRoot<DP> {
        public DP add(DP dpSum, int root);
    }

    private final Tree t;
    private final int n;
    private final DP[] subTreeDP;
    private final DP[] childrenDP;
    private final DP[] rerooting;
    
    public Rerooting(Tree t, DP e, AddChild<DP> addChild, AddSubtreeRoot<DP> addSubtreeRoot, MergeChildren<DP> mergeChildren, AddRoot<DP> addRoot) {
        this.t = t;
        this.n = t.n;
        this.subTreeDP = (DP[]) new Object[n];
        this.childrenDP = (DP[]) new Object[n];
        this.rerooting = (DP[]) new Object[n];
        dfs(e, addChild, addSubtreeRoot);
        bfs(e, addChild, addSubtreeRoot, mergeChildren, addRoot);
    }
    private void dfs(DP e, AddChild<DP> adCh, AddSubtreeRoot<DP> adSubRt) {
        for (int u : t.pst) {
            childrenDP[u] = e;
            for (int v : t.adj[u]) {
                if (v == t.par[u]) continue;
                childrenDP[u] = adCh.add(childrenDP[u], subTreeDP[v], v, u);
            }
            subTreeDP[u] = adSubRt.add(childrenDP[u], u, t.par[u]);
        }
    }
    private void bfs(DP e, AddChild<DP> adCh, AddSubtreeRoot<DP> adSubRt, MergeChildren<DP> mgCh, AddRoot<DP> adRt) {
        DP[] parDP = (DP[]) new Object[n];
        rerooting[t.root] = subTreeDP[t.root];
        for (int u : t.pre) {
            int l = t.adj[u].length;
            DP sumR = e;
            for (int i = l - 1; i >= 0; i--) {
                int v = t.adj[u][i];
                if (v == t.par[u]) {
                    sumR = adCh.add(sumR, parDP[u], v, u);
                    continue;
                }
                sumR = adCh.add(rerooting[v] = sumR, subTreeDP[v], v, u);
            }
            DP sumL = e;
            for (int i = 0; i < l; i++) {
                int v = t.adj[u][i];
                if (v == t.par[u]) {
                    sumL = adCh.add(sumL, parDP[u], v, u);
                    continue;
                }
                sumR = rerooting[v];
                parDP[v] = adSubRt.add(mgCh.merge(sumL, sumR), u, v);
                rerooting[v] = adRt.add(adCh.add(childrenDP[v], parDP[v], u, v), v);
                sumL = adCh.add(sumL, subTreeDP[v], v, u);
            }
        }
    }
    public DP[] dp(Class<DP> clazz) {
        DP[] ret = (DP[]) java.lang.reflect.Array.newInstance(clazz, n);
        System.arraycopy(rerooting, 0, ret, 0, n);
        return ret;
    }
}