# 🏗️ MSA Kafka Message Queue 실습 프로젝트 (비동기 방식)
> **Kafka를 활용한 메시지 기반 비동기 통신 및 이벤트 기반 아키텍처(EDA)**

이 프로젝트는 마이크로서비스 아키텍처(MSA) 환경에서 **Kafka**를 메시지 브로커로 사용하여 서비스 간 **비동기(Asynchronous) 통신**을 구현하고, 데이터 일관성을 유지하기 위한 **이벤트 기반 SAGA 패턴**을 학습하기 위한 실습용 프로젝트다.

---

## 🚀 0. 비동기 MSA와 Kafka
비동기 통신 방식은 서비스 간의 직접적인 의존성을 줄이고, 시스템의 전체적인 응답성과 가용성을 높이는 데 중요한 역할을 한다.

### 💡 Kafka를 사용하는 이유
- ✅ **느슨한 결합(Loose Coupling)**: 서비스들이 서로의 존재를 몰라도 이벤트를 주고받을 수 있다.
- ✅ **높은 처리량(Throughput)**: 대량의 이벤트를 빠르게 처리하고 저장할 수 있다.
- ✅ **내결함성(Fault Tolerance)**: 특정 서비스가 다운되어도 메시지는 Kafka에 보관되어 나중에 처리될 수 있다.
- ✅ **확장성**: 컨슈머 그룹을 통해 처리를 수평적으로 확장하기 용이하다.

---

## 📋 1. 프로젝트 개요
본 프로젝트는 Kafka를 중심으로 메시지를 발행(Publish)하고 구독(Subscribe)하는 구조로 설계되었다.

### 🏗️ 시스템 아키텍처
```plantuml
@startuml
!theme plain
skinparam componentStyle rectangle

package "External" {
    [Client / Browser] as Client
}

package "Message Broker" {
    component "Kafka" as Kafka
}

package "MSA Network" {
    package "Board Service (Async) (Port: 8085)" {
        component "Board Controller" as BS
        component "Board Service (Async)" as BService
        database "Board DB" as BDB
    }

    package "User Service (Async) (Port: 8084)" {
        component "User Controller" as US
        component "User Service (Async)" as UService
        database "User DB" as UDB
    }

    package "Point Service (Async) (Port: 8086)" {
        component "Point Controller" as PS
        component "Point Service (Async)" as PService
        database "Point DB" as PDB
    }
}

Client -> BS
BService -[bold]-> Kafka : Publish: BoardCreated
Kafka -[bold]-> PService : Subscribe: BoardCreated
Kafka -[bold]-> UService : Subscribe: BoardCreated
@enduml
```

### 🛠 Tech Stack
| Category | Technology |
| :--- | :--- |
| **Language** | ![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk) |
| **Framework** | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.x-green?logo=springboot) ![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-gray?logo=spring) |
| **Message Broker** | ![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?logo=apachekafka&logoColor=white) |
| **Database** | ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql) |
| **DevOps** | ![Docker](https://img.shields.io/badge/Docker-blue?logo=docker) ![Docker Compose](https://img.shields.io/badge/Docker_Compose-blue?logo=docker) |

---

## 🔄 2. 비즈니스 로직 흐름 (Choreography SAGA)
1. **게시글 작성**: `Board Service (Async)`에서 게시글을 DB에 저장하고 `BoardCreated` 이벤트를 Kafka `board-topic`에 발행한다.
2. **포인트 차감**: `Point Service (Async)`가 이벤트를 수신하여 사용자의 포인트를 차감한다.
3. **활동 점수 적립**: `User Service (Async)`가 이벤트를 수신하여 사용자의 활동 점수를 적립한다.

---

## ⚡ 4. 실행 방법

```bash
# Kafka 비동기 서비스 디렉터리로 이동
cd msa-kafka-message-queue-async

# 전체 서비스 빌드 및 실행 (DB + Kafka + Microservices)
docker-compose up -d --build
```

### 🔗 주요 엔드포인트

| Service | Method | URL | Description |
| :--- | :--- | :--- | :--- |
| **User** | `POST` | `http://localhost:8084/users/sign-up` | 회원가입 (1000pt 지급) |
| **Board** | `POST` | `http://localhost:8085/boards` | 게시글 작성 (이벤트 발행 시작) |
| **Point** | `GET` | `http://localhost:8086/points/{userId}` | 포인트 잔액 조회 |

---

## 📚 5. 핵심 학습 포인트 (Core Concepts)
