package fastaihybrid;

import java.util.*;

/**
 * High-performance Reciprocal Rank Fusion (RRF) and hybrid search combiner.
 */
public final class FastAIHybrid {

    public record Hit(String id, String text, double score) {}

    public static List<Hit> fuse(final List<Hit> lexicalResults, final List<Hit> denseResults, final int topN, final int k) {
        final Map<String, Hit> hitMap = new HashMap<>();
        final Map<String, Double> rrfScores = new HashMap<>();

        // Score lexical ranks
        if (lexicalResults != null) {
            for (int rank = 0; rank < lexicalResults.size(); rank++) {
                final Hit hit = lexicalResults.get(rank);
                hitMap.putIfAbsent(hit.id(), hit);
                rrfScores.put(hit.id(), rrfScores.getOrDefault(hit.id(), 0.0) + (1.0 / (k + rank + 1)));
            }
        }

        // Score dense ranks
        if (denseResults != null) {
            for (int rank = 0; rank < denseResults.size(); rank++) {
                final Hit hit = denseResults.get(rank);
                hitMap.putIfAbsent(hit.id(), hit);
                rrfScores.put(hit.id(), rrfScores.getOrDefault(hit.id(), 0.0) + (1.0 / (k + rank + 1)));
            }
        }

        final List<Hit> fused = new ArrayList<>(hitMap.size());
        for (final Map.Entry<String, Double> entry : rrfScores.entrySet()) {
            final Hit original = hitMap.get(entry.getKey());
            fused.add(new Hit(original.id(), original.text(), entry.getValue()));
        }

        fused.sort((a, b) -> Double.compare(b.score(), a.score()));
        return fused.subList(0, Math.min(topN, fused.size()));
    }
}
