# JavaFX Minesweeper

Classic Minesweeper game implemented as a desktop application using JavaFX.

## Features
- JavaFX graphical user interface
- Difficulty selection
- Game timer
- SQLite-based local leaderboard
- Automatic database initialization
- Maven build system

## Technologies
- Java 17 / 21
- JavaFX
- Maven
- SQLite

## Project Structure
- `src/main/java` – application source code
- `src/main/resources` – styles (CSS)
- `data/leaderboard.db` – local database (created automatically)

## Run
The project is built with Maven.

```bash
mvn javafx:run
