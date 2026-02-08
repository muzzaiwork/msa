# 🧪 MSA API 기능 테스트 결과 보고서

본 문서는 `msa-kafka-message-queue-async` 프로젝트의 주요 API 기능 및 비동기 이벤트 기반 비즈니스 로직(포인트 차감, 활동 점수 적립, 데이터 복제)에 대한 테스트 결과를 정리한 것입니다.

## 1. 테스트 환경
- **진입점**: API Gateway (http://localhost:8000)
- **인증**: JWT (JSON Web Token)
- **통신 방식**: Kafka 기반 비동기 메시징 (EDA)

---

## 2. API 테스트 시나리오 및 결과

### ✅ 2.1 회원가입 (POST /api/users/sign-up)
- **목적**: 새로운 사용자를 등록하고 초기 포인트(1000)가 지급되는지 확인
- **결과**: **성공**
- **비고**: 회원가입 시 `user.signed-up` 이벤트가 발행되어 `Board Service`의 로컬 DB에 유저 정보가 복제됨.

### ✅ 2.2 로그인 (POST /api/users/login)
- **목적**: 등록된 계정으로 인증을 수행하고 JWT 토큰을 발급받음
- **결과**: **성공**
- **응답**: `{"token": "eyJhbGciOiJIUzUxMiJ9..."}`

### ✅ 2.3 게시글 작성 (POST /api/boards) - [인증 필요]
- **목적**: 발급된 JWT 토큰을 사용하여 게시글을 작성하고, 비동기로 포인트가 차감되며 활동 점수가 적립되는지 확인
- **결과**: **성공** (상태 코드 204 No Content)
- **비동기 로직 검증**:
    - **포인트 차감**: `Board Service`가 발행한 `board.created` 이벤트를 `Point Service`가 수신하여 100 포인트 차감 확인 (1000 -> 900).
    - **활동 점수 적립**: `Board Service`가 발행한 `board.created` 이벤트를 `User Service`가 수신하여 10 활동 점수 적립 확인 (0 -> 10).

### ✅ 2.4 전체 게시글 조회 (GET /api/boards)
- **목적**: 작성된 게시글 목록을 조회하고, 작성자 정보(이름)가 포함되어 있는지 확인
- **결과**: **성공**
- **검증**: `Board Service` 로컬 DB에 복제된 사용자 정보를 조인하여 외부 API 호출 없이 정보를 가져옴.

### ✅ 2.5 특정 게시글 조회 (GET /api/boards/{id})
- **목적**: 특정 ID의 게시글 상세 정보를 조회
- **결과**: **성공**

---

## 3. 핵심 비즈니스 로직 검증 결과 요약

| 기능 | 검증 항목 | 결과 | 방식 |
| :--- | :--- | :---: | :--- |
| **인증(Auth)** | JWT 발급 및 Gateway 필터 검증 | 성공 | Centralized JWT |
| **데이터 복제** | 회원가입 시 Board 서비스로 유저 정보 복제 | 성공 | Kafka (user.signed-up) |
| **포인트 시스템** | 게시글 작성 시 100 포인트 자동 차감 | 성공 | Kafka (board.created) |
| **활동 점수** | 게시글 작성 시 10 활동 점수 자동 적립 | 성공 | Kafka (board.created) |
| **조회 최적화** | 게시글 조회 시 로컬 DB 조인을 통한 유저 정보 표시 | 성공 | Data Replication |

## 4. 최종 결론
프로젝트의 모든 주요 API와 서비스 간 비동기 연동 로직이 설계된 대로 정상 작동함을 확인하였습니다. 특히 Kafka를 통한 서비스 간 느슨한 결합(Loose Coupling)과 결과적 일관성(Eventual Consistency)이 올바르게 구현되었습니다.
