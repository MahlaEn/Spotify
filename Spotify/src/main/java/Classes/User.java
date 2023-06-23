package Classes;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String ID;
    private Map<String,Integer> playlists=new HashMap<String, Integer>();

    public Map<String, Integer> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Map<String, Integer> playlists) {
        this.playlists = playlists;
    }
    public void addPlaylist(String playlist,int playlistID){
        playlists.put(playlist,playlistID);
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

}
