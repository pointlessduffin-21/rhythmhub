# Changelog

All notable changes to the RhythmHub project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-01-22

### Added

#### User Profile System
- **DiceBear Avatar Integration**: Unique auto-generated profile pictures for each user
  - Uses DiceBear API v7 with "adventurer" style
  - SVG format for crisp rendering at any size
  - Seed-based generation ensures consistency across sessions
  - Users can regenerate avatars with new random seeds
- **Reddit-Style Username Generation**: Auto-generated usernames in format "AdjectiveNoun####"
  - 48 rhythm/music-themed adjectives (e.g., "Rhythm", "Beat", "Melody", "Harmony")
  - 48 music-themed nouns (e.g., "Note", "Chord", "Song", "Tune")
  - 4-digit random number suffix (1000-9999)
  - 6,912+ possible unique combinations
  - Usernames are editable during registration
- **Enhanced User Model**: Extended with `displayName`, `avatarSeed`, and `isAdmin` fields
  - `displayName`: User's chosen display name/IGN (In-Game Name)
  - `avatarSeed`: Seed for DiceBear avatar generation
  - `isAdmin`: Boolean flag for administrator privileges
  - `getAvatarUrl()`: Method to generate DiceBear API URL

#### Enhanced Onboarding Experience
- **4-Page Interactive Carousel**: Swipeable onboarding with HorizontalPager
  - **Page 1**: Welcome to RhythmHub - Introduction and app overview
  - **Page 2**: Find Arcades - Discover nearby Maimai machines
  - **Page 3**: Join the Queue - Digital queue management preview
  - **Page 4**: Connect with Players - Community features preview
- **Skip Functionality**: Users can skip onboarding at any time
- **Page Indicators**: Visual dots showing current page and progress
- **Navigation Controls**: Back/Next buttons with context-aware labels
- **Visual Design**: Each page features relevant Material icons and descriptions

#### Profile/Settings Screen
- **Avatar Display**: Large circular avatar (120dp) with proper image loading
- **Generate New Avatar**: Button to randomize avatar with new seed
- **Inline Display Name Editing**:
  - Read-only view with edit icon
  - Click to enter edit mode with save/cancel buttons
  - Validation: Minimum 2 characters, non-empty
  - Success/error feedback via Snackbar
- **Username Display**: Read-only username field
- **Role Badge**: Visual indicator for Administrator role
- **Logout Functionality**: Clean logout with navigation to login

#### Bottom Navigation System
- **4-Tab Structure**: Home, Arcades, Community, Profile
- **Material 3 NavigationBar**: Modern bottom navigation with proper theming
- **Icon States**: Filled icons for selected tab, outlined icons for unselected
- **Tab Highlighting**: Primary color for selected tab, surface variant for unselected
- **Indicator Design**: Primary container background for selected tab

#### Placeholder Screens
- **Arcade Locator Screen**:
  - "Coming Soon" badge
  - Feature preview cards: Find Nearby Arcades, Search & Filter, Interactive Map
  - Visual icons and descriptions
  - Consistent Material 3 design
- **Community Hub Screen**:
  - "Coming Soon" badge
  - Feature preview cards: Local Chat, Community Forums, Find Players
  - Visual icons and descriptions
  - Consistent Material 3 design

#### Enhanced Home/Dashboard
- **Avatar Display**: DiceBear avatar loaded via Coil (100dp circular)
- **User Information**:
  - Display name prominently shown
  - Username in @username format (secondary)
  - Role badge (Administrator/Player)
- **Quick Action Cards**: Placeholder cards for upcoming features
- **Welcome Message**: Informative card explaining MVP status

#### Technical Improvements
- **Coil Image Loading**:
  - `coil-compose:2.5.0` for Compose integration
  - `coil-svg:2.5.0` for SVG format support
  - AsyncImage composable for async loading
  - ContentScale.Crop for proper avatar display
- **Material Icons Extended**:
  - `material-icons-extended:1.7.6` dependency added
  - Access to 5000+ Material Design icons
  - Resolves compilation errors for extended icons
- **UsernameGenerator Utility**:
  - Object-based singleton design
  - Random adjective + noun + number generation
  - Music/rhythm-themed word lists
- **UserRepository Enhancements**:
  - `getUserProfiles()`: Retrieves profile data from SharedPreferences
  - `saveUserProfile()`: Saves profile data as JSON
  - `getUser()`: Returns complete User object with profile data
  - `updateDisplayName()`: Updates user's display name
  - `updateAvatarSeed()`: Updates user's avatar seed
  - Profile data stored separately from credentials

### Changed

#### Data Layer
- **User Model**: Expanded from simple username/password to full profile model
- **UserRepository**: Enhanced with profile management methods
- **Data Persistence**: Separate JSON storage for user profiles in SharedPreferences
  - Key: `user_profiles_json`
  - Format: JSON map of username → profile data

#### Presentation Layer
- **HomeViewModel**:
  - Changed from storing username string to User object
  - Updated `loadUserData()` to fetch complete user profiles
- **HomeScreen**:
  - Replaced static icon with DiceBear avatar
  - Enhanced user information display
  - Added visual hierarchy (displayName > @username > role)
- **AuthViewModel**:
  - Added `generateUsername()` method
  - Integration with UsernameGenerator utility
- **Navigation Structure**:
  - Replaced direct HomeScreen with MainScreen container
  - Added bottom navigation routing
  - Updated RhythmNavGraph imports and composable

#### UI/UX Improvements
- **Onboarding**: Transformed from single screen to interactive 4-page carousel
- **Profile Management**: Added comprehensive profile editing capabilities
- **Navigation**: Implemented bottom navigation for easier feature access
- **Visual Consistency**: Unified Material 3 design across all new screens

### Fixed
- **Material Icons Compilation Errors**: Added material-icons-extended dependency
  - Resolved: LibraryMusic, Visibility, VisibilityOff, Queue, Album icons
- **Avatar Loading**: Implemented proper async image loading with Coil
- **Profile Persistence**: Fixed profile data storage and retrieval

### Technical Details

#### Dependencies Added
```kotlin
// Material Icons Extended
implementation("androidx.compose.material:material-icons-extended:1.7.6")

// Coil for image loading
implementation("io.coil-kt:coil-compose:2.5.0")
implementation("io.coil-kt:coil-svg:2.5.0")
```

#### API Integration
- **DiceBear API**: `https://api.dicebear.com/7.x/adventurer/svg?seed={avatarSeed}`
- **Version**: 7.x
- **Style**: adventurer
- **Format**: SVG (scalable vector graphics)

#### New Files Created
- `data/util/UsernameGenerator.kt`
- `presentation/onboarding/OnboardingPage.kt`
- `presentation/profile/ProfileScreen.kt`
- `presentation/profile/ProfileViewModel.kt`
- `presentation/arcades/ArcadesScreen.kt`
- `presentation/community/CommunityScreen.kt`
- `presentation/main/MainScreen.kt`

#### Files Modified
- `data/model/User.kt` - Added avatarSeed, displayName, isAdmin fields
- `data/repository/UserRepository.kt` - Added profile management methods
- `presentation/onboarding/OnboardingScreen.kt` - Complete rewrite with carousel
- `presentation/auth/AuthViewModel.kt` - Added username generation
- `presentation/home/HomeScreen.kt` - Added avatar display
- `presentation/home/HomeViewModel.kt` - Changed to use User model
- `navigation/RhythmNavGraph.kt` - Updated to use MainScreen
- `build.gradle.kts` - Added Coil and Material Icons Extended dependencies

## [1.0.0] - 2025-01-15

### Added
- **Initial Release**: MVP Feature 1 - User Onboarding & Management
- **User Authentication**: Login and registration system with SharedPreferences
- **Onboarding Screen**: First-time user introduction (single page)
- **Login Screen**: Username/password authentication with "Remember Me"
- **Registration Screen**: New user account creation with validation
- **Home/Dashboard**: Basic user profile display
- **MVVM Architecture**: Clean architecture with Repository pattern
- **Material 3 Design**: Maimai-inspired color scheme and theming
- **Navigation Compose**: Proper screen navigation with back stack management
- **SharedPreferences**: Persistent user data storage
- **Custom Components**: RhythmButton, RhythmTextField, GradientBackground
- **Admin Account**: Default admin/admin credentials
- **Form Validation**: Input validation for login and registration
- **State Management**: ViewModel + StateFlow for reactive UI
- **Professional Documentation**: Comprehensive README and code comments

### Technical Stack (Initial)
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel & StateFlow
- SharedPreferences
- Minimum SDK 24
- Target SDK 35

---

## Version History Summary

- **v1.1.0** (2025-01-22): Enhanced profiles, avatars, onboarding, and navigation
- **v1.0.0** (2025-01-15): Initial MVP release with basic authentication

---

**Project**: RhythmHub
**Team**: Francis Roel Abarca, Jhonn Vincent Arcipe, Sebastian Seth Escarro
**Course**: IT-INTPROG32, BSIT - SE 4
**Institution**: University of Cebu
