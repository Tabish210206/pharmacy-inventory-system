import java.sql.*;

public class JDBC {
    public static  Connection getConnection(){

         String url = "jdbc:mysql://localhost:3306/product";
         String user = "root";
         String pass = "admin123";

         Connection con = null;

        try {
             con = DriverManager.getConnection(url,user,pass);
            }
        catch (SQLException ex) {
            System.out.println(ex);
        }
      return con;
    }
}
