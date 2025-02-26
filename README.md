# Kotlin JSON Library Benchmarks and Tests

This project benchmarks and tests the functionality, consistency, and performance of major JSON libraries (Gson, Jackson, Moshi) available for Kotlin.

*Read this in other languages: [한국어](README_ko.md)*

## Project Structure

```
kotlin_json_benchmark/
├── src/
│   ├── main/
│   │   ├── kotlin/com/livteam/benchmark/
│   │   │   └── JsonLibraryTestRunner.kt    # Main class for running tests
│   │   └── resources/benchmark/            # JSON files for testing and benchmarking
│   ├── test/
│   │   └── kotlin/com/livteam/benchmark/test/
│   │       └── JsonLibraryTest.kt          # JSON library functionality tests
│   └── jmh/
│       └── kotlin/com/livteam/benchmark/jmh/
│           └── JsonBenchmark.kt            # JMH benchmark code
└── build.gradle.kts                        # Gradle build script
```

## Test Content

The project includes tests for the following features:

### 1. Basic Functionality Tests

- **Minify**: Removing whitespace and line breaks from JSON strings
- **Beautify**: Adding indentation and line breaks to JSON strings
- **Escape**: Handling special characters (quotes, etc.)
- **Unescape**: Processing escaped characters
- **Double Escape**: Processing double-escaped characters

### 2. Dataset Parsing Tests

- **Medium-sized Dataset**: Parsing JSON data of medium complexity
- **Large Dataset**: Parsing large-scale JSON data

### 3. Cross-Library Consistency Tests

Verifying that all three libraries (Gson, Jackson, Moshi) produce consistent outputs for the same inputs.

## Benchmark Content

Using JMH (Java Microbenchmark Harness), the project measures the performance of:

### 1. Serialization Benchmarks

- **toJson**: Performance of converting objects to JSON strings
- **toJsonPretty**: Performance of converting objects to indented JSON strings

### 2. Deserialization Benchmarks

- **fromJson**: Performance of converting JSON strings to objects
- **parseJson**: Performance of parsing JSON strings

### 3. Data Size-based Benchmarks

- **Small Data**: Performance of processing simple JSON objects
- **Medium Data**: Performance of processing JSON objects of medium complexity
- **Large Data**: Performance of processing complex and large JSON objects

## Key Findings

### Library Characteristics

1. **Gson**
   - Escapes HTML tags to Unicode (`<` → `\u003c`)
   - Maintains integers as integers (`30` → `30`)
   - Simple configuration and intuitive usage

2. **Jackson**
   - Preserves HTML tags as-is (`<` → `<`)
   - Maintains integers as integers (`30` → `30`)
   - Provides diverse features and high extensibility

3. **Moshi**
   - Preserves HTML tags as-is (`<` → `<`)
   - Tends to convert integers to floating-point (`30` → `30.0`)
   - Provides Kotlin-friendly API

### Consistency Issues Resolved

The cross-library consistency tests revealed and addressed the following issues:

1. **Integer/Floating-point Handling**: Moshi tends to convert integers to floating-point, so a normalization function was implemented to ignore this difference during comparisons.

2. **HTML Encoding Differences**: Gson escapes HTML tags to Unicode, while Jackson and Moshi preserve them as-is. This difference was acknowledged and tested separately.

### Performance Comparison (General Trends)

> Note: For precise performance results, refer to the benchmark execution results.

1. **Serialization Performance**
   - Small Data: Gson ≈ Moshi > Jackson
   - Medium Data: Jackson > Gson > Moshi
   - Large Data: Jackson > Gson > Moshi

2. **Deserialization Performance**
   - Small Data: Moshi > Gson > Jackson
   - Medium Data: Jackson > Gson > Moshi
   - Large Data: Jackson > Gson > Moshi

## Environment Setup

### Requirements

- JDK 11 or higher
- Gradle 7.0 or higher

### Dependencies

- Gson 2.10.1
- Jackson 2.15.2
- Moshi 1.15.0
- JMH 1.37

## Running Tests

To run all tests:

```bash
./gradlew test
```

To run a specific test:

```bash
./gradlew test --tests "com.livteam.benchmark.test.JsonLibraryTest.test minify functionality"
```

## Running Benchmarks

To run all benchmarks:

```bash
./gradlew bench
```

Benchmark results are saved in the `build/results/jmh/results.txt` file.

To run specific benchmarks:

```bash
./gradlew jmh -Pinclude=".*toJson.*"
```

## Interpreting Test and Benchmark Results

### Test Results

When all tests pass successfully, it means that each library correctly performs basic JSON operations (minify, beautify, escape, unescape, parsing) and maintains data consistency across libraries.

Specific differences like HTML encoding are due to library design decisions and should be considered when selecting a library based on application requirements.

### Benchmark Results

Benchmark results are output in the following format:

```
Benchmark                                    Mode  Cnt    Score    Error  Units
JsonBenchmark.gsonFromJson                  thrpt   30  123.456 ±  1.234  ops/us
JsonBenchmark.jacksonFromJson               thrpt   30  234.567 ±  2.345  ops/us
JsonBenchmark.moshiFromJson                 thrpt   30  345.678 ±  3.456  ops/us
```

- **Mode**: Measurement mode (thrpt: throughput, avgt: average time)
- **Cnt**: Number of measurements
- **Score**: Measurement result value
- **Error**: Error margin
- **Units**: Measurement unit (ops/us: operations per microsecond, us/op: microseconds per operation)

Higher throughput (thrpt) and lower average time (avgt) indicate better performance.

#### Latest Benchmark Results (2025-02-26)

##### Throughput Measurement Results (thrpt, higher is better)

```
Benchmark                            Mode  Cnt      Score      Error   Units
JsonBenchmark.gsonBeautify          thrpt   30      2.234 ±    0.018  ops/us
JsonBenchmark.gsonBigDataset        thrpt   30     ≈ 10⁻⁴             ops/us
JsonBenchmark.gsonEscape            thrpt   30      2.386 ±    0.026  ops/us
JsonBenchmark.gsonMiddleDataset     thrpt   30      0.003 ±    0.001  ops/us
JsonBenchmark.gsonMinify            thrpt   30      1.438 ±    0.013  ops/us
JsonBenchmark.gsonUnescape          thrpt   30      2.357 ±    0.018  ops/us
JsonBenchmark.jacksonBeautify       thrpt   30      3.237 ±    0.027  ops/us
JsonBenchmark.jacksonBigDataset     thrpt   30     ≈ 10⁻³             ops/us
JsonBenchmark.jacksonEscape         thrpt   30      3.920 ±    0.088  ops/us
JsonBenchmark.jacksonMiddleDataset  thrpt   30      0.004 ±    0.001  ops/us
JsonBenchmark.jacksonMinify         thrpt   30      1.993 ±    0.014  ops/us
JsonBenchmark.jacksonUnescape       thrpt   30      4.253 ±    0.043  ops/us
JsonBenchmark.moshiBeautify         thrpt   30      0.258 ±    0.003  ops/us
JsonBenchmark.moshiBigDataset       thrpt   30     ≈ 10⁻⁴             ops/us
JsonBenchmark.moshiEscape           thrpt   30      0.437 ±    0.007  ops/us
JsonBenchmark.moshiMiddleDataset    thrpt   30     ≈ 10⁻³             ops/us
JsonBenchmark.moshiMinify           thrpt   30      0.162 ±    0.002  ops/us
JsonBenchmark.moshiUnescape         thrpt   30      0.500 ±    0.006  ops/us
```

##### Average Time Measurement Results (avgt, lower is better)

```
Benchmark                            Mode  Cnt      Score      Error   Units
JsonBenchmark.gsonBeautify           avgt   30      1.986 ±    0.019   us/op
JsonBenchmark.gsonBigDataset         avgt   30  15950.970 ±  125.793   us/op
JsonBenchmark.gsonEscape             avgt   30      1.824 ±    0.016   us/op
JsonBenchmark.gsonMiddleDataset      avgt   30   1539.594 ±   14.038   us/op
JsonBenchmark.gsonMinify             avgt   30      2.803 ±    0.023   us/op
JsonBenchmark.gsonUnescape           avgt   30      1.691 ±    0.014   us/op
JsonBenchmark.jacksonBeautify        avgt   30      1.206 ±    0.009   us/op
JsonBenchmark.jacksonBigDataset      avgt   30  11596.878 ±  103.669   us/op
JsonBenchmark.jacksonEscape          avgt   30      1.027 ±    0.013   us/op
JsonBenchmark.jacksonMiddleDataset   avgt   30   1103.676 ±    9.516   us/op
JsonBenchmark.jacksonMinify          avgt   30      1.965 ±    0.017   us/op
JsonBenchmark.jacksonUnescape        avgt   30      0.939 ±    0.010   us/op
JsonBenchmark.moshiBeautify          avgt   30     15.792 ±    0.347   us/op
JsonBenchmark.moshiBigDataset        avgt   30  94789.022 ± 1407.730   us/op
JsonBenchmark.moshiEscape            avgt   30      9.183 ±    0.210   us/op
JsonBenchmark.moshiMiddleDataset     avgt   30   9409.673 ±  139.245   us/op
JsonBenchmark.moshiMinify            avgt   30     25.578 ±    0.238   us/op
JsonBenchmark.moshiUnescape          avgt   30      8.252 ±    0.231   us/op
```

#### Benchmark Results Analysis

From the above results, the following analysis can be made:

1. **Basic JSON Operation Performance**:
   - **Beautify**: Jackson > Gson > Moshi
   - **Minify**: Jackson > Gson > Moshi
   - **Escape**: Jackson > Gson > Moshi
   - **Unescape**: Jackson > Gson > Moshi

2. **Data Size-based Performance**:
   - **Medium-sized Data**: Jackson > Gson > Moshi
   - **Large Data**: Jackson > Gson > Moshi

3. **Overall Performance Ranking**:
   - 1st: **Jackson** - Shows the fastest performance in most operations.
   - 2nd: **Gson** - Slower than Jackson but faster than Moshi, providing mid-level performance.
   - 3rd: **Moshi** - Shows the slowest performance overall but provides a Kotlin-friendly API.

## Library Selection Guide

Considerations when selecting a JSON library for your project:

1. **Gson**
   - Pros: Simple API, intuitive usage, minimal dependencies
   - Cons: Somewhat lower performance with large data
   - Suitable for: Simple JSON processing, lightweight applications

2. **Jackson**
   - Pros: Excellent performance, diverse features, high extensibility
   - Cons: Complex API, many dependencies
   - Suitable for: Large-scale data processing, enterprise applications

3. **Moshi**
   - Pros: Kotlin-friendly, concise API, adequate performance
   - Cons: Tendency to convert integers to floating-point
   - Suitable for: Kotlin projects, small to medium-sized applications

## License

This project is distributed under the MIT License. See the [LICENSE](LICENSE) file for details.

The MIT License grants the following permissions:
- Commercial use
- Modification
- Distribution
- Private use

However, a copy of the MIT License must be included when using the software.

## Contributing

Issues and pull requests are always welcome.
