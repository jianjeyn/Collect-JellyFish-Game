/*Asset Credit:
Image
1. Background & BG_gameover (https://free-game-assets.itch.io/nature-landscapes-free-pixel-art)
2. Basket (https://pin.it/3qD9Rwb76)
3. Lives Heart (https://pin.it/4JJu9k8XX)
4. Jelly Fish 1-4 (https://pin.it/6E4Bburmy)
5. SpongeBob_Player (https://pin.it/6Z2utQRfy)

Font Game
1. Pixeloid Mono (https://ggbot.itch.io/pixeloid-font)

Sound
1. Web_shoot (https://jdwasabi.itch.io/8-bit-16-bit-sound-effects-pack/download/eyJpZCI6MTk3MTA3LCJleHBpcmVzIjoxNzUwNDI0NTU4fQ%3d%3d.4a0vJP1EUB3wMIzhGOhqg6nsst8%3d)
2. Backsound (https://not-jam.itch.io/not-jam-music-pack) */


import view.MainMenuView; // Import the MainMenuView class to display the main menu
import javax.swing.*; // Import Swing components for GUI

public class Main { // Main class to start the game application
    public static void main(String[] args) {
        // Test database connection at startup
        System.out.println("Starting Game...");

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set look and feel: " + e.getMessage());
        }

        // Use SwingUtilities to ensure the GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                /*
                Saya Jihan Aqilah Hartono mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah 
                Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya 
                tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin. 
                */

                // Initialize the main menu view
                new MainMenuView().setVisible(true);
                System.out.println("Game started successfully!");

            } catch (Exception e) { // Handle any exceptions that occur during initialization
                System.err.println("Error starting game: " + e.getMessage());
                e.printStackTrace();

                // Show an error dialog to the user
                JOptionPane.showMessageDialog(null,
                        "Error starting game: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}