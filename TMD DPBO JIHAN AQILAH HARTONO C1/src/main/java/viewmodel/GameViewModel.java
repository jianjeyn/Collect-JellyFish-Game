package viewmodel; // ViewModel for the Game

import model.Database; // Import the Database class to save game results
import model.Player; // Import the Player class to represent the player in the game
import java.awt.*; // Import AWT classes for Point and Rectangle
import java.util.ArrayList; // Import ArrayList to manage collections of skill balls and lasso points
import java.util.List; // Import List to handle collections of skill balls and lasso points
import java.util.Random; // Import Random to generate random numbers for skill ball spawning

public class GameViewModel { // ViewModel for the Game
    private Player player; // Player object representing the player in the game
    private List<SkillBall> skillBalls; // List of skill balls currently in the game
    private List<Point> lassoPoints; // List of points representing the lasso trajectory
    private List<CaughtBallAnimation> caughtBalls; // List of caught ball animations
    private ShootingCutscene shootingCutscene; // Shooting cutscene for when a ball is caught
    private boolean lassoReturning; // Flag to indicate if the lasso is returning
    private Point lassoTarget;  // Target point for the lasso throw
    private int score; // Current score of the player
    private int count; // Count of balls caught by the player
    private int lives; // Number of lives remaining for the player
    private boolean gameRunning; // Flag to indicate if the game is currently running
    private boolean gameOver; // Game over state
    private Random random; // Random number generator for spawning skill balls
    private long lastBallSpawn; // Timestamp of the last skill ball spawn
    private String currentUsername; // Current username of the player, used for saving results
    private long lassoStartTime; // Timestamp when the lasso was thrown
    private long bounceBackStartTime; // For fast bounce back
    private static final long LASSO_DURATION = 1000; // Faster timeout
    private static final long BOUNCE_BACK_DURATION = 300; // Fast bounce back (0.3s)
    private static final int MAX_LIVES = 4; // 4 lives to match your assets

    // Enum for different types of skill balls with their points and colors
    public enum BallType {
        JF3(10, Color.PINK), // Jellyfish 1
        JF1(20, Color.RED), // Jellyfish 2
        JF2(30, Color.BLUE), // Jellyfish 3
        JF4(100, Color.BLACK); // Jellyfish 4

        private final int points;
        private final Color color;

        BallType(int points, Color color) {
            this.points = points;
            this.color = color;
        }

        public int getPoints() { return points; }
        public Color getColor() { return color; }
    }

    // Constructor to initialize the GameViewModel
    public GameViewModel() {
        player = new Player(400, 250);
        skillBalls = new ArrayList<>();
        lassoPoints = new ArrayList<>();
        caughtBalls = new ArrayList<>();
        shootingCutscene = null;
        lassoReturning = false;
        lassoTarget = null;
        random = new Random();
        gameRunning = false;
        gameOver = false; 
        score = 0;
        count = 0;
        lives = MAX_LIVES;
        lastBallSpawn = System.currentTimeMillis();
        lassoStartTime = 0;
        bounceBackStartTime = 0; // NEW
    }

    // Method to start a new game with the given username
    public void startGame(String username) {
        this.currentUsername = username;
        gameRunning = true;
        gameOver = false; // NEW
        score = 0;
        count = 0;
        lives = MAX_LIVES;
        skillBalls.clear();
        lassoPoints.clear();
        caughtBalls.clear();
        shootingCutscene = null;
        lassoReturning = false;
        lassoTarget = null;
        player = new Player(400, 250);
    }

    // Method to stop the game and save results if applicable
    public void stopGame() {
        gameRunning = false;
        if (currentUsername != null && !currentUsername.trim().isEmpty()) {
            Database.saveOrUpdateResult(currentUsername, score, count);
        }
    }

    // Method to update the game state
    public void update() {
        if (!gameRunning) return;

        // Update shooting cutscene
        if (shootingCutscene != null) {
            shootingCutscene.update();
            if (shootingCutscene.isFinished()) {
                shootingCutscene = null;
            }
            return;
        }

        // Update fast bounce back
        if (lassoReturning) {
            updateFastBounceBack();
            return; // Continue other updates during bounce back
        }

        // Normal game update - DON'T PAUSE DURING BOUNCE BACK
        if (System.currentTimeMillis() - lastBallSpawn > 1200) { // Even faster spawn
            spawnSkillBall();
            lastBallSpawn = System.currentTimeMillis();
        }

        skillBalls.removeIf(ball -> {
            ball.update();
            return ball.getX() < -50 || ball.getX() > 850;
        });

        caughtBalls.removeIf(animation -> {
            animation.update();
            return animation.isFinished();
        });

        // Check collision EVERY FRAME jika ada lasso
        if (!lassoPoints.isEmpty()) {
            if (checkLassoCollision()) {
                // Ball caught - collision already handled in checkLassoCollision()
                return;
            }
            
            // Check if lasso reached target atau timeout
            if (hasLassoReachedTarget() || System.currentTimeMillis() - lassoStartTime > LASSO_DURATION) {
                // No collision found, start IMMEDIATE bounce back
                startFastBounceBack();
            }
        }
    }

    // Method untuk check apakah lasso sudah sampai target
    private boolean hasLassoReachedTarget() {
        if (lassoTarget == null || lassoPoints.isEmpty()) return false;
        
        Point lastPoint = lassoPoints.get(lassoPoints.size() - 1);
        int distance = (int)Math.sqrt(
            Math.pow(lastPoint.x - lassoTarget.x, 2) + 
            Math.pow(lastPoint.y - lassoTarget.y, 2)
        );
        
        return distance < 20; // Tolerance 20 pixels
    }

    // Method to spawn a new skill ball with random type and position
    private void spawnSkillBall() {
        BallType ballType;
        float rand = random.nextFloat();
        
        if (rand < 0.05f) {
            ballType = BallType.JF4; // 5% chance for JF4 (paling sedikit)
        } else if (rand < 0.3f) {
            ballType = BallType.JF3; // 25% chance for web love
        } else if (rand < 0.55f) {
            ballType = BallType.JF1; // 25% chance for JF1
        } else {
            ballType = BallType.JF2; // 22.5% chance for JF2
        }
        
        boolean fromTop = random.nextBoolean();

        // CONSTANT speed - tidak berubah-ubah
        int speed = 2; // Kecepatan konstan (diperbaiki syntax error)

        if (fromTop) {
            skillBalls.add(new SkillBall(800, 50 + random.nextInt(150), -speed, 0, ballType));
        } else {
            skillBalls.add(new SkillBall(-50, 350 + random.nextInt(150), speed, 0, ballType));
        }
    }

    // Method to throw the lasso towards a target point
    public void throwLasso(int targetX, int targetY) {
        if (!gameRunning || shootingCutscene != null || lassoReturning || gameOver) return;

        System.out.println("throwLasso called - about to play sound");
    
        // PLAY SOUND EFFECT saat shooting web - DENGAN DEBUG
        try {
            util.AudioManager.getInstance().playWebShoot();
            System.out.println("Sound play command executed");
        } catch (Exception e) {
            System.out.println("Error calling playWebShoot: " + e.getMessage());
            e.printStackTrace();
        }

        lassoPoints.clear();
        lassoStartTime = System.currentTimeMillis();
        lassoTarget = new Point(targetX, targetY);

        // Calculate hand position based on player position
        Player player = getPlayer();
        int handOffsetX = player.getX() + (int)(player.getWidth() * 0.75); // 75% ke kanan (tangan kanan)
        int handOffsetY = player.getY() + (int)(player.getHeight() * 0.6);  // 40% dari atas (posisi tangan)

        // Calculate points along the lasso trajectory
        int dx = targetX - handOffsetX;
        int dy = targetY - handOffsetY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        for (int i = 0; i <= distance; i += 2) {
            double ratio = i / distance;
            int x = (int) (handOffsetX + dx * ratio);
            int y = (int) (handOffsetY + dy * ratio);
            lassoPoints.add(new Point(x, y));
        }
    }

    // Method to handle fast bounce back when lasso misses
    private void startFastBounceBack() {
        System.out.println("Lasso missed! Fast bounce back...");
        lassoReturning = true;
        bounceBackStartTime = System.currentTimeMillis();

        // IMMEDIATE life loss
        lives--;
        System.out.println("Life lost! Lives remaining: " + lives);

        if (lives <= 0) {
            // Trigger game over
            gameOver = true;
            gameRunning = false;
            System.out.println("GAME OVER! No more lives!");
            // TAMBAHAN: Simpan skor ke database saat game over
            if (currentUsername != null && !currentUsername.trim().isEmpty()) {
                Database.saveOrUpdateResult(currentUsername, score, count);
                System.out.println("Score saved: " + score + ", Count: " + count);
            }
        }
    }

    // Method to update the lasso points during fast bounce back
    private void updateFastBounceBack() {
        // Check if bounce back is in progress
        long elapsed = System.currentTimeMillis() - bounceBackStartTime;
        float progress = Math.min(1.0f, (float)elapsed / BOUNCE_BACK_DURATION);

        if (progress >= 1.0f) { 
            // Bounce back complete
            lassoPoints.clear();
            lassoReturning = false;
            return;
        }

        // Calculate how many points to remove based on progress
        int originalSize = lassoPoints.size();
        int targetSize = (int)(originalSize * (1.0f - progress));

        // Remove points from the end of the list to simulate bounce back
        while (lassoPoints.size() > targetSize && !lassoPoints.isEmpty()) {
            lassoPoints.remove(lassoPoints.size() - 1);
        }
    }

    // Method to check for lasso collision with skill balls
    private boolean checkLassoCollision() {
        if (lassoPoints.isEmpty()) return false;

        // Check if lasso is returning
        for (Point lassoPoint : lassoPoints) {
            for (int i = skillBalls.size() - 1; i >= 0; i--) {
                SkillBall ball = skillBalls.get(i);
                if (ball.getBounds().contains(lassoPoint)) {
                    System.out.println("Ball caught! Starting cutscene...");

                    // Updated basket coordinates untuk posisi ujung yang benar
                    int basketX = 800 - 130 + 60;  // Center of new basket position (closer to edge)
                    int basketY = 10 + 50;         // Center of new basket position
                    shootingCutscene = new ShootingCutscene(
                            ball.getX() + ball.getWidth()/2,
                            ball.getY() + ball.getHeight()/2,
                            basketX, basketY,
                            ball.getType()
                    );

                    // Add caught ball animation
                    score += ball.getType().getPoints();
                    count++;
                    skillBalls.remove(i);
                    lassoPoints.clear();
                    lassoReturning = false;
                    return true;
                }
            }
        }
        return false;
    }

    // Getters
    public Player getPlayer() { return player; }
    public List<SkillBall> getSkillBalls() { return skillBalls; }
    public List<Point> getLassoPoints() { return lassoPoints; }
    public List<CaughtBallAnimation> getCaughtBalls() { return caughtBalls; }
    public ShootingCutscene getShootingCutscene() { return shootingCutscene; }
    public boolean isLassoReturning() { return lassoReturning; }
    public int getScore() { return score; }
    public int getCount() { return count; }
    public int getLives() { return lives; }
    public int getMaxLives() { return MAX_LIVES; }
    public boolean isGameRunning() { return gameRunning; }
    public boolean isGameOver() { return gameOver; } // NEW

    // Class to represent the shooting cutscene when a ball is caught
    public static class ShootingCutscene {
        private float ballX, ballY;
        private float ballStartX, ballStartY;
        private float basketX, basketY;
        private BallType ballType;
        public long startTime;
        private static final long CUTSCENE_DURATION = 1000; // Even faster

        // Constructor to initialize the shooting cutscene with start and end positions
        public ShootingCutscene(int ballStartX, int ballStartY, int basketX, int basketY, BallType ballType) {
            this.ballStartX = this.ballX = ballStartX;
            this.ballStartY = this.ballY = ballStartY;
            this.basketX = basketX;
            this.basketY = basketY;
            this.ballType = ballType;
            this.startTime = System.currentTimeMillis();
        }

        // Method to update the cutscene animation
        public void update() {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float)elapsed / CUTSCENE_DURATION);
            progress = (float)(1 - Math.pow(1 - progress, 2));

            ballX = ballStartX + (basketX - ballStartX) * progress;
            ballY = ballStartY + (basketY - ballStartY) * progress;
        }

        // Method to check if the cutscene is finished
        // Returns true if the cutscene duration has elapsed
        public boolean isFinished() {
            return System.currentTimeMillis() - startTime >= CUTSCENE_DURATION;
        }

        // Getters for the cutscene properties
        public float getBallX() { return ballX; }
        public float getBallY() { return ballY; }
        public float getBasketX() { return basketX; }
        public float getBasketY() { return basketY; }
        public BallType getBallType() { return ballType; }
        public long getStartTime() { return startTime; }
    }

    // Class to represent the animation of a caught ball
    public static class CaughtBallAnimation {
        private float x, y;
        private float targetX, targetY;
        private float startX, startY;
        private BallType type;
        private long startTime;
        private static final long ANIMATION_DURATION = 600;

        // Constructor to initialize the caught ball animation with start and target positions
        public CaughtBallAnimation(int startX, int startY, int targetX, int targetY, BallType type) {
            this.startX = this.x = startX;
            this.startY = this.y = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.type = type;
            this.startTime = System.currentTimeMillis();
        }

        // Method to update the animation position based on elapsed time
        public void update() {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float)elapsed / ANIMATION_DURATION);
            progress = 1 - (1 - progress) * (1 - progress);

            x = startX + (targetX - startX) * progress;
            y = startY + (targetY - startY) * progress;
        }

        // Method to check if the animation is finished
        public boolean isFinished() {
            return System.currentTimeMillis() - startTime >= ANIMATION_DURATION;
        }

        // Getters for the animation properties
        public float getX() { return x; }
        public float getY() { return y; }
        public BallType getType() { return type; }

        // Method to get the scale of the animation based on elapsed time
        public float getScale() {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float)elapsed / ANIMATION_DURATION);
            return 1.0f - progress * 0.4f;
        }
    }

    // Class to represent a skill ball in the game
    public static class SkillBall {
        // Fields for the skill ball's position, speed, size, and type
        private int x, y, dx, dy;
        private int width = 50, height = 50;
        private BallType type;

        // Constructor to initialize the skill ball with position, speed, and type
        public SkillBall(int x, int y, int dx, int dy, BallType type) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.type = type;
        }

        // Method to update the skill ball's position based on its speed
        public void update() {
            x += dx;
            y += dy;
        }

        // Method to get the bounding rectangle of the skill ball for collision detection
        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        // Getters for the skill ball properties
        public int getX() { return x; }
        public int getY() { return y; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public BallType getType() { return type; }
    }
}