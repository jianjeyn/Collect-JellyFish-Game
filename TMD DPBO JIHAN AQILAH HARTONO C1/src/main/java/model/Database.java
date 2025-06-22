package model; // Model class for handling database operations related to game results

import java.sql.*; // Import necessary SQL classes for database operations
import java.util.ArrayList; // Import ArrayList to store game results
import java.util.List; // Import List to handle collections of game results

public class Database {
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/spongebob_game";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Static connection instance to manage database connections
    private static Connection connection;

    // Private constructor to prevent instantiation
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Check if the connection is null or closed, then create a new connection
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
            return connection;
        } catch (ClassNotFoundException e) { // Handle the case where the MySQL driver is not found
            System.err.println("MySQL Driver not found: " + e.getMessage());
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    // Method to retrieve all game results from the database
    public static List<GameResult> getAllResults() {
        List<GameResult> results = new ArrayList<>();

        // Add default data if database connection fails
        try { // SQL query to retrieve game results ordered by score
            String query = "SELECT * FROM thasil ORDER BY skor DESC";

            // Establish connection and execute the query
            try (Connection conn = getConnection();
                // Prepare the statement and execute the query
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                // Process the result set and populate the results list
                while (rs.next()) {
                    GameResult result = new GameResult(
                            rs.getString("username"),
                            rs.getInt("skor"),
                            rs.getInt("count")
                    );
                    results.add(result); // Add each result to the list
                }
            }
        } catch (SQLException e) { // Handle SQL exceptions, such as connection issues or query errors
            System.err.println("Database error: " + e.getMessage());
            System.out.println("Using default data...");

            // Fallback data if database fails
            results.add(new GameResult("Manusia", 1000, 100));
            results.add(new GameResult("BukanManusia", 800, 80));
            results.add(new GameResult("Barbie", 700, 40));
        }

        return results; // Return the list of game results
    }

    // Method to save or update game results in the database
    public static void saveOrUpdateResult(String username, int score, int count) {
        try { // SQL queries for checking, updating, and inserting game results
            String checkQuery = "SELECT * FROM thasil WHERE username = ?";
            String updateQuery = "UPDATE thasil SET skor = skor + ?, count = count + ? WHERE username = ?";
            String insertQuery = "INSERT INTO thasil (username, skor, count) VALUES (?, ?, ?)";

            // Establish connection to the database
            try (Connection conn = getConnection()) {
                // Check if user exists
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Update existing record
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, score);
                    updateStmt.setInt(2, count);
                    updateStmt.setString(3, username);
                    updateStmt.executeUpdate();
                } else {
                    // Insert new record
                    PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                    insertStmt.setString(1, username);
                    insertStmt.setInt(2, score);
                    insertStmt.setInt(3, count);
                    insertStmt.executeUpdate();
                }

                System.out.println("Score saved successfully for: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Failed to save score: " + e.getMessage());
            // Game continues even if save fails
        }
    }
}