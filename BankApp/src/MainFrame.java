import javax.swing.*;

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

    public static void main(String[] args) {
        MainFrame panel = new MainFrame();
        panel.setVisible(true);
    }

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

    }


}
