package graph;

import java.util.*;

/**
 * Concrete implementation of a weighted directed graph using an edge list.
 * Vertices are strings and edges are represented by the Edge class.
 */
public class ConcreteEdgesGraph implements Graph<String> {

    private final Set<String> vertices = new HashSet<>();
    private final List<Edge> edges = new ArrayList<>();

    // Abstraction function:
    //   AF(vertices, edges) = a weighted directed graph where
    //     - vertices contains all vertex labels
    //     - edges contains all edges, each with a source, target, and positive weight
    // Representation invariant:
    //   - vertices != null
    //   - edges != null
    //   - no edge has null source or target
    //   - all edge weights are positive
    //   - all edge sources and targets exist in vertices
    // Safety from rep exposure:
    //   - All fields are private and final
    //   - vertices() returns an unmodifiable copy
    //   - sources() and targets() return new maps
    //   - Edge is immutable

    public ConcreteEdgesGraph() {
        checkRep();
    }

    private void checkRep() {
        if (vertices == null || edges == null) throw new AssertionError("Rep invariant failed: null vertices or edges");
        for (Edge e : edges) {
            if (e.getSource() == null || e.getTarget() == null)
                throw new AssertionError("Edge has null endpoint");
            if (!vertices.contains(e.getSource()) || !vertices.contains(e.getTarget()))
                throw new AssertionError("Edge connects non-existent vertex");
            if (e.getWeight() <= 0) throw new AssertionError("Edge weight <= 0");
        }
    }

    @Override
    public boolean add(String vertex) {
        if (vertex == null) throw new IllegalArgumentException("vertex cannot be null");
        boolean added = vertices.add(vertex);
        checkRep();
        return added;
    }

    @Override
    public int set(String source, String target, int weight) {
        if (source == null || target == null) throw new IllegalArgumentException("vertices cannot be null");
        if (weight < 0) throw new IllegalArgumentException("weight cannot be negative");

        vertices.add(source);
        vertices.add(target);

        int prevWeight = 0;
        Iterator<Edge> iter = edges.iterator();
        while (iter.hasNext()) {
            Edge e = iter.next();
            if (e.getSource().equals(source) && e.getTarget().equals(target)) {
                prevWeight = e.getWeight();
                iter.remove();
                if (weight > 0) edges.add(new Edge(source, target, weight));
                checkRep();
                return prevWeight;
            }
        }

        if (weight > 0) edges.add(new Edge(source, target, weight));
        checkRep();
        return prevWeight;
    }

    @Override
    public boolean remove(String vertex) {
        if (!vertices.contains(vertex)) return false;
        vertices.remove(vertex);
        edges.removeIf(e -> e.getSource().equals(vertex) || e.getTarget().equals(vertex));
        checkRep();
        return true;
    }

    @Override
    public Set<String> vertices() {
        return Collections.unmodifiableSet(new HashSet<>(vertices));
    }

    @Override
    public Map<String, Integer> sources(String target) {
        Map<String, Integer> result = new HashMap<>();
        for (Edge e : edges) {
            if (e.getTarget().equals(target)) {
                result.put(e.getSource(), e.getWeight());
            }
        }
        return result;
    }

    @Override
    public Map<String, Integer> targets(String source) {
        Map<String, Integer> result = new HashMap<>();
        for (Edge e : edges) {
            if (e.getSource().equals(source)) {
                result.put(e.getTarget(), e.getWeight());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertices: ").append(vertices).append("\nEdges:\n");
        for (Edge e : edges) sb.append("  ").append(e).append("\n");
        return sb.toString();
    }
}

/** Immutable edge in a weighted directed graph. */
class Edge {

    private final String source;
    private final String target;
    private final int weight;

    // AF(source, target, weight) = directed edge from source -> target with weight
    // RI: source != null, target != null, weight > 0
    // Safety: immutable, private final fields

    public Edge(String source, String target, int weight) {
        if (source == null || target == null) throw new IllegalArgumentException("vertices cannot be null");
        if (weight <= 0) throw new IllegalArgumentException("weight must be positive");
        this.source = source;
        this.target = target;
        this.weight = weight;
        checkRep();
    }

    public void checkRep() {
        if (source == null || target == null) throw new AssertionError("Edge has null endpoint");
        if (weight <= 0) throw new AssertionError("Edge weight <= 0");
    }

    public String getSource() { return source; }
    public String getTarget() { return target; }
    public int getWeight() { return weight; }

    @Override
    public String toString() {
        return source + " -> " + target + " (" + weight + ")";
    }
}
