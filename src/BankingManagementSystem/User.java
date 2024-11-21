package BankingManagementSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class User {
    private Connection connection;
    private Scanner scanner;
    User(Connection connection,Scanner scanner){
        this.connection = connection;
        this.scanner=scanner;
    }
    public void register() throws SQLException{
        scanner.nextLine();
        System.out.print("Full Name: ");
        String full_name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if(userExist(email)){
            System.out.println("User Already Exists for this Email Address!!");
            return;
        }
        String query = "insert into user(fullName,email,password) values(?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,full_name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Registration Successfull!");
            } else {
                System.out.println("Registration Failed!");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }          
    }

    public String login()throws SQLException{ 
        scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        String query = "select * from user where email = ? and password = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet =  preparedStatement.executeQuery();
            if (resultSet.next()) {
                return  email;
            }
            else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
    public boolean userExist(String email) throws SQLException{
        try {
            String query = "select * from user where email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet= preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
            else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}