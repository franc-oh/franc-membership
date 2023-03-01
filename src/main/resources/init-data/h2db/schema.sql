/** 멤버십 서비스에 필요한 기초 테이블 스크립트 **/


/* 1. 회원 정보 (TB_ACCOUNT) */
CREATE TABLE TB_ACCOUNT (
    ACCOUNT_ID BIGINT NOT NULL AUTO_INCREMENT COMMENT '회원번호',
    ACCOUNT_NM VARCHAR(50) NOT NULL COMMENT '회원이름',
    STATUS CHAR DEFAULT '1' COMMENT '상태 (1:사용, 9:정지, 0:탈퇴)',
    BIRTH VARCHAR(8) NOT NULL COMMENT '생년월일',
    HPHONE VARCHAR(11) NOT NULL COMMENT '휴대폰번호',
    EMAIL VARCHAR(50) NOT NULL COMMENT '이메일',
    ACCOUNT_GRADE VARCHAR(10) DEFAULT 'USER' COMMENT '회원등급 (USER/SELLER/ADMIN)',
    INSERT_DATE TIMESTAMP COMMENT '등록일자',
    INSERT_USER BIGINT COMMENT '등록자ID',
    UPDATE_DATE TIMESTAMP COMMENT '수정일자',
    UPDATE_USER BIGINT COMMENT '수정자ID',
    CONSTRAINT TB_ACCOUNT_PK PRIMARY KEY(ACCOUNT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '회원정보';


/* 2.등급정보 (TB_GRADE) */
CREATE TABLE TB_GRADE (
    GRADE_FG VARCHAR(10) NOT NULL COMMENT '등급구분',
    GRADE_CD VARCHAR(10) NOT NULL COMMENT '등급코드',
    GRADE_NM VARCHAR(100) NOT NULL COMMENT '등급명',
    INSERT_DATE TIMESTAMP COMMENT '등록일자',
    INSERT_USER BIGINT COMMENT '등록자ID',
    UPDATE_DATE TIMESTAMP COMMENT '수정일자',
    UPDATE_USER BIGINT COMMENT '수정자ID',
    CONSTRAINT TB_GRADE_PK PRIMARY KEY(GRADE_FG, GRADE_CD)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '등급정보';


/* 3.멤버십 정보 (TB_MEMBERSHIP) */
CREATE TABLE TB_MEMBERSHIP (
    MSP_ID VARCHAR(15) NOT NULL COMMENT '등급코드',
    MSP_NM VARCHAR(100) NOT NULL COMMENT '등급명',
    STATUS CHAR DEFAULT '1' COMMENT '상태 (1:사용, 9:정지)',
    MSP_MSP_INFO VARCHAR(200) NOT NULL COMMENT '멤버십 안내문구',
    MSP_IMG_URL VARCHAR(200) COMMENT '멤버십 이미지 경로',
    HOMEPAGE_URL VARCHAR(200) COMMENT '본사 홈페이지 URL',
    ACTIVE_MONTHS INT DEFAULT 3 COMMENT '적립포인트 유효개월수',
    BIGO VARCHAR(2000) COMMENT '비고',
    INSERT_DATE TIMESTAMP COMMENT '등록일자',
    INSERT_USER BIGINT COMMENT '등록자ID',
    UPDATE_DATE TIMESTAMP COMMENT '수정일자',
    UPDATE_USER BIGINT COMMENT '수정자ID',
    CONSTRAINT TB_MEMBERSHIP_PK PRIMARY KEY(MSP_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '멤버십 정보';


/* 4. 멤버십 등급정보 (TB_MEMBERSHIP_GRADE) */
CREATE TABLE TB_MEMBERSHIP_GRADE (
    MSP_ID VARCHAR(15) NOT NULL COMMENT '멤버십ID',
    MSP_GRADE_CD VARCHAR(10) NOT NULL COMMENT '멤버십 등급코드',
    GRADE_UP_POINT_FR INT NOT NULL COMMENT '등업기준포인트 [FROM]',
    GRADE_UP_POINT_TO INT NOT NULL COMMENT '등업기준포인트 [TO]',
    ACCUM_RAT INT NOT NULL COMMENT '적립율',
    DISC_RAT INT NOT NULL COMMENT '할인율',
    INSERT_DATE TIMESTAMP COMMENT '등록일자',
    INSERT_USER BIGINT COMMENT '등록자ID',
    UPDATE_DATE TIMESTAMP COMMENT '수정일자',
    UPDATE_USER BIGINT COMMENT '수정자ID',
    CONSTRAINT TB_MEMBERSHIP_GRADE_PK PRIMARY KEY(MSP_ID, MSP_GRADE_CD)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '멤버십 등급정보';


/* 5. 멤버십 가맹점 (TB_MEMBERSHIP_FRANCHISEE) */
CREATE TABLE TB_MEMBERSHIP_FRANCHISEE (
    MSP_ID VARCHAR(15) NOT NULL COMMENT '멤버십ID',
    FRANCHISEE_ID VARCHAR(15) NOT NULL COMMENT '가맹점ID',
    STATUS CHAR DEFAULT '1' COMMENT '상태 (1:사용, 9:정지)',
    ZIP_CD VARCHAR(5) COMMENT '우편번호',
    ADDR1 VARCHAR(200) COMMENT '주소',
    ADDR2 VARCHAR(200) COMMENT '상세주소',
    TEL_NO VARCHAR(20) COMMENT '대표전화번호',
    BIGO VARCHAR(2000) COMMENT '비고',
    INSERT_DATE TIMESTAMP COMMENT '등록일자',
    INSERT_USER BIGINT COMMENT '등록자ID',
    UPDATE_DATE TIMESTAMP COMMENT '수정일자',
    UPDATE_USER BIGINT COMMENT '수정자ID',
    CONSTRAINT TB_MEMBERSHIP_FRANCHISEE_PK PRIMARY KEY(MSP_ID, FRANCHISEE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '멤버십 가맹점정보';


/* 6. 나의 멤버십정보 (TB_MY_MEMBERSHIP) */
CREATE TABLE TB_MY_MEMBERSHIP (
    ACCOUNT_ID BIGINT NOT NULL COMMENT '회원번호',
    MSP_ID VARCHAR(15) NOT NULL COMMENT '멤버십ID',
    STATUS CHAR DEFAULT '1' COMMENT '상태 (1:사용, 9:정지, 0:탈퇴)',
    TOTAL_ACCUM_POINT INT NOT NULL COMMENT '총 적립 포인트',
    MSP_GRADE_CD VARCHAR(10) NOT NULL COMMENT '멤버십 등급코드',
    INSERT_DATE TIMESTAMP NOT NULL COMMENT '가입일자',
    WITHDRAWAL_DATE TIMESTAMP COMMENT '탈퇴일자',
    BAR_CD VARCHAR(50) NOT NULL COMMENT '멤버십 바코드번호',
    CONSTRAINT TB_MY_MEMBERSHIP_PK PRIMARY KEY(ACCOUNT_ID, MSP_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '나의 멤버십정보';



/* 7. 나의 멤버십적립내역 (TB_MY_MEMBERSHIP_ACCUM_HIS) */
CREATE TABLE TB_MY_MEMBERSHIP_ACCUM_HIS (
    CANCEL_BAR_CD VARCHAR(50) NOT NULL COMMENT '취소용 바코드 번호',
    STATUS CHAR DEFAULT '1' COMMENT '상태 (1:사용, 9:취소)',
    ACCOUNT_ID BIGINT NOT NULL COMMENT '회원번호',
    MSP_ID VARCHAR(15) NOT NULL COMMENT '멤버십ID',
    FRANCHISEE_ID VARCHAR(15) NOT NULL COMMENT '가맹점ID',
    TRADE_AMT INT NOT NULL COMMENT '거래금액',
    MSP_GRADE_CD VARCHAR(10) NOT NULL COMMENT '멤버십 등급코드',
    ACCUM_RAT INT NOT NULL COMMENT '적립율',
    ACCUM_POINT INT NOT NULL COMMENT '적립 포인트',
    EXPIRE_YMD INT NOT NULL COMMENT '포인트 만료일',
    ACCUM_DATE TIMESTAMP NOT NULL COMMENT '적립일자',
    ACCUM_CANCEL_DATE TIMESTAMP COMMENT '적립취소일자',
    CONSTRAINT TB_MY_MEMBERSHIP_ACCUM_HIS_PK PRIMARY KEY(CANCEL_BAR_CD)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '나의 멤버십 적립내역';