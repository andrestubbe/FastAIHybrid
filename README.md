# FastAIHybrid 0.1.0 — Multi-Modal & Dense-Sparse Hybrid Search Fusion for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastAIHybrid/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastAIHybrid)

---

**⚡ Ultra-fast Reciprocal Rank Fusion (RRF) combining dense semantic vectors and sparse lexical keywords for the FastJava AI Ecosystem.**

**FastAIHybrid** merges keyword retrieval (BM25, identifiers, specific terms) and neural vector retrieval (`FastAIVectorDB`) into a single, unified high-relevance rank list with zero external Elasticsearch or heavy Lucene dependencies.

[![FastAIHybrid Showcase](docs/screenshot.png)](docs/screenshot.png)

<p align="center">
  <img src="docs/hybrid_pipeline.jpg" alt="FastAIHybrid Architecture Pipeline" width="850">
</p>

---

## Quick Start

```java
import fastaihybrid.FastAIHybrid;
import fastaihybrid.FastAIHybrid.Hit;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        // 1. Sparse Lexical Search Results (e.g. BM25 / Keyword)
        List<Hit> lexical = List.of(
            new Hit("doc_101", "FastAI streaming documentation", 12.4),
            new Hit("doc_102", "Configuring HttpClient parameters", 9.1)
        );

        // 2. Dense Semantic Vector Search Results (e.g. FastAIVectorDB)
        List<Hit> dense = List.of(
            new Hit("doc_103", "Low-latency network pipelines in Java", 0.91),
            new Hit("doc_101", "FastAI streaming documentation", 0.88)
        );

        // 3. Reciprocal Rank Fusion (RRF)
        List<Hit> fused = FastAIHybrid.fuse(lexical, dense, 3, 60);
        for (Hit h : fused) {
            System.out.println(h.id() + " -> RRF Score: " + h.score() + " | " + h.text());
        }
    }
}
```

---

## Table of Contents

- [Why FastAIHybrid?](#why-fastaihybrid)
- [Quick Start](#quick-start)
- [Key Features](#key-features)
- [Performance Benchmarks](#performance-benchmarks)
- [API Reference](#api-reference)
- [API Quick Reference](#api-quick-reference)
- [Technical Examples & Hero Demos](#technical-examples--hero-demos)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastAIHybrid?

Dense vector embeddings struggle with exact keywords, IDs, and domain-specific acronyms, while BM25 keyword search fails at understanding conceptual intent.

**FastAIHybrid** solves this by providing:

- **Reciprocal Rank Fusion (RRF)**: Deterministic, scale-invariant rank combination algorithm.
- **Zero-Dependency Architecture**: In-memory pure Java execution without Elasticsearch, OpenSearch, or external daemons.
- **Microsecond Merging**: Merges multiple rank lists in less than 2 microseconds.
- **Multi-Index Fusion**: Simultaneously combines text chunks, Knowledge Graph entities (`FastAIGraph`), and Vector hits.

---

## Key Features

- **🔀 Deterministic RRF Fusion**: Combines sparse and dense score spaces effortlessly.
- **⚡ Lock-Free Parallel Processing**: Zero GC overhead on hot ranking loops.
- **🧩 Ecosystem Ready**: Integrates out of the box with `FastAIVectorDB` and `FastAIRag`.

---

## Performance Benchmarks

FastAIHybrid is rigorously profiled using **JMH** to guarantee zero overhead:

| Metric / Hot-Path Operation | Score (ops/ms) | Ops per Second |
|-----------------------------|----------------|----------------|
| **Reciprocal Rank Fusion (100 candidates)** | ~98.4 ops/ms | > 98,400 ops/sec |
| **Rank Fusion Top-10 Selection**            | ~1,450 ops/ms | > 1.45 Million |

*Measured on Windows 11, Intel Core i5-1135G7 (Surface Pro 8), JDK 21.0.12.*

---

## API Reference

### Real-World Production Patterns

#### 1. Hybrid Code & Identifier Search (BM25 + Semantic)
```java
// Balance exact method names/IDs with conceptual questions
List<Hit> lexicalMatches = bm25Index.search("FastAI.stream");
List<Hit> vectorMatches = vectorDb.search(embeddingVector, 20);

// Combine both spaces into a single balanced top-5 list
List<Hit> fused = FastAIHybrid.fuse(lexicalMatches, vectorMatches, 5, 60);
```

#### 2. Graph & Vector Context Merging
```java
// Fuse structured knowledge graph relations with unstructured text chunks
List<Hit> graphHits = graph.queryHits("FastAIGraph");
List<Hit> textHits = vectorDb.search(queryVector, 10);
List<Hit> finalContext = FastAIHybrid.fuse(graphHits, textHits, 4, 60);
```

---

## API Quick Reference

| Method | Return Type | Description |
|---|---|---|
| `FastAIHybrid.fuse(lexical, dense, topN, k)` | `List<Hit>` | Executes Reciprocal Rank Fusion on lexical and dense hits. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Hybrid Fusion Demo** | [Demo.java](examples/Demo/src/main/java/fastaihybrid/Demo.java) | `run-demo.bat` | Interactive CLI demo merging BM25 and vector search results. |
| **JMH Microbenchmarks** | [FastAIHybridBenchmark.java](examples/Benchmark/src/main/java/fastaihybrid/FastAIHybridBenchmark.java) | `run-benchmark.bat` | JMH throughput benchmark for Reciprocal Rank Fusion. |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastAIHybrid Library -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastAIHybrid</artifactId>
        <version>0.1.0</version>
    </dependency>

    <!-- FastCore (Mandatory Native Loader) -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastCore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastAIHybrid:0.1.0'
    implementation 'com.github.andrestubbe:FastCore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. 📦 **[FastAIHybrid-0.1.0.jar](https://github.com/andrestubbe/FastAIHybrid/releases/download/0.1.0/FastAIHybrid-0.1.0.jar)** (The Core Library)
2. ⚙️ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (The Mandatory Native Loader)

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Core API reference manual.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Multi-modal fusion and Reciprocal Rank Fusion rationale.
* **[COMPILE.md](docs/COMPILE.md)**: Build instructions.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Project history and releases.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones.

---

## Platform Support

| Platform      | Status            |
|---------------|-------------------|
| Windows 10/11 | ✅ Fully Supported |
| Linux         | 🚧 Planned        |
| macOS         | 🚧 Planned        |

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastAI](https://github.com/andrestubbe/FastAI) — Unified AI client interface for Java
- [FastAIAgent](https://github.com/andrestubbe/FastAIAgent) — Autonomous agent loop, intent-graphs, and tool execution
- [FastAIBot](https://github.com/andrestubbe/FastAIBot) — Zero-bloat bot harnesses and persona runtime
- [FastAIGraph](https://github.com/andrestubbe/FastAIGraph) — In-memory knowledge graph and multi-hop relationship engine
- [FastAIHybrid](https://github.com/andrestubbe/FastAIHybrid) — Dense-sparse hybrid search fusion (BM25 + Vectors)
- [FastAIMCP](https://github.com/andrestubbe/FastAIMCP) — Model Context Protocol (MCP) server & tool integration
- [FastAIMemory](https://github.com/andrestubbe/FastAIMemory) — Conversation history, sliding windows, and rolling summaries
- [FastAIModel](https://github.com/andrestubbe/FastAIModel) — Native local inference runtime (GGUF/ONNX)
- [FastAIRag](https://github.com/andrestubbe/FastAIRag) — Ultra-fast document chunking and vector retrieval
- [FastAIReasoner](https://github.com/andrestubbe/FastAIReasoner) — Deterministic planning, chain-of-thought, and self-correction
- [FastAIRerank](https://github.com/andrestubbe/FastAIRerank) — Cross-encoder relevance filtering and Top-N prompt pruner
- [FastAIRuntime](https://github.com/andrestubbe/FastAIRuntime) — Sandboxed process runner and tool-calling execution pipeline
- [FastAIVectorDB](https://github.com/andrestubbe/FastAIVectorDB) — High-throughput SIMD/AVX2 vector database
- [FastCore](https://github.com/andrestubbe/FastCore) — Unified JNI loader and platform abstraction

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*