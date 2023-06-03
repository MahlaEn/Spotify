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
        String username,password;
        ResultSet resultSet;
        switch (json.getString("Command")){
            case "Login":
                username = json.getString("Username");
                password = json.getString("Password");
                resultSet=query("SELECT * FROM \"Spotify\".\"User\" \n");
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
                username=json.getString("username");
                password=json.getString("password");
                resultSet=query("SELECT * FROM \"Spotify\".\"User\" WHERE \"Username\" = " + "'" + username + "'");
                if(resultSet.next()) {
                    json.put("Status", "Fail signup");
                    response.setJson(json);
                    return response;
                }
                json.put("Status","Successfully signup");
                response.setJson(json);
                String ID="1";
                String email = json.getString("Email");
                String imagePath = json.getString("ImagePath");
                if(imagePath.charAt(0)=='"'){
                    imagePath=imagePath.substring(1,imagePath.length()-1);//Remove additional character
                }
                String playlistID="1";
                String date=json.getString("Birthday");
                String sql = "INSERT INTO\"Spotify\".\"User\" VALUES ('" + ID + "','" + username + "', '" +
                        email + "', '" + password + "','" + imagePath + "', '" + date + "', '" + playlistID + "')";

                query(sql);
                return response;

            case "Music library":
                json.put("Status","songs were displayed");
                response.setJson(json);
                return response;
        }

        return response;
    }
}