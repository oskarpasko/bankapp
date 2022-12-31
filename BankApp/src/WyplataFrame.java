import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

//            public static void main(String[] args) {
//        WyplataFrame panel = new WyplataFrame("1");
//        panel.setVisible(true);
//    }
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
