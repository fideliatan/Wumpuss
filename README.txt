(Not yet finished)

# Wumpus Game with Hints

## Introduction
This is a Java-based graphical Wumpus game where the player must explore a grid, collect gold, and avoid pits and the dangerous Wumpus. The game provides hints to the player, showing the number of gold pieces left and offering clues as they navigate through the grid.

## Features
- **Multiple Grid Sizes:** Play on a 4x4, 8x8, or 16x16 grid.
- **Randomized Placement:** Wumpus, pits, and gold are placed randomly on the grid each time the game starts.
- **Player Movement:** Use the arrow keys to move the player across the grid.
- **Hints System:** The game provides visual hints for unexplored and explored areas, and displays the number of gold pieces remaining.
- **Game Over:** The game ends if the player encounters the Wumpus, falls into a pit, or collects all gold pieces.
- **Graphical Display:** Uses Java Swing for rendering the game board, with sprites representing the player, Wumpus, pits, and gold.

## Game Setup
- **Player**: Marked as `'O'` on the grid and represented by a custom sprite.
- **Wumpus**: Moves randomly after every player movement and is represented by its own sprite.
- **Pits**: Represented by their sprite, pits are scattered randomly across the grid.
- **Gold**: Collect all the gold to win the game!

## How to Play
1. The player starts at a random location on the grid.
2. Use the **arrow keys** to move the player (up, down, left, right).
3. The goal is to collect all the gold (`G`) while avoiding the Wumpus (`W`) and pits (`P`).
4. Every time the player moves, the Wumpus also moves to a random adjacent cell.
5. The player wins if they collect all the gold without dying.
6. If the player encounters the Wumpus or falls into a pit, the game ends.

## Controls
- **Arrow Keys**: Move the player (up, down, left, right).

## Installation
1. Ensure you have Java Development Kit (JDK) installed. If not, download and install it from [Oracle's website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).
2. Clone or download this repository.
3. Compile the project by running the following command in the terminal:
    ```bash
    javac WumpusGameWithHints.java
    ```
4. Run the game using the following command:
    ```bash
    java WumpusGameWithHints
    ```

## Dependencies
- Java Swing (built-in with JDK)
- Java AWT (for keyboard and GUI controls)

## Customization
You can modify the sprites used in the game by changing the image files located in the `sprites` folder:
- `player.png` : Player's character
- `wumpus.png` : Wumpus monster
- `pit.png` : Pits
- `gold.png` : Gold