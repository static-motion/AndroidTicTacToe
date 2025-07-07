# AndroidTicTacToe

A feature-rich Android application for playing Tic Tac Toe in both single-player and multiplayer modes. The game supports AI opponents and peer-to-peer multiplayer.


*Note: This is a legacy project created during early Android development learning. While functional, the codebase reflects learning-stage coding practices and is maintained for historical/portfolio purposes.*

## Features

- 🎮 Classic Tic Tac Toe gameplay
- 🤖 Single-player mode with adjustable AI difficulty
- 🤝 Multiplayer mode (peer-to-peer device connectivity)
- ⚙️ Configurable game settings
- 📱 Smooth Android UI and transitions
- 📡 Event-driven architecture for real-time gameplay

## How Multiplayer Works

The multiplayer mode allows two Android devices to connect and play in real time. It uses local device discovery and connectivity (via Bluetooth and local Wi-Fi). Events such as device discovery, connection, and move synchronization are handled using a custom event system:

- `DeviceFoundEvent` – Triggered when a nearby player is found
- `DeviceConnectedEvent` – Fired upon successful connection
- `OpponentMoveEvent` – Transmits moves between players
- `PlayerDisconnectedEvent` – Handles disconnections gracefully

## AI Difficulty Levels

In single-player mode, users can choose from multiple AI difficulty levels:

- **Easy** – AI makes random or simple moves
- **Medium** – AI uses basic strategies to block or win
- **Hard** – AI plays optimally using algorithms like Minimax

This provides a fun challenge for players of all skill levels.

## Architecture Overview

The project follows a modular structure:

- `activities/` – UI logic for each screen (`HomeActivity`, `GameActivity`, etc.)
- `events/` – Event bus-style communication between components
- `enums/` – Defines shared constants like `GameState`
- `GameApplication.java` – Application-level configuration and initialization
- `AndroidManifest.xml` – Declares app permissions and components

This structure helps separate concerns and improve maintainability.

## Installation

1. Clone the repository or download the ZIP:
   ```bash
   git clone https://github.com/static-motion/AndroidTicTacToe.git
   ```

2. Open the project in **Android Studio**.

3. Build and run the app on an emulator or Android device.

## Usage

1. Launch the app.
2. Select **Single Player** or **Multiplayer** mode.
3. Choose AI difficulty (for single player).
4. Play and enjoy!

## Contributing

Contributions are welcome! Please fork the repository and open a pull request.

## License

This project is licensed under the [MIT License](LICENSE).

## Credits

Developed by Valentin Mihov.
