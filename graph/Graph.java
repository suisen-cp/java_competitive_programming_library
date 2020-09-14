package graph;
abstract class AbstractGraph<Edg extends AbstractEdge> {
    final int n;
    final java.util.ArrayList<Edg> edges;
    final java.util.ArrayList<java.util.ArrayList<Edg>> adj;
    public AbstractGraph(int n) {
        this.n = n;
        this.edges = new java.util.ArrayList<>(n);
        this.adj = new java.util.ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new java.util.ArrayList<>());
        }
    }
    public abstract void addEdge(Edg edge);
    public Edg getEdge(int u, int i) {
        return adj.get(u).get(i);
    }
    public java.util.ArrayList<Edg> getEdges(int u) {
        return adj.get(u);
    }
    public java.util.ArrayList<Edg> getEdges() {
        return edges;
    }
    public int deg(int u) {
        return adj.get(u).size();
    }
    public int getV() {
        return n;
    }
    public int getE() {
        return edges.size();
    }
}

class Graph<Edg extends AbstractEdge> extends AbstractGraph<Edg> {
    public Graph(int n) {
        super(n);
    }
    @SuppressWarnings("unchecked")
    @Override
    public void addEdge(Edg edge) {
        Edg rev = (Edg) edge.reverse();
        adj.get(edge.from).add(edge);
        adj.get(edge.to).add(rev);
        edges.add(edge);
    }
}

class Digraph<Edg extends AbstractEdge> extends AbstractGraph<Edg> {
    public Digraph(int n) {
        super(n);
    }
    @Override
    public void addEdge(Edg edge) {
        adj.get(edge.from).add(edge);
        edges.add(edge);
    }
}

abstract class AbstractEdge implements Comparable<AbstractEdge> {
    public final int from, to;
    public final long cost;
    public AbstractEdge(int from, int to, long cost) {
        this.from = from;
        this.to = to;
        this.cost = cost;
    }
    public AbstractEdge(int from, int to) {
        this(from, to, 1l);
    }
    public abstract AbstractEdge reverse();
    public int compareTo(AbstractEdge o) {
        return Long.compare(cost, o.cost);
    }
}

final class SimpleEdge extends AbstractEdge {
    public SimpleEdge(int from, int to, long cost) {
        super(from, to, cost);
    }
    public SimpleEdge(int from, int to) {
        super(from, to);
    }
    @Override
    public SimpleEdge reverse() {
        return new SimpleEdge(to, from, cost);
    }
}

class CapEdge extends AbstractEdge {
    long cap;
    int rev;
    public CapEdge(int from, int to, long cap, long cost) {
        super(from, to, cost);
        this.cap = cap;
    }
    public CapEdge(int from, int to, long cap) {
        this(from, to, cap, 1);
    }
    @Override
    public final AbstractEdge reverse() {
        throw new UnsupportedOperationException();
    }
}
