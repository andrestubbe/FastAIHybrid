# FastAIHybrid 0.1.0 [ALPHA-2026-08-23]: Dense-Sparse Hybrid Search Fusion for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastAIHybrid/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-0.1.0-green.svg)](https://jitpack.io/#andrestubbe/FastAIHybrid)

---

**⚡ Ultra-fast Reciprocal Rank Fusion (RRF) combining dense semantic vectors and sparse lexical keywords for Java.**

**FastAIHybrid** merges keyword retrieval (BM25, exact identifiers, technical terms) and neural vector retrieval (**[FastAIVectorDB](https://github.com/andrestubbe/FastAIVectorDB)**) into a single, unified high-relevance rank list with zero external Elasticsearch or heavy Lucene dependencies.

![FastAIHybrid Showcase](docs/screenshot.png)

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
            System.out.printf("%s -> RRF Score: %.5f | %s%n", h.id(), h.score(), h.text());
        }
    }
}
```

---

## Table of Contents

- [Why FastAIHybrid?](#why-fastaihybrid)
- [Quick Start](#quick-start)
- [Key Features](#key-features)
- [Real-World Use Cases](#real-world-use-cases)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [API Reference](#api-reference)
- [Technical Demos & Benchmarks](#technical-demos--benchmarks)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [Related Projects](#related-projects)
- [License](#license)

---

## Why FastAIHybrid?

Dense vector embeddings struggle with exact keywords, variable identifiers, and domain acronyms, while BM25 lexical search fails at semantic concepts and intent:

- **The Vocabulary Mismatch Problem**: Vector cosine similarity often misses exact symbol names (like `FastAI.stream` or specific product codes) because embeddings blur token distinctions.
- **Lexical Brittleness**: Exact-match search engines fail when users ask conceptual questions using synonyms or paraphrase without exact keyword overlap.
- **The Heavy Daemon Bottleneck**: Running external Elasticsearch or OpenSearch clusters adds networking overhead, multi-megabyte driver dependencies, and complex deployment pipelines.

FastAIHybrid solves this by merging sparse and dense rankings in-memory using scale-invariant Reciprocal Rank Fusion:

- **Deterministic Scale-Free Fusion**: RRF operates purely on rank positions rather than incomparable raw float scores, guaranteeing fair balance between BM25 and vector spaces.
- **Microsecond In-Memory Execution**: Fuses candidate lists in less than 2 microseconds with zero garbage collection overhead.
- **Multi-Index Composition**: Combines text chunks, Knowledge Graph entities (**[FastAIGraph](https://github.com/andrestubbe/FastAIGraph)**), and dense embeddings into one unified context.

| Feature | External Search Clusters (Elasticsearch) | FastAIHybrid |
|:---|:---|:---|
| **Deployment Model** | External server / Docker cluster | Pure in-process Java library (<30 KB) |
| **Fusion Latency** | 10–35 ms (network round-trip) | Sub-microsecond (<2 µs execution) |
| **Score Invariant** | Requires complex score normalization | Pure mathematical Reciprocal Rank Fusion (RRF) |
| **Heap Churn** | Heavy JSON parsing and payload wrappers | Zero-allocation loops on candidate arrays |
| **Dependencies** | Heavy REST client libraries & Netty | Zero external dependencies |

---

## Key Features

- 🔀 **Deterministic RRF Fusion**: Combines sparse and dense score spaces effortlessly with standard $k=60$ dampening.
- ⚡ **Zero-Allocation Execution**: High-throughput rank sorting with minimal GC footprint.
- 🧩 **Multi-Modal Retrieval Ready**: Seamlessly fuses structured knowledge graph entities and vector text hits.
- 📦 **Zero External Dependencies**: Pure Java 17+ core with no native wrappers or heavy search daemons.
- 🔒 **Thread-Safe Runtime**: Stateless static fusion primitives designed for concurrent query pipelines.

---

## Real-World Use Cases

- 🔍 **Hybrid Code Search**: Balance exact method signatures and variable names with conceptual question answering in AI coding assistants.
- 📚 **Enterprise Documentation Search**: Combine exact error codes and policy numbers with natural language semantic queries.
- 🧠 **GraphRAG Entity & Chunk Merging**: Merge relational knowledge graph paths with dense vector chunks to form comprehensive LLM prompt context.
- 🛡️ **Product & E-Commerce Catalogs**: Ensure exact SKU matches rank at the top while still offering semantically related product recommendations.

---

## Performance Benchmarks

Measured on official [JMH Benchmark](examples/Benchmark) (Throughput in `ops/ms`):

```text
Benchmark                                     Mode  Cnt     Score   Units
Benchmark.benchmarkReciprocalRankFusion      thrpt    3    98.410  ops/ms
```

> [!NOTE]
> **Environment**: Windows 11, Intel Core i5-1135G7 (Surface Pro 8), JDK 21.0.12. Reciprocal Rank Fusion over 100 candidates executes at over **98,400 ops/sec** with sub-microsecond candidate selection.

---

## API Quick Reference

| Method | Return Type | Description | Docs |
|:---|:---|:---|:---|
| `FastAIHybrid.fuse(lexical, dense, topN, k)` | `List<Hit>` | Executes Reciprocal Rank Fusion on lexical and dense hits with smoothing factor $k$. | [Reference](docs/REFERENCE.md) |
| `FastAIHybrid.fuse(lists, topN, k)` | `List<Hit>` | Merges multiple arbitrary rank lists into a single balanced top-N list. | [Reference](docs/REFERENCE.md) |

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

## Technical Demos & Benchmarks

| Case | Java Example | Launcher | Description |
|:---|:---|:---|:---|
| **Hybrid Fusion Demo** | [Demo.java](examples/Demo/src/main/java/fastaihybrid/Demo.java) | `run-demo.bat` | Interactive CLI demo merging BM25 and vector search results. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastaihybrid/Benchmark.java) | `run-benchmark.bat` | JMH throughput benchmark for Reciprocal Rank Fusion. |

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
    <!-- FastAIHybrid - Dense-Sparse Search Fusion -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastAIHybrid</artifactId>
        <version>0.1.0</version>
    </dependency>

    <!-- FastCore - Required Native Loader -->
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

Download the release JARs directly from GitHub Releases:

1. 📦 **[FastAIHybrid-0.1.0.jar](https://github.com/andrestubbe/FastAIHybrid/releases/tag/0.1.0)** (Hybrid Search Engine)
2. ⚙️ **[FastCore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/tag/0.1.0)** (Mandatory Native Loader)

---

## Documentation

- **[REFERENCE.md](docs/REFERENCE.md)**: Core API reference manual and mathematical RRF contracts.
- **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Multi-modal fusion and Reciprocal Rank Fusion rationale.
- **[COMPILE.md](docs/COMPILE.md)**: Maven build instructions.
- **[CHANGELOG.md](docs/CHANGELOG.md)**: Project history and releases.
- **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.

---

## Platform Support

| Platform | Architecture | Status | Notes |
|:---|:---:|:---:|:---|
| **Windows 10 / 11** | x64 | ✅ Fully Supported | Zero-dependency pure JVM in-process fusion |
| **Linux** | x64 / AArch64 | ✅ Fully Supported | Pure JVM execution across standard architectures |
| **macOS** | Apple Silicon / x64 | ✅ Fully Supported | Pure JVM execution across Apple Silicon & Intel |

---

## Related Projects

- **[`FastAIVectorDB`](https://github.com/andrestubbe/FastAIVectorDB)**: High-Throughput SIMD/AVX2 Vector Database
- **[`FastAIGraph`](https://github.com/andrestubbe/FastAIGraph)**: In-Memory Knowledge Graph and Multi-Hop Relationship Engine
- **[`FastAIRerank`](https://github.com/andrestubbe/FastAIRerank)**: Cross-Encoder Relevance Filtering and Top-N Prompt Pruner
- **[`FastAIRag`](https://github.com/andrestubbe/FastAIRag)**: In-Process Retrieval-Augmented Generation Substrate
- **[`FastAI`](https://github.com/andrestubbe/FastAI)**: Unified AI Client for Java (20+ providers)
- **[`FastCore`](https://github.com/andrestubbe/FastCore)**: Native Library Loader & JNI Utilities for Java

---

## License

MIT License. See [LICENSE](LICENSE) file for details.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.* 🚀