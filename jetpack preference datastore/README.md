# Words App

This folder contains the source code for the Words app codelab.


# Introduction
Words app allows you to select a letter and use Intents to navigate to an Activity that
presents a number of words starting with that letter. Each word can be looked up via a web search.

Words app contains a scrollable list of 26 letters A to Z in a RecyclerView. The orientation
of the RecyclerView can be changed between a vertical list or a grid of items.

The app demonstrates the use of Intents in two ways:
* to navigate inside an app by specifying an explicit destination, and,
* allowing Android to service the Intent using the apps and resources present on the device.

# Pre-requisites
* Experience with Kotlin syntax.
* Able to create an Activity.
* Able to create a RecyclerView and supply it with data.

# Getting Started
1. Install Android Studio, if you don't already have it.
2. Download the sample.
3. Import the sample into Android Studio.
4. Build and run the sample.


# Preference DataStore 소개
- Preferences DataStore는 로그인 세부정보 저장이나 어두운 모드 설정, 글꼴 크기 등 작고 간단한 데이터 세트에 적합
- 데이터베이스와 같은 복잡한 데이터 세트에는 적합하지 않음 -> Room or SQLite 사용 
- 즉 가벼운 데이터를 저장하기에 좋음

- Jetpack DataStore 라이브러리를 사용하면 데이터 저장을 위한 간단하고 안전한 비동기 API를 만들 수 있음  
- DataStore 라이브러리는 Preference DataStore와 Proto DataStore 두 가지를 제공
- 위 Store 모두 데이터 저장이 가능하지만 저장방법이 다름 
  - **Preferences DataStore**는 먼저 스키마(데이터베이스 모델)를 정의하지 않고 키에 기반하여 데이터에 액세스하고 저장
  - **Proto DataStore** 는 프로토콜 버퍼를 사용하여 스키마를 정의합니다. 프로토콜 버퍼(Protobuf)를 사용하면 강타입(strongly typed) 데이터를 유지할 수 있음
    - Protobuf는 XML 및 기타 유사한 데이터 형식보다 빠르고 작고 간결하며 덜 모호한 데이터 형식
