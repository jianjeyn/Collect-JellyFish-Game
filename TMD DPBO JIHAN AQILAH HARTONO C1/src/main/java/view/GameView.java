package view; // View for the Game

import viewmodel.GameViewModel; // ViewModel for the Game
import javax.swing.*; // Import Swing components for GUI
import java.awt.*; // Import AWT components for GUI
import java.awt.event.KeyAdapter; // Import KeyAdapter to handle key events
import java.awt.event.KeyEvent; // Import KeyEvent to handle key events
import java.awt.event.MouseAdapter; // Import MouseAdapter to handle mouse events
import java.awt.event.MouseEvent; // Import MouseEvent to handle mouse events

/**
 * GameView class - Main game window that handles the game display and user interactions
 * This class extends JFrame to create the main game window
 * It manages the game loop, user input, and coordinates with the GameViewModel
 */
public class GameView extends JFrame { // View for the Game
    private GameViewModel viewModel; // Reference to the game's view model for business logic
    private GamePanel gamePanel; // Custom panel that renders the game graphics
    private String username; // Current player's username
    private MainMenuView mainMenu; // Reference to main menu for navigation back
    private Timer gameTimer; // Swing timer that drives the game loop
    private boolean gameOverProcessed = false; // Flag to prevent multiple game over processing

    /**
     * Constructor - Initialize the game view with player username and main menu reference
     * @param username The player's username
     * @param mainMenu Reference to the main menu view for navigation
     */
    public GameView(String username, MainMenuView mainMenu) {
        this.username = username; // Store the player's username
        this.mainMenu = mainMenu; // Store reference to main menu
        this.viewModel = new GameViewModel(); // Create new game view model instance
        this.gameOverProcessed = false; // Initialize game over flag to false

        // Initialize all GUI components
        initializeComponents();
        // Set up keyboard input handling
        setupKeyBindings();
        // Start the main game loop
        startGameLoop();

        // Start the actual game logic with the player's username
        viewModel.startGame(username);
    }

    /**
     * Initialize all GUI components including window settings and event listeners
     * Sets up the game panel, window properties, and mouse input handling
     */
    private void initializeComponents() {
        // Set window title with player's username
        setTitle("Spongebob Jelly Fish - Playing as: " + username);
        
        // Set content pane size instead of window size for better layout
        gamePanel = new GamePanel(viewModel); // Create game panel with view model reference
        gamePanel.setPreferredSize(new Dimension(800, 600)); // Set game area size
        add(gamePanel); // Add game panel to the frame
        
        pack(); // Automatically size window to fit content properly
        
        // Prevent default close operation to handle game saving
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen
        setResizable(false); // Prevent window resizing to maintain game layout

        // Add mouse listener to handle lasso throws
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Only process mouse clicks when game is actively running
                if (viewModel.isGameRunning()) {
                    // Debug output for mouse click coordinates
                    System.out.println("Mouse clicked at: " + e.getX() + ", " + e.getY());
                    // Send lasso throw command to view model with click coordinates
                    viewModel.throwLasso(e.getX(), e.getY());
                    // Force immediate screen update to show lasso
                    gamePanel.repaint();
                }
            }
        });

        // Enable focus on game panel for proper mouse event handling
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

        // Enable focus on main frame for keyboard input
        setFocusable(true);
        requestFocus();
    }

    /**
     * Set up keyboard input handling for player movement and game controls
     * Handles arrow keys for movement and space bar for game actions
     */
    private void setupKeyBindings() {
        // Create key listener that handles all keyboard input
        KeyAdapter keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Debug output for key presses
                System.out.println("Key pressed: " + e.getKeyCode());

                // Handle SPACE key for both game over and normal game states
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (viewModel.isGameOver()) {
                        // Game over state - return to main menu
                        returnToMainMenu();
                    } else if (viewModel.isGameRunning()) {
                        // Game running - quit game and save progress
                        endGameWithSave();
                    }
                    return; // Exit early to prevent further processing
                }
                
                // Only process movement keys when game is running
                if (!viewModel.isGameRunning()) return;
                
                // Handle arrow key movement
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        viewModel.getPlayer().moveUp(); // Move player up
                        break;
                    case KeyEvent.VK_DOWN:
                        viewModel.getPlayer().moveDown(); // Move player down
                        break;
                    case KeyEvent.VK_LEFT:
                        viewModel.getPlayer().moveLeft(); // Move player left
                        break;
                    case KeyEvent.VK_RIGHT:
                        viewModel.getPlayer().moveRight(); // Move player right
                        break;
                    case KeyEvent.VK_SPACE:
                        endGameWithSave(); // End game and save progress
                        break;
                }
            }
        };

        // Add key listener to both frame and panel for comprehensive input handling
        addKeyListener(keyListener);
        gamePanel.addKeyListener(keyListener);
    }

    /**
     * End the game manually (when player presses space) and save the current progress
     * Shows final score dialog and returns to main menu
     */
    private void endGameWithSave() {
        System.out.println("Ending game manually...");
        
        // Stop the game timer to halt game loop
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // Save current score before stopping game
        viewModel.stopGame(); // This will save the score to persistent storage

        // Create appropriate message based on game end reason
        String message;
        if (viewModel.getLives() <= 0) {
            // Game ended due to no lives remaining
            message = "Game Over! No more lives!\nFinal Score: " + viewModel.getScore() +
                    "\nBalls Caught: " + viewModel.getCount();
        } else {
            // Game ended manually by player
            message = "Game ended!\nFinal Score: " + viewModel.getScore() +
                    "\nBalls Caught: " + viewModel.getCount();
        }

        // Display final score dialog to player
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        // Navigate back to main menu
        returnToMainMenu();
    }

    /**
     * Method to return to main menu after game ends
     * Properly cleans up current game window and shows main menu
     */
    private void returnToMainMenu() {
        System.out.println("Returning to main menu...");
        
        // Stop game timer to prevent further updates
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // Close current game window and free resources
        this.dispose();

        // Show main menu or create new one if reference is lost
        if (mainMenu != null) {
            mainMenu.refreshData(); // Refresh leaderboard with new scores
            mainMenu.setVisible(true); // Make main menu visible
        } else {
            // Fallback: create new main menu if original reference is lost
            SwingUtilities.invokeLater(() -> {
                try {
                    MainMenuView newMainMenu = new MainMenuView();
                    newMainMenu.setVisible(true);
                } catch (Exception ex) {
                    System.err.println("Error creating new main menu: " + ex.getMessage());
                }
            });
        }
    }

    /**
     * Start the main game loop with proper game over handling
     * Uses Swing Timer to update game state and handle automatic game over
     */
    private void startGameLoop() {
        // Create timer that fires every 16ms (approximately 60 FPS)
        gameTimer = new Timer(16, e -> {
            // Update game logic only when game is running
            if (viewModel.isGameRunning()) {
                viewModel.update(); // Update all game objects (player, jellies, etc.)
            }
            
            // Always repaint screen, even during game over to show game over screen
            gamePanel.repaint();
            
            // Auto save score when lives are depleted WITHOUT showing dialog
            if (viewModel.getLives() <= 0 && viewModel.isGameRunning() && !gameOverProcessed) {
                gameOverProcessed = true; // Set flag to prevent multiple processing
                System.out.println("Lives depleted - triggering game over");
                
                // Automatically end game and save score
                viewModel.stopGame(); // This will save the score to persistent storage
                
                System.out.println("Game over - score should be saved");
            }
        });
        
        // Start the game timer
        gameTimer.start();

        System.out.println("Game loop started");
    }

    /**
     * Override dispose method to ensure proper cleanup
     * Stops game timer before disposing of the window
     */
    @Override
    public void dispose() {
        // Stop game timer to prevent memory leaks
        if (gameTimer != null) {
            gameTimer.stop();
        }
        // Call parent dispose method
        super.dispose();
    }
}