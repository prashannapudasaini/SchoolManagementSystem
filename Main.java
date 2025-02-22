import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/mydatabase?useSSL=false&serverTimezone=UTC";
    private static final String USER = "your_username"; // Replace with your actual MySQL username
    private static final String PASSWORD = "your_password"; // Replace with your actual MySQL password

    public static void main(String[] args) {
        // Load MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Please add the JDBC connector JAR.");
            e.printStackTrace();
            return;
        }

        // Connect to the database
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            System.out.println("Connected to the database successfully!");

            // Query to fetch data from the "announcement" table
            String query = "SELECT * FROM announcement";
            ResultSet rs = stmt.executeQuery(query);

            // Process results
            while (rs.next()) {
                // Replace "column_name" with actual column names from the "announcement" table
                System.out.println("Announcement ID: " + rs.getInt("id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Message: " + rs.getString("message"));
                System.out.println("Date: " + rs.getString("date"));
                System.out.println("---------------------------");
            }

            // Close ResultSet
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
