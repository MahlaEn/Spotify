package org.example;

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
        JSONObject req=request.getJson();
        switch (req.getString("Command")){
            case "Login":
                String username = req.getString("Username");
                String password = req.getString("Password");
                ResultSet resultSet=query("SELECT * FROM \"Steam\".\"accounts\" \n");
                while(resultSet.next()){
                    if(resultSet.getString("Username").equals(username) && resultSet.getString("Password").equals(password)){
                        req.put("Status","Successfully login");
                        response.setJson(req);
                        return response;
                    }
                }
                req.put("Status","Fail login");
                response.setJson(req);
                return response;

            case "SignUp":

        }

        return response;
    }
}