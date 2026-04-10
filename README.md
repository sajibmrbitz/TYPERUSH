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
- [Manual Installation Guide (Build EXE)](#manual-installation-guide-build-exe)
- [Quick Play (Download Release)](#quick-play-download-release)
- [Running from Source](#running-from-source)
- [Project Structure](#project-structure)
- [License](#license)

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
| Packaging | maven-shade-plugin, Launch4j, jpackage |

---

## 📦 Manual Installation Guide (Build EXE)

Follow these steps to build and install TypeRush as a Windows `.exe` directly from the source code.

### Prerequisites

Make sure you have the following installed:

1. **JDK 17 or higher** Download from: [https://adoptium.net](https://adoptium.net) After installing, verify: `java -version`
2. **Maven 3.8+** Download from: [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) After installing, verify: `mvn -version`
3. **WiX Toolset v3** *(required by jpackage to build .exe)* Download from: [https://wixtoolset.org/releases](https://wixtoolset.org/releases) Install it, then **restart your PC** so it's added to PATH.

### Step 1 — Add the jpackage plugin to pom.xml

Open your `pom.xml` and add this inside `<build><plugins>`:

    <plugin>
        <groupId>org.panteleyev</groupId>
        <artifactId>jpackage-maven-plugin</artifactId>
        <version>1.6.0</version>
        <configuration>
            <name>TypeRush</name>
            <appVersion>1.0.0</appVersion>
            <vendor>Sajib</vendor>
            <mainClass>com.example.TYPERUSH.Launcher</mainClass>
            <mainJar>TYPERUSH-1.0-SNAPSHOT.jar</mainJar>
            <type>EXE</type>
            <winDirChooser>true</winDirChooser>
            <winShortcut>true</winShortcut>
            <winMenu>true</winMenu>
            <destination>target/installer</destination>
        </configuration>
    </plugin>

> ⚠️ Change `<mainJar>` to match the actual JAR name in your `target/` folder after building.

### Step 2 — Build the project

Open a terminal in your project root folder and run:

    mvn clean package

This compiles everything and produces a JAR in the `target/` folder.

### Step 3 — Generate the EXE installer

    mvn jpackage:jpackage

This will create the installer at:

    target/installer/TypeRush-1.0.0.exe

### Step 4 — Install the app

1. Double-click `TypeRush-1.0.0.exe`
2. Follow the installer wizard
3. Choose installation directory
4. A desktop shortcut and Start Menu entry will be created automatically
5. Launch **TypeRush** from the desktop or Start Menu

### Step 5 — First Launch

On first launch, the app will automatically generate local files in the installation directory to save your user profile, race history, and dashboard statistics locally.

---

## 🎮 Quick Play (Download Release)

You don't need to install Java, compile code, or use IDEs to play this game! It comes bundled with its own runtime environment.

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

## 📄 License

This project was developed as part of an academic project at **BUET (Bangladesh University of Engineering and Technology)**. 
Free to use for educational purposes.
