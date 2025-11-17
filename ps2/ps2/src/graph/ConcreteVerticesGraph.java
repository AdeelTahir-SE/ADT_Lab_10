package graph;

import java.util.*;

/**
 * Concrete implementation of a weighted directed graph using a list of Vertex objects.
 * Vertices are strings and edges are stored inside each Vertex.
 */
public class ConcreteVerticesGraph implements Graph<String> {

    private final List<Vertex> vertices = new ArrayList<>();

    public ConcreteVerticesGraph() { checkRep(); }

    private void checkRep() {
        if (vertices == null) throw new AssertionError("vertices is null");
        Set<String> seen = new HashSet<>();
        for (Vertex v : vertices) {
            if (v == null) throw new AssertionError("vertex is null");
            if (!seen.add(v.getLabel())) throw new AssertionError("duplicate vertex label");
            v.checkRep();
        }
    }

    @Override
    public boolean add(String vertex) {
        if (vertex == null) throw new IllegalArgumentException("vertex cannot be null");
        for (Vertex v : vertices) if (v.getLabel().equals(vertex)) return false;
        vertices.add(new Vertex(vertex));
        checkRep();
        return true;
    }

    @Override
    public int set(String source, String target, int weight) {
        if (source == null || target == null) throw new IllegalArgumentException("vertices cannot be null");
        if (weight < 0) throw new IllegalArgumentException("weight cannot be negative");

        Vertex src = null;
        Vertex tgt = null;

        // Look for existing vertices
        for (Vertex v : vertices) {
            if (v.getLabel().equals(source)) src = v;
            if (v.getLabel().equals(target)) tgt = v;
        }

        // Only create and add new vertices if not found
        if (src == null) {
            src = new Vertex(source);
            vertices.add(src);
        }
        if (tgt == null) {
            if (target.equals(source)) {
                tgt = src; // self-loop
            } else {
                tgt = new Vertex(target);
                vertices.add(tgt);
            }
        }

        // Update edges
        int prevWeight = src.setTarget(target, weight);
        tgt.setSource(source, weight);

        checkRep();
        return prevWeight;
    }

    @Override
    public boolean remove(String vertex) {
        Vertex toRemove = null;
        for (Vertex v : vertices) if (v.getLabel().equals(vertex)) { toRemove = v; break; }
        if (toRemove == null) return false;

        for (Vertex v : vertices) { v.removeSource(vertex); v.removeTarget(vertex); }
        vertices.remove(toRemove);
        checkRep();
        return true;
    }

    @Override
    public Set<String> vertices() {
        Set<String> result = new HashSet<>();
        for (Vertex v : vertices) result.add(v.getLabel());
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Map<String, Integer> sources(String target) {
        for (Vertex v : vertices) if (v.getLabel().equals(target)) return new HashMap<>(v.getSources());
        return new HashMap<>();
    }

    @Override
    public Map<String, Integer> targets(String source) {
        for (Vertex v : vertices) if (v.getLabel().equals(source)) return new HashMap<>(v.getTargets());
        return new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Vertices:\n");
        for (Vertex v : vertices) sb.append("  ").append(v).append("\n");
        return sb.toString();
    }
}

/** Mutable vertex in a weighted directed graph. */
class Vertex {

    private final String label;
    private final Map<String, Integer> sources = new HashMap<>();
    private final Map<String, Integer> targets = new HashMap<>();

    public Vertex(String label) {
        if (label == null) throw new IllegalArgumentException("label cannot be null");
        this.label = label;
        checkRep();
    }

    public void checkRep() {
        if (label == null) throw new AssertionError("label null");
        for (Map.Entry<String, Integer> e : sources.entrySet()) {
            if (e.getKey() == null || e.getValue() <= 0) throw new AssertionError("invalid source edge");
        }
        for (Map.Entry<String, Integer> e : targets.entrySet()) {
            if (e.getKey() == null || e.getValue() <= 0) throw new AssertionError("invalid target edge");
        }
    }

    public String getLabel() { return label; }
    public Map<String, Integer> getSources() { return new HashMap<>(sources); }
    public Map<String, Integer> getTargets() { return new HashMap<>(targets); }

    public int setTarget(String target, int weight) {
        int prev = targets.getOrDefault(target, 0);
        if (weight == 0) targets.remove(target);
        else targets.put(target, weight);
        checkRep();
        return prev;
    }

    public int setSource(String source, int weight) {
        int prev = sources.getOrDefault(source, 0);
        if (weight == 0) sources.remove(source);
        else sources.put(source, weight);
        checkRep();
        return prev;
    }

    public void removeTarget(String target) { targets.remove(target); checkRep(); }
    public void removeSource(String source) { sources.remove(source); checkRep(); }

    @Override
    public String toString() {
        return label + " [sources=" + sources + ", targets=" + targets + "]";
    }
}
