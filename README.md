# RemindMe

RemindMe is a simple, private, and offline reminder app designed to easily set quick reminders for tasks, events, and daily routines.
It ensures that all data stays on your device, requiring no accounts or internet connection.

## Features

- **Create and Manage Reminders:** Easily set, edit, and delete reminders for your tasks.
- **Recurring Reminders:** Support for daily, weekly, monthly, and yearly repeats to build routines.
- **Detailed Information:** Add titles and optional descriptions to your reminders.
- **Exact Notifications:** Get notified precisely at the scheduled time.
- **Privacy-First:** Works completely offline. No accounts or login required, all data stays on your device.
- **Modern Interface:** Lightweight, minimalistic, and intuitive user interface.

## Screenshots

|                                              |                                              |
|:--------------------------------------------:|:--------------------------------------------:|
| ![Screenshot-1](assets/img/Screenshot-1.jpg) | ![Screenshot-2](assets/img/Screenshot-2.jpg) |
| ![Screenshot-3](assets/img/Screenshot-3.jpg) | ![Screenshot-4](assets/img/Screenshot-4.jpg) |
| ![Screenshot-5](assets/img/Screenshot-5.jpg) |                                              |

## Installation

### From Play Store

Download and install RemindMe directly from the [Play Store](https://play.google.com/store/apps/details?id=com.cabovianco.remindme).

### From Source Code

1. Clone this repository:
    ```bash
    git clone https://github.com/cabovianco/android-remindme-app.git
    ```
2. Open the project in Android Studio.
3. Build and run the app on your device or emulator.

## Technologies

- **Language:** Kotlin.
- **Architecture:** MVVM, Clean Architecture.
- **UI:** Jetpack Compose, Material Design 3, Navigation Compose, Splash Screen.
- **Local Storage:** Room.
- **Scheduling:** AlarmManager.
- **Asynchronous & Data:** Kotlin Coroutines, Kotlin Flow, Kotlin Serialization.
- **Dependency Injection:** Dagger Hilt.

## App Structure

The app follows **Clean Architecture** principles, organized into the following package structure:

```text
com.cabovianco.remindme
├── data/                   # Implementation of data sources
│   ├── alarm/              # Alarm scheduling (AlarmManager)
│   ├── local/              # Room database, DAOs, and entities
│   └── repository/         # Repository implementations
│
├── di/                     # Dependency Injection modules
│
├── domain/                 # Business logic
│   ├── model/              # Domain entities
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Domain operations
│
├── presentation/           # UI Layer
│   ├── navigation/         # Screen definitions and NavHost
│   ├── notification/       # Notification and BroadcastReceivers
│   ├── state/              # States
│   ├── ui/
│   │   ├── screen/         # Screens and components
│   │   └── theme/          # App theme (color, type, etc.)
│   └── viewmodel/          # ViewModels
```

## Room Data Modeling

The local database structure is organized as follows:

### `reminders` Table

Stores the information for each reminder.
- **Fields:**
    - `id`: Primary Key (Integer, Auto-generated).
    - `title`: The title of the task or event.
    - `description`: Optional extra details.
    - `dateTime`: The scheduled date and time (stored as ISO Zoned Date Time).
    - `repeat`: Recurrence type (`NEVER`, `DAILY`, `WEEKLY`, `MONTHLY`, `YEARLY`).

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
