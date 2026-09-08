# FastAIHybrid — Multi-Modal & Dense-Sparse Hybrid Search Fusion for Java

## Core Purpose:
FastAIHybrid combines lexical keyword search (BM25, exact terms, identifiers) and dense semantic vector search (FastAIVectorDB, Embeddings) using deterministic Reciprocal Rank Fusion (RRF) and weighted score merging to guarantee near-zero hallucination.

## Key Features:
- **Reciprocal Rank Fusion (RRF)**: Merges sparse keyword ranks and dense embedding similarity into a unified score.
- **Lexical + Semantic Balance**: Solves both keyword-specific queries (exact IDs, function names) and conceptual natural language queries.
- **Graph & Vector Fusion**: Seamlessly blends structured subgraphs from FastAIGraph with unstructured text chunks from FastAIRag.
- **Pure Java 17 Zero-Bloat**: Lightning-fast, lock-free parallel execution without external Elasticsearch or heavy Lucene dependencies.
