# PlanWise

PlanWise is an Android app for tracking and managing client projects, payments, and statuses. Built with Jetpack Compose and Room.

## Features

- Add, edit, and delete projects
- Track client info, payments, and project status
- Filter projects (All, Ongoing, Completed, Unpaid)
- Import/export projects as CSV
- Responsive UI for portrait and landscape
- Material 3 design

## Screenshots

### 🌞 Light Mode

| MainScreen | Add project | Project list |
|--------------|--------------|--------------|
| ![1](https://github.com/user-attachments/assets/dc3f0b73-261f-49e4-997e-b6dc73e79a52) | ![2](https://github.com/user-attachments/assets/6f59c9ce-13a1-47f1-8943-4e9143d8d4f8) | ![3](https://github.com/user-attachments/assets/d80621f8-8760-461a-a06b-388c5f1a0435) |

| Project Details | Landscape |
|--------------|--------------|
| ![4](https://github.com/user-attachments/assets/873ed6e9-305e-4aa9-a338-fb2b00f1bdb7) | ![5](https://github.com/user-attachments/assets/7ad41783-7ff3-42c9-bbd0-eb4f43c97c73) |



### 🌙 Dark Mode

| Screenshot 1 | Screenshot 2 | Screenshot 3 |
|--------------|--------------|--------------|
| ![1](https://github.com/user-attachments/assets/eec815e6-12aa-467c-a51d-1896ac5dc2a9) | ![2](https://github.com/user-attachments/assets/b102546d-bc0f-4f90-93c1-8f6131d0c787) | ![3](https://github.com/user-attachments/assets/810c6ec1-673a-425d-9ffd-0bba8e3cf241) |

| Screenshot 4 |
|--------------|
| ![4](https://github.com/user-attachments/assets/70eade1a-e621-49c5-a4fb-b16ee2782acc) |



## Getting Started

### Prerequisites

- Android Studio (Giraffe or newer)
- Android SDK 24+
- Kotlin 1.9+

### Setup

1. Clone the repository: git clone https://github.com/saubhagyarb/planwise.git
2. Open in Android Studio.
3. Build and run on an emulator or device.

### Build

- Uses Gradle Kotlin DSL (`build.gradle.kts`)
- Room for local database
- Jetpack Compose for UI

## Project Structure

- `data/` — Room entities, DAO, repository
- `screens/` — UI screens and ViewModel
- `navigation/` — Navigation setup
- `ui/theme/` — Material 3 theme

## Usage

- Tap `+` to add a new project.
- Tap a project to view details.
- Use the menu to import/export CSV files.
