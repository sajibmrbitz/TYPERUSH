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
- [1. Quick Play (Download Release)](#1-quick-play-download-release)
- [2. Running from Source](#2-running-from-source)
- [3. Manual Installation Guide (Build EXE)](#3-manual-installation-guide-build-exe)
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
- **Engaging Visuals** — Dynamic combo streaks and smooth animations keep the gameplay exciting.
- **Lightweight** — Minimal system requirements, fast startup.

---

## ✨ Features

### 🏁 Core Gameplay
- **Real-Time Multiplayer Race:** Host a game server or join a friend's IP to compete in a live typing showdown.
- **Live Progress Tracking:** Visual cars racing on screen based on your progress.
- **Dynamic Combo Multiplier:** Accuracy-based 🔥 visual combo streaks (x2, x3, GODLIKE!).
- **Match Results Overlay:** Immediate post-match statistics showing WPM and Accuracy percentages.

### 📊 Dashboard & Analytics
- **All-Time Statistics:** Track total races, highest WPM, and average accuracy.
- **Graphical Visualization:** Interactive charts plotting your performance trends.
- **Local Data Persistence:** Game history and user profiles are saved locally.

---

## 🛠️ Tech Stack

| Component | Technology |
|---|---|
| Language | Java 17 |
| UI Framework | JavaFX |
| Networking | Java Sockets (TCP/IP) |
| Build Tool | Maven |
| Packaging | jpackage, WiX Toolset, Launch4j |

---

## 1. Quick Play (Download Release)

You don't need to install Java or compile any code to play this version!

### Step-by-Step Guide:
1. **Download the Game:** Go to the [Releases page](https://github.com/sajibmrbitz/TYPERUSH/releases/latest) and download `TypeRush_Release.zip`.
2. **Extract:** Unzip/extract the downloaded folder anywhere on your Windows PC.
3. **Play:** Open the extracted folder and double-click on **`TypeRush.exe`**. 

*(Note: The game comes bundled with its own JRE, so it runs without pre-installed Java).*

---

## 2. Running from Source

If you want to run the project directly from the code (For Developers):

### Prerequisites
- JDK 17 installed
- Git installed

### Steps
1. Clone the repository:
   `git clone https://github.com/sajibmrbitz/TYPERUSH.git`
2. Navigate to the directory:
   `cd TYPERUSH`
3. Run using Maven:
   `./mvnw clean javafx:run`

---

## 3. Manual Installation Guide (Build EXE)

Follow these steps to build the professional Windows `.exe` installer from scratch.

### Prerequisites

1. **JDK 17 or higher** (Verify: `java -version`)
2. **Maven 3.8+** (Verify: `mvn -version`)
   * **Important (Set Environment Variable):** After extracting Maven, you must add its `bin` folder path to your Windows Environment Variables.
   * *How to do it:* Search for "Environment Variables" in Windows Start menu ➔ Click "Edit the system environment variables" ➔ Click "Environment Variables" ➔ Under System variables, find and select `Path` ➔ Click "Edit" ➔ Click "New" and paste the path to your Maven `bin` folder (e.g., `C:\apache-maven-3.9.6\bin`) ➔ Click OK on all windows. Restart your terminal.
3. **WiX Toolset v3** (Required by jpackage. Install and **restart your PC**).

### Step 1 — Add the jpackage plugin to pom.xml
Add this inside `<build><plugins>` in your `pom.xml`:

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

### Step 2 — Build the project
Open terminal in the project root and run:
`mvn clean package`

### Step 3 — Generate the EXE installer
Run the following command:
`mvn jpackage:jpackage`

### Step 4 — Install the app
Go to `target/installer/` and double-click **`TypeRush-1.0.0.exe`** to install the game on your system with a desktop shortcut.

---

## 🗂️ Project Structure

    TYPERUSH/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/example/TYPERUSH/     # Logic & Controllers
    │   │   └── resources/                     # FXML, CSS, and Sounds
    ├── pom.xml                               # Maven config
    └── mvnw                                  # Maven wrapper

---

## 📄 License

This project was developed as part of an academic project at **BUET**. Free for educational use.

---
