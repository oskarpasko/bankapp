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
                        } catch (SQLException sqlException) {
                            // Error 12: Database is off or Your connection is invalid!
                            JOptionPane.showMessageDialog(PrzelewyFrame.this, "Error 12!");
                        }/** END QUERY **/

                        JOptionPane.showMessageDialog(PrzelewyFrame.this,"Poszedł przelew!");
                        dispose();
                        MainFrame main = new MainFrame(client_nr);
                        main.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(PrzelewyFrame.this,"Przelew musi być większy od 0!");
                    }

                }
            }
        });
    }
}
