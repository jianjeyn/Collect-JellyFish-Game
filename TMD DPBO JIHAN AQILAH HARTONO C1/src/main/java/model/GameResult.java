package model; // Model class for handling game results

// Import necessary SQL classes for database operations
public class GameResult {
    // Fields to store game result data
    private String username;
    private int score;
    private int count;

    // Constructor to initialize a GameResult object
    public GameResult(String username, int score, int count) {
        this.username = username;
        this.score = score;
        this.count = count;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}