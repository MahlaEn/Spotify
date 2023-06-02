package Client;

import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.Scanner;


public class ClientMain {
    static Scanner in = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket("127.0.0.1", 2345);
            System.out.println("Connected to server!");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Request request;
            Response response;
            request = ShowMenu();//create request
            out.writeObject(request);//send request to server
            response = (Response) in.readObject();//receive response from server
            while (response != null) {
                request = handle(response);//create new request
                out.writeObject(request);////send request to server

                response = (Response) in.readObject();//receive response from server
            }
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static Request ShowMenu(){
        System.out.println("Enter your command :\n 1)Login\n 2)SignUp");
        int command=in.nextInt();
        Request request = new Request();
        switch (command){
            case 1://Login
                System.out.println("Username :");
                String username=in.next();
                System.out.println("Password :");
                String password= in.next();
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
    public static Request handle(Response response){
        Request request=new Request();
        JSONObject resp=response.getJson();
        switch (resp.getString("Status")){
            case "Successfully login":
                System.out.println("WELCOME!");
                break;
        }
        return request;
    }
}
