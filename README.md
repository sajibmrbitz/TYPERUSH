# 🏁 TypeRush - Multiplayer Typing Game

![Java](https://img.shields.io/badge/Java-17-orange.svg) 
![JavaFX](https://img.shields.io/badge/JavaFX-GUI-blue.svg)
![Sockets](https://img.shields.io/badge/Networking-TCP%2FIP-green.svg)

TypeRush is a highly competitive, real-time multiplayer typing game built with **Java** and **JavaFX**. Challenge your friends, race against them live, and track your typing speed and accuracy through a detailed statistics dashboard.

### 🎮 [Download the Latest Game (.exe)](https://github.com/sajibmrbitz/TYPERUSH/releases/tag/v1.0.0))
*(No Java installation required! The game comes bundled with its own JRE. Just download the Zip, extract, and play!)*

---

## ✨ Key Features
* **Real-Time Multiplayer Race:** Host a server or join via IP to compete in a live typing showdown.
* **Live Progress Tracking:** See your car and your opponent's car move in real-time based on typing progress.
* **Dynamic Combo Multiplier:** Type words accurately without backspaces to trigger 🔥 visual combo streaks (x2, x3, GODLIKE!).
* **Detailed Analytics Dashboard:** Track your All-Time stats and Today's History. Includes graphical charts to visualize your WPM (Words Per Minute) and Accuracy over time.
* **Match Results Overlay:** Immediate post-match statistics showing who won, with exact WPM and Accuracy percentages.

---

## 🛠️ Tech Stack & Concepts Used
* **Language:** Java 17
* **GUI Framework:** JavaFX
* **Networking:** Java Socket Programming (TCP/IP) for real-time client-server communication
* **Data Management:** File I/O for saving/loading local user profiles, game history, and dashboard statistics
* **Build Tool:** Maven (maven-shade-plugin for Fat JAR bundling)

---

## 👨‍💻 How to Run from Source (For Developers)
If you want to clone the repository and run the code directly:

1. Clone the repository:
   
   git clone [https://github.com/sajibmrbitz/TYPERUSH.git](https://github.com/sajibmrbitz/TYPERUSH.git)

2.Navigate to the project directory:

  cd TYPERUSH
  
3.Build and Run using Maven Wrapper:

  ./mvnw clean javafx:run
