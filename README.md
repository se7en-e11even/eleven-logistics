# eleven-logistics

## 팀원 역할분담
| <img src="https://img.shields.io/badge/Leader-%2310069F%20" /> | <img src="https://img.shields.io/badge/Member-%2310069F%20" /> | <img src="https://img.shields.io/badge/Member-%2310069F%20" /> | <img src="https://img.shields.io/badge/Member-%2310069F%20" /> |   
| :---: | :---: | :---: | :---: |
| <img src="https://avatars.githubusercontent.com/u/109949465?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/140582940?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/109337974?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/115226460?v=4" width="120px;" alt=""/> |
| [임승택](https://github.com/lime1st) | [백승규](https://github.com/seungg8361) | [오연주](https://github.com/zzu-uzz) | [한석규](https://github.com/hansg0325) |
| BE / Order / Product | BE / Slack / Company / Hub | BE / Delivery / D_Route / D_Person | BE / User / Hub_Route / Auth |

<br>

## 📌 서비스 구성 및 실행 방법

- ### 서비스 구성
![service](https://github.com/user-attachments/assets/47213331-d4aa-4964-95b2-b0e04ea46b27)

- ### 실행 방법

#### - GoogleAI(Gemini) token, Slack token, Naver OpenAPI secret key 발급이 필요합니다.
#### 1. docker를 설치합니다.
#### 2. 아래의 명령어로 git clone을 진행합니다.
#### 3. 해당 프로젝트의 루트 폴더로 이동합니다.
```shell
git clone https://github.com/se7en-e11even/eleven-logistics.git
```

#### 4. docker-compose.yml 파일을 알맞은 위치에 작성합니다.
- .docker-compose.yml
```
version: "3.8"
services:

  db:
    image: "postgres:16.3"
    container_name: "postgres"
    ports:
      - 5432:5432
    environment:
      - POSTGRES_USER=eleven-logistics
      - POSTGRES_PASSWORD=1234
      - POSTGRES_DB=eleven-logistics
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d

  rabbitmq:
    image: rabbitmq:management
    container_name: "rabbitmq"
    ports:
      - "15672:15672"

  redis:
    image: redis/redis-stack
    container_name: "redis"
    ports:
      - "8001:8001"
      - "6379:6379"

  zipkin:
    image: openzipkin/zipkin
    container_name: "zipkin"
    ports:
      - "9411:9411"

volumes:
  postgres_data:
```
#### 5. 아래의 명령어를 실행합니다.
```shell
docker-compose up -d
```

#### 6. key와 token설정을 마친 뒤, 다음의 순서대로 애플리케이션을 실행합니다.
- eureka service 실행
- gateway service 실행
- auth service 실행
- delivery, hub, hub-route, order, product, slack 실행

#### 7. [Service EndPoint](https://github.com/se7en-e11even/eleven-logistics/wiki/API-%EB%AA%85%EC%84%B8%EC%84%9C)

<br>

## 📌 프로젝트 목적

- MSA(Microservices Architecture) 기반의 물류 관리 및 배송 시스템 설계 및 구현
- Spring Cloud & Spring Boot를 활용하여 MSA 기반 시스템 구축
- 클린 아키텍처 구성 및 DDD를 적용해 확장, 유지보수에 좋은 설계
- API 연동, 데이터 무결성 유지, 서비스 간 통신 안정성 확보
- Gemini API를 활용한 AI 기술 적용 경험
- Github의 이슈발행, 프로젝트 관리 기능 등을 활용한 실무 수준의 협업 경험
- MSA의 복잡성과 운영 이슈 해결 능력 배양

![프로젝트목적](https://github.com/user-attachments/assets/84649f76-6836-40ce-9b22-84afe5256ffd)
<br>
<br>
## 📌 System Architecture
<img width="7424" alt="인프라 아키텍처" src="https://github.com/user-attachments/assets/b1cf8e3f-92c3-4318-962a-c60c7b188965" />

<br>

## 📌 ERD

![erd](https://github.com/user-attachments/assets/8af0a4dd-0493-4dde-a794-6fe2fe7c3931)

<br>

## 📌 메시징 시스템 아키텍처

[메시지 시스템 도입에 관한 고민](https://github.com/se7en-e11even/eleven-logistics/wiki/%EB%A9%94%EC%8B%9C%EC%A7%95-%ED%81%90-%EB%8F%84%EC%9E%85%EC%97%90-%EA%B4%80%ED%95%9C-%EA%B3%A0%EB%AF%BC)

![image](https://github.com/user-attachments/assets/62ea6bac-8931-414b-8170-88b137999685)

<br>

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

<br>

## 트러블 슈팅

<details>
<summary>확장</summary>
<div markdown="1">

<br>
</div>
</details>

<br><br>

## API docs

**Swagger 문서 자동화**: Swagger + RestDocs

각 서비스의 Swagger 문서를 gateway로 통합하여 제공
http://localhost:19091/docs

