# RhythmHub 🎵

![Version](https://img.shields.io/badge/version-1.1.0-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Language](https://img.shields.io/badge/language-Kotlin-purple.svg)
![License](https://img.shields.io/badge/license-MIT-orange.svg)

> A mobile queue management system for Maimai arcade rhythm game players

## 📖 About

**RhythmHub** is an Android application designed to solve the common problem of disorganized queues for the popular arcade rhythm game, Maimai. The app provides a streamlined digital queueing system, allowing players to see their place in line in real-time while also serving as a community hub for players to share scores and coordinate play sessions.

This project is developed as part of the IT-INTPROG32 course (BSIT - SE 4) and demonstrates modern Android development practices using the latest technologies and architecture patterns.

## ✨ Features

### MVP Feature 1: Enhanced User Onboarding & Management ✅
- **Auto-Generated Usernames**: Reddit-style username generation (e.g., "CoolDancer1234")
- **DiceBear Avatars**: Unique auto-generated profile pictures for each user
- **User Authentication**: Secure login system with "Remember Me" functionality
- **Interactive Onboarding**: 4-page carousel with skip functionality and visual guidance
- **Profile Management**: Edit display name (IGN), regenerate avatar, view user info
- **Bottom Navigation**: Easy access to Home, Arcades, Community, and Profile

### UI Features Implemented ✅
- **Home/Dashboard**: User profile display with avatar and quick actions
- **Profile/Settings**: Edit display name, regenerate avatar, view role badge
- **Arcade Locator (Placeholder)**: Coming soon screen with planned features
- **Community Hub (Placeholder)**: Coming soon screen with planned features

### Coming Soon 🚀
- **Live Queueing System**: Real-time digital queue management
- **Next Turn Alerts**: Push notifications when it's your turn
- **Arcade Locator**: Interactive map with arcade locations
- **Community Features**: Forums, local chat, and player connections
- **Firebase Integration**: Cloud storage and real-time features

## 🏗️ Architecture

This application follows **MVVM (Model-View-ViewModel)** architecture pattern with **Clean Architecture** principles:

```
app/
├── data/                          # Data Layer
│   ├── model/                     # Data models
│   │   └── User.kt               # Enhanced with avatarSeed, displayName
│   ├── util/                      # Utilities
│   │   └── UsernameGenerator.kt  # Reddit-style username generation
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
│   │   ├── OnboardingScreen.kt   # 4-page carousel with skip
│   │   ├── OnboardingPage.kt     # Page data model
│   │   └── OnboardingViewModel.kt
│   │
│   ├── auth/                      # Authentication feature
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   └── AuthViewModel.kt
│   │
│   ├── main/                      # Main container with bottom nav
│   │   └── MainScreen.kt         # Bottom navigation structure
│   │
│   ├── home/                      # Home/Dashboard feature
│   │   ├── HomeScreen.kt         # Avatar display & user info
│   │   └── HomeViewModel.kt
│   │
│   ├── profile/                   # Profile/Settings feature
│   │   ├── ProfileScreen.kt      # Edit profile, regenerate avatar
│   │   └── ProfileViewModel.kt
│   │
│   ├── arcades/                   # Arcade Locator (placeholder)
│   │   └── ArcadesScreen.kt
│   │
│   └── community/                 # Community Hub (placeholder)
│       └── CommunityScreen.kt
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

// Material Icons Extended
implementation("androidx.compose.material:material-icons-extended:1.7.6")

// ViewModel & Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Coil for image loading (DiceBear avatars)
implementation("io.coil-kt:coil-compose:2.5.0")
implementation("io.coil-kt:coil-svg:2.5.0")
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
- 4-page interactive carousel with swipe navigation
- Skip functionality available at any time
- Page indicators showing progress
- Visual icons and descriptions for each feature
- Shown only on first launch

### 2. Login Screen
- Username/password authentication
- "Remember Me" checkbox
- Password visibility toggle
- Navigation to registration
- Default admin account (admin/admin)

### 3. Registration Screen
- Auto-generated Reddit-style username (editable)
- Password confirmation with visibility toggle
- Input validation
- Automatic redirect to login on success

### 4. Main Screen with Bottom Navigation
Four tabs providing access to all features:

**Home/Dashboard Tab:**
- DiceBear avatar display using Coil
- User display name and username (@username)
- Role badge (Administrator/Player)
- Quick action cards for upcoming features
- Logout functionality

**Arcades Tab (Placeholder):**
- "Coming Soon" screen with planned features
- Feature preview cards
- Map and location icons

**Community Tab (Placeholder):**
- "Coming Soon" screen with planned features
- Feature preview for local chat, forums, and player connections

**Profile/Settings Tab:**
- Large avatar display (120dp)
- Generate new avatar button (randomizes DiceBear seed)
- View and edit display name/IGN
- Username display (read-only)
- Role badge for administrators
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
1. ✅ **Module Integration**: Seamless navigation between 7 screens with bottom navigation
2. ✅ **Data Passing**: User objects, authentication state, profile data across screens
3. ✅ **SharedPreferences**: Persistent storage of user data, profiles, and preferences
4. ✅ **Navigation**: Navigation Compose with proper back stack and bottom navigation
5. ✅ **UI Consistency**: Unified Material 3 design system throughout all screens
6. ✅ **External API Integration**: DiceBear avatar API for unique profile pictures
7. ✅ **Image Loading**: Coil library for async SVG image loading
8. ✅ **Advanced UI**: HorizontalPager carousel, bottom navigation, inline editing

## 👥 Team

**RhythmHub Development Team**
- Francis Roel Abarca ( Project Manager & Lead Developer )
- Jhonn Vincent Arcipe ( Backend Developer )
- Sebastian Seth Escarro ( Mobile Developer )

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

**Built with ❤️ and ☕**