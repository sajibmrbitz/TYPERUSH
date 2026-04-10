# 🏁 TypeRush - Multiplayer Typing Game

![Java](https://img.shields.io/badge/Java-17-orange.svg) 
![JavaFX](https://img.shields.io/badge/JavaFX-GUI-blue.svg)
![Sockets](https://img.shields.io/badge/Networking-TCP%2FIP-green.svg)

A competitive, real-time multiplayer typing game built with **JavaFX** and **Socket Programming**, designed for racing against friends, tracking typing speed, and improving accuracy — all from a single, fast, and responsive desktop application.

---

## 📋 Table of Contents

- [About](#about)
- [Benefits](#benefits)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Installation Guide (exe)](#installation-guide-exe)
- [Running from Source](#running-from-source)
- [Project Structure](#project-structure)

---

## About

TypeRush is a highly competitive typing game that allows players to host or join real-time typing races. It features a robust networking system for live synchronization, dynamic visual feedback, and a comprehensive dashboard to track user performance over time. The game runs locally with data persistence, making it fast and reliable.

---

## ✅ Benefits

- **Real-Time Competition** — Race against friends live via IP connection.
- **Immediate Feedback** — See your WPM, accuracy, and progress update instantly as you type.
- **Skill Tracking** — Detailed statistics dashboard to monitor your long-term improvement.
- **No Installation Required** — Ships as a standalone executable with a bundled JRE; just unzip and play.
- **Engaging Visuals** — Dynamic combo streaks and smooth animations keep the gameplay exciting.
- **Lightweight** — Minimal system requirements, fast startup.

---

## ✨ Features

### 🏁 Core Gameplay
- **Real-Time Multiplayer Race:** Host a game server or join a friend's IP to compete in a live typing showdown.
- **Live Progress Tracking:** Visual representation (e.g., cars racing) of your progress compared to your opponent in real-time.
- **Dynamic Combo Multiplier:** Type words accurately without backspaces to trigger awesome visual combo streaks (x2, x3, GODLIKE!).
- **Immediate Validation:** Keystrokes are validated instantly, with visual color-coding for correct and incorrect inputs.
- **Match Results Overlay:** Immediate post-match statistics showing who won, with exact WPM (Words Per Minute) and Accuracy percentages.

### 📊 Dashboard & Analytics
- **All-Time Statistics:** Track your total races, highest WPM, and average accuracy across all sessions.
- **Today's History:** View a detailed breakdown of your performance for the current day.
- **Graphical Visualization:** Interactive charts plotting your WPM and Accuracy trends over time to visualize improvement.
- **Local Data Persistence:** Game history and user profiles are saved locally.

---

## 🛠️ Tech Stack

| Component | Technology |
|---|---|
| Language | Java 17 |
| UI Framework | JavaFX |
| Networking | Java Sockets (TCP/IP) |
| Build Tool | Maven |
| Packaging | maven-shade-plugin (Fat JAR), Launch4j (.exe wrapper) |

---

## 📦 Installation Guide (EXE)

You don't need to install Java or any IDE to play this game! It comes bundled with its own runtime environment.

### Step-by-Step Guide:

1. **Download the Game:** Go to the [Releases page](https://github.com/sajibmrbitz/TYPERUSH/releases/latest) and download the `TypeRush_Release.zip` file.
2. **Extract:** Unzip/extract the downloaded folder anywhere on your Windows PC.
3. **Play:** Open the extracted folder and double-click on `TypeRush.exe`. 

*(Note: Ensure that the `jre` folder remains in the same directory as the `.exe` file for the game to run properly).*

---

## 🚀 Running from Source

If you want to run the project directly without building an EXE (For Developers):

### Prerequisites
- JDK 17 installed
- Git installed

### Steps

1. Clone the repository:
   
       git clone [https://github.com/sajibmrbitz/TYPERUSH.git](https://github.com/sajibmrbitz/TYPERUSH.git)

2. Navigate to the project directory:
   
       cd TYPERUSH

3. Run using the Maven Wrapper:
   
       ./mvnw clean javafx:run

---

## 🗂️ Project Structure

    TYPERUSH/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/example/TYPERUSH/
    │   │   │   ├── BaseController.java           # Shared UI logic
    │   │   │   ├── GameClient.java               # Network client logic
    │   │   │   ├── GameServer.java               # Network server logic
    │   │   │   ├── GameSession.java              # Manages current game state
    │   │   │   ├── HelloApplication.java         # Main JavaFX application class
    │   │   │   ├── Launcher.java                 # Entry point for the executable
    │   │   │   ├── MultiplayerGameController.java# Core gameplay UI & logic
    │   │   │   ├── ProgressListener.java         # Interface for UI updates
    │   │   │   └── SoundManager.java             # Audio playback
    │   │   └── resources/
    │   │       ├── com/example/TYPERUSH/         # FXML layout files and CSS
    │   │       └── sounds/                       # Audio assets
    ├── pom.xml                                   # Maven configuration & dependencies
    └── mvnw / mvnw.cmd                           # Maven wrapper scripts

---

**Happy Gaming!!!**
Free to use for educational purposes.

