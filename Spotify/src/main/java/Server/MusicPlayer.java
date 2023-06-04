package Server;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class MusicPlayer {
    private Player player;
    private AudioDevice audioDevice;
    private boolean isPlaying;

    public void play(String filePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
            player = new Player(bufferedInputStream, audioDevice);
            isPlaying = true;

            new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (player != null && isPlaying) {
            try {
                player.close();
                isPlaying = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
