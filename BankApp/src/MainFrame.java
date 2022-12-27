import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JButton wplataButton;
    private JButton wyplataButton;
    private JButton przelewButton;
    private JButton wyjdzButton;
    private JTable kartyTable;
    private JTable historiaTable;
    private JLabel idValueLabel;
    private JLabel saldoValueLabel;

    public MainFrame() {
        super("MainFrame");
        this.setContentPane(this.panel1); // wyswietlanie okienka na ekranie
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);



        String[] columnNames = {"Nr Karty",
                "Rodzaj karty"};

        Object[][] data = {
                {"14151515", "Kredytowa"},
                {"23523525", "Debetowa"}
        };

        kartyTable = new JTable(data, columnNames);

        wyjdzButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginFrame n = new LoginFrame();
                n.setVisible(true);
            }
        });
        wplataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                WplataFrame n = new WplataFrame();
                n.setVisible(true);
            }
        });
        wyplataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                WyplataFrame n = new WyplataFrame();
                n.setVisible(true);
            }
        });
    }


}
