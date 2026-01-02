# Escape-the-Matrix

A console-based strategy game developed as a university final project.

## Overview

In this game, you control a team of 3 players (P1, P2, P3) navigating a 5x5 grid. The goal is to escape the "Matrix" by reaching the exit at the bottom-right corner (coordinates `[4, 4]`) within a limited number of rounds.

## Game Mechanics

### The Grid
- **Size**: 5x5.
- **Start**: All players start at top-left `[0, 0]`.
- **Exit**: Bottom-right `[4, 4]`.

### Rounds & Shared Resources
- The game is played in **Rounds**. You have **12 rounds** to win.
- At the start of each round, random "doors" (North, East, South, West) become available.
- **The Twist**: Movement directions are a **shared resource**.
    - If "North" is available, *any* player can use it.
    - However, once a player moves North, that direction is **consumed** and becomes unavailable for *all* players for the rest of that round.
- This requires careful coordination. If you get stuck, you must end the round to refresh the doors, which consumes one of your limited rounds.

### Lives & Penalties
- Each player starts with **3 lives**.
- **Revisit Penalty**: Sticking together is crucial. If a player moves into a previously visited room and ends up there **alone**, they lose **1 life**.
- **Items**: Hidden items (Life Boosts or Penalties) are scattered across the grid.

### Winning & Losing
- **Win**: Navigate at least one player to the Exit `[4, 4]`.
- **Lose**:
    1. Rounds run out (0 rounds left).
    2. All players lose all their lives.

## How to Play

Run the `Main` class to start the game application.

### Commands
- `go <player> <direction>`
    - Move a specific player.
    - Example: `go P1 east`
    - Valid directions: `north`, `east`, `south`, `west`.
- `endround`
    - Forces the current round to end. This decrements the `Rounds Left` counter and randomizes available doors again. Use this when no useful moves are left.
- `help`
    - Displays the list of commands.
- `exit`
    - Quits the game.

## Project Structure

- `Main.java`: Entry point of the application.
- `Game.java`: Contains the core game loop, grid logic, and command processing.
- `Player.java`: Represents a player entity, tracking their position, lives, and inventory.
- `Room.java`: Represents a single cell in the grid, storing state like visited status and available doors.
- `Direction.java`: Enum representing the cardinal directions.
- `Item.java` / `ItemType.java`: Classes defining the hidden items found in rooms.
