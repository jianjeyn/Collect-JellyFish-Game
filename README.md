# Collect-JellyFish-Game

A simple game created with Java where the player controls SpongeBob to catch as many jellyfish as possible to score points.

## Description

In this game, the player controls the SpongeBob character at the bottom of the screen. Jellyfish will fall from the top of the screen, and the player must move SpongeBob to catch them. Each jellyfish caught will award points. There is also a life system, where the player will lose a life if they fail to catch a jellyfish. The game ends when the player runs out of lives. The highest scores will be saved and displayed on the scoreboard.

## Features

  * **Simple Gameplay:** Easy to play, just move the player left and right to catch the falling objects.
  * **Scoreboard:** Saves and displays the highest scores ever achieved.
  * **Life System:** The player has a limited number of lives, adding to the challenge of the game.
  * **Graphical Interface:** An attractive game display with pixel assets.
  * **Background Music and Sound Effects:** Equipped with background music and sound effects for a better playing experience.

## Prerequisites

  * Java Development Kit (JDK) 8 or newer.
  * MySQL Server.
  * A Java IDE (optional, but recommended) such as IntelliJ IDEA, Eclipse, or NetBeans.

## How to Run the Project

1.  **Clone the Repository**

    ```bash
    git clone https://github.com/jianjeyn/collect-jellyfish-game.git
    ```

2.  **Setup the Database**

      * Open your MySQL server.
      * Create a new database named `spongebob_game`.
      * Import the `database/spongebob_game.sql` file into your newly created database. This will create the necessary tables.

3.  **Configure the Database Connection**

      * Open the `src/main/java/model/Database.java` file.
      * Adjust the `url`, `user`, and `password` according to your MySQL configuration.

    <!-- end list -->

    ```java
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/spongebob_game";
    static final String USER = "root"; // Replace with your username
    static final String PASS = ""; // Replace with your password
    ```

4.  **Open the Project in an IDE**

      * Open this project using your preferred Java IDE.
      * Make sure all dependencies (if any) are loaded correctly.

5.  **Run the Application**

      * Find and run the `src/main/java/Main.java` file.
      * The main menu of the game will appear.

## Created By

  * **Name:** Jihan Aqilah Hartono
  * **NIM:** 2106337
  * **Class:** C1

## Asset Credits

### Image

1.  **Background & BG\_gameover:** [Nature Landscapes Free Pixel Art](https://free-game-assets.itch.io/nature-landscapes-free-pixel-art)
2.  **Basket:** [Pinterest](https://pin.it/3qD9Rwb76)
3.  **Lives Heart:** [Pinterest](https://pin.it/4JJu9k8XX)
4.  **Jelly Fish 1-4:** [Pinterest](https://pin.it/6E4Bburmy)
5.  **SpongeBob\_Player:** [Pinterest](https://pin.it/6Z2utQRfy)

### Font

1.  **Pixeloid Mono:** [Pixeloid Font](https://ggbot.itch.io/pixeloid-font)

### Sound

1.  **Web\_shoot:** [8-bit 16-bit Sound Effects Pack](https://jdwasabi.itch.io/8-bit-16-bit-sound-effects-pack)
2.  **Backsound:** [Not Jam Music Pack](https://not-jam.itch.io/not-jam-music-pack)

-----
