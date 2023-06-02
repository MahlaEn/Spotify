package Server;

import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerMain {

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerMain server = new ServerMain(2345);
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
                while(request!=null){
                    response = handle(request);//create new response
                    System.out.println(request.getJson());
                    out.println(response.getJson().toString());//send response to client

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
    public static Response handle(Request request) throws SQLException {
        Response response=new Response();
        JSONObject req=request.getJson();
        DataBase DB=new DataBase();
        switch (req.getString("Command")){
            case "Login":
                response= DB.handle(request);
                return response;

            case "SignUp":


                return response;

            case "Music library":
                response = DB.handle(request);
                return response;
        }
        return response;
    }
}