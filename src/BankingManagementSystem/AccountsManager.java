package BankingManagementSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;



public class AccountsManager {
    private Connection connection;
    private Scanner scanner;
    AccountsManager(Connection connection,Scanner scanner){
        this.connection = connection;
        this.scanner=scanner;
    }
    public void criditMoney(long accountNumber)throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if(accountNumber != 0) {
                String query = "select * from accounts where account_no = ? and security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String setQuery = "update accounts set balance = balance +? where account_no = ? ";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(setQuery);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, accountNumber);
                    int affectedRows = preparedStatement1.executeUpdate();
                    if(affectedRows > 0){
                        System.out.println("Rs."+amount+" credited Successfully");
                        connection.commit();;
                        connection.setAutoCommit(true);
                        checkBalance(accountNumber);
                        return;
                    }
                    else{
                        System.out.println("Transaction Failed!");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void debitMoney(long accountNumber)throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if(accountNumber != 0) {
                String query = "select * from accounts where account_no = ? and security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount<=current_balance){
                        String debit_query = "update accounts set balance = balance - ? where account_no = ? ";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, accountNumber);
                        int rowsAffected = preparedStatement1.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Rs."+amount+" debited Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            checkBalance(accountNumber);
                            return;
                        } else {
                            System.out.println("Transaction Failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else{
                        System.out.println("Insufficient Balance!");
                    }                    
                }else{
                    System.out.println("Invalid Security Pin!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void transferMoney(long sender_account_number)throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiver_account_number = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if(sender_account_number!=0 && receiver_account_number!=0){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_no = ? AND security_pin = ? ");
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount<=current_balance){

                        // Write debit and credit queries
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_no = ?";
                        String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_no = ?";

                        // Debit and Credit prepared Statements
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);

                        // Set Values for debit and credit prepared statements
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);
                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction Successful!");
                            System.out.println("Rs."+amount+" Transferred Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            checkBalance(sender_account_number);
                            return;
                        } else {
                            System.out.println("Transaction Failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else{
                        System.out.println("Insufficient Balance!");
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                }
            }else{
                System.out.println("Invalid account number");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void getBalance(long accountNumber) throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        String query = "select balance from accounts where account_no =? and  security_pin =?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, security_pin);
            ResultSet resultset = preparedStatement.executeQuery();
            if (resultset.next()) {
                double balance = resultset.getDouble(1);
                System.out.println(balance);
            }
            else{
                System.out.println("Invalid Pin!");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void checkBalance(long accountNumber) throws SQLException{
        
        String query = "select balance from accounts where account_no =? ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, accountNumber);
            ResultSet resultset = preparedStatement.executeQuery();
            if (resultset.next()) {
                double balance = resultset.getDouble(1);
                System.out.println("your current balance is "+balance);
            }
            else{
                System.out.println("Invalid Pin!");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
