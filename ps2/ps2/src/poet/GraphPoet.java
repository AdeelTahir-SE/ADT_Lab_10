package poet;

import graph.Graph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * GraphPoet constructs a word affinity graph from a text corpus
 * and generates poetry by inserting "bridge words" between word pairs.
 */
public class GraphPoet {

    private final Graph<String> graph = Graph.empty();
    private final Map<String, String> caseMap = new HashMap<>();

    // AF: graph = word affinity graph from corpus
    // RI: graph != null
    // Safety from rep exposure: graph is private final; access through poem() returns new string

    public GraphPoet(File corpus) throws IOException {
        List<String> words = Files.readAllLines(corpus.toPath())
                .stream()
                .flatMap(line -> Arrays.stream(line.split("\\s+")))
                .filter(w -> !w.isEmpty())
                .toList();

        String prev = null;
        for (String w : words) {
            String lower = w.toLowerCase();
            graph.add(lower);

            // Preserve original casing of first occurrence
            caseMap.putIfAbsent(lower, w);

            if (prev != null) {
                int prevWeight = graph.targets(prev).getOrDefault(lower, 0);
                graph.set(prev, lower, prevWeight + 1);
            }
            prev = lower;
        }
        checkRep();
    }

    private void checkRep() {
        assert graph != null;
        assert caseMap != null;
    }

    /**
     * Generates a poem by inserting bridge words between input words.
     * Preserves the original input case.
     */
    public String poem(String input) {
        if (input == null || input.isBlank()) return "";

        String[] tokens = input.split("\\s+");
        List<String> output = new ArrayList<>();
        output.add(tokens[0]); // keep first word as is

        for (int i = 0; i < tokens.length - 1; i++) {
            String w1 = tokens[i].replaceAll("\\p{Punct}+$", ""); // remove trailing punctuation
            String w2 = tokens[i + 1].replaceAll("\\p{Punct}+$", "");

            String bridge = null;
            int maxWeight = 0;

            Map<String, Integer> targetsW1 = graph.targets(w1.toLowerCase());

            for (String candidate : targetsW1.keySet()) {
                Map<String, Integer> targetsCandidate = graph.targets(candidate);
                if (targetsCandidate.containsKey(w2.toLowerCase())) {
                    int pathWeight = targetsW1.get(candidate) + targetsCandidate.get(w2.toLowerCase());
                    if (pathWeight > maxWeight) {
                        maxWeight = pathWeight;
                        bridge = candidate;
                    }
                }
            }

            if (bridge != null) {
                output.add(caseMap.getOrDefault(bridge, bridge));
            }

            output.add(tokens[i + 1]); // keep original word with punctuation
        }

        return String.join(" ", output);
    }

    @Override
    public String toString() {
        return graph.toString();
    }
}
