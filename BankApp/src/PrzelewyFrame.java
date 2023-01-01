import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PrzelewyFrame extends JFrame {
    private JPanel panel1;
    private JButton backButton;
    private JButton OKButton;
    private JComboBox cardBox;
    private JTextField odbiorcaText;
    private JTextField kwotaText;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";

//    public static void main(String[] args) {
//        PrzelewyFrame panel = new PrzelewyFrame("1");
//        panel.setVisible(true);
//    }
    public PrzelewyFrame(String client_nr) {
        super("PrzelewyFrame");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFrame main = new MainFrame(client_nr);
                main.setVisible(true);
            }
        });
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(kwotaText.getText().equals("")) {
                    JOptionPane.showMessageDialog(PrzelewyFrame.this,"Podaj kwotę przelewu!");
                } else if(odbiorcaText.getText().equals("")) {
                    JOptionPane.showMessageDialog(PrzelewyFrame.this,"Podaj odbiorcę przelewu!");
                } else {
                    float value = Float.parseFloat(kwotaText.getText());
                    if(value>0) {
/**
 * Download cards data QUERY
 */
                        String[] cards = new String[0];
                        try {
                            Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                    "root", "rootroot");

                            /** Query which return count of client's cards **/
                            PreparedStatement countRows = (PreparedStatement) connection
                                    .prepareStatement("SELECT count(card_nr) as countRows FROM bankapp.card WHERE client_nr =?");

                            countRows.setString(1, client_nr);
                            ResultSet countRowsResult = countRows.executeQuery();
                            int rows = 0;

                            // download how many rows/cards have our client
                            if (countRowsResult.next()) rows = countRowsResult.getInt(1);
                            // END QUERY

                            cards = new String[rows];

                            /** Query which is getting number client's cards **/
                            PreparedStatement cardInfo = (PreparedStatement) connection
                                    .prepareStatement("SELECT card_nr FROM bankapp.card WHERE client_nr =?;");

                            cardInfo.setString(1, client_nr);
                            ResultSet sumAllCardInfo = cardInfo.executeQuery();

                            for (int i = 0; i < rows; i++) {
                                if (sumAllCardInfo.next()) {
                                    String card_nr = sumAllCardInfo.getString("card_nr");

                                    cards[i] = card_nr;
                                }
                            }
<<<<<<< Updated upstream
                        } catch (SQLException sqlException) {
                            // Error 12: Database is off or Your connection is invalid!
                            JOptionPane.showMessageDialog(PrzelewyFrame.this, "Error 12!");
                        }/** END QUERY **/

                        JOptionPane.showMessageDialog(PrzelewyFrame.this,"Poszedł przelew!");
                        dispose();
                        MainFrame main = new MainFrame(client_nr);
                        main.setVisible(true);
=======
                        }
                        float balance =  Float.parseFloat((String) finalCardData[r][2]);
                        String card_type = (String) finalCardData[r][1];
                        if(card_type.equals("Kredytowa")) {
                            if((balance-value)>=-5000) {
/**
 * Query to overflow from credit card
 */
                                try {
                                    Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                            "root", "rootroot");

                                    /** Query which is updating balance card balance minus value **/
                                    PreparedStatement updateBalance = (PreparedStatement) connection
                                            .prepareStatement("UPDATE card SET card_balance = card_balance - " + value + " WHERE card_nr =?;");

                                    updateBalance.setString(1, card_nr);
                                    updateBalance.executeUpdate();

                                    /** Query which is checking if card exists with card number which users inputed **/
                                    PreparedStatement checkRecipient = (PreparedStatement) connection
                                            .prepareStatement("SELECT EXISTS(SELECT card_nr FROM bankapp.card WHERE card_nr =?);");

                                    checkRecipient.setString(1, nr_odbiorca);
                                    ResultSet checkRecipientResult = checkRecipient.executeQuery();

                                    if(checkRecipientResult.next())
                                    {
                                        int check = checkRecipientResult.getInt(1);
                                        if(check==1)
                                        {
                                            /** Query which is updating recipient's balance card balance plus value **/
                                            PreparedStatement updateRecipientBalance = (PreparedStatement) connection
                                                    .prepareStatement("UPDATE card SET card_balance = card_balance + " + value + " WHERE card_nr =?;");

                                            updateRecipientBalance.setString(1, nr_odbiorca);
                                            updateRecipientBalance.executeUpdate();
                                        }
                                    }

                                } catch (SQLException sqlException) {
                                    // Error 12: Database is off or Your connection is invalid!
                                    JOptionPane.showMessageDialog(PrzelewyFrame.this, "Error 12!");
                                }/** END QUERY **/

                                JOptionPane.showMessageDialog(PrzelewyFrame.this,"Poszedł przelew o kwocie: "+value+" z karty: "+card_nr+" dla odbiorcy: "+nr_odbiorca);
                                dispose();
                                MainFrame main = new MainFrame(client_nr);
                                main.setVisible(true);

                            } else JOptionPane.showMessageDialog(PrzelewyFrame.this,"Za mało środków na karcie!");
                        } else {
                            if(balance-value>=0) {
/**
 * Query to overflow from debet card
 */
                                try {
                                    Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                                            "root", "rootroot");

                                    /** Query which is updating balance card balance minus value **/
                                    PreparedStatement updateBalance = (PreparedStatement) connection
                                            .prepareStatement("UPDATE card SET card_balance = card_balance - " + value + " WHERE card_nr =?;");

                                    updateBalance.setString(1, card_nr);
                                    updateBalance.executeUpdate();

                                    /** Query which is checking if card exists with card number which users inputed **/
                                    PreparedStatement checkRecipient = (PreparedStatement) connection
                                            .prepareStatement("SELECT EXISTS(SELECT card_nr FROM bankapp.card WHERE card_nr =?);");

                                    checkRecipient.setString(1, nr_odbiorca);
                                    ResultSet checkRecipientResult = checkRecipient.executeQuery();

                                    if(checkRecipientResult.next())
                                    {
                                        int check = checkRecipientResult.getInt(1);
                                        if(check==1)
                                        {
                                            /** Query which is updating recipient's balance card balance plus value **/
                                            PreparedStatement updateRecipientBalance = (PreparedStatement) connection
                                                    .prepareStatement("UPDATE card SET card_balance = card_balance + " + value + " WHERE card_nr =?;");

                                            updateRecipientBalance.setString(1, nr_odbiorca);
                                            updateRecipientBalance.executeUpdate();
                                        }
                                    }

                                } catch (SQLException sqlException) {
                                    // Error 12: Database is off or Your connection is invalid!
                                    JOptionPane.showMessageDialog(PrzelewyFrame.this, "Error 12!");
                                }/** END QUERY **/

                                JOptionPane.showMessageDialog(PrzelewyFrame.this,"Poszedł przelew o kwocie: "+value+" z karty: "+card_nr+" dla odbiorcy: "+nr_odbiorca);
                                dispose();
                                MainFrame main = new MainFrame(client_nr);
                                main.setVisible(true);

                            } else JOptionPane.showMessageDialog(PrzelewyFrame.this,"Za mało środków na karcie!");
                        }
>>>>>>> Stashed changes
                    } else {
                        JOptionPane.showMessageDialog(PrzelewyFrame.this,"Przelew musi być większy od 0!");
                    }

                }
            }
        });
    }
}
