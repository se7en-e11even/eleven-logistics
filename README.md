# eleven-logistics

## 팀원 역할분담
| <img src="https://img.shields.io/badge/Leader-%2310069F%20" /> | <img src="https://img.shields.io/badge/Member-%2310069F%20" /> | <img src="https://img.shields.io/badge/Member-%2310069F%20" /> | <img src="https://img.shields.io/badge/Member-%2310069F%20" /> |   
| :---: | :---: | :---: | :---: |
| <img src="https://avatars.githubusercontent.com/u/109949465?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/140582940?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/109337974?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/115226460?v=4" width="120px;" alt=""/> |
| [임승택](https://github.com/lime1st) | [백승규](https://github.com/seungg8361) | [오연주](https://github.com/zzu-uzz) | [한석규](https://github.com/hansg0325) |
| BE / Order / Product | BE / Slack / Company / Hub | BE / Delivery / D_Route / D_Person | BE / User / Hub_Route / Auth |

<br><br>

## 서비스 구성 및 실행 방법

### 서비스 구성
![service](https://github.com/user-attachments/assets/47213331-d4aa-4964-95b2-b0e04ea46b27)

### 실행 방법

#### 1. git clone
```shell
git clone https://github.com/se7en-e11even/eleven-logistics.git
```

#### 2. 인프라 환경 설정
- 도커 설치
- 콘솔창을 열어 git clone한 프로젝트의 루트 폴더로 이동
- docker-compose up -d

#### 3. 애플리케이션 실행 순서
1. eureka service
2. gateway service
3. auth service
4. 

<br><br>

## 프로젝트 목적

![프로젝트목적](https://github.com/user-attachments/assets/84649f76-6836-40ce-9b22-84afe5256ffd)

<details>
<summary>텍스트</summary>
<div markdown="1">
<h4>MSA(Microservices Architecture) 기반의 물류 관리 및 배송 시스템 설계 및 구현</h4>
<li>Spring Cloud & Spring Boot를 활용하여 MSA 기반 시스템 구축</li>
<li>클린 아키텍처 구성 및 DDD를 적용해 확장, 유지보수에 좋은 설계</li>
<li>API 연동, 데이터 무결성 유지, 서비스 간 통신 안정성 확보</li>
<li>Gemini API를 활용한 AI 기술 적용 경험</li>
<li>실무 수준의 협업을 경험하며, MSA의 복잡성과 운영 이슈 해결 능력 배양</li>
</div>
</details>


<br><br>

## ERD

![erd](https://github.com/user-attachments/assets/8af0a4dd-0493-4dde-a794-6fe2fe7c3931)

<br><br>

## 기술 스택

- **백엔드:** Spring Boot 3.4.3
- **데이터베이스:** PostgreSQL
- **빌드 툴:** Gradle
- **API 문서화:** Swagger + RestDoc 통합
- **API 게이트웨이:** Spring Cloud Gateway
- **메시지 시스템:** RabbitMQ
- **분산 추적:** Zipkin
- **Cache:** Redis
- **서비스 디스커버리:** Spring Cloud Eureka
- **버전 관리:** Git / GitHub
- **컨테이너:** Docker

<br><br>

## 트러블 슈팅

<details>
<summary>확장</summary>
<div markdown="1">

<br>
</div>
</details>

<br><br>

## API docs(선택)


