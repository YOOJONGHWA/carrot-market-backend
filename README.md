# 당근 마켓 클론 코딩

이 프로젝트는 당근 마켓의 주요 기능을 클론 코딩하여 구현한 애플리케이션입니다.

지역 기반 중고 거래 플랫폼의 주요 기능을 포함하며, 프론트엔드와 백엔드, 데이터베이스 설정을 포함합니다.

## 주요 기능

### 사용자 인증 및 권한 관리
- **로그인/회원가입**: 사용자가 애플리케이션에 로그인하거나 회원가입할 수 있는 기능.
- **비밀번호 재설정**: 비밀번호를 잊었을 때 재설정할 수 있는 기능.
- **사용자 프로필 관리**: 사용자 프로필 정보를 업데이트하고 관리할 수 있는 기능.

### 게시물 관리
- **게시물 등록/수정/삭제**: 사용자가 게시물을 등록하고 수정하며 삭제할 수 있는 기능.
- **게시물 검색 및 필터링**: 게시물을 검색하고 필터링할 수 있는 기능.
- **게시물 상세 보기**: 게시물의 자세한 정보를 확인할 수 있는 기능.

### 채팅 기능
- **실시간 채팅**: 사용자가 실시간으로 채팅할 수 있는 기능.
- **채팅 기록 저장**: 채팅 기록을 저장하고 조회할 수 있는 기능.

### 지역 기반 검색
- **현재 위치 기반으로 게시물 검색**: 사용자의 현재 위치를 기준으로 게시물을 검색할 수 있는 기능.
- **지역 필터링**: 특정 지역으로 게시물을 필터링할 수 있는 기능.

### 알림 기능
- **채팅 알림**: 새로운 채팅 메시지에 대한 알림.
- **게시물에 대한 댓글, 관심 추가 알림**: 게시물에 댓글이 달리거나 관심이 추가되었을 때 알림.

### 거래 이력 관리
- **거래 내역 조회**: 사용자의 거래 내역을 조회할 수 있는 기능.
- **거래 상태 업데이트**: 거래 상태를 업데이트할 수 있는 기능.

## 기술 스택

### 프론트엔드
- **React**  사용자 인터페이스를 동적으로 업데이트할 수 있는 프레임워크.
- **Bootstrap**: UI 디자인 및 스타일링.

### 백엔드

- **Spring Boot** (Java): 대규모 애플리케이션에 적합.

### 데이터베이스
- **MySQL**: 관계형 데이터베이스, 구조화된 데이터에 유리.

### 기타
- **Socket.io**: 실시간 채팅 기능 구현.
- **JWT** 또는 **OAuth**: 인증 및 권한 관리.
- **AWS** : 클라우드 호스팅 및 저장.
