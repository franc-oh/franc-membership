# 등급별로 차등 할인을 해주는 멤버십 서비스

* 기존 어떤 서비스의 회원정보를 기반으로 ‘멤버십’ 기능을 붙인다고 가정하고 설계
* 추후 ‘Spring Cloud’를 통한 MSA에 포함되어야 하기 때문에 독립적인 어플리케이션으로 설계

## Description

* 등급별로 차등 할인을 해주는 멤버십 서비스를 제공한다.
* 멤버십 회원들은 결제 시 바코드를 제시, 스캔 시 할인과 포인트를 적립 할 수 있다.
* 적립으로 쌓인 포인트가 등업 기준을 충족하면 자동으로 등업된다.
* 적립률과 포인트 만료기간은 멤버십별 정책에 따라 다르다.
* 멤버십 혜택은 멤버십 가맹점에서만 받을 수 있다.

## Environment

* Java 11
* Spring Boot 2.7.x
* Spring Boot Test
* Spring Rest Docs
* JUnit5, Mockito
* Mybatis
* Gradle 7.1
* H2-DB (test), Maria DB (prod)

## Prerequisite


## TABLE

* 'schema.sql', 'data.sql'에 테이블 설계정보 및 기준데이터 등록

### 1. 회원정보 (TB_ACCOUNT)

- 기존에 관리되고 있는 회원정보

### 2. 멤버십 정보 (TB_MEMBERSHIP)

- 멤버십 서비스와 가맹이 된 멤버십 정보
- 백오피스에서 등록하는 기준정보

### 3. 멤버십 등급정보 (TB_MEMBERSHIP_GRADE)

- 멤버십의 등급별 혜택정보 및 등업에 대한 기준정보 관리
- 백오피스에서 등록하는 기준정보
  ex.) xx멤버십의 BRONZE 등급은 10000원 이상일 때 등업, 할인율은 5%

### 4. 멤버십 가맹점 (TB_MEMBERSHIP_FRANCHISEE)

- 멤버십의 가맹점 정보, 해당 멤버십은 가입된 가맹점에서만 사용할 수 있다.
- 백오피스에서 등록하는 기준정보

### 5. 나의 멤버십정보 (TB_MY_MEMBERSHIP)

- 회원이 가입한 멤버십, 회원은 ‘멤버십 가입’ 버튼을 통해 등록한다.

### 6. 나의 멤버십적립내역 (TB_MY_MEMBERSHIP_ACCUM_HIS)

- 바코드를 통한 적립 시 쌓이는 테이블
- 멤버십 별 거래내역과 적립내역을 관리, 취소_바코드를 통해 취소 가능

## Usage

### 1. Install
`$ git clone https://github.com/franc-oh/franc-membership.git`

### 2. Build

- `franc-membership 디렉토리 이동`
- `$ ./gradlew build`
-'Permission dinied' 발생한 경우
  - `$ chmod +x ./gradlew` 후 다시 빌드

### 3. Run

`java -jar ./build/libs/franc-membership-1.0-SNAPSHOT.war --spring.profiles.active=test --jasypt.encryptor.password=franc_msp`

