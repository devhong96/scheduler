# Scheduler Frontend (React)

출결/수강신청 스케줄러의 **프론트엔드**입니다. 백엔드(Spring Boot)가 제공하는 REST API를
호출해서 화면을 그립니다. 이 문서는 **프론트엔드가 처음인 사람도** 구조를 이해할 수 있도록
폴더의 의미와 React 개념을 함께 설명합니다.

---

## 1. 이게 어떻게 돌아가나요? (큰 그림)

예전에는 백엔드(Spring)가 HTML(Thymeleaf)까지 다 만들어서 브라우저에 내려줬습니다.
이제는 역할을 나눕니다.

```
[브라우저: React 앱]  ──(JSON 요청/응답)──▶  [Spring Boot: REST API]  ──▶  [DB]
  화면을 그림                  /api/...              데이터만 처리
```

- **백엔드**는 이제 화면을 안 만들고 **데이터(JSON)만** 주고받습니다. (예: `POST /api/auth/login`)
- **프론트엔드(이 프로젝트)**가 그 JSON을 받아 **화면(HTML)을 브라우저에서 직접** 만듭니다.
- 둘은 완전히 분리돼 있어서, 프론트는 프론트대로 백엔드는 백엔드대로 개발/배포할 수 있습니다.

이 프론트엔드는 **React + Vite** 조합입니다.
- **React**: 화면을 "컴포넌트"라는 조각으로 만드는 라이브러리.
- **Vite**: 개발 서버 + 빌드 도구. `npm run dev` 하면 빠르게 개발 서버가 뜹니다.

---

## 2. 실행 방법

```bash
# 1) 백엔드 먼저 실행 (프로젝트 루트에서, 로컬 H2 + 시드 데이터)
./gradlew bootRun --args='--spring.profiles.active=local'

# 2) 프론트엔드 실행 (frontend 폴더에서)
npm install      # 최초 1회 — 의존성 설치
npm run dev      # 개발 서버 → http://localhost:5173
```

- 개발 서버는 **5173** 포트, 백엔드는 **3205** 포트입니다.
- 화면에서 `/api/...`를 호출하면 Vite가 자동으로 백엔드(3205)로 넘겨줍니다. (아래 "프록시" 참고)

로그인 계정(로컬 시드):
- 관리자 `admin` / `root123!@#`
- 교사 `teacher1` / `teacher123!`
- 학생 수강신청 화면(로그인 불필요): 시드 학생 이름 `김민준`, `이서연`

---

## 3. 폴더 구조와 의미

```
frontend/
├─ index.html          ← 앱이 로드되는 진짜 HTML 파일 1개 (<div id="root"> 뿐)
├─ package.json        ← 의존성 목록 + 실행 스크립트(dev/build) 정의
├─ vite.config.js      ← Vite 설정 (개발 서버 포트, /api 프록시)
├─ public/             ← 그대로 서빙되는 정적 파일(파비콘 등)
└─ src/                ← 실제 소스 코드 (여기서 대부분 작업)
   ├─ main.jsx         ← 앱의 시작점. React를 index.html의 #root에 심는다
   ├─ App.jsx          ← 최상위 컴포넌트. "어떤 URL에 어떤 페이지" 라우팅 정의
   ├─ api/             ← 백엔드 API 호출 코드 모음
   │  ├─ client.js     ← axios 설정 (토큰 자동첨부, 401 자동 갱신) — 가장 중요
   │  ├─ teacher.js    ← 수업/학생 관리 API 함수들
   │  ├─ class.js      ← 학생 수강신청 API 함수들
   │  ├─ board.js      ← 공지/댓글 API 함수들
   │  └─ admin.js      ← 교사 승인/변경/삭제 API 함수들
   ├─ auth/
   │  └─ AuthContext.jsx ← 로그인 상태를 앱 전체에서 공유 (누가 로그인했나)
   ├─ components/      ← 여러 페이지에서 재사용하는 조각
   │  └─ ProtectedRoute.jsx ← 로그인 안 했으면 로그인 페이지로 보내는 "문지기"
   └─ pages/           ← 화면(페이지) 단위 컴포넌트
      ├─ EnrollPage.jsx      ← 학생 수강신청 (공개, 랜딩 페이지 "/")
      ├─ LoginPage.jsx       ← 로그인 "/login"
      ├─ BoardListPage.jsx   ← 공지 목록 "/board"
      ├─ BoardDetailPage.jsx ← 공지 상세 + 댓글 "/board/:id"
      ├─ BoardFormPage.jsx   ← 공지 작성/수정 "/board/new", "/board/:id/edit" (관리자)
      ├─ ManagePage.jsx      ← 수업 시간표 관리 "/manage" (교사/관리자)
      ├─ StudentListPage.jsx ← 학생 정보 관리 "/manage/students" (관리자는 담당교사 변경)
      └─ AdminTeachersPage.jsx ← 교사 승인/삭제 "/admin/teachers" (관리자)
```

**폴더를 왜 이렇게 나눴나?** — 역할별로 모아두면 찾기 쉽고 재사용이 됩니다.
- `api/` = "백엔드랑 대화하는 부분"만 모음 → 나중에 주소가 바뀌어도 여기만 고치면 됨
- `pages/` = "한 화면 전체"
- `components/` = "여러 화면이 공유하는 작은 부품"
- `auth/` = "로그인 상태"라는 특별한 공유 데이터

---

## 4. React 핵심 개념 (여기만 읽어도 코드가 보입니다)

### 4-1. 컴포넌트(Component)와 JSX
컴포넌트는 **화면 조각을 만드는 함수**입니다. `return` 안에 HTML처럼 생긴 것(=JSX)을 씁니다.

```jsx
function Hello() {
  return <h1>안녕하세요</h1>   // 이 JSX가 실제 <h1>로 그려짐
}
```
- 함수 이름은 **대문자로 시작**해야 컴포넌트로 인식됩니다.
- JSX는 HTML과 거의 같지만, `class` 대신 `className`, 중괄호 `{}` 안에 JS 값을 넣습니다.
  예: `<p>{user.name}</p>` → user.name 값이 들어감.

### 4-2. props (부모 → 자식으로 값 전달)
컴포넌트에 넘기는 "입력값"입니다. 함수의 인자라고 보면 됩니다.

```jsx
<ProtectedRoute><ManagePage /></ProtectedRoute>
// ProtectedRoute 입장에서 <ManagePage/>는 children 이라는 prop 으로 들어옴
```

### 4-3. 훅(Hook) — ⭐가장 헷갈리는 부분
**훅**은 이름이 `use`로 시작하는 특수 함수입니다. 컴포넌트에 "기억"과 "생명주기"를 붙여줍니다.
컴포넌트 함수는 화면을 다시 그릴 때마다 처음부터 다시 실행되는데, 그래도 값을 **기억**하게
해주는 장치가 훅입니다.

| 훅 | 한 줄 의미 | 이 프로젝트에서 |
|---|---|---|
| `useState` | 값을 **기억**하고, 바뀌면 화면을 다시 그림 | 입력창 글자, 목록 데이터, 로딩 여부 |
| `useEffect` | 특정 시점에 **부수 작업** 실행(주로 데이터 불러오기) | 페이지 열릴 때 API 호출 |
| `useContext` | 멀리 있는 공유 데이터를 꺼내 씀 | 로그인 정보 꺼내기 |
| `useNavigate` | 코드로 페이지 이동 | 로그인 성공 → `/manage`로 이동 |

**useState 예시** (StudentListPage에서):
```jsx
const [search, setSearch] = useState('')   // search=현재값, setSearch=바꾸는 함수
<input value={search} onChange={e => setSearch(e.target.value)} />
// 입력할 때마다 setSearch로 값을 바꾸면 → React가 화면을 다시 그림
```
- `useState('')`는 `[현재값, 바꾸는함수]` 형태로 돌려줍니다. 이름은 마음대로 지어도 됩니다.
- **값을 바꿀 땐 반드시 `setXxx()`를 써야** 화면이 갱신됩니다. (`search = '홍'` 직접 대입 ❌)

**useEffect 예시** (ManagePage에서):
```jsx
useEffect(load, [])
// []는 "처음 화면이 뜰 때 딱 한 번" load()를 실행하라는 뜻.
// 이때 백엔드에서 수업 목록을 불러옵니다.
```
- 두 번째 인자 `[]`(의존성 배열)가 **언제 실행할지**를 정합니다.
  `[]` = 최초 1회, `[page]` = page 값이 바뀔 때마다.

**커스텀 훅** — 훅을 조합해 내가 만든 훅. 이 프로젝트의 `useAuth()`가 그 예입니다.
```jsx
const { user, login, logout } = useAuth()   // 로그인 관련 기능을 한 번에 꺼냄
```

### 4-4. 라우팅(Routing)
"URL마다 어떤 페이지를 보여줄까"를 정하는 것. `react-router-dom` 라이브러리를 씁니다.
`App.jsx`에 모두 정의돼 있습니다.

```jsx
<Route path="/login" element={<LoginPage />} />   // /login → 로그인 페이지
<Route path="/" element={<EnrollPage />} />        // / → 수강신청 페이지
```
- SPA(Single Page Application): HTML은 `index.html` 하나뿐이고, URL이 바뀌면 **서버에 다시 요청하지 않고** React가 화면만 갈아끼웁니다. 그래서 빠릅니다.

---

## 5. 백엔드와 통신하는 방법 (api 폴더)

### axios와 client.js
`axios`는 백엔드에 HTTP 요청을 보내는 라이브러리입니다. `src/api/client.js`에서 공통 설정을
한 번만 해두고, 모든 API가 이걸 재사용합니다. 핵심 2가지:

1. **요청 인터셉터**: 모든 요청에 로그인 토큰을 자동으로 붙입니다.
   ```js
   config.headers.Authorization = `Bearer ${token}`
   ```
   → 페이지마다 토큰을 신경 쓸 필요가 없습니다.

2. **응답 인터셉터**: 토큰이 만료돼 `401`이 오면, refresh 토큰으로 **자동 재발급**한 뒤
   원래 요청을 다시 보냅니다. 실패하면 로그인 페이지로 보냅니다.

### 프록시 (vite.config.js)
개발 중엔 프론트(5173)와 백엔드(3205) 포트가 달라서 브라우저가 막습니다(CORS).
Vite가 `/api`로 시작하는 요청을 백엔드로 **몰래 전달**해주면 같은 출처처럼 동작합니다.
```js
proxy: { '/api': { target: 'http://localhost:3205' } }
```

---

## 6. 로그인(인증)은 어떻게 동작하나 — JWT

이 앱은 **JWT 토큰** 방식입니다. (예전 세션/쿠키 방식과 다름)

```
1. 로그인 → 백엔드가 accessToken + refreshToken(문자열) 발급
2. 프론트는 이 토큰을 브라우저의 localStorage 에 저장
3. 이후 모든 API 요청 헤더에 accessToken 을 붙여 "나 로그인한 사람이야" 증명
4. accessToken 만료 → refreshToken 으로 새로 발급 (자동)
```

- **AuthContext.jsx**: 지금 누가 로그인했는지(`user`)를 앱 전체가 공유하게 해줍니다.
  `login()`, `logout()` 함수도 여기서 제공합니다.
- **ProtectedRoute.jsx**: 로그인 안 한 사람이 `/manage`에 들어오면 `/login`으로 돌려보냅니다.
- 토큰 저장/삭제는 `client.js`의 `tokenStore`가 담당합니다.

---

## 7. 각 페이지가 하는 일

| 파일 | URL | 설명 | 권한 |
|---|---|---|---|
| `EnrollPage.jsx` | `/` | 학생이 이름으로 조회 후 요일별 수업 신청 | 공개 |
| `LoginPage.jsx` | `/login` | 아이디/비번으로 로그인 | 공개 |
| `BoardListPage.jsx` | `/board` | 공지사항 목록/검색 | 공개 |
| `BoardDetailPage.jsx` | `/board/:id` | 공지 상세 + 댓글(학생 인증) | 공개 |
| `BoardFormPage.jsx` | `/board/new`, `/board/:id/edit` | 공지 작성/수정 | 관리자 |
| `ManagePage.jsx` | `/manage` | 수업 시간표 조회/삭제 | 교사·관리자 |
| `StudentListPage.jsx` | `/manage/students` | 학생 정보 검색/삭제, 담당교사 변경(관리자) | 교사·관리자 |
| `AdminTeachersPage.jsx` | `/admin/teachers` | 교사 승인/승인취소/삭제 | 관리자 |

---

## 8. 자주 쓰는 명령어

```bash
npm run dev      # 개발 서버 (수정하면 화면 자동 갱신 = HMR)
npm run build    # 배포용 파일 생성 (dist/ 폴더에 정적 파일)
npm run preview  # build 결과물을 로컬에서 미리보기
```

---

## 9. 새 화면을 추가하고 싶다면 (흐름 요약)

1. `src/api/`에 백엔드 호출 함수 추가 (예: `getNotices()`)
2. `src/pages/`에 새 페이지 컴포넌트 작성 → `useState`로 데이터 담고 `useEffect`로 불러오기
3. `src/App.jsx`에 `<Route path="..." element={<새페이지 />} />` 등록
4. 로그인 필요하면 `<ProtectedRoute>`로 감싸기

이 순서만 지키면 기존 코드와 같은 패턴으로 확장됩니다.
