package Server;

import javazoom.jl.player.Player;

import java.io.FileInputStream;

public class PlayMusic {
    public static void play(String musicPath) {
        try {
            Player player = new Player(new FileInputStream(musicPath));
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}