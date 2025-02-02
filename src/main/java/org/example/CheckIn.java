package org.example;

import java.sql.*;

import java.time.LocalDate;

public class CheckIn {

    public static void main(String[] args){
        System.out.println(isAccountValid(2));
    }

    public static boolean isAccountValid(int id)  {
        String url = "jdbc:mysql:aws://sysenghealthclub.cmrd2f4vkt0f.us-east-2.rds.amazonaws.com:3306/sysenghealthclub";
        String username = "nczap";
        String password = "group2healthclub";

        boolean isValid = false;

        try {
            Class.forName("software.aws.rds.jdbc.mysql.Driver");

            Connection con = DriverManager.getConnection(url, username, password);

            String getMemberExpiration = "SELECT expiration_date " +
                    "FROM hcmember " +
                    "WHERE member_id = " + id;

            Statement statement = con.createStatement();

            ResultSet expiration = statement.executeQuery(getMemberExpiration);

            if (!expiration.next()) {
                System.out.println("Member Does Not Exist!");
                return false;
            }

            Date expirationDateInitial = expiration.getDate("expiration_date");
            LocalDate expirationDate = expirationDateInitial.toLocalDate();

            System.out.println(expirationDate);

            LocalDate currentDate = LocalDate.now();

            System.out.println(currentDate);

            isValid = !(expirationDate.isBefore(currentDate));

            if (isValid) {
                String updateLastVisit = "UPDATE hcmember " +
                        "SET last_visit = ? " +
                        "WHERE member_id = ?;";

                PreparedStatement preparedStatement = con.prepareStatement(updateLastVisit);

                preparedStatement.setDate(1, Date.valueOf(currentDate));
                preparedStatement.setInt(2, id);

                int rowsUpdated = preparedStatement.executeUpdate();

                System.out.println(rowsUpdated);
            }
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return isValid;
    }
}
