# FastAIHybrid API Reference

## Core Engine

### `FastAIHybrid`
High-performance Reciprocal Rank Fusion (RRF) and search result combiner.

* `fuse(List<Hit> lexicalHits, List<Hit> denseHits, int topN, double k)`: Fuses sparse/BM25 and dense/vector rankings into a single sorted score list.
* `fuseMultiple(List<List<Hit>> rankings, int topN, double k)`: Multi-way RRF across N ranking sources (BM25, vector, graph, rerank).

### `Hit`
Represents a scored search result:
* `id()`: Document or chunk ID.
* `score()`: Relevance or similarity score.
* `payload()`: Metadata or snippet string.
