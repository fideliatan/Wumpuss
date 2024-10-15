import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class WumpusGameWithHints extends JFrame {
    private int gridSize;
    private char[][] board;
    private boolean[][] explored; // Track explored cells
    private int playerRow, playerCol;
    private int goldCount; // Count of remaining gold
    private String playerSprite = "sprites/player.png";
    private String wumpusSprite = "sprites/wumpus.png";
    private String pitSprite = "sprites/pit.png";
    private String goldSprite = "sprites/gold.png";
    private String emptySprite = "sprites/empty.png"; // Added for explored empty cells
    private int points; // Variable to store the player's points
    private boolean gameOver = false; // Flag to check if the game has ended

    private JLabel[][] gridLabels;
    private JLabel hintLabel;
    private JLabel goldCountLabel;
    private JLabel pointsLabel; // Label to display points

    public WumpusGameWithHints(int gridSize) {
        this.gridSize = gridSize;
        this.board = new char[gridSize][gridSize];
        this.explored = new boolean[gridSize][gridSize]; // Initialize explored array
        this.gridLabels = new JLabel[gridSize][gridSize];
        createBoard();
        placeItems();
        findPlayer();

        setTitle("Wumpus Game with Hints");
        setLayout(new BorderLayout()); // Use BorderLayout for main layout

        // Inside your constructor or setup method
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(gridSize, gridSize, 0, 0)); // No gaps between cells
        initializeGrid(gridPanel);
        add(gridPanel, BorderLayout.CENTER);
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); // Vertical layout for info panel
        infoPanel.setPreferredSize(new Dimension(200, getHeight())); // Fixed width for the hints sidebar


        hintLabel = new JLabel("Hints: ");
        goldCountLabel = new JLabel("Gold Remaining: " + goldCount);
        infoPanel.add(hintLabel);
        infoPanel.add(goldCountLabel);
        this.points = 0; // Initialize points to zero
        pointsLabel = new JLabel("Points: " + points);
        infoPanel.add(pointsLabel); // Add the points label to the info panel

        // Add the controls instruction at the bottom of the sidebar
        JLabel controlsLabel = new JLabel("<html><br><br>Controls:<br>"
                + "Move: Arrow Keys (-1 Point)<br>"
                + "Shoot: WASD (-10 Points, +1000 points if Wumpus killed)<br>"
                + "Collect all the gold to end the game safely (+1000 points per gold)<br>"
                + "Avoid Wumpus and Pits (-1000 points upon death)<br>"
                + "Pay attention to the hints to figure out where pits and wumpus are");
        infoPanel.add(controlsLabel); // Add controls text to the info panel

        add(infoPanel, BorderLayout.EAST); // Add the info panel to the right

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleMovement(e);
                shootArrow(e);
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(690, 510); // Set initial size
        setResizable(false); // Make the window non-resizable
        setVisible(true);
        setFocusable(true);
        setLocationRelativeTo(null); // Center the window
    }

    private void createBoard() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                board[i][j] = '-'; // Initialize board with empty spaces
                explored[i][j] = false; // Mark all cells as unexplored
            }
        }
    }

    private void placeItems() {
        Random rand = new Random();
        int pitCount, goldCount, wumpusCount;

        // Set number of pits, gold, and Wumpuses based on grid size
        if (gridSize == 4) {
            pitCount = 2;
            goldCount = 2;
            wumpusCount = 1;
        } else if (gridSize == 8) {
            pitCount = rand.nextInt(4) + 4;
            goldCount = rand.nextInt(3) + 3;
            wumpusCount = 2; // 2 Wumpuses for 8x8 grid
        } else { // 16x16 grid
            pitCount = rand.nextInt(5) + 6;
            goldCount = rand.nextInt(5) + 4;
            wumpusCount = 3; // 3 Wumpuses for 16x16 grid
        }

        // Ensure player starts at a safe position (0, 0)
        board[0][0] = 'O'; // Player
        explored[0][0] = true; // Mark starting point as explored
        playerRow = 0;
        playerCol = 0;

        // Initialize gold count
        this.goldCount = goldCount; // Store gold count in instance variable

        // Place Wumpuses, pits, and gold
        char[] items = new char[wumpusCount + goldCount + pitCount]; // Wumpuses, Gold, Pits

        // Add Wumpuses
        for (int i = 0; i < wumpusCount; i++) {
            items[i] = 'W'; // Wumpus
        }

        // Add gold
        for (int i = wumpusCount; i < wumpusCount + goldCount; i++) {
            items[i] = 'G'; // Gold
        }

        // Add pits
        for (int i = wumpusCount + goldCount; i < items.length; i++) {
            items[i] = 'P'; // Pit
        }

        // Shuffle items
        for (int i = items.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            char temp = items[i];
            items[i] = items[j];
            items[j] = temp;
        }

        // Place items randomly, but don't place anything at (0, 0)
        for (char item : items) {
            int row, col;
            do {
                row = rand.nextInt(gridSize);
                col = rand.nextInt(gridSize);
            } while (board[row][col] != '-' || (row == 0 && col == 0)); // Ensure (0, 0) is empty
            board[row][col] = item;
        }
    }


    private void findPlayer() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (board[i][j] == 'O') {
                    playerRow = i;
                    playerCol = j;
                    return;
                }
            }
        }
    }

    private void initializeGrid(JPanel gridPanel) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                gridLabels[i][j] = new JLabel();
                gridLabels[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                gridLabels[i][j].setBorder(null); // Remove borders to eliminate gaps
                gridLabels[i][j].setPreferredSize(new Dimension(60, 60)); // Set size to match the new sprite size
                gridLabels[i][j].setOpaque(true); // Ensure the background fills the label
                gridPanel.add(gridLabels[i][j]);
            }
        }
        updateGrid(); // Initial update of the grid
    }





    private ImageIcon loadSprite(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image scaledImage = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH); // Scale to match cell size
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.out.println("Could not load image: " + path);
            return null;
        }
    }

    private void updateGrid() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                char cell = board[i][j];
                // Display content for explored cells
                if (explored[i][j]) {
                    switch (cell) {
                        case 'G':
                            gridLabels[i][j].setIcon(loadSprite(goldSprite));
                            break;
                        case 'O':
                            gridLabels[i][j].setIcon(loadSprite(playerSprite));
                            break;
                        case 'P':
                            gridLabels[i][j].setIcon(loadSprite(pitSprite));
                            break;
                        case 'W':
                            gridLabels[i][j].setIcon(loadSprite(wumpusSprite));
                            break;
                        default:
                            gridLabels[i][j].setIcon(loadSprite(emptySprite)); // Show empty sprite for explored empty cells
                            break;
                    }
                } else {
                    gridLabels[i][j].setIcon(loadSprite(emptySprite)); // Show empty sprite for unexplored cells
                }
            }
        }
        revalidate();
        repaint();
    }


    private void handleMovement(KeyEvent e) {
        int newRow = playerRow;
        int newCol = playerCol;

        // Determine the new position based on key press
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                newRow--;
                break;
            case KeyEvent.VK_DOWN:
                newRow++;
                break;
            case KeyEvent.VK_LEFT:
                newCol--;
                break;
            case KeyEvent.VK_RIGHT:
                newCol++;
                break;
            default:
                return; // Ignore other keys
        }


        // Ensure the move is within bounds
        if (newRow >= 0 && newRow < gridSize && newCol >= 0 && newCol < gridSize) {
            // Check if the player is attempting to move into a Wumpus or pit
            if (board[newRow][newCol] == 'W') {
                // Encountered a Wumpus: end the game
                points -= 1000;
                JOptionPane.showMessageDialog(this, "You have been eaten by the Wumpus! Game Over!\nPoints: " + points);
                System.exit(0); // End the game
            } else if (board[newRow][newCol] == 'P') {
                // Encountered a pit: end the game
                points -= 1000;
                JOptionPane.showMessageDialog(this, "You fell into a pit! Game Over!\nPoints: " + points);
                System.exit(0); // End the game
            } else {
                // If the player collected gold
                if (board[newRow][newCol] == 'G') {
                    goldCount--; // Decrease the gold count
                    points += 1000;
                    board[newRow][newCol] = '-'; // Remove the gold from the board
                }

                // Mark the new position as explored
                explored[newRow][newCol] = true;

                // Clear previous player position
                board[playerRow][playerCol] = '-'; // Remove the player from the old position
                playerRow = newRow;
                playerCol = newCol;
                board[playerRow][playerCol] = 'O'; // Place the player in the new position

                // Update the grid display
                updateGrid();
                goldCountLabel.setText("Gold Remaining: " + goldCount); // Update the gold count label
                points -= 1;
                // Check for game-over conditions (like if all gold is collected)
                checkGameOver();
                updateHints();
                pointsLabel.setText("Points: " + points); // Update points label
            }
        }
    }

    private void shootArrow(KeyEvent e) {


        int arrowRow = playerRow;
        int arrowCol = playerCol;
        boolean hit = false;

        // Move the arrow one grid in the specified direction based on the key pressed
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: // Shoot up
                arrowRow--;
                break;
            case KeyEvent.VK_A: // Shoot left
                arrowCol--;
                break;
            case KeyEvent.VK_S: // Shoot down
                arrowRow++;
                break;
            case KeyEvent.VK_D: // Shoot right
                arrowCol++;
                break;
            default:
                return; // Exit if a wrong key is pressed
        }

        // Check if the arrow is still within bounds and hits the Wumpus
        if (arrowRow >= 0 && arrowRow < gridSize && arrowCol >= 0 && arrowCol < gridSize) {
            if (board[arrowRow][arrowCol] == 'W') {
                points += 1000; // Award 1000 points for hitting the Wumpus
                hintLabel.setText("You hit the Wumpus! +1000 points.");
                board[arrowRow][arrowCol] = '-'; // Remove the Wumpus from the board
                hit = true;
            }
        }

        if (!hit) {
            hintLabel.setText("Missed! The Wumpus is still lurking.");
            points -= 10; // Deduct points for shooting
        }

        pointsLabel.setText("Points: " + points); // Update points label
    }



    private void updateHints() {
        StringBuilder hints = new StringBuilder("Hints:\n");

        // Check directly adjacent cells (up, down, left, right)
        int[][] directions = {
                {-1, 0}, // Up
                {1, 0},  // Down
                {0, -1}, // Left
                {0, 1}   // Right
        };

        for (int[] direction : directions) {
            int row = playerRow + direction[0];
            int col = playerCol + direction[1];

            // Ensure we don't go out of bounds
            if (row >= 0 && row < gridSize && col >= 0 && col < gridSize) {
                if (board[row][col] == 'W') {
                    hints.append("You smell a Wumpus nearby!\n");
                }
                if (board[row][col] == 'P') {
                    hints.append("You feel a draft (pit nearby)!\n");
                }
            }
        }

        // Update hint label
        hintLabel.setText("<html>" + hints.toString().replaceAll("\n", "<br>") + "</html>");
    }



    private void checkGameOver() {
        // Check if player has encountered the Wumpus or fallen into a pit
        if (board[playerRow][playerCol] == 'W') {
            JOptionPane.showMessageDialog(this, "You have been eaten by the Wumpus! Game Over!\nPoints: " + points);
            gameOver = true; // Set game over flag
        } else if (board[playerRow][playerCol] == 'P') {
            JOptionPane.showMessageDialog(this, "You fell into a pit! Game Over!\nPoints: " + points);
            gameOver = true; // Set game over flag
        } else if (goldCount == 0) {
            JOptionPane.showMessageDialog(this, "You collected all the gold! You win!\nPoints: " + points);
            gameOver = true; // Set game over flag
        }
    }


    public static void main(String[] args) {
        int gridSize = Integer.parseInt(JOptionPane.showInputDialog("Enter grid size (4, 8, or 16):"));
        new WumpusGameWithHints(gridSize);
    }
}
