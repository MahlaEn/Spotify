package Server;

import Shared.Request;
import Shared.Response;
import org.json.JSONObject;

import java.sql.*;

public class DataBase {
    static Statement statement;
    static private Connection connection;
    public DataBase() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AP","postgres","12345678");
            statement = connection.createStatement();
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

    public static Response handle(Request request) throws SQLException {
        Response response = new Response();
        JSONObject json=request.getJson();
        switch (json.getString("Command")){
            case "Login":
                String username = json.getString("Username");
                String password = json.getString("Password");
                ResultSet resultSet=query("SELECT * FROM \"Spotify\".\"User\" \n");
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

            case "SignUp":


                return response;

            case "Music library":
                json.put("Status","songs were displayed");
                response.setJson(json);
                return response;
        }

        return response;
    }
}