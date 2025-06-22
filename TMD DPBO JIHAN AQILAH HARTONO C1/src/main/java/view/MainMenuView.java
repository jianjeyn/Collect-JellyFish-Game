package view; // Contains the main menu view for the game

import viewmodel.MainMenuViewModel; // ViewModel for the Main Menu
import model.GameResult; // Model class for handling game results

import javax.imageio.ImageIO; // Import for image handling
import javax.swing.*; // Import necessary Swing components for GUI
import javax.swing.table.DefaultTableModel; // Import for table model
import javax.swing.table.DefaultTableCellRenderer; // Import for table cell rendering
import java.awt.*; // Import necessary AWT classes for graphics and layout
import java.awt.event.ActionEvent; // Import for action events
import java.awt.event.ActionListener; // Import for action listeners
import java.awt.image.BufferedImage; // Import for buffered images
import java.io.InputStream; // Import for input streams
import java.util.Random; // Import for random number generation

// MainMenuView class represents the main menu of the game
public class MainMenuView extends JFrame {
    // ViewModel instance to manage game results
    private MainMenuViewModel viewModel;
    // GUI components
    private JTextField usernameField;
    // Table to display high scores
    private JTable scoreTable;
    // Buttons for game actions
    private JButton playButton;
    private JButton quitButton;
    
    // Constructor to initialize the main menu view
    public MainMenuView() {
        viewModel = new MainMenuViewModel();
        initializeComponents();
        setupLayout();
        loadScoreData();
        
        // Start background music di main menu
        startMainMenuMusic();
    }
    
    // Method untuk start background music
    private void startMainMenuMusic() {
        try { // Get the audio manager instance
            util.AudioManager audioManager = util.AudioManager.getInstance();
            // Set volume to a lower level for main menu
            audioManager.setBackgroundMusicVolume(0.4f);
            audioManager.playBackgroundMusic();
            System.out.println("Main menu background music started with lower volume!");
        } catch (Exception e) {
            System.out.println("Failed to start main menu music: " + e.getMessage());
        }
    }

    // Update initializeComponents()
    private void initializeComponents() {
        // Set the title and size of the main menu window
        setTitle("COLLECT THE JELLY FISH - SpongeBob Edition");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Username field
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 34));
        usernameField.setBackground(new Color(255, 255, 255, 240));
        usernameField.setForeground(new Color(20, 20, 50));
        usernameField.setFont(new Font("Pixeloid Mono", Font.PLAIN, 14));
        
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15, new Color(255, 200, 0), 3),
            BorderFactory.createEmptyBorder(2, 15, 2, 15)
        ));
        
        // Play button
        playButton = new JButton("PLAY GAME");
        playButton.setPreferredSize(new Dimension(120, 35)); 
        playButton.setBackground(new Color(255, 200, 0));
        playButton.setForeground(new Color(20, 20, 50));
        playButton.setFont(new Font("Pixeloid Mono", Font.BOLD, 14));
        playButton.setBorder(new RoundedBorder(12, new Color(200, 160, 0), 2));
        playButton.setFocusPainted(false);
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Quit button 
        quitButton = new JButton("QUIT GAME");
        quitButton.setPreferredSize(new Dimension(120, 30));
        quitButton.setBackground(new Color(255, 200, 0));
        quitButton.setForeground(new Color(20, 20, 50));
        quitButton.setFont(new Font("Pixeloid Mono", Font.BOLD, 12));
        quitButton.setBorder(new RoundedBorder(10, new Color(200, 160, 0), 2));
        quitButton.setFocusPainted(false);
        quitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Mouse hover effects for buttons
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playButton.setBackground(new Color(255, 220, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                playButton.setBackground(new Color(255, 200, 0));
            }
        });
        
        // Quit button hover effects
        quitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                quitButton.setBackground(new Color(255, 30, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                quitButton.setBackground(new Color(220, 20, 60));
            }
        });
        
        // Table setup
        String[] columnNames = {"USERNAME", "SCORE", "BALLS CAUGHT"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoreTable = new JTable(tableModel);

        // Set table properties
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(Color.WHITE);
        headerRenderer.setForeground(Color.BLACK);
        headerRenderer.setFont(new Font("Pixeloid Mono", Font.BOLD, 14));
        scoreTable.getTableHeader().setDefaultRenderer(headerRenderer);
        
        // Button actions
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    startGame(username);
                } else {
                    JOptionPane.showMessageDialog(MainMenuView.this, 
                        "Please enter your name to start the adventure!", 
                        "Enter Your Name", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Quit button action
        quitButton.addActionListener(e -> {
            util.AudioManager.getInstance().stopAllSounds();
            System.exit(0);
        });
        
        usernameField.addActionListener(e -> playButton.doClick());
    }

    // Method to set up the layout of the main menu
    private void setupLayout() {
        setLayout(new BorderLayout());
        // Main panel with background image and bubble effects
        JPanel mainPanel = new JPanel() {
            private BufferedImage bgImg;
            {
                try { // Try to load the background image from resources
                    String[] imageFormats = {".png", ".jpg", ".jpeg"}; // List of possible image formats
                    for (String format : imageFormats) { // Loop through each format
                        try { // Attempt to load the image
                            InputStream is = getClass().getResourceAsStream("/images/background" + format);
                            if (is != null) { // If the image is found
                                bgImg = ImageIO.read(is);
                                System.out.println("Main menu background loaded: background" + format);
                                break;
                            }
                        } catch (Exception e) { // If loading fails, print the error and try the next format
                            // Try next format
                        }
                    }
                } catch (Exception e) { // If all formats fail, print an error message
                    System.out.println("Could not load main menu background: " + e.getMessage());
                }
            }
             
            @Override // Override paintComponent to draw the background and bubble effects
            protected void paintComponent(Graphics g) { 
                // Graphics2D for better rendering
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw the background image or gradient
                if (bgImg != null) {
                    g2d.drawImage(bgImg, 0, 0, getWidth(), getHeight(), null);
                    g2d.setColor(new Color(0, 20, 40, 80));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else { // If image loading fails, draw a gradient background
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(20, 50, 80),
                        0, getHeight(), new Color(10, 30, 60)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                
                // Draw bubble effects
                drawBubbleEffects(g2d);
            }
            
            // Method to draw bubble effects on the background
            private void drawBubbleEffects(Graphics2D g2d) {
                g2d.setColor(new Color(255, 255, 255, 25));
                Random random = new Random(42);
                for (int i = 0; i < 20; i++) {
                    int x = random.nextInt(getWidth());
                    int y = random.nextInt(getHeight());
                    int size = 5 + random.nextInt(15);
                    g2d.fillOval(x, y, size, size);
                }
            }
        };
        // Set the main panel properties
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        
        // Title panel with title label
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0)); 
        
        // Title label with custom font and color
        JLabel titleLabel = new JLabel("COLLECT THE JELLY FISH");
        titleLabel.setFont(new Font("Pixeloid Mono", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 255, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        
        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 25, 0));
        
        // Instruction label for username input
        JLabel instructionLabel = new JLabel("Enter your name to start the adventure:");
        instructionLabel.setFont(new Font("Pixeloid Mono", Font.PLAIN, 14));
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Username input row with play button
        JPanel usernameRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        usernameRow.setOpaque(false);
        usernameRow.add(usernameField);
        usernameRow.add(playButton);
        
        // Add components to the input panel
        inputPanel.add(instructionLabel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(usernameRow);
        
        // Score panel
        JPanel scorePanel = createScorePanel();
        
        // Bottom panel dengan quit button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        bottomPanel.add(quitButton);
        
        // Add all panels
        mainPanel.add(titlePanel);
        mainPanel.add(inputPanel);
        mainPanel.add(scorePanel);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(bottomPanel);
        
        // Set the main panel as the content pane
        add(mainPanel, BorderLayout.CENTER);
    }

    // Custom rounded border class
    class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color color;
        private int thickness;
        
        public RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }
    
    // Method to create the score panel with high scores
    private JPanel createScorePanel() {
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setOpaque(false);
        scorePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 30, 10, 30),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 200, 0), 2),
                "HIGH SCORES",
                0, 0,
                new Font("Pixeloid Mono", Font.BOLD, 16),
                new Color(255, 255, 100)
            )
        ));
        
        // Table styling
        scoreTable.setBackground(new Color(20, 40, 70, 200)); // Semi-transparent dark blue
        scoreTable.setForeground(Color.WHITE);
        scoreTable.setGridColor(new Color(255, 200, 0, 100)); // Yellow grid
        scoreTable.setSelectionBackground(new Color(255, 200, 0, 100));
        scoreTable.setSelectionForeground(new Color(20, 20, 50));
        scoreTable.setRowHeight(30);
        scoreTable.setFont(new Font("Pixeloid Mono", Font.PLAIN, 13));
        
        // Header styling
        scoreTable.getTableHeader().setBackground(new Color(255, 200, 0));
        scoreTable.getTableHeader().setForeground(new Color(20, 20, 50));
        scoreTable.getTableHeader().setFont(new Font("Pixeloid Mono", Font.BOLD, 14));
        scoreTable.getTableHeader().setPreferredSize(new Dimension(0, 35));

        
        // Cell alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setOpaque(false);
        scoreTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Score column
        scoreTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Count column
        
        // Set the username column to left alignment
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        scrollPane.getViewport().setBackground(new Color(20, 40, 70, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 0, 150), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Add the table to the score panel
        scorePanel.add(scrollPane, BorderLayout.CENTER);
        
        return scorePanel;
    }
    
    // Method to load score data into the table
    private void loadScoreData() {
        DefaultTableModel model = (DefaultTableModel) scoreTable.getModel();
        model.setRowCount(0);
        
        // Loop through the game results from the ViewModel
        for (GameResult result : viewModel.getGameResults()) {
            model.addRow(new Object[]{
                result.getUsername(),
                result.getScore(),
                result.getCount()
            });
        }
    }
    
    // Method to start the game with the given username
    private void startGame(String username) {
        this.setVisible(false);
        // Continue music ke game
        new GameView(username, this).setVisible(true);
    }
    
    // Method to refresh the data in the main menu
    public void refreshData() {
        viewModel.refreshResults();
        loadScoreData();
    }
}