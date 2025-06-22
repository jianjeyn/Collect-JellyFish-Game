package view; // View for the Game Panel

import viewmodel.GameViewModel; // ViewModel for the game logic and state
import model.Player; // Model for the player character
import javax.swing.*; // Swing components for GUI
import javax.imageio.ImageIO; // Image loading utilities
import java.awt.*; // AWT components for graphics
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage; // BufferedImage for image handling
import java.io.IOException; // IOException for handling image loading errors
import java.io.InputStream; // InputStream for reading image files
import java.util.List; // List for managing collections of objects
import java.util.Random; // Random for generating random numbers

// GamePanel class to display the game view
public class GamePanel extends JPanel {
    // ViewModel instance to manage game state
    private GameViewModel viewModel; 
    private JFrame parentFrame;

    // Images
    private BufferedImage playerImg, jf3, jf4, jf1, jf2, basketImg, bgImg;
    private BufferedImage gameOverImg;

    // Lives images
    private BufferedImage lives4Img, lives3Img, lives2Img, lives1Img;

    // Static background image
    private BufferedImage staticBackground;
    private boolean backgroundGenerated = false;

    // Constructor to initialize the game panel
    public GamePanel(GameViewModel viewModel) { 
        this.viewModel = viewModel;
        setBackground(new Color(20, 30, 60));
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true); 

        // Initialize audio manager
        util.AudioManager.getInstance();
        // START BACKGROUND MUSIC setelah semua initialized
        startBackgroundMusic();

        // Load images (with fallback to shapes if images not found)
        loadImages();
        generateStaticBackground(); // Generate background sekali saja

    }

    // Method untuk set parent frame
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }

    //IMAGE LOADING SYSTEM

    // Method to load images and handle loading errors
    private void loadImages() {
        System.out.println("=== LOADING IMAGES ===");

        try { // Define image formats to try
            String[] imageFormats = {".png", ".jpg", ".jpeg"};

            // Load existing images
            bgImg = loadImageWithFormats("background", imageFormats);
            playerImg = loadImageWithFormats("spongebob_player", imageFormats);
            jf3 = loadImageWithFormats("jf3", imageFormats);
            jf4 = loadImageWithFormats("jf4", imageFormats);
            jf1 = loadImageWithFormats("jf1", imageFormats);
            jf2 = loadImageWithFormats("jf2", imageFormats);
            basketImg = loadImageWithFormats("basket", imageFormats);
            gameOverImg = loadImageWithFormats("background", imageFormats);

            // Load 4 different lives images
            lives4Img = loadImageWithFormats("lives_4", imageFormats); // 4 hati penuh
            lives3Img = loadImageWithFormats("lives_3", imageFormats); // 3 hati penuh, 1 kosong
            lives2Img = loadImageWithFormats("lives_2", imageFormats); // 2 hati penuh, 2 kosong
            lives1Img = loadImageWithFormats("lives_1", imageFormats); // 1 hati penuh, 3 kosong

            // Report loading status
            System.out.println("Background: " + (bgImg != null ? "LOADED" : "FAILED"));
            System.out.println("Player: " + (playerImg != null ? "LOADED" : "FAILED"));
            System.out.println("Jf3: " + (jf3 != null ? "LOADED" : "FAILED"));
            System.out.println("Jf4: " + (jf4 != null ? "LOADED" : "FAILED"));
            System.out.println("Basket: " + (basketImg != null ? "LOADED" : "FAILED"));
            System.out.println("Game Over BG: " + (gameOverImg != null ? "LOADED" : "FAILED"));
            System.out.println("Lives 4: " + (lives4Img != null ? "LOADED" : "FAILED"));
            System.out.println("Lives 3: " + (lives3Img != null ? "LOADED" : "FAILED"));
            System.out.println("Lives 2: " + (lives2Img != null ? "LOADED" : "FAILED"));
            System.out.println("Lives 1: " + (lives1Img != null ? "LOADED" : "FAILED"));

        } catch (Exception e) { // Handle any exceptions during image loading
            System.out.println("Error loading images: " + e.getMessage());
            System.out.println("Using shapes instead");
        }

        System.out.println("=== END LOADING IMAGES ===");
    }

    // Method to load images with multiple formats
    private BufferedImage loadImageWithFormats(String baseName, String[] formats) {
        for (String format : formats) {
            String fileName = baseName + format;
            BufferedImage img = loadImage(fileName);
            if (img != null) {
                System.out.println("SUCCESS: Loaded " + fileName);
                return img;
            }
        }
        System.out.println("FAILED: Could not find " + baseName + " with any format");
        return null;
    }

    // Method to load an image from resources
    private BufferedImage loadImage(String path) {
        try {
            System.out.println("Attempting to load: /images/" + path);

            // Method 1: Try getResourceAsStream
            InputStream stream = getClass().getResourceAsStream("/images/" + path);
            if (stream != null) { // If the image is found in resources
                BufferedImage img = ImageIO.read(stream); // Read the image from the stream
                // Print success message
                System.out.println("SUCCESS: Loaded " + path + " via getResourceAsStream");
                return img;
            }

            // Method 2: Try getResource
            java.net.URL imageURL = getClass().getResource("/images/" + path);
            if (imageURL != null) { // If the image is found in resources
                BufferedImage img = ImageIO.read(imageURL);
                System.out.println("SUCCESS: Loaded " + path + " via getResource");
                return img;
            }

            // Method 3: Try class loader
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            imageURL = classLoader.getResource("images/" + path); // Use class loader to find the image
            if (imageURL != null) { // If the image is found using class loader
                BufferedImage img = ImageIO.read(imageURL);
                System.out.println("SUCCESS: Loaded " + path + " via ClassLoader");
                return img;
            }

            return null; // If all methods fail, return null

        } catch (IOException e) {
            System.out.println("ERROR: Loading " + path + " - " + e.getMessage());
            return null;
        }
    }

    //MAIN RENDER LOOP

    @Override // Method to paint the game panel
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Check for game over first
        if (viewModel.isGameOver()) {
            drawBackground(g2d); // Draw background first
            drawGameOverScreen(g2d); // Draw game over screen to overlay background
            return;
        }

        if (!viewModel.isGameRunning()) {
            drawGameOverScreen(g2d); // Draw game over screen if game is not running
            return;
        }

        drawBackground(g2d); // Draw background first

        if (viewModel.getShootingCutscene() != null) {
            drawPlayer(g2d); // Draw player in cutscene
            drawSkillBalls(g2d); // Draw skill balls in cutscene
            drawBasketForCutscene(g2d); // Draw basket in cutscene
            drawShootingCutscene(g2d); // Draw shooting cutscene elements
            return;
        }

        drawPlayer(g2d); // Draw player character
        drawSkillBalls(g2d); // Draw skill balls in the game
        drawWebLasso(g2d); // Draw web lasso if active
        drawCaughtBallAnimations(g2d); // Draw caught ball animations
        drawUI(g2d); // Draw the game UI elements
    }

    // Background drawing tanpa random
    private void drawBackground(Graphics2D g2d) {
        if (backgroundGenerated && staticBackground != null) {
            // Draw pre-generated static background
            g2d.drawImage(staticBackground, 0, 0, null);
        } else {
            // Fallback jika background belum di-generate
            generateStaticBackground();
            g2d.drawImage(staticBackground, 0, 0, null);
        }
    }

    // Method untuk start background music
    private void startBackgroundMusic() {
        System.out.println("=== STARTING BACKGROUND MUSIC ===");
        
        try {
            util.AudioManager audioManager = util.AudioManager.getInstance();
            
            // Set volume ke 70% (adjust sesuai keinginan)
            audioManager.setBackgroundMusicVolume(0.6f);
            
            // Start background music dengan loop
            audioManager.playBackgroundMusic();
            
            System.out.println("Background music start command sent!");
            
            // Check status setelah 1 detik
            Timer statusChecker = new Timer(1000, e -> {
                boolean isPlaying = audioManager.isBackgroundMusicPlaying();
                System.out.println("Background music status: " + (isPlaying ? "✓ PLAYING" : "✗ NOT PLAYING"));
                ((Timer)e.getSource()).stop();
            });
            // Set timer to not repeat
            statusChecker.setRepeats(false);
            statusChecker.start();
            
        } catch (Exception e) { // Handle any exceptions during music playback
            System.out.println("Failed to start background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // GAME OBJECT RENDERING

    // Method to draw player character
    private void drawPlayer(Graphics2D g2d) {
        Player player = viewModel.getPlayer(); // Get the player model from the view model

        if (playerImg != null) { // If player image is loaded, draw it
            g2d.drawImage(playerImg, player.getX(), player.getY(),
            player.getWidth(), player.getHeight(), null);
        } else { // If image not loaded, draw a simple shape
            // Enhanced larger JF4-Gwen character
            int centerX = player.getX() + player.getWidth() / 2;
            int centerY = player.getY() + player.getHeight() / 2;

            // Main body (white with pink accents) - LARGER
            g2d.setColor(Color.WHITE);
            g2d.fillOval(player.getX(), player.getY(), player.getWidth(), player.getHeight());

            // Pink hood/mask accents - LARGER
            g2d.setColor(new Color(255, 105, 180));
            g2d.fillOval(player.getX() + 8, player.getY() + 8,
                    player.getWidth() - 16, player.getHeight() - 16);

            // Eyes (white with pink outline) - LARGER
            g2d.setColor(Color.WHITE);
            g2d.fillOval(player.getX() + 15, player.getY() + 20, 18, 12);
            g2d.fillOval(player.getX() + 45, player.getY() + 20, 18, 12);

            // Eye outline - THICKER
            g2d.setColor(new Color(255, 105, 180));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(player.getX() + 15, player.getY() + 20, 18, 12);
            g2d.drawOval(player.getX() + 45, player.getY() + 20, 18, 12);

            // JF4 logo on chest (LARGER AND MORE DETAILED)
            g2d.setColor(new Color(255, 105, 180));
            g2d.setStroke(new BasicStroke(4));

            // JF4 body (larger)
            g2d.fillOval(centerX - 6, centerY - 4, 12, 8);

            // JF4 legs (8 legs with joints) - LONGER
            g2d.setStroke(new BasicStroke(3));
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                int x2 = centerX + (int)(25 * Math.cos(angle));
                int y2 = centerY + (int)(18 * Math.sin(angle));
                g2d.drawLine(centerX, centerY, x2, y2);

                // Leg joints - BIGGER
                int midX = centerX + (int)(12 * Math.cos(angle));
                int midY = centerY + (int)(8 * Math.sin(angle));
                g2d.fillOval(midX - 2, midY - 2, 4, 4);
            }

            // Web pattern on suit (more detailed and larger)
            g2d.setColor(new Color(255, 105, 180, 120));
            g2d.setStroke(new BasicStroke(2));

            // Radial web pattern - LARGER
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                int x2 = centerX + (int)(35 * Math.cos(angle));
                int y2 = centerY + (int)(25 * Math.sin(angle));
                if (x2 >= player.getX() && x2 <= player.getX() + player.getWidth() &&
                        y2 >= player.getY() && y2 <= player.getY() + player.getHeight()) {
                    g2d.drawLine(centerX, centerY, x2, y2);
                }
            }

            // Concentric web circles - BIGGER
            for (int r = 12; r <= 35; r += 8) {
                g2d.drawOval(centerX - r, centerY - r, r*2, r*2);
            }

            // Add JF4-Gwen signature hood points - LARGER
            g2d.setColor(new Color(255, 105, 180));
            g2d.setStroke(new BasicStroke(3));

            // Hood spikes - BIGGER
            int[] hoodX = {player.getX() + 20, player.getX() + 12, player.getX() + 28};
            int[] hoodY = {player.getY(), player.getY() - 12, player.getY() + 8};
            g2d.fillPolygon(hoodX, hoodY, 3);

            int[] hoodX2 = {player.getX() + 60, player.getX() + 68, player.getX() + 52};
            int[] hoodY2 = {player.getY(), player.getY() - 12, player.getY() + 8};
            g2d.fillPolygon(hoodX2, hoodY2, 3);
        }
    }

    private void drawSkillBalls(Graphics2D g2d) { // Draw skill balls in the game
        // Check if skill balls are empty
        for (GameViewModel.SkillBall ball : viewModel.getSkillBalls()) {
            int centerX = ball.getX() + ball.getWidth() / 2; // Calculate center X position
            int centerY = ball.getY() + ball.getHeight() / 2; // Calculate center Y position

            // Draw the ball based on its type
            if (ball.getType() == GameViewModel.BallType.JF3) { // JF3 ball
                if (jf3 != null) { // If jf3 image is loaded, draw it
                    g2d.drawImage(jf3, ball.getX(), ball.getY(),
                         ball.getWidth(), ball.getHeight(), null);
                } else { // Fallback drawing for jf3
                    g2d.setColor(new Color(255, 182, 193));
                    g2d.fillOval(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());

                    // BIGGER heart
                    g2d.setColor(new Color(255, 105, 180));
                    drawHeart(g2d, centerX, centerY, 12);

                    // Web pattern overlay - MORE VISIBLE
                    g2d.setColor(new Color(255, 255, 255, 180));
                    g2d.setStroke(new BasicStroke(2));
                    for (int i = 0; i < 6; i++) {
                        double angle = i * Math.PI / 3;
                        int x2 = centerX + (int)(18 * Math.cos(angle));
                        int y2 = centerY + (int)(18 * Math.sin(angle));
                        g2d.drawLine(centerX, centerY, x2, y2);
                    }
                }

                // Text for points
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 14));
                g2d.drawString("10", ball.getX() + 5, ball.getY() - 5);

            } else if (ball.getType() == GameViewModel.BallType.JF4) { // JF4 ball
                if (jf4 != null) { // If jf4 image is loaded, draw it
                    g2d.drawImage(jf4, ball.getX(), ball.getY(),
                            ball.getWidth(), ball.getHeight(), null);
                } else {
                    // Fallback JF4 drawing
                    g2d.setColor(Color.BLACK);
                    g2d.fillOval(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
                    g2d.setColor(new Color(139, 0, 0));
                    g2d.fillOval(centerX - 10, centerY - 6, 20, 12);
                }

                // BIGGER points text
                g2d.setColor(Color.YELLOW);
                g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 14));
                g2d.drawString("100", ball.getX() + 5, ball.getY() - 5);

            } else if (ball.getType() == GameViewModel.BallType.JF1) { //jf1
                if (jf1 != null) { // If jf1 image is loaded, draw it
                    g2d.drawImage(jf1, ball.getX(), ball.getY(),
                            ball.getWidth(), ball.getHeight(), null);
                } else {
                    // Fallback for jf1
                    g2d.setColor(new Color(255, 200, 0));
                    g2d.fillOval(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
                }

                // BIGGER points text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 14));
                g2d.drawString("20", ball.getX() + 5, ball.getY() - 5);

            } else { //jf2
                if (jf2 != null) { // If jf2 image is loaded, draw it
                    g2d.drawImage(jf2, ball.getX(), ball.getY(),
                            ball.getWidth(), ball.getHeight(), null);
                } else {
                    // Fallback for jf2
                    g2d.setColor(new Color(0, 255, 150));
                    g2d.fillOval(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
                }

                // BIGGER points text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 14));
                g2d.drawString("30", ball.getX() + 5, ball.getY() - 5);

            }
        }
    }

    // Method to draw lasso
    private void drawWebLasso(Graphics2D g2d) { 
        // Check if lasso points are available
        List<Point> lassoPoints = viewModel.getLassoPoints();

        // If there are no lasso points, return early
        if (!lassoPoints.isEmpty()) {
            // Change color based on state
            if (viewModel.isLassoReturning()) { 
                // Returning lasso color with transparency
                g2d.setColor(new Color(255, 255, 255, 180)); // Putih dengan transparansi
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            } else {
                // Normal white color
                g2d.setColor(new Color(255, 255, 255, 250));
                g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }

            // Draw the lasso points
            for (int i = 0; i < lassoPoints.size() - 1; i++) {
                Point p1 = lassoPoints.get(i);
                Point p2 = lassoPoints.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            // Draw the lasso points again for thicker lines
            for (int i = 0; i < lassoPoints.size() - 1; i++) {
                Point p1 = lassoPoints.get(i);
                Point p2 = lassoPoints.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            // Draw end point where user clicked
            if (!lassoPoints.isEmpty()) {
                Point endPoint = lassoPoints.get(lassoPoints.size() - 1);
                if (viewModel.isLassoReturning()) { // If lasso is returning, use a different color
                    g2d.setColor(new Color(255, 100, 100));
                } else {
                    g2d.setColor(new Color(255, 105, 180));
                }
                g2d.fillOval(endPoint.x - 6, endPoint.y - 6, 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.drawOval(endPoint.x - 6, endPoint.y - 6, 12, 12);
            }
        }
    }

    //ANIMATION METHODS

    // Method to draw caught ball animations
    private void drawCaughtBallAnimations(Graphics2D g2d) {
        // Check if there are any caught ball animations
        for (GameViewModel.CaughtBallAnimation animation : viewModel.getCaughtBalls()) {
            // If animation is not active, skip drawing
            float scale = animation.getScale();
            int size = (int)(50 * scale);
            int x = (int)(animation.getX() - size/2);
            int y = (int)(animation.getY() - size/2);

            // Add stronger glow effect
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.fillOval(x - 8, y - 8, size + 16, size + 16);

            //if ball type is JF3, draw with image
            if (animation.getType() == GameViewModel.BallType.JF3) { 
                // Draw JF3 ball with image
                g2d.setColor(new Color(255, 182, 193));
                g2d.fillOval(x, y, size, size);
                g2d.setColor(new Color(255, 105, 180));
                drawHeart(g2d, (int)animation.getX(), (int)animation.getY(), (int)(12 * scale));
            } else { // If not JF3, draw as a simple circle
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x, y, size, size);
                g2d.setColor(new Color(139, 0, 0));
                int centerX = (int)animation.getX();
                int centerY = (int)animation.getY();
                g2d.fillOval(centerX - (int)(10*scale), centerY - (int)(6*scale),
                        (int)(20*scale), (int)(12*scale));
            }

            // Points text
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, (int)(20 * scale)));
            String points = "+" + animation.getType().getPoints();
            g2d.drawString(points, (int)animation.getX() + 25, (int)animation.getY() - 15);
        }
    }

    // Method to draw the basket for cutscene
    private void drawShootingCutscene(Graphics2D g2d) {
        GameViewModel.ShootingCutscene cutscene = viewModel.getShootingCutscene();
        if (cutscene == null) return; // If no cutscene, return early

        // Tambahkan web dari tangan kiri ke ball
        Player player = viewModel.getPlayer();
        
        // Posisi tangan kiri yang sama dengan throwLasso
        int handOffsetX = player.getX() + (int)(player.getWidth() * 0.17); // Tangan kanan
        int handOffsetY = player.getY() + (int)(player.getHeight() * 0.28);  // Posisi tangan
        
        // Posisi ball saat ini dalam animasi
        float ballX = cutscene.getBallX();
        float ballY = cutscene.getBallY();
        
        // Web line TIPIS seperti JF4 web
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(handOffsetX, handOffsetY, (int)ballX, (int)ballY);
        
        // Subtle glow yang sangat tipis
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(handOffsetX, handOffsetY, (int)ballX, (int)ballY);

        // Draw normal game elements but slightly faded
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw basket HANYA saat shooting cutscene dengan ukuran asli
        drawBasketForCutscene(g2d);

        // Draw flying ball with simple trail - GUNAKAN GAMBAR
        drawFlyingBallWithImage(g2d, cutscene);

        // Draw simple UI
        drawSimpleCutsceneUI(g2d, cutscene);
    }

    // Method to draw flying ball with image to basket
    private void drawFlyingBallWithImage(Graphics2D g2d, GameViewModel.ShootingCutscene cutscene) {
        // Get the current position of the ball in the cutscene
        int ballX = (int)cutscene.getBallX();
        int ballY = (int)cutscene.getBallY();
        int ballSize = 50;

        // Draw ball dengan GAMBAR ASLI
        if (cutscene.getBallType() == GameViewModel.BallType.JF3) {
            // GUNAKAN GAMBAR JF3
            if (jf3 != null) {
                // Draw with glow effect
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(ballX - ballSize/2 - 10, ballY - ballSize/2 - 10, ballSize + 20, ballSize + 20);
                
                // Draw actual web love image
                g2d.drawImage(jf3, ballX - ballSize/2, ballY - ballSize/2, ballSize, ballSize, null);
            } else {
                // Fallback: Original pink ball with heart
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(ballX - ballSize/2 - 10, ballY - ballSize/2 - 10, ballSize + 20, ballSize + 20);
                
                g2d.setColor(new Color(255, 182, 193));
                g2d.fillOval(ballX - ballSize/2, ballY - ballSize/2, ballSize, ballSize);
                g2d.setColor(new Color(255, 105, 180));
                drawHeart(g2d, ballX, ballY, 12);
            }
        } else if (cutscene.getBallType() == GameViewModel.BallType.JF4) {
            // GUNAKAN GAMBAR JF4
            if (jf4 != null) {
                // Draw with glow effect
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(ballX - ballSize/2 - 10, ballY - ballSize/2 - 10, ballSize + 20, ballSize + 20);
                
                // Draw actual JF4 image
                g2d.drawImage(jf4, ballX - ballSize/2, ballY - ballSize/2, ballSize, ballSize, null);
            } else {
                // Fallback: Original black ball with JF4
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(ballX - ballSize/2 - 10, ballY - ballSize/2 - 10, ballSize + 20, ballSize + 20);
                
                g2d.setColor(Color.BLACK);
                g2d.fillOval(ballX - ballSize/2, ballY - ballSize/2, ballSize, ballSize);
                g2d.setColor(new Color(139, 0, 0));
                g2d.fillOval(ballX - 10, ballY - 6, 20, 12);
            }
        } else if (cutscene.getBallType() == GameViewModel.BallType.JF1) {
            // GUNAKAN GAMBAR JF1
            if (jf1 != null) {
                // Draw with glow effect
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(ballX - ballSize/2 - 10, ballY - ballSize/2 - 10, ballSize + 20, ballSize + 20);
                
                // Draw actual jf1 image
                g2d.drawImage(jf1, ballX - ballSize/2, ballY - ballSize/2, ballSize, ballSize, null);
            } else {
                // Fallback for jf1
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(ballX - ballSize/2 - 10, ballY - ballSize/2 - 10, ballSize + 20, ballSize + 20);
                
                g2d.setColor(new Color(255, 200, 0));
                g2d.fillOval(ballX - ballSize/2, ballY - ballSize/2, ballSize, ballSize);
            }
        } else {
            // GUNAKAN GAMBAR JF2
            if (jf2 != null) {
                // Draw with glow effect
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(ballX - ballSize/2 - 10, ballY - ballSize/2 - 10, ballSize + 20, ballSize + 20);
                
                // Draw actual jf2 image
                g2d.drawImage(jf2, ballX - ballSize/2, ballY - ballSize/2, ballSize, ballSize, null);
            } else {
                // Fallback for jf2
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(ballX - ballSize/2 - 10, ballY - ballSize/2 - 10, ballSize + 20, ballSize + 20);
                
                g2d.setColor(new Color(0, 255, 150));
                g2d.fillOval(ballX - ballSize/2, ballY - ballSize/2, ballSize, ballSize);
            }
        } 

        // Draw points text
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 20));
        g2d.drawString("+" + cutscene.getBallType().getPoints(), ballX + 30, ballY - 15);
    }

    // Method to draw cutscene UI like progress bar and text
    private void drawSimpleCutsceneUI(Graphics2D g2d, GameViewModel.ShootingCutscene cutscene) {
        // Simple progress bar
        int barWidth = 300;
        int barHeight = 20;
        int barX = (getWidth() - barWidth) / 2;
        int barY = getHeight() - 60;

        // Draw background for progress bar
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(barX, barY, barWidth, barHeight);

        // Draw border for progress bar
        g2d.setColor(new Color(255, 105, 180));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(barX, barY, barWidth, barHeight);

        // Progress
        long elapsed = System.currentTimeMillis() - cutscene.getStartTime();
        float progress = Math.min(1.0f, (float)elapsed / 1000);

        // Fill progress bar with color
        g2d.setColor(new Color(255, 105, 180));
        g2d.fillRect(barX + 2, barY + 2, (int)((barWidth - 4) * progress), barHeight - 4);

        // Simple text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 16));
        String text = "SHOOTING TO BASKET...";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        g2d.drawString(text, textX, barY - 10);
    }

    // Update method drawLivesDisplay
    private void drawLivesDisplay(Graphics2D g2d) {
        // Position and size for lives display
        int livesX = 10;
        int livesY = -16;

        // Lives size and aspect ratio
        int livesWidth = 350;
        int livesHeight = 140;

        // Choose the appropriate lives image based on the current lives
        BufferedImage livesImg = null;
        switch (viewModel.getLives()) {
            case 4: // Use lives4Img for 4 lives
                livesImg = lives4Img;
                break;
            case 3: // Use lives3Img for 3 lives
                livesImg = lives3Img;
                break;
            case 2: // Use lives2Img for 2 lives
                livesImg = lives2Img;
                break;
            case 1: // Use lives1Img for 1 life
                livesImg = lives1Img;
                break;
            case 0: 
            default:
                livesImg = null;
                break;
        }

        if (livesImg != null) {
            // Maintain aspect ratio dengan ukuran yang lebih kecil
            int originalWidth = livesImg.getWidth();
            int originalHeight = livesImg.getHeight();

            if (originalWidth > 0 && originalHeight > 0) {
                float aspectRatio = (float)originalWidth / originalHeight;

                // Gunakan ukuran yang lebih kecil
                livesHeight = 100;  // Fixed smaller height
                livesWidth = (int)(livesHeight * aspectRatio);

                // Pastikan tidak terlalu lebar
                if (livesWidth > 300) {
                    livesWidth = 300;
                    livesHeight = (int)(livesWidth / aspectRatio);
                }
            }

            // Draw dengan ukuran yang tepat
            g2d.drawImage(livesImg, livesX, livesY, livesWidth, livesHeight, null);
        }
    }

    // Method for drawing the basket in cutscene
    private void drawBasketForCutscene(Graphics2D g2d) {
        if (basketImg != null) {
            // Use the basket image with original size
            int basketWidth = 100;
            int basketHeight = 100;
            
            // Posisi di ujung kanan atas
            int basketX = getWidth() - 140;  // Tepat di ujung kanan
            int basketY = 15;
            
            // Draw with original size
            g2d.drawImage(basketImg, basketX, basketY, basketWidth, basketHeight, null);
        } else {
            // Fallback: Draw basket outline
            int basketX = getWidth() - 140;  // Tepat di ujung kanan
            int basketY = 15;
            int basketWidth = 100;
            int basketHeight = 100;

            g2d.setColor(new Color(255, 105, 180, 150));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(basketX, basketY, basketWidth, basketHeight);
        }
    }

    // Method to generate static background image
    private void generateStaticBackground() {
        // Set panel width and height
        int panelWidth = getWidth() > 0 ? getWidth() : 800;
        int panelHeight = getHeight() > 0 ? getHeight() : 600;
        
        // Create a new BufferedImage for the static background
        staticBackground = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = staticBackground.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Load background image if available
        if (bgImg != null) {
            // Isi dengan warna dasar dulu
            g2d.setColor(new Color(20, 30, 60));
            g2d.fillRect(0, 0, panelWidth, panelHeight);
            
            // Gambar background dengan ukuran panel sebenarnya
            g2d.drawImage(bgImg, 0, 0, panelWidth, panelHeight, null);
        } else {
            // Isi dengan warna dasar
            g2d.setColor(new Color(20, 30, 60));
            g2d.fillRect(0, 0, panelWidth, panelHeight);
        }

        g2d.dispose();
        backgroundGenerated = true;
    }

    // UI RENDERING METHODS
    
    // Method to draw the game UI elements
    private void drawUI(Graphics2D g2d) {
        // Score display
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 16));
        g2d.drawString("Score: " + viewModel.getScore(), 17, 65);
        g2d.drawString("Count: " + viewModel.getCount(), 17, 90);

        // Lives Display
        drawLivesDisplay(g2d);

        // Instructions
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Arrow keys: Move | Click: Throw web | Space: Quit", 500, getHeight() - 15);

        // Ball types legend
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 14));
    }

    // Method to draw a heart shape
    private void drawHeart(Graphics2D g2d, int centerX, int centerY, int size) {
        // Draw heart using curves for smoother appearance
        g2d.fillOval(centerX - size/2, centerY - size/2, size/2, size/2);
        g2d.fillOval(centerX, centerY - size/2, size/2, size/2);

        // Heart bottom triangle
        int[] triangleX = {centerX - size/2, centerX + size/2, centerX};
        int[] triangleY = {centerY - size/4, centerY - size/4, centerY + size/2};
        g2d.fillPolygon(triangleX, triangleY, 3);
    }

    // Method to draw the game over screen
    private void drawGameOverScreen(Graphics2D g2d) {
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw background
        if (gameOverImg != null) {
            g2d.drawImage(gameOverImg, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Underwater gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(15, 45, 75),
                getWidth(), getHeight(), new Color(25, 65, 95)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Dark vignette overlay for dramatic effect
        RadialGradientPaint vignette = new RadialGradientPaint(
            getWidth()/2f, getHeight()/2f, getWidth()/2f,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{
                new Color(0, 0, 0, 0),
                new Color(0, 0, 0, 50),
                new Color(0, 0, 0, 120)
            }
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // GAME OVER text with glowing effect
        drawGlowingGameOverText(g2d);

        // Gaming-style stats panel with neon accents
        drawGamingStatsPanel(g2d);
    }

    // Method to draw glowing "GAME OVER" text
    private void drawGlowingGameOverText(Graphics2D g2d) {
        String gameOverText = "GAME OVER";
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 56));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(gameOverText)) / 2;
        int textY = getHeight() / 2 - 100;
        
        // Multiple glow layers for neon effect
        for (int i = 8; i >= 0; i--) {
            float alpha = (9 - i) * 0.1f;
            g2d.setColor(new Color(255, 100, 100, (int)(alpha * 80)));
            g2d.drawString(gameOverText, textX - i, textY - i);
            g2d.drawString(gameOverText, textX + i, textY + i);
        }
        
        // Outer glow
        g2d.setColor(new Color(255, 150, 150, 60));
        for (int i = 0; i < 3; i++) {
            g2d.drawString(gameOverText, textX - i, textY);
            g2d.drawString(gameOverText, textX + i, textY);
            g2d.drawString(gameOverText, textX, textY - i);
            g2d.drawString(gameOverText, textX, textY + i);
        }
        
        // Main text with gradient
        GradientPaint textGradient = new GradientPaint(
            textX, textY - 30, new Color(255, 255, 255),
            textX, textY + 10, new Color(255, 180, 180)
        );
        g2d.setPaint(textGradient);
        g2d.drawString(gameOverText, textX, textY);
    }

    // Method to draw the gaming stats panel with neon accents
    private void drawGamingStatsPanel(Graphics2D g2d) {
        int panelWidth = 400;
        int panelHeight = 160;
        int panelX = (getWidth() - panelWidth) / 2;
        int panelY = getHeight() / 2 - 10;
        
        // Panel glow effect
        for (int i = 15; i > 0; i--) {
            float alpha = (16 - i) * 0.02f;
            g2d.setColor(new Color(0, 150, 255, (int)(alpha * 255)));
            g2d.fillRoundRect(panelX - i, panelY - i, panelWidth + (i*2), panelHeight + (i*2), 25, 25);
        }
        
        // Main panel background - dark glass effect
        g2d.setColor(new Color(5, 15, 35, 180));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // Neon border
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(0, 200, 255, 200));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        // Inner highlight
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(100, 220, 255, 100));
        g2d.drawRoundRect(panelX + 5, panelY + 5, panelWidth - 10, panelHeight - 10, 15, 15);

        // Header section with cyber style
        g2d.setColor(new Color(0, 180, 255, 40));
        g2d.fillRoundRect(panelX + 15, panelY + 15, panelWidth - 30, 35, 10, 10);
        
        // Header border
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(0, 220, 255, 120));
        g2d.drawRoundRect(panelX + 15, panelY + 15, panelWidth - 30, 35, 10, 10);
        
        // Header text with glow
        g2d.setColor(new Color(0, 255, 255, 80));
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 20));
        String headerText = "MISSION RESULTS";
        FontMetrics fm = g2d.getFontMetrics();
        int headerX = panelX + (panelWidth - fm.stringWidth(headerText)) / 2;
        g2d.drawString(headerText, headerX + 1, panelY + 43);
        g2d.drawString(headerText, headerX - 1, panelY + 43);
        
        g2d.setColor(new Color(255, 255, 255));
        g2d.drawString(headerText, headerX, panelY + 40);

        // Stats with gaming style
        // Final Score
        g2d.setColor(new Color(255, 220, 0, 150));
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 17));
        g2d.drawString("►", panelX + 30, panelY + 80);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 17));
        String scoreText = "FINAL SCORE: " + viewModel.getScore();
        g2d.drawString(scoreText, panelX + 55, panelY + 80);

        // Jellyfish Caught with icon
        g2d.setColor(new Color(100, 255, 150, 150));
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 17));
        g2d.drawString("►", panelX + 30, panelY + 110);
        
        g2d.setColor(new Color(150, 255, 200));
        String jellyfishText = "JELLYFISH CAUGHT: " + viewModel.getCount();
        g2d.drawString(jellyfishText, panelX + 55, panelY + 110);

        // Instruction with cyber styling
        g2d.setColor(new Color(255, 255, 100, 180));
        g2d.setFont(new Font("Pixeloid Mono", Font.BOLD, 15));
        String instructText = "[ PRESS SPACE TO CONTINUE ]";
        fm = g2d.getFontMetrics();
        int instructX = panelX + (panelWidth - fm.stringWidth(instructText)) / 2;
        
        // Blinking effect simulation
        g2d.setColor(new Color(255, 255, 0, 100));
        g2d.drawString(instructText, instructX + 1, panelY + 140);
        g2d.drawString(instructText, instructX - 1, panelY + 140);
        
        g2d.setColor(new Color(255, 255, 150));
        g2d.drawString(instructText, instructX, panelY + 140);
    }
}