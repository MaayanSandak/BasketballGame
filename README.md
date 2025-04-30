# 🏀 Basketball Endless Runner Game

An Android game project where the player controls a basketball that moves left and right between 3 lanes to avoid falling pins.

## 🎮 Features

- 3-lane movement system
- Randomly generated obstacles (pins)
- Vibrations and toast on hit
- 3 hearts system (lives)
- Automatic restart after game over
- Clean architecture using GameManager & GameController
- Material UI components

## 📁 Structure

- `MainActivity` — the main screen and UI
- `GameController` — logic of game ticks and flow
- `GameManager` — grid matrix, collision, lives
- `SignalManager` — vibration & toast manager
- `Constants.kt` — rows, columns, lives etc.

## 🚀 How to run

1. Clone the repo
2. Open in Android Studio
3. Run on an emulator or connected phone