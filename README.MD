# 🏗️ MSA 실습 프로젝트 가이드

이 저장소는 마이크로서비스 아키텍처(MSA)의 다양한 구현 방식과 패턴을 학습하기 위한 공간이다.

## 📂 프로젝트 구조

현재 프로젝트는 통신 방식 및 핵심 기술에 따라 다음과 같이 구성되어 있다.

### 1. [REST API 기반 MSA (SAGA Pattern - 동기 방식)](msa-rest-api-sync/msa-rest-api-sync.md)
- **위치**: `msa-rest-api-sync/msa-rest-api-sync.md` (상세 설명) / `/msa-rest-api-sync` (소스 코드)
- **주요 기술**: Spring Boot, Spring Data JPA, MySQL, Docker, RestClient
- **설명**: 서비스 간 직접적인 HTTP 통신(동기 방식)을 통해 데이터를 주고받으며, 데이터 일관성을 위해 SAGA 패턴(Orchestration 방식)을 구현한 예제다.
- **포함 서비스**: `boardservice-sync`, `pointservice-sync`, `userservice-sync`

### 2. [Kafka 기반 MSA (비동기 방식)](msa-kafka-message-queue-async/msa-kafka-message-queue-async.md)
- **위치**: `msa-kafka-message-queue-async/msa-kafka-message-queue-async.md` (상세 설명) / `/msa-kafka-message-queue-async` (소스 코드)
- **주요 기술**: Spring Boot, Apache Kafka, Docker
- **설명**: 메시지 브로커인 Kafka를 활용하여 서비스 간 비동기 통신 및 이벤트 기반 아키텍처(EDA)를 구현하는 예제다.
- **포함 서비스**: `boardservice-async`, `pointservice-async`, `userservice-async`

---

## 💡 MSA 통신 방식: 동기(Sync) vs 비동기(Async)

MSA 환경에서는 서비스 간 통신 방식을 상황에 맞게 선택하는 것이 중요하다. 두 방식은 상호 배타적인 것이 아니며, 많은 경우 혼용하여 사용한다.

### 1. 동기 통신 (Synchronous)
- **특징**: 요청을 보내면 응답이 올 때까지 기다리는 방식 (Request-Response)
- **사용 사례**:
    - 응답 결과가 다음 작업의 필수 조건일 때 (예: 포인트 차감 확인 후 게시글 작성)
    - 사용자에게 즉각적인 처리 결과 응답이 필요할 때
- **구현 방식**: REST API (RestClient, OpenFeign 등)

---

### 2. 비동기 통신 (Asynchronous)
- **특징**: 요청을 보낸 후 응답을 기다리지 않고 다음 작업을 수행하는 방식 (Messaging)
- **사용 사례**:
    - 처리가 끝날 때까지 사용자가 기다릴 필요가 없는 부가 작업 (예: 게시글 작성 후 활동 점수 적립)
    - 실패 시 보상 트랜잭션 처리
- **구현 방식**: Message Queue (Kafka, RabbitMQ 등)

> **핵심 가이드**: "무조건 비동기가 정답은 아니다." [토스의 환전 서비스 사례](https://www.youtube.com/watch?t=590&v=xpwRTu47fqY&feature=youtu.be)와 같이, 데이터의 성격과 사용자 경험을 고려하여 **동기**와 **비동기**를 적절히 혼합하여 설계하는 것이 실무적인 접근이다.

---

### 3. MSA와 쿠버네티스(Kubernetes)
- **핵심**: "MSA라고 해서 반드시 쿠버네티스가 정답은 아니다."
- **설명**: 
    - 서비스 규모가 작을 때(3~5개 내외)는 쿠버네티스 도입이 오히려 오버스펙이 될 수 있다.
    - 실제 실무에서도 EC2나 ECS만으로 안정적으로 운영하는 사례가 많다.
---


## 🚀 시작하기

각 디렉터리 하위의 `README.MD` 파일을 참고하여 각 프로젝트의 상세 내용 및 실행 방법을 확인하자.

- [REST API 프로젝트 바로가기](msa-rest-api-sync/msa-rest-api-sync.md)
- [Kafka Message Queue 프로젝트 바로가기](msa-kafka-message-queue-async/msa-kafka-message-queue-async.md)
