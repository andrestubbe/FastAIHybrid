package fastaihybrid;

import fastaihybrid.FastAIHybrid.Hit;
import java.util.List;

/**
 * Interactive Demo showcasing FastAIHybrid Reciprocal Rank Fusion (RRF).
 */
public class Demo {

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("     ⚡ FastAIHybrid — Dense-Sparse Hybrid Search Fusion Demo            ");
        System.out.println("=========================================================================\n");

        // 1. Sparse Lexical Search Results (e.g. BM25 / Exact Keywords)
        List<Hit> lexical = List.of(
            new Hit("doc_101", "FastAI streaming documentation and architecture", 14.2),
            new Hit("doc_102", "Configuring HttpClient timeouts and connection pools", 11.5),
            new Hit("doc_103", "Low-latency network pipelines in Java", 6.8)
        );

        // 2. Dense Semantic Vector Search Results (e.g. FastAIVectorDB)
        List<Hit> dense = List.of(
            new Hit("doc_103", "Low-latency network pipelines in Java", 0.94),
            new Hit("doc_101", "FastAI streaming documentation and architecture", 0.89),
            new Hit("doc_104", "Embedding generation with local ONNX models", 0.81)
        );

        System.out.println("📄 Sparse Lexical Hits (BM25):");
        for (Hit h : lexical) {
            System.out.printf("   • [%s] score=%.2f -> %s%n", h.id(), h.score(), h.text());
        }
        System.out.println();

        System.out.println("🧠 Dense Semantic Hits (Vector DB):");
        for (Hit h : dense) {
            System.out.printf("   • [%s] score=%.2f -> %s%n", h.id(), h.score(), h.text());
        }
        System.out.println();

        // 3. Reciprocal Rank Fusion (RRF)
        System.out.println("🔀 Reciprocal Rank Fusion Results (Top 3, k=60):");
        List<Hit> fused = FastAIHybrid.fuse(lexical, dense, 3, 60);
        for (int i = 0; i < fused.size(); i++) {
            Hit h = fused.get(i);
            System.out.printf("   %d. [%s] RRF Score=%.5f -> %s%n", (i + 1), h.id(), h.score(), h.text());
        }

        System.out.println("\n✅ FastAIHybrid fusion complete: balanced semantic intent with exact keyword matches.");
    }
}
