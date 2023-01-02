import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class NewUser extends JFrame {

    private JPanel panel1;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JButton backButton;
    private JTextField numberField;
    private JTextField nameField;
    private JTextField surnameField;
    private JPasswordField passwordField;
    private JButton OKButton;
    private JLabel klientLabel;
    private JLabel nameLabel;
    private JLabel surnameLabel;
    private JLabel passLabel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

    public NewUser() {
        super("Register new User");
        StylesFunction();
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout());
        this.pack();
        /** center window **/
        this.setLocationRelativeTo(null);



        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            }
        });

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nr_klient = numberField.getText();
                String password = String.valueOf(passwordField.getPassword());
                String name = nameField.getText();
                String surname = surnameField.getText();

                if(nr_klient.equals("")||password.equals("")||name.equals("")||surname.equals("")) {
                    JOptionPane.showMessageDialog(NewUser.this,"Musisz podać dane!");
                } else {
                    if(!isNumeric(nr_klient)||nr_klient.length()!=6) {
                        JOptionPane.showMessageDialog(NewUser.this,"Nr klienta musi składać się tylko z 6 liczb!");
                    } else {
                        if(!isLetters(name)||!isLetters(surname)) {
                            JOptionPane.showMessageDialog(NewUser.this,"Imie i Nazwisko musi zawierać tylko litery!");
                        } else {
                            name = name.toLowerCase();
                            surname = surname.toLowerCase();
                            name = name.substring(0, 1).toUpperCase() + name.substring(1);
                            surname = surname.substring(0, 1).toUpperCase() + surname.substring(1);

/**
 * INSERT USER QUERY
 *
 */
                            try {
                                Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                        "root", "rootroot");

                                /** Query which is updating balance on card adding value **/
                                PreparedStatement insertUser = (PreparedStatement) connection
                                        .prepareStatement("INSERT INTO client values(?, ?, ?, ?);");

                                insertUser.setString(1, nr_klient);
                                insertUser.setString(2, password);
                                insertUser.setString(3, name);
                                insertUser.setString(4, surname);

                                insertUser.executeUpdate();

                            } catch (SQLException sqlException) {
                                // Error 12: Database is off or Your connection is invalid!
                                JOptionPane.showMessageDialog(NewUser.this, "Error 12!");
                            }/** END QUERY **/

                            JOptionPane.showMessageDialog(NewUser.this,"Poszło!" +
                                    "\nImie: "+name+"\nNazwisko: "+surname+"\nHasło"+password+"\n nr_klienta:"+nr_klient);
                        }
                    }
                }

            }
        });
    }

    private void StylesFunction() {
        Color main_green = new Color(108, 220, 96);
        Color foreground_white = new Color(222, 222, 222);
        Color dark_gray = new Color(42, 42, 42);
        Color button_green = new Color(129, 161, 125);

        /** background setter **/
        panel1.setBackground(dark_gray);
        mainPanel.setBackground(dark_gray);
        formPanel.setBackground(dark_gray);

        /** foreground setter **/
        nameLabel.setForeground(foreground_white);
        surnameLabel.setForeground(foreground_white);
        passLabel.setForeground(foreground_white);
        klientLabel.setForeground(foreground_white);

        /** buttons **/
        OKButton.setBackground(main_green);
        OKButton.setForeground(dark_gray);
        backButton.setBackground(button_green);
        backButton.setForeground(foreground_white);

        /** margins **/
        panel1.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        klientLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        passLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        surnameLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

    }
    private Pattern pattern = Pattern.compile("[0-9]+");
    private boolean isNumeric(String card_nr) {
        if (card_nr == null) {
            return false;
        }
        return pattern.matcher(card_nr).matches();
    }

    private Pattern letter_pattern = Pattern.compile("^[a-zA-Z]*$");
    private boolean isLetters(String words) {
        if (words == null) {
            return false;
        }
        return letter_pattern.matcher(words).matches();
    }
}