# SSG.COM- 과제 동작 설명

<h2>기본 사항:</h2>
- 개발 언어: Java (Spring Boot 사용)
<div/><div/>
- 데이터베이스: In-memory DB (H2 사용)
<div/><div/>
- API 문서화: REST Docs
  -root folder에 문서 추출본 index.html 을 별도로 첨부했습니다.
<div/>
- 테스트 코드: JUnit5 사용
<div/>
- 테스트 화면: 없음
<div/>

<h2>프로젝트 실행: </h2>
  1.터미널을 root Directory 에 두고 ./gradlew bootRun 명령어를 입력해 Spring을 실행할 수 있습니다.
<div/>
2.프로젝트 시작 후 생성되는 API문서는 ssg_project\build\docs\asciidoc\index.html 에서 확인 가능합니다.
<div/>
3.기능 위주의 통합테스트 작성은 주로 ssg_project\src\test\java\com\ssg_project\api\service\order\OrderServiceTest.java 파일에서 확인할 수 있습니다.
<div/>
<h2>프로젝트 구현 흐름:</h2>
<div/>
1.진행 방식: TDD(Test Driven Develop) 방식으로 최대한 요구사항에 있는 구현에 대한 test 코드를 작성하고, 구현을 완성하는 방식으로 진행했습니다.
<div/>

<div/>
  2.도메인 설계:
<div/>
    order <-> orderProduct <-> product,  stock <- product -> sale
<div/>
    구조로 작성했습니다.
<div/>

<h2>폴더 구조:</h2>

```ssg_project/

├── src
│   ├── docs
│   │   └── asciidoc
│   │       ├── api
│   │       │   └── order
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── ssg_project
│   │   │           ├── api
│   │   │           │   ├── ApiControllerAdvice
│   │   │           │   ├── controller
│   │   │           │   │   ├── order
│   │   │           │   │   │   ├── request
│   │   │           │   │   │   ├── response
│   │   │           │   ├── service
│   │   │           │   │   ├── order
│   │   │           │   │   │   ├── dto
│   │   │           │   │   │   │   ├── request
│   │   │           │   │   └── product
│   │   │           ├── config
│   │   │           ├── domain
│   │   │           │   ├── order
│   │   │           │   ├── orderproduct
│   │   │           │   ├── product
│   │   │           │   ├── sale
│   │   │           │   ├── stock
│   │   └── resources
│   └── test
│       ├── java
│       │   └── com
│       │       └── ssg_project
│       │           ├── api
│       │           │   ├── controller
│       │           │   │   └── order
│       │           │   └── service
│       │           │       └── order
│       │           ├── docs
│       │           │   ├── order
│       │           ├── domain
│       │           │   ├── product
│       │           │   ├── sale
│       │           │   ├── stock
│       └── resources
│           └── org
│               └── springframework
│                   └── restdocs
│                       └── templates
main/
├── java
│   └── com
│       └── ssg_project
│           ├── api
│           │   ├── ApiControllerAdvice
│           │   ├── controller
│           │   │   ├── order
│           │   │   │   ├── request
│           │   │   │   ├── response
│           │   ├── service
│           │   │   ├── order
│           │   │   │   ├── dto
│           │   │   │   │   ├── request
│           │   │   └── product
│           ├── config
│           ├── domain
│           │   ├── order
│           │   ├── orderproduct
│           │   ├── product
│           │   ├── sale
│           │   ├── stock
└── resources
test/
├── java
│   └── com
│       └── ssg_project
│           ├── api
│           │   ├── controller
│           │   │   └── order
│           │   └── service
│           │       └── order
│           ├── docs
│           │   ├── order
│           ├── domain
│           │   ├── product
│           │   ├── sale
│           │   ├── stock
└── resources
    └── org
        └── springframework
            └── restdocs
                └── templates
```
