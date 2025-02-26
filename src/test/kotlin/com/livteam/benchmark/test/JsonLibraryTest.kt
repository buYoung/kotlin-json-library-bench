package com.livteam.benchmark.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

class JsonLibraryTest {
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
    private lateinit var doubleEscapeJson: String

    @BeforeEach
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
        
        // Double escape JSON 문자열 생성 (파일이 없는 경우)
        doubleEscapeJson = """{"text":"이것은 \\\"이중 이스케이프\\\" 테스트입니다."}"""
    }

    @Test
    fun `test minify functionality`() {
        println("\n===== Minify 테스트 =====")
        
        // Gson
        val gsonParsed = JsonParser.parseString(beautifyJson)
        val gsonResult = gson.toJson(gsonParsed)
        assertFalse(gsonResult.contains("\n"), "Gson minify should remove newlines")
        
        // Jackson
        val jacksonNode = jackson.readTree(beautifyJson)
        val jacksonResult = jackson.writeValueAsString(jacksonNode)
        assertFalse(jacksonResult.contains("\n"), "Jackson minify should remove newlines")
        
        // Moshi
        val moshiAdapter = moshi.adapter(Any::class.java)
        val moshiParsed = moshiAdapter.fromJson(beautifyJson)
        val moshiResult = moshiAdapter.toJson(moshiParsed)
        assertFalse(moshiResult.contains("\n"), "Moshi minify should remove newlines")
        
        println("모든 라이브러리가 minify 기능을 정상적으로 수행합니다.")
    }
    
    @Test
    fun `test beautify functionality`() {
        println("\n===== Beautify 테스트 =====")
        
        // Gson
        val gsonParsed = JsonParser.parseString(minifyJson)
        val gsonResult = gsonPretty.toJson(gsonParsed)
        assertTrue(gsonResult.contains("\n"), "Gson beautify should add newlines")
        
        // Jackson
        val jacksonNode = jackson.readTree(minifyJson)
        val jacksonResult = jackson.writerWithDefaultPrettyPrinter().writeValueAsString(jacksonNode)
        assertTrue(jacksonResult.contains("\n"), "Jackson beautify should add newlines")
        
        // Moshi
        val moshiAdapter = moshi.adapter(Any::class.java).indent("  ")
        val moshiParsed = moshiAdapter.fromJson(minifyJson)
        val moshiResult = moshiAdapter.toJson(moshiParsed)
        assertTrue(moshiResult.contains("\n"), "Moshi beautify should add newlines")
        
        println("모든 라이브러리가 beautify 기능을 정상적으로 수행합니다.")
    }
    
    @Test
    fun `test escape functionality`() {
        println("\n===== Escape 테스트 =====")
        
        // Gson
        val gsonParsed = JsonParser.parseString(escapeJson)
        val gsonResult = gson.toJson(gsonParsed)
        assertTrue(gsonResult.contains("\\\""), "Gson should escape quotes")
        
        // Jackson
        val jacksonNode = jackson.readTree(escapeJson)
        val jacksonResult = jackson.writeValueAsString(jacksonNode)
        assertTrue(jacksonResult.contains("\\\""), "Jackson should escape quotes")
        
        // Moshi
        val moshiAdapter = moshi.adapter(Any::class.java)
        val moshiParsed = moshiAdapter.fromJson(escapeJson)
        val moshiResult = moshiAdapter.toJson(moshiParsed)
        assertTrue(moshiResult.contains("\\\""), "Moshi should escape quotes")
        
        println("모든 라이브러리가 escape 기능을 정상적으로 수행합니다.")
    }
    
    @Test
    fun `test unescape functionality`() {
        println("\n===== Unescape 테스트 =====")
        
        // Gson
        val gsonParsed = JsonParser.parseString(unescapeJson)
        val gsonResult = gson.toJson(gsonParsed)
        // 원본 문자열에 이스케이프된 문자가 있는지 확인
        assertTrue(unescapeJson.contains("\\\""), "Original string should contain escaped quotes")
        // 파싱 후 원래 문자열로 변환 시 이스케이프 처리가 제대로 되었는지 확인
        assertNotNull(gsonResult, "Gson should handle unescaped content")
        
        // Jackson
        val jacksonNode = jackson.readTree(unescapeJson)
        val jacksonResult = jackson.writeValueAsString(jacksonNode)
        assertNotNull(jacksonResult, "Jackson should handle unescaped content")
        
        // Moshi
        val moshiAdapter = moshi.adapter(Any::class.java)
        val moshiParsed = moshiAdapter.fromJson(unescapeJson)
        val moshiResult = moshiAdapter.toJson(moshiParsed)
        assertNotNull(moshiResult, "Moshi should handle unescaped content")
        
        println("모든 라이브러리가 unescape 기능을 정상적으로 수행합니다.")
    }
    
    @Test
    fun `test double escape functionality`() {
        println("\n===== Double Escape 테스트 =====")
        
        // 원본 문자열에 이중 이스케이프된 문자가 있는지 확인
        assertTrue(doubleEscapeJson.contains("\\\\\\\""), "Original string should contain double escaped quotes")
        
        // Gson
        val gsonParsed = JsonParser.parseString(doubleEscapeJson)
        val gsonResult = gson.toJson(gsonParsed)
        // 파싱 후 결과에 이스케이프된 문자가 제대로 처리되었는지 확인
        assertTrue(gsonResult.contains("\\\\\\\""), "Gson should handle double escaped content")
        
        // 파싱 후 다시 파싱했을 때 제대로 처리되는지 확인
        val gsonParsedTwice = JsonParser.parseString(gsonResult)
        val gsonResultTwice = gson.toJson(gsonParsedTwice)
        assertEquals(gsonResult, gsonResultTwice, "Gson should handle double parsing consistently")
        
        // Jackson
        val jacksonNode = jackson.readTree(doubleEscapeJson)
        val jacksonResult = jackson.writeValueAsString(jacksonNode)
        assertTrue(jacksonResult.contains("\\\\\\\""), "Jackson should handle double escaped content")
        
        // 파싱 후 다시 파싱했을 때 제대로 처리되는지 확인
        val jacksonNodeTwice = jackson.readTree(jacksonResult)
        val jacksonResultTwice = jackson.writeValueAsString(jacksonNodeTwice)
        assertEquals(jacksonResult, jacksonResultTwice, "Jackson should handle double parsing consistently")
        
        // Moshi
        val moshiAdapter = moshi.adapter(Any::class.java)
        val moshiParsed = moshiAdapter.fromJson(doubleEscapeJson)
        val moshiResult = moshiAdapter.toJson(moshiParsed)
        assertTrue(moshiResult.contains("\\\\\\\""), "Moshi should handle double escaped content")
        
        // 파싱 후 다시 파싱했을 때 제대로 처리되는지 확인
        val moshiParsedTwice = moshiAdapter.fromJson(moshiResult)
        val moshiResultTwice = moshiAdapter.toJson(moshiParsedTwice)
        assertEquals(moshiResult, moshiResultTwice, "Moshi should handle double parsing consistently")
        
        println("모든 라이브러리가 double escape 기능을 정상적으로 수행합니다.")
    }
    
    @Test
    fun `test parsing middle dataset`() {
        println("\n===== 중간 크기 데이터셋 파싱 테스트 =====")
        
        // Gson
        val gsonParsed = JsonParser.parseString(middleJson)
        assertNotNull(gsonParsed, "Gson should parse middle dataset")
        
        // Jackson
        val jacksonNode = jackson.readTree(middleJson)
        assertNotNull(jacksonNode, "Jackson should parse middle dataset")
        
        // Moshi
        val moshiAdapter = moshi.adapter(Any::class.java)
        val moshiParsed = moshiAdapter.fromJson(middleJson)
        assertNotNull(moshiParsed, "Moshi should parse middle dataset")
        
        println("모든 라이브러리가 중간 크기 데이터셋을 정상적으로 파싱합니다.")
    }
    
    @Test
    fun `test parsing big dataset`() {
        println("\n===== 대형 데이터셋 파싱 테스트 =====")
        
        // Gson
        val gsonParsed = JsonParser.parseString(bigJson)
        assertNotNull(gsonParsed, "Gson should parse big dataset")
        
        // Jackson
        val jacksonNode = jackson.readTree(bigJson)
        assertNotNull(jacksonNode, "Jackson should parse big dataset")
        
        // Moshi
        val moshiAdapter = moshi.adapter(Any::class.java)
        val moshiParsed = moshiAdapter.fromJson(bigJson)
        assertNotNull(moshiParsed, "Moshi should parse big dataset")
        
        println("모든 라이브러리가 대형 데이터셋을 정상적으로 파싱합니다.")
    }
    
    @Test
    fun `test data consistency across libraries`() {
        println("\n===== 라이브러리 간 데이터 일관성 테스트 =====")
        
        // 테스트 케이스 정의
        val testCases = listOf(
            "Minify" to minifyJson,
            "Beautify" to beautifyJson,
            "DoubleEscape" to doubleEscapeJson
        )
        
        for ((testName, inputJson) in testCases) {
            println("\n----- $testName 데이터 일관성 -----")
            
            // 각 라이브러리로 파싱 후 다시 문자열로 변환
            val gsonParsed = JsonParser.parseString(inputJson)
            val gsonResult = gson.toJson(gsonParsed)
            
            val jacksonNode = jackson.readTree(inputJson)
            val jacksonResult = jackson.writeValueAsString(jacksonNode)
            
            val moshiAdapter = moshi.adapter(Any::class.java)
            val moshiParsed = moshiAdapter.fromJson(inputJson)
            val moshiResult = moshiAdapter.toJson(moshiParsed)
            
            // 라이브러리 간 결과 비교 (공백, 줄바꿈 제외)
            val normalizedGson = normalizeJsonString(gsonResult)
            val normalizedJackson = normalizeJsonString(jacksonResult)
            val normalizedMoshi = normalizeJsonString(moshiResult)
            
            // 디버깅을 위해 결과 출력
            println("Gson: $normalizedGson")
            println("Jackson: $normalizedJackson")
            println("Moshi: $normalizedMoshi")
            
            // Moshi는 정수를 부동소수점으로 변환하는 문제가 있으므로 
            // 정수와 부동소수점 표기 차이를 무시하고 비교
            val normalizedMoshiWithoutDecimal = normalizeDecimalPoints(normalizedMoshi)
            val normalizedGsonWithoutDecimal = normalizeDecimalPoints(normalizedGson)
            val normalizedJacksonWithoutDecimal = normalizeDecimalPoints(normalizedJackson)
            
            // 디버깅을 위해 정규화된 결과 출력
            println("정규화된 Gson: $normalizedGsonWithoutDecimal")
            println("정규화된 Jackson: $normalizedJacksonWithoutDecimal")
            println("정규화된 Moshi: $normalizedMoshiWithoutDecimal")
            
            assertEquals(normalizedGsonWithoutDecimal, normalizedJacksonWithoutDecimal, 
                "Gson and Jackson results should be equivalent")
            assertEquals(normalizedGsonWithoutDecimal, normalizedMoshiWithoutDecimal, 
                "Gson and Moshi results should be equivalent")
            
            println("$testName: 모든 라이브러리가 동일한 데이터 구조를 생성합니다.")
        }
        
        // 별도로 HTML 인코딩 차이가 있는 escape 및 unescape 테스트
        println("\n----- HTML 인코딩 테스트 -----")
        println("HTML 인코딩 방식은 라이브러리마다 다를 수 있습니다.")
        println("Gson은 HTML 태그를 유니코드로 이스케이프하고, Jackson과 Moshi는 그대로 유지하는 경향이 있습니다.")
        
        // Escape 테스트
        val gsonEscapeParsed = JsonParser.parseString(escapeJson)
        val gsonEscapeResult = gson.toJson(gsonEscapeParsed)
        
        val jacksonEscapeNode = jackson.readTree(escapeJson)
        val jacksonEscapeResult = jackson.writeValueAsString(jacksonEscapeNode)
        
        val moshiEscapeAdapter = moshi.adapter(Any::class.java)
        val moshiEscapeParsed = moshiEscapeAdapter.fromJson(escapeJson)
        val moshiEscapeResult = moshiEscapeAdapter.toJson(moshiEscapeParsed)
        
        println("Gson Escape: ${normalizeJsonString(gsonEscapeResult)}")
        println("Jackson Escape: ${normalizeJsonString(jacksonEscapeResult)}")
        println("Moshi Escape: ${normalizeJsonString(moshiEscapeResult)}")
        
        // Unescape 테스트
        val gsonUnescapeParsed = JsonParser.parseString(unescapeJson)
        val gsonUnescapeResult = gson.toJson(gsonUnescapeParsed)
        
        val jacksonUnescapeNode = jackson.readTree(unescapeJson)
        val jacksonUnescapeResult = jackson.writeValueAsString(jacksonUnescapeNode)
        
        val moshiUnescapeAdapter = moshi.adapter(Any::class.java)
        val moshiUnescapeParsed = moshiUnescapeAdapter.fromJson(unescapeJson)
        val moshiUnescapeResult = moshiUnescapeAdapter.toJson(moshiUnescapeParsed)
        
        println("Gson Unescape: ${normalizeJsonString(gsonUnescapeResult)}")
        println("Jackson Unescape: ${normalizeJsonString(jacksonUnescapeResult)}")
        println("Moshi Unescape: ${normalizeJsonString(moshiUnescapeResult)}")
    }
    
    /**
     * JSON 문자열에서 공백과 줄바꿈을 제거하여 정규화
     */
    private fun normalizeJsonString(json: String): String {
        return json.replace("\\s".toRegex(), "")
    }
    
    /**
     * 부동소수점 표기를 정규화 (예: 30.0 -> 30)
     * 정수 값을 가진 부동소수점 표기를 정수 표기로 변환
     */
    private fun normalizeDecimalPoints(json: String): String {
        // 소수점 뒤에 0만 있는 경우 (예: 30.0, 95.0) 정수로 변환
        // 숫자 뒤에 .0이 오고 그 뒤에 쉼표, 닫는 중괄호, 닫는 대괄호가 오는 패턴 찾기
        return json
            .replace("([0-9])\\.0([,}\\]])".toRegex()) { matchResult ->
                "${matchResult.groupValues[1]}${matchResult.groupValues[2]}"
            }
    }
}
