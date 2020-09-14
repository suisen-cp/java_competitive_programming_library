package tree;

class TreeBuilder {
    private static final class TreeEdge {
        final int from, to;
        final long cost;
        TreeEdge(int from, int to, long cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }
    }
    private final int n;
    private int ptr = 0;
    private final int root;
    private final TreeEdge[] edges;
    private final int[] count;
    private final int[][] adj;
    public TreeBuilder(int n, int root) {
        this.n = n;
        this.root = root;
        this.edges = new TreeEdge[n - 1];
        this.count = new int[n];
        this.adj = new int[n][];
    }
    public TreeBuilder(int n) {
        this(n, 0);
    }
    public void addEdge(int u, int v, long cost) {
        edges[ptr++] = new TreeEdge(u, v, cost);
        count[u]++;
        count[v]++;
    }
    public void addEdge(int u, int v) {
        addEdge(u, v, 1);
    }
    public Tree build() {
        for (int i = 0; i < n; i++) {
            adj[i] = new int[count[i]];
        }
        for (TreeEdge e : edges) {
            int u = e.from;
            int v = e.to;
            adj[u][--count[u]] = v;
            adj[v][--count[v]] = u;
        }
        return new Tree(n, root, adj);
    }
    public WeightedTree buildWeightedTree() {
        long[][] cst = new long[n][];
        for (int i = 0; i < n; i++) {
            adj[i] = new int[count[i]];
            cst[i] = new long[count[i]];
        }
        for (TreeEdge e : edges) {
            int u = e.from;
            int v = e.to;
            adj[u][--count[u]] = v;
            adj[v][--count[v]] = u;
            cst[u][count[u]] = e.cost;
            cst[v][count[v]] = e.cost;
        }
        return new WeightedTree(n, root, adj, cst);
    }
}

class Tree {
    final int n;
    final int root;
    final int[][] adj;
    final int[] par;
    final int[] pre;
    final int[] pst;
    Tree(int n, int root, int[][] adj) {
        this.n = n;
        this.adj = adj;
        this.root = root;
        this.par = new int[n];
        this.pre = new int[n];
        this.pst = new int[n];
        build();
    }
    private void build() {
        int preOrd = 0, pstOrd = 0;
        java.util.Arrays.fill(par, -1);
        int[] stack = new int[n << 1];
        int ptr = 0;
        stack[ptr++] = ~root;
        stack[ptr++] =  root;
        while (ptr > 0) {
            int u = stack[--ptr];
            if (u >= 0) {
                pre[preOrd++] = u;
                for (int v : adj[u]) {
                    if (v == par[u]) continue;
                    par[v] = u;
                    stack[ptr++] = ~v;
                    stack[ptr++] =  v;
                }
            } else {
                pst[pstOrd++] = ~u;
            }
        }
    }
    public int getV() {
        return n;
    }
    public int getRoot() {
        return root;
    }
    public int[] getEdges(int u) {
        return adj[u];
    }
    public int[] parent() {
        return par;
    }
    public int[] preOrder() {
        return pre;
    }
    public int[] postOrder() {
        return pst;
    }
}

class WeightedTree extends Tree {
    final long[] cst;
    final long[][] adjCost;
    WeightedTree(int n, int root, int[][] adj, long[][] adjCost) {
        super(n, root, adj);
        this.cst = new long[n];
        this.adjCost = adjCost;
        for (int u = 0; u < n; u++) {
            int k = adj[u].length;
            for (int i = 0; i < k; i++) {
                int v = adj[u][i];
                long c = adjCost[u][i];
                if (v == par[u]) {
                    cst[u] = c;
                } else {
                    cst[v] = c;
                }
            }
        }
    }
    public long[] getWeights() {
        return cst;
    }
    public long getWeight(int u, int i) {
        return adjCost[u][i];
    }
}