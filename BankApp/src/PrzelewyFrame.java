import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrzelewyFrame extends JFrame {
    private JPanel panel1;
    private JButton backButton;
    private JButton OKButton;
    private JComboBox cardBox;
    private JTextField odbiorcaText;
    private JTextField kwotaText;

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
