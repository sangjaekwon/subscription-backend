# Subscription Backend

구독 서비스의 결제 내역과 갱신 일정을 한곳에서 관리하고,  
사용자 구독 데이터를 기반으로 AI 분석 결과까지 제공하는 백엔드 서비스입니다.

단순 CRUD 구현에 그치지 않고, 인증/인가, 소셜 로그인, 토큰 재발급, 이메일 인증, 알림, 테스트, 배포 자동화까지 포함해 **실제 서비스 운영을 고려한 구조**를 목표로 개발했습니다.

- 서비스 주소: https://구독관리서비스.site
- Swagger UI: https://구독관리서비스.site/swagger-ui/index.html

---

## 1. 프로젝트 소개

여러 구독 서비스를 사용하는 사용자는  
매달 어떤 서비스에 얼마를 쓰고 있는지 한눈에 파악하기 어렵고,  
결제일이나 갱신일을 놓치기 쉽습니다.

이 프로젝트는 이런 문제를 해결하기 위해 시작했습니다.

- 사용자가 구독 정보를 직접 등록하고 관리할 수 있는 서비스 구현
- 결제 이력과 알림 기능을 통해 실제로 도움이 되는 백엔드 설계
- 인증, 테스트, 배포, 운영 관측까지 포함한 실무형 구조 경험

---

## 2. 핵심 기능

### 인증 / 사용자
- JWT 기반 Access Token / Refresh Token 인증
- Redis 기반 Refresh Token 저장 및 재발급 검증
- OAuth2 소셜 로그인 지원
  - Kakao
  - Google
  - Naver
- 이메일 인증 코드 발송 및 검증

### 구독 관리
- 구독 등록 / 수정 / 삭제 / 조회
- 필터링 / 정렬 / 페이징 조회
- 결제 주기 기반 구독 정보 관리

### 결제 / 알림
- 구독별 결제 이력 저장
- 스케줄러 기반 알림 메일 발송

### AI 분석
- Gemini 기반 구독 성향 분석
- 사용자 구독 데이터를 바탕으로 소비 패턴 해석
- 핵심 해석 / 인사이트 / 한 줄 요약 형태의 결과 제공

### 운영 / 공통 처리
- 공통 예외 처리 및 응답 포맷 통일
- AOP 기반 로깅 및 재시도 처리
- 배포 자동화 구성
- 메트릭 수집 및 모니터링 환경 연동

---

## 3. 기술 스택

### Backend
- Java 21
- Spring Boot 4.0.2
- Spring Security
- Spring Data JPA
- QueryDSL

### Authentication / External
- JWT
- OAuth2 Client
- Redis(RefreshToken Store, EmailCode Store)
- Spring Mail
- Thymeleaf

### Database
- MySQL
- H2 (테스트 환경)

### AI
- Spring AI
- Gemini

### DevOps / Infra
- Docker
- GitHub Actions
- GHCR
- EC2
- Nginx

### Monitoring / Docs
- Spring Boot Actuator
- Prometheus
- Grafana
- Swagger

---

## 4. 아키텍처

<img width="1536" height="1024" alt="시스템 아키텍처 다이어그램" src="https://github.com/user-attachments/assets/ba7fc1f9-b6d7-4305-9e79-66c05755f2ef" />


이 프로젝트는 요청 처리, 인증, 데이터 저장, 외부 연동, 운영 관측을 분리하는 방향으로 구성했습니다.

- 클라이언트 요청은 Nginx를 거쳐 Spring Boot API로 전달됩니다.
- 사용자 / 구독 / 결제 이력 데이터는 MySQL에 저장됩니다.
- Refresh Token과 이메일 인증 정보는 Redis를 활용해 관리합니다.
- AI 분석 기능은 Gemini 연동을 통해 사용자 구독 데이터를 해석합니다.
- 운영 환경에서는 Prometheus와 Grafana를 연동해 애플리케이션 상태를 확인합니다.
- GitHub Actions를 통해 이미지 빌드 및 배포 과정을 자동화했습니다.

---

## 5. ERD

구독 관리 서비스의 핵심 데이터는 아래 엔티티를 중심으로 설계했습니다.

- User
- Subscription
- PaymentHistory

사용자는 여러 개의 구독 정보를 가질 수 있고,  
각 구독은 실제 결제 이력을 별도로 저장합니다.

이를 통해 단순 목록 조회를 넘어서  
월별 총 결제 금액, 결제 건 수, 결제 내역 추적이 가능하도록 구성했습니다.

<img width="1157" height="532" alt="ERD" src="https://github.com/user-attachments/assets/adbc6eb0-1c75-454a-b63c-c0afe580a8d9" />

---

## 6. 핵심 구현 포인트

### 1) JWT + Redis 기반 인증 구조

단순히 JWT만 발급하는 구조가 아니라,  
Refresh Token을 Redis에 저장해 재발급 시 서버에서 한 번 더 검증하도록 구성했습니다.

이를 통해 다음을 고려했습니다.

- Refresh Token 재사용 방지
- 로그아웃 시 서버 측 무효화 처리
- 최대한의 Stateless 인증 구조를 유지하면서 최소한의 제어 지점(State) 확보

### 2) OAuth2 로그인과 자체 로그인 흐름 통합

일반 로그인과 소셜 로그인을 함께 지원하되,  
최종적으로는 동일한 JWT 인증 구조로 연결되도록 설계했습니다.

인증 방식이 달라도 이후 인가 로직과 사용자 처리 흐름을 일관되게 유지할 수 있도록 구현했습니다.

### 3) AI 기능을 실제 서비스 데이터와 연결

AI 기능을 단순 호출에 그치지 않고,  
사용자의 실제 구독 데이터를 기반으로 소비 성향을 분석하는 기능으로 연결했습니다.

- 프롬프트에서 출력 형식을 JSON으로 제한
- 응답 결과를 DTO로 매핑해 프론트엔드에서 바로 활용 가능하도록 구성
- 핵심 해석 / 인사이트 / 한 줄 요약 형태로 가공된 분석 결과 제공

### 4) 운영까지 고려한 백엔드 구성

기능 구현에서 끝나지 않고, 운영 과정까지 고려해 구조를 확장했습니다.

- 공통 예외 처리 및 응답 포맷 통일
- AOP 기반 로깅 및 재시도 처리
- GitHub Actions 기반 배포 자동화
- 운영 환경 모니터링 연동

---

## 7. 테스트

주요 비즈니스 로직과 인증 흐름에 대해 테스트를 작성했습니다.

- JWT 발급 / 만료 / 검증
- 인증 서비스 로직
- 회원가입 로직
- 구독 서비스 로직
- 결제 이력 서비스 로직
- 메일 서비스 로직
- 알림 서비스 로직

변경 이후에도 핵심 기능이 안정적으로 동작하는지 확인할 수 있도록 테스트를 구성했습니다.

```bash
./gradlew test
```

---

## 8. CI/CD

GitHub Actions를 활용해 아래 흐름을 자동화했습니다.

### CI
- `main` 브랜치 `push` / `pull_request` 시 테스트 실행
- 테스트 결과 리포트 업로드

### CD
- 프로젝트 build
- Docker 이미지 빌드
- GHCR 업로드
- EC2 서버에서 최신 이미지 pull
- 컨테이너 재기동으로 배포

이 과정을 통해 기능 개발 이후 검증과 배포를 반복 가능한 흐름으로 만들었습니다.

---

## 9. 모니터링

Spring Boot Actuator와 Prometheus, Grafana를 연동해 애플리케이션 상태와 메트릭을 확인할 수 있도록 구성했습니다.

예시 사진
<img width="1378" height="627" alt="스크린샷 2026-04-09 오전 1 19 19" src="https://github.com/user-attachments/assets/194627c5-ea7d-487c-b351-b64a6a58112e" />


주요 확인 대상:
- 요청 처리 흐름
- 서버 상태 및 운영 지표

운영 환경에서는 기능 구현뿐 아니라 문제가 생겼을 때 추적 가능한가도 중요하다고 생각해 관측 환경까지 함께 구성했습니다.

---

## 10. 프로젝트 목표

이 프로젝트는 단순한 구독 CRUD 서비스 구현을 넘어서,
아래와 같은 백엔드 역량을 실제 기능으로 연결해보는 데 목적이 있습니다.

- 인증/인가 구조 설계
- 외부 서비스(OAuth2, 메일, AI) 연동
- 테스트 가능한 서비스 계층 구성
- 배포 자동화 경험
- 운영 관측 환경 구성

---

## 11. 향후 개선 방향

- 패스키(WebAuthn) 기반 로그인 도입
- API 요청 rate limiting 적용
- 배포 후 health check 및 롤백 전략 고도화
- AI 분석 결과 품질 개선
- 사용자 소비 통계 시각화 기능 확장
- 구독 비용 절감을 위한 구독 모임 찾기 기능 추가
