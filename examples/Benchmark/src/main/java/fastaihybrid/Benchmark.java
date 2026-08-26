package fastaihybrid;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JMH Microbenchmark — FastAIHybrid Reciprocal Rank Fusion throughput.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-server", "-XX:+UseG1GC", "-Xms256m", "-Xmx256m"})
public class Benchmark {

    private List<FastAIHybrid.Hit> lexical;
    private List<FastAIHybrid.Hit> dense;

    @Setup(Level.Trial)
    public void setup() {
        this.lexical = new ArrayList<>(50);
        this.dense = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            this.lexical.add(new FastAIHybrid.Hit("doc_" + i, "Lexical hit document " + i, 10.0 - (i * 0.1)));
            this.dense.add(new FastAIHybrid.Hit("doc_" + (49 - i), "Dense vector hit document " + (49 - i), 0.99 - (i * 0.01)));
        }
    }

    @Benchmark
    public List<FastAIHybrid.Hit> benchmarkReciprocalRankFusion() {
        return FastAIHybrid.fuse(this.lexical, this.dense, 10, 60);
    }
}
