package Server;

import Database.DataBase;
import Database.ImportData;
import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerMain {
    private final ServerSocket serverSocket;
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    public static Map<Integer,Socket> Client = new HashMap<>();
    static MusicPlayer player=new MusicPlayer();
    public static void main(String[] args) throws IOException, SQLException, URISyntaxException {
        ServerMain server = new ServerMain(2345);
        DataBase.Init();
        new ImportData();
        server.start();
    }
    public void start(){
        System.out.println("Server started.");
        while(true){
            try{
                Socket socket=serverSocket.accept();
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                String ID= String.valueOf(UUID.randomUUID());
                Client.put(ID.hashCode(),socket);
                handler.start();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public ServerMain(int portNumber) throws IOException {
        this.serverSocket = new ServerSocket(portNumber);
    }
    public static Response handle(Request request, PrintWriter out, int ID) throws SQLException {
        Response response=new Response();
        JSONObject req=request.getJson();
        switch (req.getString("Command")){
            case "Login":
                response= DataBase.Login(request);
                return response;
            case "SignUp":
                response=DataBase.SignUp(request);
                return response;
            case "Music library":
                ShowMusics(request,out);
                JSONObject res=new JSONObject();
                res.put("Status","Musics was showed");
                response.setJson(res);
                return response;
            case "View playlists":
                ViewPlaylists(request,out,ID);
                res=new JSONObject();
                res.put("Status","Playlists was showed");
                response.setJson(res);
                return response;

            case "Play song":
                return playSong(request);

            case "Pause song":
                return pauseSong(request);
            case "Like":
                return likeSong(request,ID);
            case "toPlaylist":
                return addToPlaylist(request,ID);
            case "Search artist":
                SearchArtist(request,out);
                res=new JSONObject();
                res.put("Status","Searched artist");
                response.setJson(res);
                return response;
            case "Search title":
                SearchTitle(request,out);
                res=new JSONObject();
                res.put("Status","Searched title");
                response.setJson(res);
                return response;
            case "Search album":
                SearchAlbum(request,out);
                res=new JSONObject();
                res.put("Status","Searched album");
                response.setJson(res);
                return response;
            case "Search genre":
                SearchGenre(request,out);
                res=new JSONObject();
                res.put("Status","Searched genre");
                response.setJson(res);
                return response;
            case "View profile":
                ViewProfile(request,out,ID);
                res=new JSONObject();
                res.put("Status","Searched profile");
                response.setJson(res);
                return response;
            case "Logout":
                res=new JSONObject();
                res.put("Status","Logged out");
                response.setJson(res);
                return response;
            case "Create playlist":
                return CreatePlaylist(request,ID);
            case "Show playlist songs":
                ShowPlaylist(request,out,ID);
                res=new JSONObject();
                res.put("Status","Playlist was showed");
                response.setJson(res);
                return response;

        }
        return response;
    }

    private static void ShowPlaylist(Request request, PrintWriter out, int userID) throws SQLException {
        JSONObject res=new JSONObject();
        ResultSet resultSet=DataBase.SearchPlaylist(request,userID);
        resultSet.next();
        int playlistID=resultSet.getInt("playlistID");
        res.put("Status","Show playlist");
        out.println(res);
        resultSet=DataBase.ShowPlaylist(playlistID);
        ArrayList<Integer>trackIDs=new ArrayList<Integer>();
        while (resultSet.next()){
            trackIDs.add(resultSet.getInt("musicID"));
        }
        for(int trackID:trackIDs){
            JSONObject json=new JSONObject();
            ResultSet resultSong=DataBase.findSong(trackID);
            resultSong.next();
            json.put("Music",toString(resultSong,"Music"));
            out.println(json);
        }
    }

    private static void ViewPlaylists(Request request, PrintWriter out, int ID) throws SQLException {
        ResultSet resultSet=DataBase.ViewPlaylists(request,ID);
        JSONObject res=new JSONObject();
        res.put("Status","Display playlists");
        out.println(res);
        while (resultSet.next()){
            res=new JSONObject();
            res.put("Name",resultSet.getString("Playlist"));
            out.println(res);
        }
    }

    private static Response addToPlaylist(Request request, int userID) throws SQLException {
        DataBase.addToPlaylist(request.getJson().getString("Name"),userID,request.getJson().getInt("trackID"));
        JSONObject res=new JSONObject();
        Response response=new Response();
        res.put("Status","added to playlist");
        response.setJson(res);
        return response;
    }

    private static Response CreatePlaylist(Request request, int userID) {
        DataBase.createPlaylist(request.getJson().getString("Name"),userID);
        JSONObject res=new JSONObject();
        Response response=new Response();
        res.put("Status","created playlist");
        response.setJson(res);
        return response;
    }

    private static Response likeSong(Request request, int ID) throws SQLException {
        int trackID=request.getJson().getInt("trackID");
        DataBase.Like(ID,trackID);
        JSONObject res=new JSONObject();
        Response response=new Response();
        res.put("Status","liked");
        response.setJson(res);
        return response;
    }

    private static void ViewProfile(Request request, PrintWriter out, int ID) throws SQLException {
        ResultSet resultSet=DataBase.ViewProfile(request);
        JSONObject res=new JSONObject();
        res.put("Status","View profile");
        out.println(res);
        while (resultSet.next()){
            res=new JSONObject();
            res.put("user",toString(resultSet,"User"));
            out.println(res);
        }
    }
    private static void SearchGenre(Request request, PrintWriter out) throws SQLException {
        JSONObject req=request.getJson();
        JSONObject res=new JSONObject();
        ResultSet resultSet=DataBase.SearchGenre(request);
        res.put("Status","Searching genre");
        out.println(res);
        while(resultSet.next()){
            JSONObject json=new JSONObject();
            json.put("Music",toString(resultSet,"Music"));
            out.println(json);
        }
    }
    private static void SearchAlbum(Request request, PrintWriter out) throws SQLException {
        JSONObject req=request.getJson();
        String album=req.getString("Album");
        JSONObject res=new JSONObject();
        ResultSet resultSet=DataBase.SearchAlbum(request);
        res.put("Status","Searching album");
        out.println(res);
        while(resultSet.next()){
            JSONObject json=new JSONObject();
            json.put("Music",toString(resultSet,"Music"));
            out.println(json);
        }
    }
    private static void SearchTitle(Request request, PrintWriter out) throws SQLException {
        JSONObject req=request.getJson();
        String title=req.getString("Title");
        JSONObject res=new JSONObject();
        ResultSet resultSet=DataBase.SearchTitle(request);
        res.put("Status","Searching title");
        out.println(res);
        while(resultSet.next()){
            JSONObject json=new JSONObject();
            json.put("Music",toString(resultSet,"Music"));
            out.println(json);
        }
    }
    private static void SearchArtist(Request request, PrintWriter out) throws SQLException {
        JSONObject req=request.getJson();
        String artist=req.getString("Artist");
        JSONObject res=new JSONObject();
        ResultSet resultSet=DataBase.SearchArtist(request);
        res.put("Status","Searching artist");
        out.println(res);
        while(resultSet.next()){
            JSONObject json=new JSONObject();
            json.put("Music",toString(resultSet,"Music"));
            out.println(json);
        }
    }
    private static Response pauseSong(Request request) {
        Response response = new Response();
        JSONObject json=new JSONObject();
        json.put("Status","pauseSong");
        response.setJson(json);
        player.pause();
        return response;
    }
    private static Response playSong(Request request) throws SQLException {
        JSONObject req=request.getJson();
        String title=req.getString("Title");
        String artist=req.getString("Artist");
        Response response = DataBase.PlaySong(request);
        String songPath=response.getJson().getString("songPath");
        player.play(songPath);
        return response;
    }
    public static void ShowMusics(Request request, PrintWriter out) throws SQLException {
        ResultSet resultSet=DataBase.ShowMusic(request);
        JSONObject res=new JSONObject();
        res.put("Status","Display songs");
        out.println(res);
        while (resultSet.next()){
            res=new JSONObject();
            res.put("Music",toString(resultSet,"Music"));
            out.println(res);
        }
    }
    public static String toString(ResultSet resultSet,String type) throws SQLException {
        switch (type){
            case "Music":
                return (
                    "TrackID: "+resultSet.getInt("TrackID") + "\nTitle: "+ resultSet.getString("Title") +
                            "\nArtist: " + resultSet.getString("Artist") + "\nAlbum: " + resultSet.getString("Album") +
                            "\nGenre: " + resultSet.getString("Genre") +
                            "\nDuration: " + resultSet.getString("Duration") + "\nRelease Date: " + resultSet.getString("ReleaseDate") +
                            "\nPopularity: " + resultSet.getDouble("Popularity") +
                            "\n____________________________________"
            );
            case "User":
                return (
                        "Username: "+resultSet.getString("Username") + "\nEmail: "+ resultSet.getString("Email") +
                    "\nPassword: " + resultSet.getString("Password") + "\nBirthday: " + resultSet.getString("Birthday") +
                    "\nPlaylistID: " + resultSet.getInt("PlaylistID") +
                    "\nUserID: " + resultSet.getInt("UserID") +
                    "\n____________________________________"
                );

        }

        return type;
    }
}