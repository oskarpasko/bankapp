import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JPanel panel1;
    private JTextField client_number;
    private JPasswordField client_password;
    private JButton loginButton;
    private JLabel witajLabel;
    private JLabel klientLabel;
    private JPanel panelLogin;
    private JLabel passLabel;
    private JButton newUserButton;
    private JPanel buttonsPanel;

    // URL for connection with database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

    public static void main(String[] args) {
        LoginFrame panel = new LoginFrame();
        panel.setVisible(true);
    }

    public LoginFrame() {
        super("LoginFrame");
        StylesFunction();
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout());
        this.pack();
        /** center window **/
        this.setLocationRelativeTo(null);

        /** Login Listener **/
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginQuery();
            }
        });

        newUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                NewUser newUser = new NewUser();
                newUser.setVisible(true);
            }
        });
    }

/**
 * Function with mechanism to logging in
 */
    private void LoginQuery()
    {
        String client_nr = client_number.getText();
        String client_pass = client_password.getText();

        try {
            Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                    "root", "rootroot");

            PreparedStatement st = (PreparedStatement) connection
                    .prepareStatement("SELECT client_nr FROM bankapp.client WHERE client_nr=? AND client_password=?");

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
            // Error 12: Database is off or Your connection is invalid!
            JOptionPane.showMessageDialog(LoginFrame.this, "Error 12!");
        }
    }/** END FUNCTION **/

    private void StylesFunction() {
        Color main_green = new Color(108, 220, 96);
        Color foreground_white = new Color(222, 222, 222);
        Color dark_gray = new Color(42, 42, 42);
        
        /** login button style **/
        loginButton.setBackground(main_green);
        newUserButton.setBackground(main_green);

        /** text field style **/
        panel1.setBackground(dark_gray);
        panelLogin.setBackground(dark_gray);
        buttonsPanel.setBackground(dark_gray);

        /** font colors **/
        witajLabel.setForeground(main_green);
        klientLabel.setForeground(foreground_white);
        passLabel.setForeground(foreground_white);
        /** margin **/
//        Border border = witajLabel.getBorder();
//        Border margin = new EmptyBorder(0,100,100,100);
//        witajLabel.setBorder(new CompoundBorder(border, margin));
        panelLogin.setBorder(BorderFactory.createEmptyBorder(40,40,40,40));
    }

}
