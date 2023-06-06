package Database;

import Classes.Music;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ImportData {
    public ImportData() throws SQLException {
        ArrayList<Music>musics=new ArrayList<>();
        Music SoldierSide=new Music(1,"Soldier side","System of a down","Mezmerize","hard rock","3,39","2005",8.9,"D:\\Uni\\Ap\\Spotify\\Spotify\\src\\main\\resources\\SoldierSide.mp3");
        Music BornToTouchYourFeelings=new Music(2,"Born To Touch Your Feelings","Scorpions","","hard rock","4,02","2017",9.1,"D:\\Uni\\Ap\\Spotify\\Spotify\\src\\main\\resources\\born_to_touch_your_feelings.mp3");

        musics.add(SoldierSide);
        musics.add(BornToTouchYourFeelings);
        for(Music music : musics){
            String title=music.getTitle();
            ResultSet resultSet=DataBase.query("SELECT * FROM \"Spotify\".\"Music\" WHERE \"Title\" = " + "'" + title + "'");
            if(!resultSet.next()){
                String sql = "INSERT INTO\"Spotify\".\"Music\" VALUES ('" + music.getTrackID() + "','" + music.getTitle() + "', '" +
                        music.getArtist() + "', '" + music.getAlbum() + "','" + music.getGenre() + "', '" + music.getDuration() + "', '" +
                        music.getReleaseDate() + "', '" + music.getPopularity() + "','" + music.getMusicPath() + "')";
                DataBase.query(sql);
            }
        }
    }
}
