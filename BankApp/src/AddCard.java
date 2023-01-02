
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;
import java.util.regex.Pattern;

public class AddCard extends JFrame {
    private JPanel panel1;
    private JButton backButton;
    private JTextField nrCardText;
    private JComboBox typComboBox;
    private JButton OKButton;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JLabel kartaLabel;
    private JLabel nrLabel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

    public AddCard(String client_number) {
        super("Add Cards");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StylesFunction();
        this.setLayout(new GridLayout());
        this.pack();
        this.setMinimumSize(new Dimension(600,400));
        this.setLocationRelativeTo(null);



        typComboBox.setModel(new DefaultComboBoxModel(new String[]{"Debetowa","Kredytowa"}));

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFrame main = new MainFrame(client_number);
                main.setVisible(true);
            }

        });
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String card_nr = nrCardText.getText();
                String card_type = (String) typComboBox.getItemAt(typComboBox.getSelectedIndex());
                if(card_nr.equals("")||!isNumeric(card_nr)||card_nr.length()!=16) {
                    JOptionPane.showMessageDialog(AddCard.this,"Podaj prawidłowy numer karty!");
                } else {
                    Random random = new Random();
                    int cvc_number = random.nextInt(900) + 100;
                    LocalDate today = LocalDate.now();

/**
 * INSERT QUERY
 *
 */
                    try {
                        Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                "root", "rootroot");

                        /** Query which is inserting new card to database **/
                        PreparedStatement addCard = (PreparedStatement) connection
                                .prepareStatement("INSERT INTO card VALUES(?, ?, ?, ?, 0, ?);");

                        addCard.setString(1, card_nr);
                        addCard.setString(2, String.valueOf(today.plusYears(5)));
                        addCard.setString(3, String.valueOf(cvc_number));
                        addCard.setString(4, card_type);
                        addCard.setString(5, client_number);

                        addCard.executeUpdate();

                    } catch (SQLException sqlException) {
                        // Error 12: Database is off or Your connection is invalid!
                        JOptionPane.showMessageDialog(AddCard.this, "Error 12!");
                    }/** END QUERY **/
                    JOptionPane.showMessageDialog(AddCard.this,"Prawidłowo dodano kartę!");

                    dispose();
                    MainFrame main = new MainFrame(client_number);
                    main.setVisible(true);
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
        kartaLabel.setForeground(foreground_white);
        nrLabel.setForeground(foreground_white);

        /** buttons **/
        OKButton.setBackground(main_green);
        OKButton.setForeground(dark_gray);
        backButton.setBackground(button_green);
        backButton.setForeground(foreground_white);
        /** margins **/
        panel1.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }

    private Pattern pattern = Pattern.compile("[0-9]+");
    private boolean isNumeric(String card_nr) {
        if (card_nr == null) {
            return false;
        }
        return pattern.matcher(card_nr).matches();
    }
}


