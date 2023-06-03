package Server;

import Database.DataBase;
import Database.ImportData;
import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerMain {

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    public static void main(String[] args) throws IOException, SQLException {
        ServerMain server = new ServerMain(2345);
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

    private class ClientHandler extends Thread{
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }
        public void run() {
            Response response;
            try{
                Request request = new Request();
                request.setJson(new JSONObject( in.readLine()));//receive request from client
                while(request.getJson()!=null){
                    response = handle(request,out);//create new response
                    if(response.getJson()!=null) {
                        out.println(response.getJson().toString());//send response to client
                    }
                    request.setJson(new JSONObject( in.readLine()));//receive request from client
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try {
                    socket.close();
                    clients.remove(this);
                    System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static Response handle(Request request,PrintWriter out) throws SQLException {
        Response response=new Response();
        JSONObject req=request.getJson();
        DataBase DB=new DataBase();
        switch (req.getString("Command")){
            case "Login":
                response= DB.handle(request);
                return response;

            case "SignUp":
                response= DB.handle(request);
                return response;

            case "Music library":
                ShowMusics(out);
                JSONObject res=new JSONObject();
                res.put("Status","Musics was showed");
                response.setJson(res);
                return response;
        }
        return response;
    }
    public static void ShowMusics(PrintWriter out) throws SQLException {
        DataBase DB=new DataBase();
        ResultSet resultSet=DB.query("SELECT * FROM \"Spotify\".\"Music\" \n");
        JSONObject res=new JSONObject();
        res.put("Status","Display songs");
        out.println(res);
        while(resultSet.next()){
            JSONObject json=new JSONObject();
            json.put("MusicData",toString(resultSet));
            out.println(json);
        }
    }
    public static String toString(ResultSet resultSet) throws SQLException {
        return (
                "TrackID: "+resultSet.getInt("TrackID") + "\nTitle: "+ resultSet.getString("Title") +
                        "\nArtist: " + resultSet.getString("Artist") + "\nAlbum: " + resultSet.getString("Album") +
                        "\nGenre: " + resultSet.getString("Genre") +
                        "\nDuration: " + resultSet.getString("Duration") + "\nRelease Date: " + resultSet.getString("ReleaseDate") +
                        "\nPopularity: " + resultSet.getDouble("Popularity") +
                        "\n____________________________________"
        );
    }
}