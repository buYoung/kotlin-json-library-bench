plugins {
    kotlin("jvm") version "1.9.22"
    id("me.champeau.jmh") version "0.7.2"
}

group = "com.livteam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // JSON Libraries
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("com.squareup.moshi:moshi:1.15.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    
    // JMH Dependencies
    jmh("org.openjdk.jmh:jmh-core:1.37")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    
    // Test Dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    
    // 테스트 결과를 콘솔에 출력하도록 설정
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    
    // 테스트 실행 시 JVM 옵션 설정
    jvmArgs("-Xms512m", "-Xmx1g")
    
    // 테스트 실행 시 시스템 속성 설정
    systemProperty("file.encoding", "UTF-8")
}

jmh {
    warmupIterations.set(3)  // 안정적인 워밍업
    iterations.set(10)       // 정확한 측정을 위한 반복 횟수
    fork.set(3)              // 적절한 포크 수
    jvmArgsAppend.set(listOf("-Xms4G", "-Xmx4G", "-XX:+UseG1GC"))  // GC 설정 최적화
    benchmarkMode.set(listOf("thrpt", "avgt"))  // 필요한 벤치마크 모드만 실행
    timeUnit.set("us")
    includes.add(".*Benchmark.*")
    resultFormat.set("text")  // 텍스트 형식으로 결과 출력
    resultsFile.set(project.file("${project.buildDir}/results/jmh/results.txt"))
    timeOnIteration.set("1s")  // 반복당 시간 제한
    synchronizeIterations.set(true)  // 반복 동기화
    failOnError.set(true)  // 오류 발생 시 실패 처리
}

// 벤치마크 실행을 위한 태스크 추가
tasks.register("bench") {
    dependsOn("jmh")
    group = "benchmark"
    description = "JSON 라이브러리 벤치마크 실행"
}