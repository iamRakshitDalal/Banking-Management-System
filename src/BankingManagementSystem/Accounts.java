package BankingManagementSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private Scanner scanner;
    Accounts(Connection connection,Scanner scanner){
        this.connection = connection;
        this.scanner=scanner;
    }
    public long openAcount(String email){
        if(!account_exist(email)){
            String userQuery = "Select fullName from user where email=?";
            String accountsQurery = "insert into accounts(account_no,fullName,email,balance,security_pin) values(?,?,?,?,?)";
            try {
                PreparedStatement preparedStatementUserTable = connection.prepareStatement(userQuery);
                preparedStatementUserTable.setString(1, email);
                ResultSet resultSet =preparedStatementUserTable.executeQuery();
                String name="";
                while(resultSet.next()){
                    name =  resultSet.getString(1);
                }
                scanner.nextLine();
                System.out.print("Enter Initial Amount: ");
                double balance = scanner.nextDouble();
                while (balance<3000) {
                    System.out.print("Initial Amount must be greater than 3000\nEnter Initial Amount: ");
                    balance= scanner.nextDouble();
                  }
                scanner.nextLine();
                System.out.print("Enter Security Pin: ");
                String security_pin = scanner.nextLine();
                long accountNo = generateAccountNumber();

                PreparedStatement preparedStatementAccountsTable = connection.prepareStatement(accountsQurery);
                preparedStatementAccountsTable.setLong(1,accountNo );
                preparedStatementAccountsTable.setString(2, name);
                preparedStatementAccountsTable.setString(3, email);
                preparedStatementAccountsTable.setDouble(4, balance);
                preparedStatementAccountsTable.setString(5, security_pin);
                int rowAffected = preparedStatementAccountsTable.executeUpdate();
                if(rowAffected > 0){
                    return accountNo;
                }
                else{
                    throw new RuntimeException("Account Creation failed!!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account Already Exist");
    }


    public long getAccountNumber(String email){
        String query = "select account_no from accounts where email=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Account Number Doesn't Exist!"); 
    }

    private long generateAccountNumber() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT account_no from accounts ORDER BY account_no DESC LIMIT 1");
            if (resultSet.next()) {
                long last_account_number = resultSet.getLong("account_no");
                return last_account_number+1;
            } else {
                return 10000100;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 10000100;
    }
    
    public boolean account_exist(String email){
        String query = "SELECT account_no from accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
