import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class WyplataFrame extends JFrame {
    private JPanel panel1;
    private JButton OKButton;
    private JButton backButton;
    private JRadioButton a50RadioButton;
    private JRadioButton a100RadioButton;
    private JRadioButton a200RadioButton;
    private JRadioButton a300RadioButton;
    private JRadioButton a500RadioButton;
    private JRadioButton wlasnaRadio;
    private JTextField wlasnaText;
    private int value;

    // URL for connection with database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BankApp";

    public WyplataFrame(String client_nr) {
        super("WyplataFrame");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);
        ButtonGroup bg = new ButtonGroup();
        bg.add(a50RadioButton);
        bg.add(a100RadioButton);
        bg.add(a200RadioButton);
        bg.add(a300RadioButton);
        bg.add(a500RadioButton);
        bg.add(wlasnaRadio);

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(wlasnaRadio.isSelected()) {
                    wlasnaText.setEnabled(true);
                }
                else {
                    wlasnaText.setText("");
                    wlasnaText.setEnabled(false);
                }
            }
        };

        a50RadioButton.addActionListener(listener);
        a100RadioButton.addActionListener(listener);
        a200RadioButton.addActionListener(listener);
        a300RadioButton.addActionListener(listener);
        a500RadioButton.addActionListener(listener);
        wlasnaRadio.addActionListener(listener);

        OKButton.addActionListener(new ActionListener() {
                                       @Override
            public void actionPerformed(ActionEvent e) {
                if (a50RadioButton.isSelected()) {
                    value = 50;}
                else if (a100RadioButton.isSelected()) {
                    value = 100;}
                else if (a200RadioButton.isSelected()) {
                    value = 200;}
                else if (a300RadioButton.isSelected()) {
                    value = 300;}
                else if (a500RadioButton.isSelected()) {
                    value = 500;
                    }
                else if (wlasnaRadio.isSelected()) {
                    if (wlasnaRadio.getText().equals("")) {
                        JOptionPane.showMessageDialog(WyplataFrame.this, "Błędna kwota!");}
                        else {
                            value = Integer.parseInt(wlasnaText.getText());
                        }}

                   if (value >= 10 && value % 10 == 0) {



/**
* Download cards data QUERY
 *
*/
                   try {
                       Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                               "root", "rootroot");

                       /** Query which return count of client's cards **/
                       PreparedStatement countRows = (PreparedStatement) connection
                               .prepareStatement("SELECT count(card_nr) as countRows FROM BankApp.Card WHERE client_nr =?");

                       countRows.setString(1, client_nr);
                       ResultSet countRowsResult = countRows.executeQuery();
                       int rows = 0;

                       // download how many rows/cards have our client
                       if(countRowsResult.next()) rows = countRowsResult.getInt(1);
                       // END QUERY

                       /** Query which is getting number, type and balnce client's cards **/
                       PreparedStatement cardInfo = (PreparedStatement) connection
                               .prepareStatement("SELECT card_nr, card_type, card_balance FROM BankApp.Card WHERE client_nr =?;");

                       cardInfo.setString(1, client_nr);
                       ResultSet sumAllCardInfo = cardInfo.executeQuery();

                       //declaration 2d object of data which we are gonna use later
                       Object[][] cardData = new Object[rows][3];

                       for(int r=0;r<rows;r++) {
                           while(true){
                               int i = 0;
                               if (sumAllCardInfo.next()) {
                                   String card_nr = sumAllCardInfo.getString("card_nr");
                                   String card_type = sumAllCardInfo.getString("card_type");
                                   String card_balance = sumAllCardInfo.getString("card_balance");

                                   cardData[r][i] = card_nr;
                                   cardData[r][i+1] = card_type;
                                   cardData[r][i+2] = card_balance;
                                   break;
                               }
                           }
                       }

                       } catch (SQLException sqlException) {
                           // Error 12: Database is off or Your connection is invalid!
                           JOptionPane.showMessageDialog(WyplataFrame.this, "Error 12!");
                       }/** END QUERY **/

                       JOptionPane.showMessageDialog(WyplataFrame.this, "Poszło kwota!");
                       dispose();
                       MainFrame main = new MainFrame(client_nr);
                       main.setVisible(true);
                   }
                   else JOptionPane.showMessageDialog(WyplataFrame.this, "Błędna kwota!");
        }});

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFrame main = new MainFrame(client_nr);
                main.setVisible(true);
            }
        });
    }
}