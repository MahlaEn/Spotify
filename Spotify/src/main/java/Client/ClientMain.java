package Client;

import Server.DataBase;
import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class ClientMain {
    static Scanner inp = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket("127.0.0.1", 2345);
            System.out.println("Connected to server!");
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            PrintWriter out = new PrintWriter(output, true);
            Request request;
            Response response = new Response();

            request = ShowMainMenu();//create request
            out.println(request.getJson().toString());//send request to server
            response.setJson(new JSONObject(in.readLine()));//receive response from server

            while (response != null) {
                request = handle(response);//create new request
                out.println(request.getJson().toString());////send request to server

                response.setJson(new JSONObject(in.readLine()));//receive response from server
            }
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Request ShowMainMenu(){
        System.out.println("Enter your command :\n 1)Login\n 2)SignUp");
        int command=inp.nextInt();
        Request request = new Request();
        switch (command){
            case 1://Login
                System.out.println("Username :");
                String username=inp.next();
                System.out.println("Password :");
                String password= inp.next();
                JSONObject json=new JSONObject();
                json.put("Command","Login");
                json.put("Username",username);
                json.put("Password",password);
                request.setJson(json);//create request
                break;
            case 2://SignUp

                break;
        }
        return request;
    }
    public static Request handle(Response response) throws SQLException {
        Request request=new Request();
        JSONObject resp=response.getJson();
        switch (resp.getString("Status")){
            case "Successfully login":
                System.out.println("WELCOME!");
                return ShowUserMenu();
            case "Fail login":

        }
        return request;
    }
    public static Request ShowUserMenu() throws SQLException {
        System.out.println("Enter your command :\n 1)Music library\n 2)Search artist name\n" +
                "3)Search song title\n 4)Search album title\n 5)Search genre\n 6)View personal profile page");
        int command=inp.nextInt();
        JSONObject json;
        Request request = new Request();
        switch (command){
            case 1://Music library
                json=new JSONObject();
                json.put("Command","Music library");
                ShowMusics();
                request.setJson(json);
                break;

            case 2://Search artist name
                System.out.println("Artist name :");
                String artist=inp.next();
                json=new JSONObject();
                json.put("Command","Search artist");
                json.put("Artist",artist);
                request.setJson(json);//create request
                break;

            case 3://Search song title
                System.out.println("Song title :");
                String song=inp.next();
                json=new JSONObject();
                json.put("Command","Search song");
                json.put("song",song);
                request.setJson(json);//create request
                break;

            case 4://Search album title

                break;
        }

        return request;
    }

    private static void ShowMusics() throws SQLException {
        DataBase DB=new DataBase();
        ResultSet resultSet=DB.query("SELECT * FROM \"Spotify\".\"Music\" \n");
        while(resultSet.next()){
            toString(resultSet);
        }
    }
    public static void toString(ResultSet resultSet) throws SQLException {
        System.out.println(
                "TrackID: "+resultSet.getString("TrackID") + "\nTitle: "+ resultSet.getString("Title") +
                        "\nArtist: " + resultSet.getString("Artist") + "\nAlbum: " + resultSet.getDouble("Album") +
                        "\nGenre: " + resultSet.getInt("Genre") +
                        "\nDuration: " + resultSet.getString("Duration") + "\nRelease Date: " + resultSet.getDouble("ReleaseDate") +
                        "\nPopularity: " + resultSet.getInt("Popularity") +
                        "\n____________________________________"
        );
    }
}