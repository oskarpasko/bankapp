import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JPanel panel1;
    private JTextField client_number;
    private JPasswordField client_password;
    private JButton loginButton;
    private JPanel panelLogin;
    private JLabel witajLabel;
    private JLabel klientLabel;
    private JLabel passLabel;

    Button bt = new Button("buttonn");
    // URL for connection with database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BankApp";

    public static void main(String[] args) {
        LoginFrame panel = new LoginFrame();
        panel.setVisible(true);
    }

    public LoginFrame() {
        super("LoginFrame");
        StylesFunction();
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLayout(null);
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
                    // Error 12: Database is off or Your connection is invalid!
                    JOptionPane.showMessageDialog(LoginFrame.this, "Error 12!");
                }
            }
        });
    }

    private void StylesFunction() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)size.getWidth();
        int height = (int)size.getHeight();
        this.setLocation(width/2,height/2);

        bt.setPreferredSize(new Dimension(200,10));
        bt.setBackground(new Color(108, 220, 96));
        /** login button style **/
            loginButton.setBackground(new Color(108, 220, 96));
            loginButton.setPreferredSize(new Dimension(200,10));
        /** text field style **/
        panel1.setBackground(new Color(42, 42, 42));
        panelLogin.setBackground(new Color(42, 42, 42));
        /** font colors **/
        witajLabel.setForeground(new Color(108, 220, 96));
        klientLabel.setForeground(new Color(255, 255, 255));
        passLabel.setForeground(new Color(255, 255, 255));
        /** margin **/
        Border border = witajLabel.getBorder();
        Border margin = new EmptyBorder(0,100,100,100);
        witajLabel.setBorder(new CompoundBorder(border, margin));

    }

}

