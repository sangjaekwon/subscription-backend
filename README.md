# Subscription Backend

구독 관리 서비스 백엔드입니다.  
JWT 인증/인가, OAuth2 소셜 로그인, 구독 관리 기능과 알람 기능, Gemini 기반 구독 성향 분석을 제공합니다.

---

## API 문서
https://구독관리서비스.site/swagger-ui/index.html

---

## 주요 기능

- JWT 기반 인증/인가 (Access / Refresh Token)
- OAuth2 소셜 로그인 (Kakao, Google, Naver)
- 구독 관리 CRUD
- 필터링 / 정렬 / 페이징 조회
- Gemini API 기반 구독 성향 분석
- 다가오는 구독 알람(이메일) 전송
- 글로벌 예외 처리
- 에러 및 응답 공통화
- 로그 및 재시도 AOP
- Prometheus + Grafana 모니터링

---

## 기술 스택
- **Language** : Java 21
- **Backend** : Spring Boot 4.0.2, Spring Security, Spring Data JPA  
- **Auth** : JWT, OAuth2  
- **Database** : MySQL, Redis  
- **Reverse Proxy** : Nginx  
- **Monitoring** : Prometheus, Grafana  
- **AI** : Gemini API  

---

## Architecture
```
Client
↓
Nginx (Reverse Proxy)
↓
Spring Boot API
↓
MySQL

Refresh Token Store → Redis
Monitoring → Prometheus + Grafana
```
