package com.livteam.benchmark.jmh

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.Buffer
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.io.File
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput, Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 3, jvmArgs = ["-Xms4G", "-Xmx4G", "-XX:+UseG1GC"])
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(4) // CPU 코어 수의 약 1/2 정도 사용
open class JsonBenchmark {
    private lateinit var gson: Gson
    private lateinit var gsonPretty: Gson
    private lateinit var jackson: ObjectMapper
    private lateinit var moshi: Moshi
    private lateinit var minifyJson: String
    private lateinit var beautifyJson: String
    private lateinit var escapeJson: String
    private lateinit var unescapeJson: String
    private lateinit var middleJson: String
    private lateinit var bigJson: String

    @Setup(Level.Trial)
    fun setup() {
        gson = Gson()
        gsonPretty = GsonBuilder().setPrettyPrinting().create()
        jackson = jacksonObjectMapper()
        moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val resourcePath = "src/main/resources/benchmark"
        minifyJson = File("$resourcePath/minify_test.json").readText()
        beautifyJson = File("$resourcePath/beautify_test.json").readText()
        escapeJson = File("$resourcePath/escape_test.json").readText()
        unescapeJson = File("$resourcePath/unescape_test.json").readText()
        middleJson = File("$resourcePath/middle_dataset_test.json").readText()
        bigJson = File("$resourcePath/big_dataset_test.json").readText()
    }

    // Minify Benchmarks
    @Benchmark
    fun gsonMinify(blackhole: Blackhole) {
        val jsonElement = JsonParser.parseString(beautifyJson)
        blackhole.consume(gson.toJson(jsonElement))
    }

    @Benchmark
    fun jacksonMinify(blackhole: Blackhole) {
        val node = jackson.readTree(beautifyJson)
        blackhole.consume(jackson.writeValueAsString(node))
    }
    
    @Benchmark
    fun moshiMinify(blackhole: Blackhole) {
        val jsonAdapter = moshi.adapter(Any::class.java)
        val parsedJson = jsonAdapter.fromJson(beautifyJson)
        blackhole.consume(jsonAdapter.toJson(parsedJson))
    }

    // Beautify Benchmarks
    @Benchmark
    fun gsonBeautify(blackhole: Blackhole) {
        val jsonElement = JsonParser.parseString(minifyJson)
        blackhole.consume(gsonPretty.toJson(jsonElement))
    }

    @Benchmark
    fun jacksonBeautify(blackhole: Blackhole) {
        val node = jackson.readTree(minifyJson)
        blackhole.consume(jackson.writerWithDefaultPrettyPrinter().writeValueAsString(node))
    }
    
    @Benchmark
    fun moshiBeautify(blackhole: Blackhole) {
        val jsonAdapter = moshi.adapter(Any::class.java).indent("  ")
        val parsedJson = jsonAdapter.fromJson(minifyJson)
        blackhole.consume(jsonAdapter.toJson(parsedJson))
    }

    // Escape Benchmarks
    @Benchmark
    fun gsonEscape(blackhole: Blackhole) {
        val jsonElement = JsonParser.parseString(escapeJson)
        blackhole.consume(gson.toJson(jsonElement))
    }

    @Benchmark
    fun jacksonEscape(blackhole: Blackhole) {
        val node = jackson.readTree(escapeJson)
        blackhole.consume(jackson.writeValueAsString(node))
    }
    
    @Benchmark
    fun moshiEscape(blackhole: Blackhole) {
        val jsonAdapter = moshi.adapter(Any::class.java)
        val parsedJson = jsonAdapter.fromJson(escapeJson)
        blackhole.consume(jsonAdapter.toJson(parsedJson))
    }

    // Unescape Benchmarks
    @Benchmark
    fun gsonUnescape(blackhole: Blackhole) {
        val jsonElement = JsonParser.parseString(unescapeJson)
        blackhole.consume(gson.toJson(jsonElement))
    }

    @Benchmark
    fun jacksonUnescape(blackhole: Blackhole) {
        val node = jackson.readTree(unescapeJson)
        blackhole.consume(jackson.writeValueAsString(node))
    }
    
    @Benchmark
    fun moshiUnescape(blackhole: Blackhole) {
        val jsonAdapter = moshi.adapter(Any::class.java)
        val parsedJson = jsonAdapter.fromJson(unescapeJson)
        blackhole.consume(jsonAdapter.toJson(parsedJson))
    }

    // Middle Dataset Benchmarks
    @Benchmark
    fun gsonMiddleDataset(blackhole: Blackhole) {
        val jsonElement = JsonParser.parseString(middleJson)
        blackhole.consume(gson.toJson(jsonElement))
    }

    @Benchmark
    fun jacksonMiddleDataset(blackhole: Blackhole) {
        val node = jackson.readTree(middleJson)
        blackhole.consume(jackson.writeValueAsString(node))
    }
    
    @Benchmark
    fun moshiMiddleDataset(blackhole: Blackhole) {
        val jsonAdapter = moshi.adapter(Any::class.java)
        val parsedJson = jsonAdapter.fromJson(middleJson)
        blackhole.consume(jsonAdapter.toJson(parsedJson))
    }

    // Big Dataset Benchmarks
    @Benchmark
    fun gsonBigDataset(blackhole: Blackhole) {
        val jsonElement = JsonParser.parseString(bigJson)
        blackhole.consume(gson.toJson(jsonElement))
    }

    @Benchmark
    fun jacksonBigDataset(blackhole: Blackhole) {
        val node = jackson.readTree(bigJson)
        blackhole.consume(jackson.writeValueAsString(node))
    }
    
    @Benchmark
    fun moshiBigDataset(blackhole: Blackhole) {
        val jsonAdapter = moshi.adapter(Any::class.java)
        val parsedJson = jsonAdapter.fromJson(bigJson)
        blackhole.consume(jsonAdapter.toJson(parsedJson))
    }
}
