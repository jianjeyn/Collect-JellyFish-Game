package model; // Model class for the Player in the game

import java.awt.*; // Import necessary classes for handling graphics and geometry

// Player class representing the player character in the game
public class Player {
    private int x, y; // Player's position on the screen
    private int width = 150;  // Player's width
    private int height = 150;  // Player's height
    private int speed = 5; // Player's movement speed

    // Constructor to initialize the player's position
    public Player(int startX, int startY) {
        this.x = startX; // Set initial x position
        this.y = startY; // Set initial y position
    }

    // Methods to move the player in different directions
    public void moveUp() {
        y = Math.max(0, y - speed);
    }

    public void moveDown() {
        y = Math.min(520, y + speed);
    }

    public void moveLeft() {
        x = Math.max(0, x - speed);
    }

    public void moveRight() {
        x = Math.min(720, x + speed); 
    }

    // Method to get the player's bounding rectangle for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}