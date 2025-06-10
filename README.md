# SSG.COM- 과제 동작 설명

<h2>기본 사항:</h2>
- 개발 언어: Java (Spring Boot 사용)
<div/>
- 데이터베이스: In-memory DB (H2 사용)
<div/>
- API 문서화: REST Docs
  -root folder에 문서 추출본 index.html 을 별도로 첨부했습니다.
<div/>
- 테스트 코드: JUnit5 사용
<div/>
- 테스트 화면: 없음
<div/>

<h2>프로젝트 구조: </h2>

<div/>
프로젝트 구현 방법:
<div/>
  진행 방식: TDD(Test Driven Develop) 방식으로 최대한 요구사항에 있는 구현에 대한 test 코드를 작성하고, 구현을 완성하는 방식으로 진행했습니다.
<div/>
  DB 설계:
<div/>
    order <-> orderProduct <-> product,  stock <- product -> sale
<div/>
    구조로 작성했습니다.
<div/>
