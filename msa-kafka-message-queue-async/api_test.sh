#!/bin/bash

RANDOM_ID=$RANDOM
EMAIL="test${RANDOM_ID}@example.com"
PASSWORD="password123"

echo "### 1. 회원가입 API 테스트 (POST /api/users/sign-up)"
echo "이메일: $EMAIL"
curl -s -X POST http://localhost:8000/api/users/sign-up \
     -H "Content-Type: application/json" \
     -d "{\"email\":\"$EMAIL\", \"name\":\"테스터\", \"password\":\"$PASSWORD\"}"
echo -e "\n회원가입 완료\n"

sleep 3

echo "### 2. 로그인 API 테스트 (POST /api/users/login)"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8000/api/users/login \
     -H "Content-Type: application/json" \
     -d "{\"email\":\"$EMAIL\", \"password\":\"$PASSWORD\"}")
echo "응답 데이터: $LOGIN_RESPONSE"

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo -e "추출된 JWT 토큰: $TOKEN\n"

if [ -z "$TOKEN" ]; then
  echo "토큰 발급 실패로 테스트를 중단합니다."
  exit 1
fi

# 토큰에서 userId 추출 (base64 decoding)
PAYLOAD=$(echo $TOKEN | cut -d'.' -f2)
# base64 padding 처리
LEN=$((${#PAYLOAD} % 4))
if [ $LEN -eq 2 ]; then PAYLOAD="${PAYLOAD}=="; elif [ $LEN -eq 3 ]; then PAYLOAD="${PAYLOAD}="; fi
USER_ID=$(echo $PAYLOAD | base64 -d | grep -o '"sub":"[^"]*' | cut -d'"' -f4)
echo "추출된 User ID: $USER_ID"

echo "### 3. 게시글 작성 API 테스트 (POST /api/boards)"
curl -s -X POST http://localhost:8000/api/boards \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{"title":"테스트 게시글", "content":"게시글 내용입니다."}'
echo -e "\n게시글 작성 완료\n"

sleep 3

echo "### 4. 전체 게시글 조회 API 테스트 (GET /api/boards)"
ALL_BOARDS=$(curl -s -X GET http://localhost:8000/api/boards)
echo "$ALL_BOARDS" | python3 -m json.tool
echo -e "\n"

# 첫 번째 게시글 ID 추출
BOARD_ID=$(echo "$ALL_BOARDS" | grep -o '"boardId":[0-9]*' | head -n 1 | cut -d':' -f2)

if [ ! -z "$BOARD_ID" ]; then
  echo "### 5. 특정 게시글 조회 API 테스트 (GET /api/boards/$BOARD_ID)"
  curl -s -X GET http://localhost:8000/api/boards/$BOARD_ID | python3 -m json.tool
  echo -e "\n"
else
  echo "게시글이 조회되지 않아 특정 게시글 조회 테스트를 건너뜁니다."
fi

echo "### 6. 내부 데이터 검증 (포인트 및 활동 점수)"
echo "사용자 포인트 조회 (userId: $USER_ID):"
curl -s http://localhost:8086/internal/points/$USER_ID
echo -e "\n"

echo "사용자 정보 및 활동 점수 조회 (userId: $USER_ID):"
curl -s http://localhost:8084/internal/users/$USER_ID | python3 -m json.tool
echo -e "\n"
