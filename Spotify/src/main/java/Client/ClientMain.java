package Client;

import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.rmi.UnknownHostException;
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                json.put("Username",username);
                json.put("Password",password);
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
    public static Request handle(Response response,PrintWriter out,BufferedReader in) throws SQLException, IOException {
        Request request=new Request();
        JSONObject resp=response.getJson();
        switch (resp.getString("Status")){
            case "Successfully login":
                System.out.println("WELCOME!");
                return ShowUserMenu();

            case "Fail login":
                return ShowMainMenu();

            case "Display songs":
                response.setJson(new JSONObject(in.readLine()));//receive response from server
                while (response.getJson().has("MusicData")) {
                    System.out.println(response.getJson().getString("MusicData"));
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
                System.out.println("1)Pause");
                command=inp.nextInt();
                switch (command){
                    case 1://pause
                        JSONObject json=new JSONObject();
                        json.put("Command","Pause song");
                        json.put("songPath",response.getJson().getString("songPath"));
                        request.setJson(json);
                        return request;
                }
            case "pauseSong":
                return ShowUserMenu();
            case "Searching artist", "Searching title", "Searching album","Searching genre":
                response.setJson(new JSONObject(in.readLine()));//receive response from server
                while (response.getJson().has("Music")) {
                    System.out.println(response.getJson().getString("Music"));
                    response.setJson(new JSONObject(in.readLine()));//receive response from server
                }
                return ShowUserMenu();

        }
        return request;
    }
    public static Request ShowUserMenu() throws SQLException {
        System.out.println("Enter your command : \n1)Music library \n2)Search artist name\n" +
                "3)Search song title \n4)Search album title \n5)Search genre \n6)View personal profile page");
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
        }

        return request;
    }

}