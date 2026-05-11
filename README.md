# sample_java4

Spring Boot 기반 Java 샘플 백엔드 프로젝트 (Book API)

## 프로젝트 요약

- 목적: Spring Boot 기반의 간단한 REST API 샘플 + 스캐너 검증용 의도적 취약점
- 도메인: Book CRUD + 검색 + Health Check
- 현재 상태: 학습/스캐너 검증용 픽스처 포함

## 기술 스택/버전

- Java 17 toolchain
- Spring Boot 3.4.2
- Gradle Wrapper 8.7 설정
- Validation: `jakarta.validation`

## 실행

```bash
./gradlew bootRun
```

## 빌드

```bash
./gradlew build
```

## 기능 명세(API)

### GET /health

응답: `{ "status": "ok" }`

### POST /books

요청 예시:

```json
{ "title": "Clean Code", "author": "Robert C. Martin", "price": 39.99 }
```

검증:

- title: 공백 불가
- author: 공백 불가
- price: 양수 필수

### GET /books

전체 목록 반환

### GET /books/{id}

- 존재 시 200 + book
- 미존재 시 404 + `{ "error": "not found" }`

### DELETE /books/{id}

- 존재 시 200 + `{ "status": "deleted" }`
- 미존재 시 404 + `{ "error": "not found" }`

### GET /books/search?q=키워드

title 기준 부분일치 검색(대소문자 무시)

## 데이터 저장 방식

- DB 없음, 메모리(Map) 저장
- 앱 재시작 시 데이터 초기화

## 의도적 취약점 (학습/스캐너 검증용)

운영에 절대 사용 금지. 분류와 위치:

### 설정 / 일반

- **Information Disclosure** — `application.properties` 의 `server.error.include-message=always`, `server.error.include-stacktrace=always`
- **Log Injection** — `BookController#searchBooks` 에서 사용자 입력 `q` 를 정화 없이 로그 기록

### `AdminController` 대체로 `VulnController` 에 집중 배치된 취약점

| Endpoint                  | CWE       | 카테고리                  |
| ------------------------- | --------- | ------------------------- |
| `GET /vuln/users`         | CWE-89    | SQL Injection             |
| `GET /vuln/exec`          | CWE-78    | OS Command Injection      |
| `GET /vuln/read`          | CWE-22    | Path Traversal            |
| `GET /vuln/download`      | CWE-22    | Path Traversal (nio)      |
| `POST /vuln/deserialize`  | CWE-502   | Insecure Deserialization  |
| `POST /vuln/xml`          | CWE-611   | XXE                       |
| `GET /vuln/fetch`         | CWE-918   | SSRF                      |
| `GET /vuln/token`         | CWE-327   | Weak Hash (MD5)           |
| `GET /vuln/redirect`      | CWE-601   | Open Redirect             |
| (소스 상수)               | CWE-798   | Hardcoded Credentials     |
