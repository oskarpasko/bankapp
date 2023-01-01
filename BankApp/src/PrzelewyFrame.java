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
        /**
         * Download cards data QUERY
         *
         */
        Object[][] cardData = new Object[0][];
        int rows = 0;
        try {
            Connection connection = (Connection) DriverManager.getConnection(DB_URL,
                    "root", "rootroot");

            /** Query which return count of client's cards **/
            PreparedStatement countRows = (PreparedStatement) connection
                    .prepareStatement("SELECT count(card_nr) as countRows FROM bankapp.card WHERE client_nr =?");

            countRows.setString(1, client_nr);
            ResultSet countRowsResult = countRows.executeQuery();
            rows = 0;

            // download how many rows/cards have our client
            if (countRowsResult.next()) rows = countRowsResult.getInt(1);
            // END QUERY

            /** Query which is getting number, type and balnce client's cards **/
            PreparedStatement cardInfo = (PreparedStatement) connection
                    .prepareStatement("SELECT card_nr, card_type, card_balance FROM bankapp.card WHERE client_nr =?;");

            cardInfo.setString(1, client_nr);
            ResultSet sumAllCardInfo = cardInfo.executeQuery();

            //declaration 2d object of data which we are gonna use later
            cardData = new Object[rows][3];

            for (int r = 0; r < rows; r++) {
                while (true) {
                    int i = 0;
                    if (sumAllCardInfo.next()) {
                        String card_nr = sumAllCardInfo.getString("card_nr");
                        String card_type = sumAllCardInfo.getString("card_type");
                        String card_balance = sumAllCardInfo.getString("card_balance");

                        cardData[r][i] = card_nr;
                        cardData[r][i + 1] = card_type;
                        cardData[r][i + 2] = card_balance;
                        break;
                    }
                }
            }
        } catch (SQLException sqlException) {
            // Error 12: Database is off or Your connection is invalid!
            JOptionPane.showMessageDialog(PrzelewyFrame.this, "Error 12!");
        }/** END QUERY **/

        String nr_cards[] = new String[rows];
        for(int i=0;i<rows;i++) {
            nr_cards[i]= (String) cardData[i][0];
        }
        cardBox.setModel(new DefaultComboBoxModel(nr_cards));

        Object[][] finalCardData = cardData;
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nr_odbiorca = odbiorcaText.getText();

                if(kwotaText.getText().equals("")) {
                    JOptionPane.showMessageDialog(PrzelewyFrame.this,"Podaj kwotę przelewu!");
                } else if(nr_odbiorca.equals("")) {
                    JOptionPane.showMessageDialog(PrzelewyFrame.this,"Podaj odbiorcę przelewu!");
                } else {
                    float value = Float.parseFloat(kwotaText.getText());
                    if(value>0) {
                        String card_nr = (String) cardBox.getItemAt(cardBox.getSelectedIndex());
                        int r = 0;
                        while(true) {
                            if(card_nr.equals(finalCardData[r][0])) {
                                break;
                            } else {
                                r++;
                            }
                        }
                        float balance =  Float.parseFloat((String) finalCardData[r][2]);
                        String card_type = (String) finalCardData[r][1];
                        System.out.println(value);
                        if(card_type.equals("Kredytowa")) {
                            if((balance-value)>=-5000) {
                                // TODO: UPDATE balance
                                JOptionPane.showMessageDialog(PrzelewyFrame.this,"Poszedł przelew o kwocie: "+value+" z karty: "+card_nr+" dla odbiorcy: "+nr_odbiorca);
                                dispose();
                                MainFrame main = new MainFrame(client_nr);
                                main.setVisible(true);
                            } else JOptionPane.showMessageDialog(PrzelewyFrame.this,"Za mało środków na karcie!");
                        } else {
                            if(balance-value>=0) {
                                // TODO: UPDATE balance
                                JOptionPane.showMessageDialog(PrzelewyFrame.this,"Poszedł przelew o kwocie: "+value+" z karty: "+card_nr+" dla odbiorcy: "+nr_odbiorca);
                                dispose();
                                MainFrame main = new MainFrame(client_nr);
                                main.setVisible(true);
                            } else JOptionPane.showMessageDialog(PrzelewyFrame.this,"Za mało środków na karcie!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(PrzelewyFrame.this,"Przelew musi być większy od 0!");
                    }

                }
            }
        });
    }
}
