/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for instance methods of Graph<String>.
 *
 * Testing strategy:
 *
 * vertices():
 *   - initially empty
 *   - after adding 1 vertex
 *   - after adding multiple vertices
 *   - after removing vertex
 *
 * add():
 *   - return true for new vertex
 *   - return false for duplicate
 *
 * remove():
 *   - remove existing vertex
 *   - remove missing vertex
 *   - remove vertex with incoming/outgoing edges
 *
 * set(source, target, weight):
 *   - add new edge (weight > 0)
 *   - update edge (replace weight)
 *   - delete edge (weight = 0)
 *   - self-loop
 *   - auto-create vertices
 *
 * sources():
 *   - no incoming edges
 *   - single source
 *   - multiple sources
 *
 * targets():
 *   - no outgoing edges
 *   - single target
 *   - multiple targets
 */
public abstract class GraphInstanceTest {

    /** @return a new empty graph implementation under test */
    public abstract Graph<String> emptyInstance();

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // ---------------------------------------------------------
    // vertices()
    // ---------------------------------------------------------

    @Test
    public void testInitialVerticesEmpty() {
        assertEquals("expected new graph to have no vertices",
                Collections.emptySet(), emptyInstance().vertices());
    }

    @Test
    public void testAddVertex() {
        Graph<String> g = emptyInstance();
        assertTrue(g.add("A"));
        assertEquals(Set.of("A"), g.vertices());
    }

    @Test
    public void testAddDuplicateVertex() {
        Graph<String> g = emptyInstance();
        g.add("A");
        assertFalse("adding duplicate vertex should return false", g.add("A"));
    }

    @Test
    public void testRemoveExistingVertex() {
        Graph<String> g = emptyInstance();
        g.add("A");

        assertTrue(g.remove("A"));
        assertEquals(Collections.emptySet(), g.vertices());
    }

    @Test
    public void testRemoveMissingVertex() {
        Graph<String> g = emptyInstance();

        assertFalse(g.remove("Z"));
    }

    @Test
    public void testRemoveVertexWithEdges() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 5); // outgoing
        g.set("C", "A", 4); // incoming

        assertTrue(g.remove("A"));

        assertFalse(g.vertices().contains("A"));
        assertEquals(Collections.emptyMap(), g.sources("A"));
        assertEquals(Collections.emptyMap(), g.targets("A"));
    }

    // ---------------------------------------------------------
    // set(source, target, weight)
    // ---------------------------------------------------------

    @Test
    public void testAddNewEdge() {
        Graph<String> g = emptyInstance();

        int old = g.set("A", "B", 3);

        assertEquals(0, old);
        assertEquals(Map.of("B", 3), g.targets("A"));
        assertEquals(Map.of("A", 3), g.sources("B"));
    }

    @Test
    public void testUpdateEdgeWeight() {
        Graph<String> g = emptyInstance();

        g.set("A", "B", 3);
        int old = g.set("A", "B", 7);

        assertEquals(3, old);
        assertEquals(Map.of("B", 7), g.targets("A"));
    }

    @Test
    public void testDeleteEdge() {
        Graph<String> g = emptyInstance();

        g.set("A", "B", 3);
        int old = g.set("A", "B", 0);

        assertEquals(3, old);
        assertEquals(Collections.emptyMap(), g.targets("A"));
        assertEquals(Collections.emptyMap(), g.sources("B"));
    }

    @Test
    public void testAutoCreateVerticesWithSet() {
        Graph<String> g = emptyInstance();

        g.set("X", "Y", 10);

        assertTrue(g.vertices().containsAll(Set.of("X", "Y")));
    }

    @Test
    public void testSelfLoopEdge() {
        Graph<String> g = emptyInstance();

        g.set("A", "A", 5);

        assertEquals(Map.of("A", 5), g.targets("A"));
        assertEquals(Map.of("A", 5), g.sources("A"));
    }

    // ---------------------------------------------------------
    // sources() and targets()
    // ---------------------------------------------------------

    @Test
    public void testSourcesNone() {
        Graph<String> g = emptyInstance();
        g.add("A");

        assertEquals(Collections.emptyMap(), g.sources("A"));
    }

    @Test
    public void testSourcesOne() {
        Graph<String> g = emptyInstance();

        g.set("X", "A", 4);

        assertEquals(Map.of("X", 4), g.sources("A"));
    }

    @Test
    public void testSourcesMultiple() {
        Graph<String> g = emptyInstance();

        g.set("X", "A", 3);
        g.set("Y", "A", 7);

        assertEquals(Map.of("X", 3, "Y", 7), g.sources("A"));
    }

    @Test
    public void testTargetsNone() {
        Graph<String> g = emptyInstance();
        g.add("A");

        assertEquals(Collections.emptyMap(), g.targets("A"));
    }

    @Test
    public void testTargetsOne() {
        Graph<String> g = emptyInstance();

        g.set("A", "B", 4);

        assertEquals(Map.of("B", 4), g.targets("A"));
    }

    @Test
    public void testTargetsMultiple() {
        Graph<String> g = emptyInstance();

        g.set("A", "X", 2);
        g.set("A", "Y", 6);

        assertEquals(Map.of("X", 2, "Y", 6), g.targets("A"));
    }
}
