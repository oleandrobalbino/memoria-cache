import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Menu {
    private JFrame frame;
    private JTextArea statusTextArea;
    private JRadioButton diretoRadioButton;
    private JRadioButton associativoRadioButton;
    private ButtonGroup tipoMapeamentoGroup;
    private JComboBox<String> metodoSubstituicaoComboBox;

    public Menu() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Menu App");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        frame.getContentPane().add(controlPanel, BorderLayout.NORTH);

        diretoRadioButton = new JRadioButton("Mapeamento Direto");
        diretoRadioButton.setSelected(true);
        associativoRadioButton = new JRadioButton("Mapeamento Associativo");
        tipoMapeamentoGroup = new ButtonGroup();
        tipoMapeamentoGroup.add(diretoRadioButton);
        tipoMapeamentoGroup.add(associativoRadioButton);

        controlPanel.add(diretoRadioButton);
        controlPanel.add(associativoRadioButton);

        metodoSubstituicaoComboBox = new JComboBox<>();
        metodoSubstituicaoComboBox.addItem("Random");
        metodoSubstituicaoComboBox.addItem("FIFO");
        metodoSubstituicaoComboBox.addItem("LFU");
        metodoSubstituicaoComboBox.addItem("LRU");

        controlPanel.add(new JLabel("Método de Substituição:"));
        controlPanel.add(metodoSubstituicaoComboBox);

        JButton startButton = new JButton("Iniciar Processamento");
        startButton.addActionListener(e -> processar());
        frame.getContentPane().add(startButton, BorderLayout.SOUTH);
    }

    private ArrayList<String> carregarArquivo(String nomeArquivo) {
        ArrayList<String> linhas = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(nomeArquivo);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linhas;
    }

    private void processar() {
        ArrayList<String> teste = carregarArquivo("/data/teste_2.txt");
        ArrayList<String> configuracao = carregarArquivo("/data/config.txt");

        if (diretoRadioButton.isSelected()) {
            MapeamentoDireto mapeamentoDireto = new MapeamentoDireto(teste, configuracao);
            exibirResultado(mapeamentoDireto.resultado());
        } else if (associativoRadioButton.isSelected()) {
            int indiceMetodo = metodoSubstituicaoComboBox.getSelectedIndex() + 1;
            MapeamentoAssociativo mapeamentoAssociativo = new MapeamentoAssociativo(teste, configuracao, indiceMetodo);
            exibirResultado(mapeamentoAssociativo.resultado());
        }

    }

    private void exibirResultado(String resultado) {
        statusTextArea.setText("");

        statusTextArea.append("Processamento iniciado...\n");
        statusTextArea.append("Resultado:\n" + resultado + "\n");
        statusTextArea.append("Processamento concluído.\n");

        JOptionPane.showMessageDialog(frame, "Processamento concluído com sucesso!", "Aviso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Menu window = new Menu();
            window.show();
        });
    }
}
