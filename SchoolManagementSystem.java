package testing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

// Database Connection
class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Main Menu
public class SchoolManagementSystem extends JFrame {
    public SchoolManagementSystem() {
        setTitle("School Management System");
        setLayout(new GridLayout(3, 2, 10, 10));
        getContentPane().setBackground(new Color(40, 40, 40));

        JButton adminButton = createButton("Admin Section");
        JButton teacherButton = createButton("Teacher Section");
        JButton studentButton = createButton("Student Section");
        JButton accountButton = createButton("Account Section");
        JButton teacherSignupButton = createButton("Teacher Signup");
        JButton studentSignupButton = createButton("Student Signup");

        adminButton.addActionListener(e -> new LoginSection("admin"));
        teacherButton.addActionListener(e -> new LoginSection("teacher"));
        studentButton.addActionListener(e -> new LoginSection("student"));
        accountButton.addActionListener(e -> new AccountDashboard());
        teacherSignupButton.addActionListener(e -> new SignupSection("teacher"));
        studentSignupButton.addActionListener(e -> new SignupSection("student"));

        add(adminButton);
        add(teacherButton);
        add(studentButton);
        add(accountButton);
        add(teacherSignupButton);
        add(studentSignupButton);

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(34, 167, 240));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 70));
        button.setBorder(new LineBorder(Color.WHITE, 2, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 123, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(34, 167, 240));
            }
        });
        return button;
    }

    public static void main(String[] args) {
        new SchoolManagementSystem();

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Database connection successful!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class AccountDashboard extends JFrame {
        public AccountDashboard() {
            setTitle("Account Dashboard");
            setLayout(new GridLayout(2, 1, 10, 10));
            getContentPane().setBackground(new Color(255, 255, 255));

            JButton viewProfileButton = createButton("View Profile");
            JButton changePasswordButton = createButton("Change Password");

            viewProfileButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Profile Information"));
            changePasswordButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Change Password"));

            add(viewProfileButton);
            add(changePasswordButton);

            setSize(300, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        private JButton createButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(0, 123, 255));
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(200, 50));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return button;
        }
    }
}

// Login Section
class LoginSection extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String role;

    public LoginSection(String role) {
        this.role = role;
        setTitle(role.toUpperCase() + " Login");
        setLayout(new FlowLayout());
        getContentPane().setBackground(new Color(255, 255, 255));

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = createButton("Login");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);

        loginButton.addActionListener(e -> authenticateUser());

        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 40));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Welcome, " + role + "!");
                    openDashboard(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openDashboard(String username) {
        switch (role) {
            case "admin": new AdminDashboard(); break;
            case "teacher": new TeacherDashboard(username); break;
            case "student": new StudentDashboard(username); break;
        }
    }

    private static class AdminDashboard extends JFrame {
        public AdminDashboard() {
            setTitle("Admin Dashboard");
            setLayout(new GridLayout(3, 1, 10, 10));
            getContentPane().setBackground(new Color(255, 255, 255));

            JButton viewUsersButton = createButton("View Users");
            JButton addUserButton = createButton("Add User");
            JButton deleteUserButton = createButton("Delete User");

            viewUsersButton.addActionListener(e -> viewUsers());
            addUserButton.addActionListener(e -> addUser());
            deleteUserButton.addActionListener(e -> deleteUser());

            add(viewUsersButton);
            add(addUserButton);
            add(deleteUserButton);

            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        private JButton createButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(0, 123, 255));
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(200, 50));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return button;
        }

        private void viewUsers() {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    String query = "SELECT * FROM users";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    StringBuilder users = new StringBuilder("Users:\n");
                    while (rs.next()) {
                        users.append(rs.getString("username")).append(" - ").append(rs.getString("role")).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, users.toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void addUser() {
            String username = JOptionPane.showInputDialog("Enter Username:");
            String password = JOptionPane.showInputDialog("Enter Password:");
            String role = JOptionPane.showInputDialog("Enter Role (admin/teacher/student):");
            if (username != null && password != null && role != null) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    if (conn != null) {
                        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, username);
                        stmt.setString(2, password);
                        stmt.setString(3, role);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "User Added Successfully!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void deleteUser() {
            String username = JOptionPane.showInputDialog("Enter Username to Delete:");
            if (username != null) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    if (conn != null) {
                        String query = "DELETE FROM users WHERE username = ?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, username);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "User Deleted Successfully!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

// Signup Section
class SignupSection extends JFrame {
    private JTextField nameField, usernameField;
    private JPasswordField passwordField;
    private String role;

    public SignupSection(String role) {
        this.role = role;
        setTitle(role.toUpperCase() + " Signup");
        setLayout(new FlowLayout());
        getContentPane().setBackground(new Color(255, 255, 255));

        nameField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton signupButton = createButton("Signup");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(signupButton);

        signupButton.addActionListener(e -> signupUser());

        setSize(300, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 40));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void signupUser() {
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (name != null && username != null && password != null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, role);
                    stmt.executeUpdate();

                    if (role.equals("teacher")) {
                        query = "INSERT INTO teachers (name, username) VALUES (?, ?)";
                    } else if (role.equals("student")) {
                        query = "INSERT INTO students (name, username) VALUES (?, ?)";
                    }
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, name);
                    stmt.setString(2, username);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Signup Successful!");
                    dispose();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

// Student Dashboard
class StudentDashboard extends JFrame {
    private String username;

    public StudentDashboard(String username) {
        this.username = username;
        setTitle("Student Dashboard");
        setLayout(new GridLayout(4, 1, 10, 10));
        getContentPane().setBackground(new Color(255, 255, 255));

        JButton profileButton = createButton("Profile Information");
        JButton settingsButton = createButton("Profile Settings");
        JButton assignmentsButton = createButton("View Assignments");
        JButton announcementsButton = createButton("View Announcements");

        profileButton.addActionListener(e -> viewProfile());
        settingsButton.addActionListener(e -> new ProfileSettings(username, "student"));
        assignmentsButton.addActionListener(e -> viewAssignments());
        announcementsButton.addActionListener(e -> viewAnnouncements());

        add(profileButton);
        add(settingsButton);
        add(assignmentsButton);
        add(announcementsButton);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void viewProfile() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                String query = "SELECT * FROM students WHERE username = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String profile = "ID: " + rs.getInt("id") + "\nName: " + rs.getString("name") + "\nUsername: " + rs.getString("username") + "\nResult: " + rs.getString("result");
                    JOptionPane.showMessageDialog(this, profile);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewAssignments() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                String query = "SELECT * FROM assignments";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                StringBuilder assignments = new StringBuilder("Assignments:\n");
                while (rs.next()) {
                    assignments.append(rs.getString("title")).append(" - ").append(rs.getString("description")).append("\n");
                }
                JOptionPane.showMessageDialog(this, assignments.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewAnnouncements() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                String query = "SELECT * FROM announcements";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                StringBuilder announcements = new StringBuilder("Announcements:\n");
                while (rs.next()) {
                    announcements.append(rs.getString("title")).append(" - ").append(rs.getString("description")).append("\n");
                }
                JOptionPane.showMessageDialog(this, announcements.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Teacher Dashboard
class TeacherDashboard extends JFrame {
    private String username;

    public TeacherDashboard(String username) {
        this.username = username;
        setTitle("Teacher Dashboard");
        setLayout(new GridLayout(5, 1, 10, 10));
        getContentPane().setBackground(new Color(255, 255, 255));

        JButton profileButton = createButton("Profile Information");
        JButton settingsButton = createButton("Profile Settings");
        JButton uploadResultButton = createButton("Upload Result");
        JButton postAssignmentButton = createButton("Post Assignment");
        JButton postAnnouncementButton = createButton("Post Announcement");

        profileButton.addActionListener(e -> viewProfile());
        settingsButton.addActionListener(e -> new ProfileSettings(username, "teacher"));
        uploadResultButton.addActionListener(e -> uploadResult());
        postAssignmentButton.addActionListener(e -> postAssignment());
        postAnnouncementButton.addActionListener(e -> postAnnouncement());

        add(profileButton);
        add(settingsButton);
        add(uploadResultButton);
        add(postAssignmentButton);
        add(postAnnouncementButton);

        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void viewProfile() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                String query = "SELECT * FROM teachers WHERE username = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String profile = "ID: " + rs.getInt("id") + "\nName: " + rs.getString("name") + "\nUsername: " + rs.getString("username");
                    JOptionPane.showMessageDialog(this, profile);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void uploadResult() {
        String studentId = JOptionPane.showInputDialog("Enter Student ID:");
        String result = JOptionPane.showInputDialog("Enter Result:");
        if (studentId != null && result != null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    String query = "UPDATE students SET result = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, result);
                    stmt.setInt(2, Integer.parseInt(studentId));
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Result Updated Successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void postAssignment() {
        String title = JOptionPane.showInputDialog("Enter Assignment Title:");
        String description = JOptionPane.showInputDialog("Enter Assignment Description:");
        if (title != null && description != null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    String query = "INSERT INTO assignments (title, description) VALUES (?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, title);
                    stmt.setString(2, description);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Assignment Posted Successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void postAnnouncement() {
        String title = JOptionPane.showInputDialog("Enter Announcement Title:");
        String description = JOptionPane.showInputDialog("Enter Announcement Description:");
        if (title != null && description != null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    String query = "INSERT INTO announcements (title, description) VALUES (?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, title);
                    stmt.setString(2, description);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Announcement Posted Successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

// Profile Settings
class ProfileSettings extends JFrame {
    private String username;
    private String role;

    public ProfileSettings(String username, String role) {
        this.username = username;
        this.role = role;
        setTitle("Profile Settings");
        setLayout(new GridLayout(3, 1, 10, 10));
        getContentPane().setBackground(new Color(255, 255, 255));

        JButton changeUsernameButton = createButton("Change Username");
        JButton changePasswordButton = createButton("Change Password");

        changeUsernameButton.addActionListener(e -> changeUsername());
        changePasswordButton.addActionListener(e -> changePassword());

        add(changeUsernameButton);
        add(changePasswordButton);

        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void changeUsername() {
        String newUsername = JOptionPane.showInputDialog("Enter New Username:");
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    String query = "UPDATE " + role + "s SET username = ? WHERE username = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, newUsername);
                    stmt.setString(2, username);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Username Updated Successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void changePassword() {
        String newPassword = JOptionPane.showInputDialog("Enter New Password:");
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    String query = "UPDATE users SET password = ? WHERE username = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, newPassword);
                    stmt.setString(2, username);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Password Updated Successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}