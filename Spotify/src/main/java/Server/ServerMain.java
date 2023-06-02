package Server;

import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(socket,out,in);
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
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket, ObjectOutputStream out, ObjectInputStream in) throws IOException {
            this.socket = socket;
            this.out = out;
            this.in = in;
        }
        public void run() {
            Response response;
            try{
                Request request=(Request)in.readObject();//receive request from client
                while(request!=null){
                    response = handle(request);//create new response
                    out.writeObject(response);//send response to client

                    request=(Request)in.readObject();//receive request from client
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
        switch (req.getString("Command")){
            case "Login":
                response= DataBase.handle(request);
                return response;

            case "SignUp":

        }
        return response;
    }
}