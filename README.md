# ![App icon](./readme_data/app_icon_v1.0.png)Travery (위치형 일기 앱)

Travery는 **Trave**l과 Dia**ry**를 합친 단어로, 자신의 일상 생활을 지도 위에 기록할 수 있는 ‘위치기반 일기’ 어플리케이션입니다.

- 자신의 일상을 실시간 경로 일기로 남기어 자신이 갔었던 정확한 위치와 무엇을 했는지를 기억할 수 있도록 도와줍니다.
- 활동 기록을 테마와 날짜로 구분하여 사용자가 원하는 자신의 기록을 쉽게 찾을 수 있습니다.
- 지금 위치에서 다른 사람은 어떤 코스로 여행 혹은 데이트를 즐겼는지 정보를 제공하여 계획에 도움을 줄 수 있습니다.
- 여행 같은 경우에 자신이 다녀왔던 길을 저장하여 길을 잃어버리지 않도록 도와줍니다.

#### 주제를 생각하게 된 동기

"저번에 갔던 곳이 어디였지?", "그때 무엇을 먹었더라?" 같은 고민과 "여기에선 무엇을 하면 좋을까?" 같은 고민을 해결하고 좀 더 재미있고 기억에 남는 일기를 기록해 보고 싶어서 생각하게 되었습니다.

#### 기존에 존재하는 비슷한 서비스와 차별되는 점

strava, 오픈라이더 같은 경로 저장 어플리케이션은 주로 운동 활동에 사용되었습니다. 하지만 이 어플리케이션은 일반인을 대상으로 자신이 활용하고 싶은 방법대로 사용할 수 있으며 일상 공유에 목적을 두고 있습니다.

<br>

# Features

### # 경로(Course)

#### 경로 기록하기

[스크린샷 추가 예정]

- 실시간으로 위치를 추적하며 폴리라인으로 지나온 길을 그려줍니다.
- 오랫동안 한 곳에 머물러 있다면 앱이 자동으로 인식하여 활동 저장을 제안합니다.
- 경로 기록 중에는 앱을 종료하더라도 계속하여 위치를 기록합니다.
  - 경로 기록 중에는 언제든 자신의 위치에서 활동을 저장할 수 있습니다.

#### 경로 상세보기

[스크린샷 추가 예정]

- 해당 경로와 경로 위의 활동들을 지도에 표시합니다.
- 경로 위에 활동 정보를 마커로 표시합니다.
  - 현재 보고있는 활동이 상단에 표시됩니다.
  - 활동을 선택시 활동에 대한 정보가 하단 뷰에 나타납니다.
  - 왼쪽의 스크롤을 이용해 활동 목록을 조회할 수 있습니다.

<br>

### # 활동(UserAction)

[스크린샷 추가 예정]

- 코스 기록 중 해당 위치(좌표) 위에 활동을 추가할 수 있습니다.
  - 제목, 내용, 해시태그, 사진 등을 저장할 수 있습니다.
- 저장한 활동은 지도 위에 마커 형식, 피드 형식으로 한눈에 볼 수 있습니다.
  - 검색 기능을 통해 찾고자 하는 활동만을 필터링하여 볼 수 있습니다.

<br>

## Core Techniques

- 실시간 위치 트래킹을 위한 FusedLocationService
- 전체 경로를 간소화시켜 보여주기 위한 Google Static Map API
- 여러 활동마커를 지도위에 Clustering
- Rx를 통한 데이터 관련 비동기 처리
- Glide를 이용해 이미지 Cashing

<br>

# Development Environment

#### Develope tool

Android studio, Adobe XD, Kotlin, Github, SourseTree, Jenkins, with Google :D

<br>

## Architecture

우리는 앱 아키텍처를 고민할 때 [앱 아키텍처 가이드](https://developer.android.com/jetpack/docs/guide)에 제시된 권장 사항을 따르고자 했습니다. MVVM 패턴을 적용하기 위해 Activity에서 로직을 구현하지 않고, ViewModel로 옮겼습니다. 그리고 LiveData와 RxJava를 사용하여 데이터를 관찰하고 DataBinding을 사용하여 레이아웃의 UI 구성 요소를 앱의 데이터 소스에 바인딩했습니다.

### Libraries

- [Material design](https://material.io/develop/android/docs/building-from-source/)
- [RxJava](https://github.com/ReactiveX/RxAndroid)
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [DataBinding](https://developer.android.com/topic/libraries/data-binding/)
- [Room](https://developer.android.com/topic/libraries/architecture/room)
- [Glide](https://github.com/bumptech/glide)
- [Ted Permission](https://github.com/ParkSangGwon/TedPermission)

<br>

## Contributors

- [박주영](https://github.com/park-ju1008)
- [원지연](https://github.com/Onedelay)
- [현명준](https://github.com/myung6024)

<br>

## Documents

- [기능 기획서](https://docs.google.com/presentation/d/1DXavhXVLeU8ka2dPkgo0IMwUVNcy3K3AQOxKI9jtsQI/edit?usp=sharing)
- [화면 기획서](https://xd.adobe.com/view/241f9a86-50b2-4d45-7a09-a49544e8297d-522e/)
- [일정 계획서](https://docs.google.com/spreadsheets/d/1L4kp2iZllHA4a7ph0ld1K2v9Q3nztz7UG0-R9L9LhyI/edit#gid=0)