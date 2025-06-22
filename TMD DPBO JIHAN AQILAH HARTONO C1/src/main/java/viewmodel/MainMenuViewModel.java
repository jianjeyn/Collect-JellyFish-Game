package viewmodel; // ViewModel for the Main Menu

import model.Database; // Import the Database class to access game results
import model.GameResult; // Import the GameResult class to represent game results
import java.util.List; // Import List to handle collections of game results

// ViewModel for the Main Menu
public class MainMenuViewModel { 
    // List to hold game results
    private List<GameResult> gameResults;
    
    // Constructor to initialize the ViewModel and load game results
    public MainMenuViewModel() {
        loadGameResults();
    }
    
    // Method to load game results from the database
    public void loadGameResults() {
        gameResults = Database.getAllResults();
    }
    
    // Getter method to retrieve the list of game results
    public List<GameResult> getGameResults() {
        return gameResults;
    }
    
    // Method to refresh the game results, typically called when the view needs to update
    public void refreshResults() {
        loadGameResults();
    }
}