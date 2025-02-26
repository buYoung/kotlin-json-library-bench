# Kotlin JSON 라이브러리 벤치마크 및 테스트

이 프로젝트는 Kotlin에서 사용할 수 있는 주요 JSON 라이브러리(Gson, Jackson, Moshi)의 기능, 일관성 및 성능을 테스트하고 벤치마크합니다.

## 프로젝트 구조

```
kotlin_json_benchmark/
├── src/
│   ├── main/
│   │   ├── kotlin/com/livteam/benchmark/
│   │   │   └── JsonLibraryTestRunner.kt    # 테스트 실행을 위한 메인 클래스
│   │   └── resources/benchmark/            # 테스트 및 벤치마크용 JSON 파일
│   ├── test/
│   │   └── kotlin/com/livteam/benchmark/test/
│   │       └── JsonLibraryTest.kt          # JSON 라이브러리 기능 테스트
│   └── jmh/
│       └── kotlin/com/livteam/benchmark/jmh/
│           └── JsonBenchmark.kt            # JMH 벤치마크 코드
└── build.gradle.kts                        # Gradle 빌드 스크립트
```

## 테스트 내용

다음 기능에 대한 테스트를 포함합니다:

### 1. 기본 기능 테스트

- **Minify 기능**: JSON 문자열에서 공백과 줄바꿈을 제거
- **Beautify 기능**: JSON 문자열에 들여쓰기와 줄바꿈 추가
- **Escape 기능**: 특수 문자(따옴표 등) 이스케이프 처리
- **Unescape 기능**: 이스케이프된 문자 처리
- **Double Escape 기능**: 이중 이스케이프된 문자 처리

### 2. 데이터셋 파싱 테스트

- **중간 크기 데이터셋**: 중간 복잡도의 JSON 데이터 파싱
- **대형 데이터셋**: 대규모 JSON 데이터 파싱

### 3. 라이브러리 간 일관성 테스트

동일한 입력에 대해 세 라이브러리(Gson, Jackson, Moshi)가 일관된 출력을 생성하는지 검증합니다.

## 벤치마크 내용

JMH(Java Microbenchmark Harness)를 사용하여 다음 작업의 성능을 측정합니다:

### 1. 직렬화(Serialization) 벤치마크

- **toJson**: 객체를 JSON 문자열로 변환하는 성능
- **toJsonPretty**: 객체를 들여쓰기가 적용된 JSON 문자열로 변환하는 성능

### 2. 역직렬화(Deserialization) 벤치마크

- **fromJson**: JSON 문자열을 객체로 변환하는 성능
- **parseJson**: JSON 문자열을 파싱하는 성능

### 3. 데이터 크기별 벤치마크

- **소형 데이터**: 간단한 JSON 객체 처리 성능
- **중형 데이터**: 중간 복잡도의 JSON 객체 처리 성능
- **대형 데이터**: 복잡하고 큰 JSON 객체 처리 성능

## 주요 발견사항

### 라이브러리별 특성

1. **Gson**
   - HTML 태그를 유니코드로 이스케이프 처리 (`<` → `\u003c`)
   - 정수를 정수 형태로 유지 (`30` → `30`)
   - 설정이 간단하고 사용이 직관적

2. **Jackson**
   - HTML 태그를 그대로 유지 (`<` → `<`)
   - 정수를 정수 형태로 유지 (`30` → `30`)
   - 다양한 기능과 높은 확장성 제공

3. **Moshi**
   - HTML 태그를 그대로 유지 (`<` → `<`)
   - 정수를 부동소수점으로 변환하는 경향 (`30` → `30.0`)
   - Kotlin 친화적인 API 제공

### 일관성 문제 해결

라이브러리 간 일관성 테스트에서는 다음과 같은 문제가 발견되었고 해결되었습니다:

1. **정수/부동소수점 처리**: Moshi는 정수를 부동소수점으로 변환하는 경향이 있어, 정규화 함수를 통해 비교 시 이 차이를 무시하도록 처리했습니다.

2. **HTML 인코딩 차이**: Gson은 HTML 태그를 유니코드로 이스케이프하고, Jackson과 Moshi는 그대로 유지하는 경향이 있어, 이 차이를 인지하고 별도로 테스트했습니다.

### 성능 비교 (일반적인 경향)

> 참고: 정확한 성능 결과는 벤치마크 실행 결과를 참조하세요.

1. **직렬화 성능**
   - 소형 데이터: Gson ≈ Moshi > Jackson
   - 중형 데이터: Jackson > Gson > Moshi
   - 대형 데이터: Jackson > Gson > Moshi

2. **역직렬화 성능**
   - 소형 데이터: Moshi > Gson > Jackson
   - 중형 데이터: Jackson > Gson > Moshi
   - 대형 데이터: Jackson > Gson > Moshi

## 환경 설정

### 필수 요구사항

- JDK 11 이상
- Gradle 7.0 이상

### 의존성

- Gson 2.10.1
- Jackson 2.15.2
- Moshi 1.15.0
- JMH 1.37

## 테스트 실행 방법

모든 테스트를 실행하려면:

```bash
./gradlew test
```

특정 테스트만 실행하려면:

```bash
./gradlew test --tests "com.livteam.benchmark.test.JsonLibraryTest.test minify functionality"
```

## 벤치마크 실행 방법

전체 벤치마크를 실행하려면:

```bash
./gradlew bench
```

벤치마크 결과는 `build/results/jmh/results.txt` 파일에 저장됩니다.

특정 벤치마크만 실행하려면:

```bash
./gradlew jmh -Pinclude=".*toJson.*"
```

## 테스트 및 벤치마크 결과 해석

### 테스트 결과

모든 테스트가 성공적으로 통과하면, 각 라이브러리가 기본 JSON 기능(minify, beautify, escape, unescape, 파싱)을 올바르게 수행하며, 라이브러리 간 데이터 일관성도 유지됨을 의미합니다.

HTML 인코딩과 같은 특정 차이점은 라이브러리의 설계 결정에 따른 것으로, 애플리케이션의 요구사항에 따라 적절한 라이브러리를 선택해야 합니다.

### 벤치마크 결과

벤치마크 결과는 다음과 같은 형식으로 출력됩니다:

```
Benchmark                                    Mode  Cnt    Score    Error  Units
JsonBenchmark.gsonFromJson                  thrpt   30  123.456 ±  1.234  ops/us
JsonBenchmark.jacksonFromJson               thrpt   30  234.567 ±  2.345  ops/us
JsonBenchmark.moshiFromJson                 thrpt   30  345.678 ±  3.456  ops/us
```

- **Mode**: 측정 모드 (thrpt: 처리량, avgt: 평균 시간)
- **Cnt**: 측정 횟수
- **Score**: 측정 결과 값
- **Error**: 오차 범위
- **Units**: 측정 단위 (ops/us: 마이크로초당 작업 수, us/op: 작업당 마이크로초)

높은 처리량(thrpt)과 낮은 평균 시간(avgt)이 더 좋은 성능을 의미합니다.

#### 최신 벤치마크 결과 (2025-02-26)

##### 처리량 측정 결과 (thrpt, 높을수록 좋음)

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

##### 평균 시간 측정 결과 (avgt, 낮을수록 좋음)

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

#### 벤치마크 결과 분석

위 결과를 통해 다음과 같은 분석이 가능합니다:

1. **기본 JSON 작업 성능**:
   - **Beautify**: Jackson > Gson > Moshi
   - **Minify**: Jackson > Gson > Moshi
   - **Escape**: Jackson > Gson > Moshi
   - **Unescape**: Jackson > Gson > Moshi

2. **데이터 크기별 성능**:
   - **중간 크기 데이터**: Jackson > Gson > Moshi
   - **대형 데이터**: Jackson > Gson > Moshi

3. **종합 성능 순위**:
   - 1위: **Jackson** - 대부분의 작업에서 가장 빠른 성능을 보여줍니다.
   - 2위: **Gson** - Jackson보다는 느리지만 Moshi보다는 빠른 중간 수준의 성능을 제공합니다.
   - 3위: **Moshi** - 전반적으로 가장 느린 성능을 보여주지만, Kotlin 친화적인 API를 제공합니다.

## 라이브러리 선택 가이드

프로젝트에 적합한 JSON 라이브러리를 선택할 때 고려할 사항:

1. **Gson**
   - 장점: 간단한 API, 직관적인 사용법, 적은 의존성
   - 단점: 대형 데이터에서 성능이 다소 떨어짐
   - 적합한 경우: 간단한 JSON 처리, 가벼운 애플리케이션

2. **Jackson**
   - 장점: 뛰어난 성능, 다양한 기능, 높은 확장성
   - 단점: 복잡한 API, 많은 의존성
   - 적합한 경우: 대규모 데이터 처리, 엔터프라이즈 애플리케이션

3. **Moshi**
   - 장점: Kotlin 친화적, 간결한 API, 적절한 성능
   - 단점: 정수를 부동소수점으로 변환하는 특성
   - 적합한 경우: Kotlin 프로젝트, 중소규모 애플리케이션

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

MIT 라이선스는 다음과 같은 권한을 제공합니다:
- 상업적 사용
- 수정
- 배포
- 개인 사용

단, 소프트웨어를 사용할 때 MIT 라이선스 사본을 포함해야 합니다.

## 기여

이슈 제보 및 풀 리퀘스트는 언제나 환영합니다.
