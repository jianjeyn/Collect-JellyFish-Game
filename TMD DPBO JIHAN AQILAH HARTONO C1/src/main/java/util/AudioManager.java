package util; // Utility class for managing audio playback in the game

import javax.sound.sampled.*; // Import necessary classes for audio playback
import java.io.File; // Import File class for handling file paths
import java.io.IOException; // Import IOException for handling file-related exceptions

public class AudioManager {
    private static AudioManager instance; // Singleton instance of AudioManager
    private Clip webShootClip; // Clip for web shoot sound
    private Clip backgroundMusicClip;  // Background music clip
    
    private AudioManager() { // Private constructor to prevent instantiation
        System.out.println("=== INITIALIZING AUDIO MANAGER ===");
        loadSounds();
    }
    
    public static AudioManager getInstance() { // Method to get the singleton instance of AudioManager
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    private void loadSounds() { // Method to load audio files
        try {
            System.out.println("Loading sounds...");
            
            // Load web shoot sound
            loadWebShootSound();
            
            // Load background music
            loadBackgroundMusic();
            
        } catch (Exception e) {
            System.out.println("Error in loadSounds: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadWebShootSound() { // Method to load the web shoot sound
        System.out.println("Loading web shoot sound...");
        
        // Try multiple locations
        String[] paths = {
            "src/main/resources/sounds/web_shoot.wav",
            "sounds/web_shoot.wav",
            "web_shoot.wav"
        };
        
        for (String path : paths) {
            File soundFile = new File(path);
            System.out.println("Trying web shoot: " + soundFile.getAbsolutePath());
            System.out.println("File exists: " + soundFile.exists());
            
            if (soundFile.exists()) {
                try {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                    AudioFormat originalFormat = audioStream.getFormat();
                    
                    System.out.println("Web shoot original format: " + originalFormat);
                    
                    // Convert to PCM format if needed
                    AudioFormat targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        originalFormat.getSampleRate(),
                        16, // 16-bit
                        originalFormat.getChannels(),
                        originalFormat.getChannels() * 2, // frame size
                        originalFormat.getSampleRate(),
                        false // little endian
                    );
                    
                    // Convert if necessary
                    if (!originalFormat.matches(targetFormat)) {
                        System.out.println("Converting web shoot audio format...");
                        audioStream = AudioSystem.getAudioInputStream(targetFormat, audioStream);
                    }
                    
                    webShootClip = AudioSystem.getClip();
                    webShootClip.open(audioStream);
                    
                    System.out.println("SUCCESS: Loaded web shoot sound - " + path);
                    System.out.println("Web shoot clip length: " + (webShootClip.getMicrosecondLength() / 1000000.0) + " seconds");
                    
                    // Add line listener for debugging
                    webShootClip.addLineListener(event -> {
                        System.out.println("Web shoot audio event: " + event.getType());
                    });
                    
                    return; // Exit if successful
                } catch (Exception e) {
                    System.out.println("Error loading web shoot " + path + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Web shoot file not found: " + path);
            }
        }
        
        System.out.println("FAILED: Could not load web shoot sound from any location");
    }
    
    // Load background music
    private void loadBackgroundMusic() {
        System.out.println("Loading background music...");
        
        String[] paths = {
            "src/main/resources/sounds/backsound.wav",  // Try WAV first
            "src/main/resources/sounds/backsound.mp3",  // Then MP3
            "src/main/resources/sounds/bg_music.wav",
            "src/main/resources/sounds/bg_music.mp3",
            "sounds/backsound.wav",
            "sounds/backsound.mp3",
            "sounds/bg_music.wav",
            "sounds/bg_music.mp3",
            "backsound.wav",
            "backsound.mp3",
            "bg_music.wav",
            "bg_music.mp3"
        };
        
        // First try native supported formats
        for (String path : paths) {
            File soundFile = new File(path);
            System.out.println("Trying background music: " + soundFile.getAbsolutePath());
            System.out.println("BG Music file exists: " + soundFile.exists());
            
            if (soundFile.exists()) {
                try {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                    AudioFormat originalFormat = audioStream.getFormat();
                    
                    System.out.println("BG Music original format: " + originalFormat);
                    
                    // Convert to PCM format if needed
                    AudioFormat targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        originalFormat.getSampleRate(),
                        16,
                        originalFormat.getChannels(),
                        originalFormat.getChannels() * 2,
                        originalFormat.getSampleRate(),
                        false
                    );
                    
                    if (!originalFormat.matches(targetFormat)) {
                        System.out.println("Converting background music format...");
                        audioStream = AudioSystem.getAudioInputStream(targetFormat, audioStream);
                    }
                    
                    backgroundMusicClip = AudioSystem.getClip();
                    backgroundMusicClip.open(audioStream);
                    
                    System.out.println("SUCCESS: Loaded background music - " + path);
                    System.out.println("BG Music length: " + (backgroundMusicClip.getMicrosecondLength() / 1000000.0) + " seconds");
                    
                    // Add line listener for debugging
                    backgroundMusicClip.addLineListener(event -> {
                        System.out.println("BG Music event: " + event.getType());
                    });
                    
                    return; // Exit if successful
                } catch (Exception e) {
                    System.out.println("Error loading BG music " + path + ": " + e.getMessage());
                    System.out.println("Reason: " + e.getClass().getSimpleName());
                }
            } else {
                System.out.println("BG Music file not found: " + path);
            }
        }
        
        // If no supported format found, try OGG with warning
        String[] oggPaths = {
            "src/main/resources/sounds/backsound.ogg",
            "sounds/backsound.ogg",
            "backsound.ogg"
        };
        
        for (String path : oggPaths) {
            File soundFile = new File(path);
            if (soundFile.exists()) {
                System.out.println("Found OGG file but Java doesn't support OGG natively: " + path);
                System.out.println("Please convert " + path + " to WAV or MP3 format");
                break;
            }
        }
        
        System.out.println("FAILED: Could not load background music from any location");
        System.out.println("SOLUTION: Convert your backsound.ogg to backsound.wav or backsound.mp3");
    }
    
    public void playWebShoot() { // Method to play the web shoot sound
        System.out.println("playWebShoot() called!");
        
        if (webShootClip != null) {
            try {
                // NEW: Use SwingUtilities to ensure proper threading
                javax.swing.SwingUtilities.invokeLater(() -> {
                    try {
                        // Stop if already playing
                        if (webShootClip.isRunning()) {
                            System.out.println("Stopping currently playing web shoot clip...");
                            webShootClip.stop();
                        }
                        
                        // Wait a bit for stop to complete
                        Thread.sleep(10);
                        
                        // Reset to beginning
                        webShootClip.setFramePosition(0);
                        System.out.println("Reset web shoot to frame 0");
                        
                        // Play with flush
                        webShootClip.flush();
                        webShootClip.start();
                        System.out.println("Web shoot sound START command sent!");
                        
                        // Wait a bit and check again
                        Thread.sleep(50);
                        System.out.println("After 50ms wait - Web shoot Running: " + webShootClip.isRunning());
                        System.out.println("After 50ms wait - Web shoot Active: " + webShootClip.isActive());
                        
                        // If still not running, try alternative approach
                        if (!webShootClip.isRunning()) {
                            System.out.println("Web shoot clip still not running - trying alternative approach");
                            playWithSourceDataLine();
                        }
                        
                    } catch (Exception e) {
                        System.out.println("Error in web shoot playback thread: " + e.getMessage());
                        e.printStackTrace();
                        
                        // FALLBACK: System beep
                        java.awt.Toolkit.getDefaultToolkit().beep();
                    }
                });
                
            } catch (Exception e) {
                System.out.println("Error playing web shoot sound: " + e.getMessage());
                e.printStackTrace();
                
                // FALLBACK: System beep
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        } else {
            System.out.println("ERROR: webShootClip is null - sound not loaded!");
            
            // FALLBACK: System beep
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
    }
    
    // Background music controls
    public void playBackgroundMusic() {
        System.out.println("playBackgroundMusic() called!");
        
        if (backgroundMusicClip != null) {
            try {
                // Stop if already playing
                if (backgroundMusicClip.isRunning()) {
                    System.out.println("Stopping currently playing background music...");
                    backgroundMusicClip.stop();
                }
                
                // Reset to beginning
                backgroundMusicClip.setFramePosition(0);
                System.out.println("Reset background music to frame 0");
                
                // Loop continuously
                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                System.out.println("Background music started with LOOP_CONTINUOUSLY!");
                System.out.println("BG Music running: " + backgroundMusicClip.isRunning());
                
            } catch (Exception e) {
                System.out.println("Error playing background music: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR: backgroundMusicClip is null - music not loaded!");
        }
    }
    
    public void stopBackgroundMusic() { // Method to stop the background music
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            System.out.println("Background music stopped");
        }
    }
    
    public void pauseBackgroundMusic() { // Method to pause the background music
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            System.out.println("Background music paused");
        }
    }
    
    public void resumeBackgroundMusic() { // Method to resume the background music
        if (backgroundMusicClip != null && !backgroundMusicClip.isRunning()) {
            backgroundMusicClip.start();
            System.out.println("Background music resumed");
        }
    }
    
    public void setBackgroundMusicVolume(float volume) { // Method to set the volume of the background music
        if (backgroundMusicClip != null) {
            try {
                if (backgroundMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl volumeControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                    float min = volumeControl.getMinimum();
                    float max = volumeControl.getMaximum();
                    float value = min + (max - min) * volume; // volume dari 0.0 to 1.0
                    volumeControl.setValue(value);
                    System.out.println("Background music volume set to: " + volume + " (dB: " + value + ")");
                } else {
                    System.out.println("Volume control not supported for background music");
                }
            } catch (Exception e) {
                System.out.println("Cannot control background music volume: " + e.getMessage());
            }
        }
    }
    
    public boolean isBackgroundMusicPlaying() { // Method to check if background music is currently playing
        return backgroundMusicClip != null && backgroundMusicClip.isRunning();
    }
    
    // Alternative playback method using SourceDataLine
    private void playWithSourceDataLine() {
        System.out.println("Trying alternative playback with SourceDataLine...");
        
        try {
            // Simple beep generation as test
            int sampleRate = 44100;
            int duration = 200; // 200ms
            int frameSize = sampleRate * duration / 1000;
            
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            
            // Generate simple beep
            byte[] buffer = new byte[frameSize * 2];
            for (int i = 0; i < frameSize; i++) {
                double angle = 2.0 * Math.PI * i * 800 / sampleRate; // 800 Hz tone
                short sample = (short) (Math.sin(angle) * 32767 * 0.5); // 50% volume
                buffer[i * 2] = (byte) (sample & 0xFF);
                buffer[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }
            
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
            
            System.out.println("Alternative playback completed");
            
        } catch (Exception e) {
            System.out.println("Alternative playback also failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void stopAllSounds() { // Method to stop all sounds
        // Stop web shoot sound if playing
        if (webShootClip != null && webShootClip.isRunning()) {
            webShootClip.stop();
            System.out.println("Web shoot sound stopped");
        }
        // Stop background music if playing
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            System.out.println("Background music stopped");
        }
    }
}