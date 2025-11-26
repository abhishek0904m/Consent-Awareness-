# C-SUPER (Consent-Aware Sensor–UI Correlation Profiler)

## Overview

C-SUPER is a privacy-focused Android application that collects UI interaction events and sensor data only after explicit user consent. It correlates sensor and UI data by timestamps and provides a transparent dashboard showing what sensors are being used and when.

## Privacy Statement

**C-SUPER is designed for research and transparency purposes only. It does not record personal data or send information to any external server. All captured data remains local and fully under user control.**

## Tech Stack

- **Android**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room with SQLCipher encryption
- **Background Processing**: ForegroundService + SensorManager
- **UI Tracking**: AccessibilityService + UsageStats
- **Security**: AndroidX Security-Crypto (AES encryption)
- **Async**: Kotlin Coroutines
- **Dependency Injection**: Hilt
- **Min SDK**: 26
- **Target SDK**: 34

## Architecture

The app follows MVVM (Model-View-ViewModel) architecture with clean architecture principles:

```
├── data/                       # Data layer
│   ├── AppDatabase.kt         # Room database with SQLCipher
│   ├── Entities.kt            # Data entities
│   └── dao/                   # Data Access Objects
│       ├── SensorEventDao.kt
│       ├── UiEventDao.kt
│       ├── CorrelationResultDao.kt
│       └── ConsentReceiptDao.kt
├── ui/                        # UI layer (Jetpack Compose)
│   ├── ConsentScreen.kt       # Permission consent screen
│   ├── DashboardScreen.kt     # Main dashboard
│   ├── theme/                 # Material3 theme
│   └── navigation/            # Navigation graph
├── viewmodel/                 # ViewModels
│   ├── ConsentViewModel.kt
│   └── DashboardViewModel.kt
├── service/                   # Background services
│   ├── SensorForegroundService.kt
│   └── UiAccessibilityService.kt
├── correlation/               # Correlation engine
│   └── CorrelationEngine.kt
├── crypto/                    # Encryption utilities
│   └── EncryptionHelper.kt
├── util/                      # Utilities
│   └── ConsentStore.kt
└── di/                        # Dependency injection
    └── AppModule.kt
```

## Features

### 1. Consent Management
- Granular permission consent UI
- Store consent receipts with timestamps
- Ability to revoke consent anytime
- DataStore for persistent consent storage

### 2. Sensor Data Collection
- **Supported Sensors**:
  - Accelerometer
  - Gyroscope
  - Light sensor
  - GPS location
- Runs as Foreground Service with visible notification
- Data stored in encrypted Room database

### 3. UI Interaction Tracking
- Uses AccessibilityService to monitor:
  - Foreground app changes
  - Button clicks
  - Focus changes
  - Window state changes
- Privacy-conscious: No text content captured

### 4. Correlation Engine
- Correlates UI events with sensor events within ±500ms window
- Computes statistical summaries:
  - RMS (Root Mean Square)
  - Mean and variance
  - Sensor type distribution
- Stores correlation results in database

### 5. Dashboard
- Real-time monitoring status
- Active permissions display
- Statistics (sensor events, UI events, correlations)
- Recent correlations timeline
- Data export and deletion controls

### 6. Privacy & Security
- All data stored locally with AES encryption (SQLCipher)
- Encryption keys stored in Android Keystore
- No external data transmission
- Visible notification during monitoring
- User has full control over data

## Required Permissions

The app requests the following permissions:

- `BODY_SENSORS` - Access to device sensors
- `HIGH_SAMPLING_RATE_SENSORS` - High-frequency sensor sampling
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` - Location tracking
- `ACCESS_BACKGROUND_LOCATION` - Background location access
- `RECORD_AUDIO` - Microphone monitoring (not actively recording)
- `CAMERA` - Camera monitoring (not actively recording)
- `FOREGROUND_SERVICE` - Background service for sensor collection
- `PACKAGE_USAGE_STATS` - App usage tracking
- `BIND_ACCESSIBILITY_SERVICE` - UI interaction tracking
- `POST_NOTIFICATIONS` - Notification permission (Android 13+)

## Building the App

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Gradle 8.0+

### Build Instructions

1. Clone the repository:
```bash
git clone https://github.com/abhishek0904m/Consent-Awareness-.git
cd Consent-Awareness-
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build the project:
```bash
./gradlew build
```

5. Run on device/emulator:
```bash
./gradlew installDebug
```

## Setup Instructions

### First Launch

1. **Consent Screen**: Grant permissions for the sensors you want to monitor
2. **System Permissions**: 
   - Allow runtime permissions (Location, Camera, Microphone, etc.)
   - Enable Accessibility Service in Settings → Accessibility → C-SUPER
   - Enable Usage Stats in Settings → Apps → Special Access → Usage Access
3. **Dashboard**: Start profiling from the FAB button

### Using the App

- **Start Profiling**: Tap the play button (FAB) on dashboard
- **Stop Profiling**: Tap the stop button (FAB) on dashboard
- **View Statistics**: Scroll through dashboard to see counts and recent correlations
- **Export Data**: Use "Export Data" button (future implementation)
- **Delete Data**: Use "Delete All Data" button with confirmation

## Data Storage

All data is stored in an encrypted SQLite database (`csuper_db`) using SQLCipher:

### Tables

1. **sensor_events**: Sensor readings with timestamp, type, values, accuracy
2. **ui_events**: UI interactions with timestamp, package, event type, class
3. **correlation_results**: Correlation analysis results
4. **consent_receipts**: User consent records

### DataStore Preferences

User consent preferences are stored using Android DataStore:
- Permission consent flags
- Consent timestamps
- Profiling active state

## Security Considerations

### Encryption
- Database encrypted with SQLCipher (AES-256)
- Encryption keys stored in Android Keystore
- EncryptionHelper uses AES/GCM/NoPadding

### Privacy Protections
- No text content captured from UI
- Content descriptions limited to 50 characters
- No personal identifiable information stored
- All processing happens locally
- No network connectivity required

### Transparency
- Visible notification while monitoring
- Clear consent dialogs
- Privacy statement displayed in app
- User can revoke consent anytime

## Development Notes

### Dependency Versions
- Kotlin: 1.9.0
- Compose BOM: 2023.10.01
- Room: 2.6.0
- Hilt: 2.48
- SQLCipher: 4.5.4

### Code Quality
- All code documented with KDoc comments
- Privacy statements in all sensitive components
- Error handling for permission edge cases
- Proper resource cleanup in services

### Testing
- Unit tests: `./gradlew test`
- Instrumented tests: `./gradlew connectedAndroidTest`

## Future Enhancements

- [ ] Data export to JSON/CSV
- [ ] More sophisticated correlation algorithms
- [ ] Real-time sensor update rate display
- [ ] Filtering and search in correlations
- [ ] Consent receipt audit log
- [ ] More sensor types (proximity, magnetic field, etc.)
- [ ] Advanced privacy controls
- [ ] Data retention policies

## Troubleshooting

### AccessibilityService not working
1. Go to Settings → Accessibility
2. Find C-SUPER in installed services
3. Toggle it on
4. Grant permission

### Usage Stats not available
1. Go to Settings → Apps → Special Access → Usage Access
2. Find C-SUPER
3. Enable usage access

### Sensors not recording
- Check that profiling is active (green indicator)
- Verify permissions were granted
- Check notification is showing
- Restart the app

## License

This project is intended for research and educational purposes only.

## Credits

Developed as a privacy transparency and consent awareness tool for Android.

## Contact

For issues or questions, please open an issue on GitHub.
