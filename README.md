
# Meeting Room Reservation API

회의실 예약 시스템 REST API

## 기술 스택

- Java 17
- Spring Boot 3.4.5
- Spring Data JPA
- MySQL (Docker 사용)
- Swagger (springdoc-openapi)
- Gradle
- JUnit5

## 실행 방법 (Docker)

$ docker-compose up --build

## Swagger 문서

    # 접속
      http://localhost:8080/swagger-ui.html

    # 사용자 목록 (userId, userNm, password, email)

      'e101010', '김재원', 'pwd10', 'e101010@gmail.com'
      'e202020', '송길수', 'pwd20', 'e202020@gmail.com'
      'e303030', '이수현', 'pwd30', 'e303030@gmail.com'
      'e404040', '이범석', 'pwd40', 'e404040@gmail.com'

    # 회의실 목록 (roomCd, roomNm, capacity)

      'A101', '대회의실', 10
      'B202', '소회의실', 20
      'C303', '큐빅룸', 15

    # 회의실 예약 등록, 수정 // 시작, 종료 시간은 yyyy-MM-dd HH:mm 형식으로 작성
       ex)	
            {
             "userId": "e101010",
             "roomCd": "A101",
             "startTime": "2025-05-06 10:00", // yyyy-MM-dd HH:mm
             "endTime": "2025-05-06 11:00" // yyyy-MM-dd HH:mm
            }

    # 회의실 예약 조회
      - 월간 조회(yyyyMM)
      - 일간 조회(yyyyMMdd)

## 테스트 실행 방법

$ ./gradlew test