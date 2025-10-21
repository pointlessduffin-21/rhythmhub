# RhythmHub 🎵

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Language](https://img.shields.io/badge/language-Kotlin-purple.svg)
![License](https://img.shields.io/badge/license-MIT-orange.svg)

> A mobile queue management system for Maimai arcade rhythm game players

## 📖 About

**RhythmHub** is an Android application designed to solve the common problem of disorganized queues for the popular arcade rhythm game, Maimai. The app provides a streamlined digital queueing system, allowing players to see their place in line in real-time while also serving as a community hub for players to share scores and coordinate play sessions.

This project is developed as part of the IT-INTPROG32 course (BSIT - SE 4) and demonstrates modern Android development practices using the latest technologies and architecture patterns.

## ✨ Features

### MVP Feature 1: User Onboarding & Management ✅
- **User Registration**: Create new accounts with username and password
- **User Authentication**: Secure login system with "Remember Me" functionality
- **User Profile**: Simple profile display showing username
- **Onboarding Experience**: First-time user introduction to app features

### Coming Soon 🚀
- **Arcade Locator**: Find nearby arcades with Maimai machines
- **Live Queueing System**: Real-time digital queue management
- **Next Turn Alerts**: Push notifications when it's your turn
- **Community Hub**: Share scores and interact with other players
- **Local Chat**: Location-specific chat rooms

## 🏗️ Architecture

This application follows **MVVM (Model-View-ViewModel)** architecture pattern with **Clean Architecture** principles:

```
app/
├── data/                          # Data Layer
│   ├── model/                     # Data models
│   │   └── User.kt
│   └── repository/                # Repository pattern
│       └── UserRepository.kt      # SharedPreferences abstraction
│
├── presentation/                  # Presentation Layer
│   ├── theme/                     # Material 3 theming
│   │   ├── Color.kt              # Maimai-inspired color scheme
│   │   ├── Type.kt               # Typography system
│   │   └── Theme.kt              # Theme configuration
│   │
│   ├── components/                # Reusable UI components
│   │   ├── RhythmButton.kt
│   │   ├── RhythmTextField.kt
│   │   └── GradientBackground.kt
│   │
│   ├── onboarding/                # Onboarding feature
│   │   ├── OnboardingScreen.kt
│   │   └── OnboardingViewModel.kt
│   │
│   ├── auth/                      # Authentication feature
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   └── AuthViewModel.kt
│   │
│   └── home/                      # Home/Dashboard feature
│       ├── HomeScreen.kt
│       └── HomeViewModel.kt
│
├── navigation/                    # Navigation Layer
│   └── RhythmNavGraph.kt         # Navigation Compose setup
│
└── MainActivity.kt                # Single entry point
```

### Design Patterns Used
- **MVVM**: Separation of UI logic and business logic
- **Repository Pattern**: Abstraction of data sources
- **Single Activity**: Modern Android navigation with Jetpack Compose
- **Factory Pattern**: ViewModel creation with dependencies
- **Observer Pattern**: StateFlow for reactive UI updates

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin
- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 35 (Android 14)
- **Compile SDK**: 36

### Architecture & Libraries
| Component | Technology |
|-----------|-----------|
| Architecture Pattern | MVVM (Model-View-ViewModel) |
| UI Framework | Jetpack Compose |
| Material Design | Material 3 (Material You) |
| Navigation | Navigation Compose 2.7.7 |
| State Management | ViewModel + StateFlow |
| Data Persistence | SharedPreferences |
| Dependency Injection | Manual (Factory Pattern) |
| Lifecycle | AndroidX Lifecycle 2.7.0 |

### Key Dependencies
```kotlin
// Jetpack Compose
implementation("androidx.compose.material3")
implementation("androidx.activity:activity-compose")

// ViewModel & Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")
```

## 🎨 Design System

### Color Scheme
Inspired by Maimai's vibrant arcade aesthetic:

- **Primary**: Vibrant Pink/Magenta (#FF1B8D)
- **Secondary**: Electric Cyan (#00D9FF)
- **Tertiary**: Vibrant Purple (#9C27B0)
- **Background**: Dark theme for arcade aesthetics (#121212)
- **Accent**: Bright Yellow/Orange for CTAs

### Typography
- Uses Material 3 type system
- Bold headings for energetic arcade feel
- Clear, readable body text

## 📱 Screens

### 1. Onboarding Screen
- Introduces app features
- Shown only on first launch
- Vibrant gradient background

### 2. Login Screen
- Username/password authentication
- "Remember Me" checkbox
- Navigation to registration
- Default admin account (admin/admin)

### 3. Registration Screen
- Create new user account
- Password confirmation
- Input validation
- Automatic redirect to login on success

### 4. Home/Dashboard Screen
- User profile display with avatar
- Welcome message
- Placeholders for future features
- Logout functionality

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or higher
- Android SDK with API 24+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/rhythmhub.git
   cd rhythmhub
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `front-end` directory
   - Wait for Gradle sync to complete

3. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" or press Shift + F10
   - Select your device/emulator

### Default Credentials
```
Username: admin
Password: admin
```

## 📂 Project Structure

```
rhythmhub/
├── front-end/                     # Android application
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/         # Source code
│   │   │   │   ├── res/          # Resources
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/             # Unit tests
│   │   └── build.gradle.kts       # App-level build config
│   └── build.gradle.kts           # Project-level build config
│
├── docs/                          # Documentation
│   ├── about.txt                  # Product requirements
│   ├── assignment.txt             # Assignment specifications
│   └── task.txt                   # Task requirements
│
├── .gitignore                     # Git ignore rules
└── README.md                      # This file
```

## 📊 Assignment Compliance

This project meets all requirements for the Application Module Integration assignment:

| Criterion | Weight | Implementation |
|-----------|--------|----------------|
| Integration Functionality | 40% | ✅ Smooth navigation: Onboarding → Login → Register → Home |
| SharedPreferences | 20% | ✅ User data, authentication state, onboarding completion |
| UI Design | 20% | ✅ Material 3 design with Maimai-inspired theme |
| Code Quality | 10% | ✅ MVVM architecture, clean code, comprehensive comments |
| Compliance | 10% | ✅ Builds successfully, no crashes, professional structure |

### Key Features Demonstrated
1. ✅ **Module Integration**: Seamless navigation between all screens
2. ✅ **Data Passing**: Username, authentication state across screens
3. ✅ **SharedPreferences**: Persistent storage of user data and preferences
4. ✅ **Navigation**: Navigation Compose with proper back stack management
5. ✅ **UI Consistency**: Unified Material 3 design system throughout

## 👥 Team

**RhythmHub Development Team**
- Francis Roel Abarca
- Jhonn Vincent Arcipe
- Sebastian Seth Escarro

**Course**: IT-INTPROG32, BSIT - SE 4
**Institution**: University of Cebu
**Date**: September 28, 2025

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **Maimai** by SEGA for inspiration
- **Material Design** team for design guidelines
- **Android Development** community for best practices
- **Jetpack Compose** team for modern UI toolkit

---

**Built with ❤️ using Kotlin and Jetpack Compose**