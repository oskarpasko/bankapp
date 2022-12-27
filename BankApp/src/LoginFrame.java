import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JPanel panel1;
    private JTextField client_number;
    private JPasswordField client_password;
    private JButton loginButton;

    // URL for connection with database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BankApp";

    public static void main(String[] args) {
        LoginFrame panel = new LoginFrame();
        panel.setVisible(true);
    }

    public LoginFrame() {
        super("LoginFrame");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String client_nr = client_number.getText();
                String client_pass = client_password.getText();

                try {
                    Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                            "root", "rootroot");

                    PreparedStatement st = (PreparedStatement) connection
                            .prepareStatement("SELECT client_nr FROM BankApp.Client WHERE client_nr=? AND client_password=?");

                    st.setString(1, client_nr);
                    st.setString(2, client_pass);
                    ResultSet rs = st.executeQuery();
                    if (rs.next()) {
                        // Działania po odnalezieniu usera z podanym loginem i hasłem
                        MainFrame mainframe = new MainFrame(rs.getString("client_nr"));
                        dispose();
                        mainframe.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Wrong Username & Password");
                    }
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        });
    }
}
