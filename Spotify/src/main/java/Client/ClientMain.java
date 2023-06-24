package Client;

import Server.MusicPlayer;
import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.UnknownHostException;
import java.util.Base64;
import java.util.Scanner;


public class ClientMain {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    static MusicPlayer player=new MusicPlayer();
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

            while (response.getJson() != null) {
                request = handle(response,out,in);//create new request
                if(request.getJson()!=null) {
                    out.println(request.getJson().toString());////send request to server
                }
                response.setJson(new JSONObject(in.readLine()));//receive response from server
            }
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Request ShowMainMenu(){
        System.out.println("Enter your command :\n1)Login\n2)SignUp");
        int command=inp.nextInt();
        Request request = new Request();
        JSONObject json=new JSONObject();
        switch (command){
            case 1://Login
                System.out.println("Username :");
                String username=inp.next();
                System.out.println("Password :");
                String password= inp.next();
                json.put("Command","Login");
                json.put("username",username);
                json.put("password",password);
                request.setJson(json);//create request
                break;
            case 2://SignUp
                System.out.println("Username : ");
                username=inp.next();
                System.out.println("Password : ");
                password=inp.next();
                System.out.println("Email address :");
                String Email=inp.next();
                System.out.println("Enter an imagePath for your account profile");
                String ImagePath=inp.next();
                System.out.println("Birth Date : ");
                String date=inp.next();
                json = new JSONObject();
                json.put("Command","SignUp");
                json.put("username", username);
                json.put("password", password);
                json.put("Birthday",date);
                json.put("Email",Email);
                json.put("ImagePath",ImagePath);
                request.setJson(json);//create request
                break;
        }
        return request;
    }
    public static Request handle(Response response,PrintWriter out,BufferedReader in) throws Exception {
        Request request=new Request();
        JSONObject resp=response.getJson();
        switch (resp.getString("Status")){
            case "Successfully login","Successfully signup":
                System.out.println("WELCOME!");
                return ShowUserMenu();

            case "Fail login":
                return ShowMainMenu();

            case "Display songs","Show playlist":
                response.setJson(new JSONObject(in.readLine()));//receive response from server
                while (response.getJson().has("Music")) {
                    System.out.println(response.getJson().getString("Music"));;
                    receiveImage(response.getJson().getString("Cover"));
                    response.setJson(new JSONObject(in.readLine()));//receive response from server
                }
                System.out.println("1)Play song \n2)Back to menu");
                int command=inp.nextInt();
                switch (command) {
                    case 1://play song
                        System.out.println("Enter song title");
                        inp.nextLine();
                        String title=inp.nextLine();
                        System.out.println("Enter song artist");
                        String artist=inp.nextLine();
                        JSONObject json=new JSONObject();
                        json.put("Command","Play song");
                        json.put("Title",title);
                        json.put("Artist",artist);
                        request.setJson(json);
                        return request;
                    case 2://Back to menu
                        return ShowUserMenu();
                }
            case "Find song path":
                receiveFile(response.getJson());
                System.out.println("1)Pause \n2)Like \n3)Add to playlist");
                command=inp.nextInt();
                JSONObject json=new JSONObject();
                switch (command){
                    case 1://pause
                        player.pause();
                        ShowUserMenu();
                    case 2://like
                        json=new JSONObject();
                        json.put("Command","Like");
                        json.put("trackID",response.getJson().getString("trackID"));
                        request.setJson(json);
                        return request;
                    case 3://add to playlist
                        System.out.println("Enter playlist name:");
                        inp.nextLine();
                        String name=inp.nextLine();
                        json=new JSONObject();
                        json.put("Command","toPlaylist");
                        json.put("trackID",response.getJson().getInt("trackID"));
                        json.put("Name",name);
                        request.setJson(json);
                        return request;
                }
            case "pauseSong":
                return ShowUserMenu();
            case "Searching artist", "Searching title", "Searching album","Searching genre":
                response.setJson(new JSONObject(in.readLine()));//receive response from server
                while (response.getJson().has("Music")) {
                    System.out.println(response.getJson().getString("Music"));
                    receiveImage(response.getJson().getString("Cover"));
                    response.setJson(new JSONObject(in.readLine()));//receive response from server
                }
                return ShowUserMenu();
            case "View profile":
                response.setJson(new JSONObject(in.readLine()));//receive response from server
                while (response.getJson().has("user")) {
                    System.out.println(response.getJson().getString("user"));
                    response.setJson(new JSONObject(in.readLine()));//receive response from server
                }
                return ShowUserMenu();
            case "Logged out":
                System.out.println("Logged out successfully!:(");
                return ShowMainMenu();
            case "liked":
                System.out.println("Music liked.");
                return ShowUserMenu();
            case "created playlist":
                System.out.println("Playlist created.");
                return ShowUserMenu();
            case "added to playlist":
                System.out.println("Added to playlist.");
                return ShowUserMenu();
            case "Display playlists":
                response.setJson(new JSONObject(in.readLine()));//receive response from server
                while (response.getJson().has("Name")) {
                    System.out.println(response.getJson().getString("Name"));
                    response.setJson(new JSONObject(in.readLine()));//receive response from server
                }
                System.out.println("Enter playlist to show songs :");
                inp.nextLine();
                String name=inp.nextLine();
                json=new JSONObject();
                json.put("Command","Show playlist songs");
                json.put("Name",name);
                request.setJson(json);
                return request;

        }
        return request;
    }

    private static void receiveImage(String cover) throws IOException {
        String encodedImageData = cover;
        byte[] fileData = Base64.getDecoder().decode(encodedImageData);
        String filePath = "D:\\Uni\\Ap\\Spotify\\Spotify\\cover.jpg";
        Files.write(Paths.get(filePath), fileData);
        System.out.println("Image file is saved at: " + filePath);
    }

    public static Request ShowUserMenu() {
        System.out.println("Enter your command : \n1)Music library \n2)Search artist name\n" +
                "3)Search song title \n4)Search album title \n5)Search genre \n6)View personal profile page \n7)Create playlist \n8)View playlists \n9)Logout");
        int command=inp.nextInt();
        JSONObject json;
        Request request = new Request();
        switch (command){
            case 1://Music library
                json=new JSONObject();
                json.put("Command","Music library");
                request.setJson(json);//create request
                break;

            case 2://Search artist name
                inp.nextLine();
                System.out.println("Artist name :");
                String artist=inp.nextLine();
                json=new JSONObject();
                json.put("Command","Search artist");
                json.put("Artist",artist);
                request.setJson(json);//create request
                break;

            case 3://Search song title
                inp.nextLine();
                System.out.println("Song title :");
                String title=inp.nextLine();
                json=new JSONObject();
                json.put("Command","Search title");
                json.put("Title",title);
                request.setJson(json);//create request
                break;

            case 4://Search album title
                inp.nextLine();
                System.out.println("Album name:");
                String album=inp.nextLine();
                json=new JSONObject();
                json.put("Command","Search album");
                json.put("Album",album);
                request.setJson(json);//create request
                break;

            case 5://Search genre
                inp.nextLine();
                System.out.println("Enter genre:");
                String genre=inp.nextLine();
                json=new JSONObject();
                json.put("Command","Search genre");
                json.put("Genre",genre);
                request.setJson(json);//create request
                break;
            case 6://View personal profile page
                json=new JSONObject();
                json.put("Command","View profile");
                request.setJson(json);
                break;
            case 7://Create playlist
                System.out.println("Enter playlist name:");
                inp.nextLine();
                String playlist=inp.nextLine();
                json=new JSONObject();
                json.put("Command","Create playlist");
                json.put("Name",playlist);
                request.setJson(json);
                break;

            case 8://View playlists
                json=new JSONObject();
                json.put("Command","View playlists");
                request.setJson(json);//create request
                break;

            case 9://Logout
                json=new JSONObject();
                json.put("Command","Logout");
                request.setJson(json);
                break;
        }

        return request;
    }
    private static void receiveFile(JSONObject jsonObject) throws Exception {
        String encodedData = jsonObject.getString("musicData");
        byte[] fileData = Base64.getDecoder().decode(encodedData);
        String filePath = "D:\\Uni\\Ap\\Spotify\\Spotify\\file.mp3";//TODO
        Files.write(Paths.get(filePath), fileData);
        player.play(filePath);
    }
}