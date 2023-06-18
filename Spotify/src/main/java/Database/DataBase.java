package Database;

import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.sql.*;
import java.util.UUID;

public class DataBase {
    static Statement statement;
    static private Connection connection;
    public static void Init() {
        try {
            Class.forName("org.postgresql.Driver");
            DataBase.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AP","postgres","12345678");
            DataBase.statement = connection.createStatement();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Statement getStatement() {
        return statement;
    }

    public static void setStatement(Statement statement) {
        DataBase.statement = statement;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        DataBase.connection = connection;
    }

    public static ResultSet query(String sql){
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Response SignUp(Request request) throws SQLException {
        JSONObject json=request.getJson();
        String username=json.getString("username");
        String password = json.getString("password");
        ResultSet resultSet=query("SELECT * FROM \"Spotify\".\"User\" WHERE \"Username\" = " + "'" + username + "'");
        Response response = new Response();
        if(resultSet.next()) {
            json.put("Status", "Fail signup");
            response.setJson(json);
            return response;
        }
        json.put("Status","Successfully signup");
        response.setJson(json);
        String email = json.getString("Email");
        String imagePath = json.getString("ImagePath");
        if(imagePath.charAt(0)=='"'){
            imagePath=imagePath.substring(1,imagePath.length()-1);//Remove additional character
        }
        UUID ID = UUID.randomUUID();
        String playlistID=UUID.randomUUID().toString();
        String date=json.getString("Birthday");
        String sql = "INSERT INTO\"Spotify\".\"User\" VALUES ('" +  username + "', '" +
                email + "', '" + password + "','" + imagePath + "', '" + date + "', '" + playlistID.hashCode() + "','" + ID.hashCode()  + "')";

        query(sql);
        return response;
    }
    public static Response Login(Request request) throws SQLException {
        JSONObject json=request.getJson();
        String username = json.getString("username");
        String password = json.getString("password");
        ResultSet resultSet=query("SELECT * FROM \"Spotify\".\"User\" \n");
        Response response=new Response();
        while(resultSet.next()){
            if(resultSet.getString("Username").equals(username) && resultSet.getString("Password").equals(password)){
                json.put("Status","Successfully login");
                response.setJson(json);
                return response;
            }
        }
        json.put("Status","Fail login");
        response.setJson(json);
        return response;
    }
    public static ResultSet ViewProfile(Request request){
        ResultSet resultSet=DataBase.query("SELECT * FROM \"Spotify\".\"User\" WHERE \"Username\" = " + "'" + request.getJson().getString("username") + "'");
        return resultSet;
    }
    public static ResultSet ShowMusic(Request request){
        ResultSet resultSet=DataBase.query("SELECT * FROM \"Spotify\".\"Music\" \n");
        return resultSet;
    }
    public static Response PlaySong(Request request) throws SQLException {
        JSONObject json=request.getJson();
        Response response = new Response();
        String title=json.getString("Title");
        String artist=json.getString("Artist");
        ResultSet resultSet = query("SELECT * FROM \"Spotify\".\"Music\" WHERE \"Title\" = " + "'" + title + "'" + "AND" + "\"Artist\" = " + "'" + artist + "'");
        if(resultSet.next()) {
            json.put("Status", "Find song path");
            json.put("songPath",resultSet.getString("MusicPath"));
            response.setJson(json);
        }
        return response;
    }
    public static ResultSet SearchTitle(Request request) {
        ResultSet resultSet=DataBase.query("SELECT * FROM \"Spotify\".\"Music\" WHERE \"Title\" = " + "'" + request.getJson().getString("Title") + "'");
        return resultSet;
    }
    public static ResultSet SearchGenre(Request request) {
        ResultSet resultSet=DataBase.query("SELECT * FROM \"Spotify\".\"Music\" WHERE \"Genre\" = " + "'" + request.getJson().getString("Genre") + "'");
        return resultSet;
    }
    public static ResultSet SearchAlbum(Request request) {
        ResultSet resultSet=DataBase.query("SELECT * FROM \"Spotify\".\"Music\" WHERE \"Album\" = " + "'" + request.getJson().getString("Album") + "'");
        return resultSet;
    }
    public static ResultSet SearchArtist(Request request) {
        ResultSet resultSet=DataBase.query("SELECT * FROM \"Spotify\".\"Music\" WHERE \"Artist\" = " + "'" + request.getJson().getString("Artist") + "'");
        return resultSet;
    }
}