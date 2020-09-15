package graph;

/**
 * @verified
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_3_A
 * - http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id=GRL_3_B
 * 
 * @param <Edg> type of edge
 */
public class LowLink<Edg extends AbstractEdge> {
    final int N;
    final int[] Ord;
    final int[] Low;
    final int[] Par;
    final java.util.ArrayList<Integer> Articulation;
    final java.util.ArrayList<Edg> Bridge;
    public LowLink(Graph<Edg> g) {
        this.Articulation = new java.util.ArrayList<>();
        this.Bridge = new java.util.ArrayList<>();
        this.N = g.getV();
        this.Ord = new int[N];
        this.Low = new int[N];
        this.Par = new int[N];
        build(g);
    }

    public java.util.ArrayList<Integer> getArticulations() {
        return Articulation;
    }
    public java.util.ArrayList<Edg> getBridges() {
        return Bridge;
    }

    private void build(Graph<Edg> g) {
        int nowOrd = 0;
        java.util.Arrays.fill(Ord, -1);
        long[] stack = new long[N];
        int ptr = 0;
        for (int i = 0; i < N; i++) {
            if (Ord[i] >= 0) continue;
            Par[i] = -1;
            stack[ptr++] = 0l << 32 | i;
            while (ptr > 0) {
                long p = stack[--ptr];
                int u = (int) (p & 0xffff_ffffl);
                int j = (int) (p >>> 32);
                if (j == 0) {
                    Low[u] = Ord[u] = nowOrd++;
                }
                if (j < g.deg(u)) {
                    int v = g.getEdge(u, j).to;
                    stack[ptr++] += 1l << 32;
                    if (v == Par[u]) continue;
                    if (Ord[v] == -1) {
                        stack[ptr++] = 0l << 32 | v;
                        Par[v] = u;
                    } else {
                        Low[u] = Math.min(Low[u], Ord[v]);
                    }
                } else {
                    boolean isArticulation = false;
                    int cnt = 0;
                    while (j --> 0) {
                        Edg e = g.getEdge(u, j);
                        int v = e.to;
                        if (Par[v] == u) {
                            Low[u] = Math.min(Low[u], Low[v]);
                            cnt++;
                            isArticulation |= u != i && Ord[u] <= Low[v];
                            if (Ord[u] < Low[v]) Bridge.add(e);
                        }
                    }
                    isArticulation |= u == i && cnt > 1;
                    if (isArticulation) Articulation.add(u);
                }
            }
        }
    }
}
