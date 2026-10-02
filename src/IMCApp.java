import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

/**
 * Aplicação JavaFX para cálculo de IMC com persistência em arquivo CSV.
 */
public class IMCApp extends Application {

    private final ObservableList<Pessoa> pessoas = FXCollections.observableArrayList();

    private TextField txtNome;
    private TextField txtAltura;
    private TextField txtPeso;
    private Label lblResultado;
    private Label lblClassificacao;
    private TableView<Pessoa> tabela;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(criarMenu());
        root.setCenter(criarConteudo());

        Scene scene = new Scene(root, 700, 520);
        stage.setTitle("Cálculo IMC");
        stage.setScene(scene);
        stage.show();
    }

    // ------------------------------------------------------------------
    // Construção da interface
    // ------------------------------------------------------------------

    private MenuBar criarMenu() {
        MenuItem itemSair = new MenuItem("Sair");
        itemSair.setOnAction(e -> System.exit(0));
        Menu menuFile = new Menu("File");
        menuFile.getItems().add(itemSair);

        MenuItem itemLimpar = new MenuItem("Limpar campos");
        itemLimpar.setOnAction(e -> limparCampos());
        Menu menuEdit = new Menu("Edit");
        menuEdit.getItems().add(itemLimpar);

        MenuItem itemSobre = new MenuItem("Sobre");
        itemSobre.setOnAction(e -> mostrarAlerta(Alert.AlertType.INFORMATION, "Sobre",
                "Aplicação de cálculo de IMC em JavaFX."));
        Menu menuHelp = new Menu("Help");
        menuHelp.getItems().add(itemSobre);

        return new MenuBar(menuFile, menuEdit, menuHelp);
    }

    private VBox criarConteudo() {
        // --- Formulário (lado esquerdo) ---
        txtNome = new TextField();
        txtAltura = new TextField();
        txtAltura.setPromptText("ex: 1.75");
        txtPeso = new TextField();
        txtPeso.setPromptText("ex: 70.5");

        VBox formulario = new VBox(5,
                new Label("Nome:"), txtNome,
                new Label("Altura (m):"), txtAltura,
                new Label("Peso (kg):"), txtPeso);
        formulario.setPrefWidth(300);

        // --- Resultado (lado direito) ---
        Label titulo = new Label("Cálculo IMC");
        titulo.setFont(Font.font("System", 20));

        lblResultado = new Label("0.00");
        lblResultado.setFont(Font.font("System", 72));

        lblClassificacao = new Label("");
        lblClassificacao.setFont(Font.font("System", 16));

        VBox resultado = new VBox(5, titulo, lblResultado, lblClassificacao);
        resultado.setAlignment(Pos.CENTER);
        HBox.setHgrow(resultado, javafx.scene.layout.Priority.ALWAYS);

        HBox topo = new HBox(20, formulario, resultado);
        topo.setPadding(new Insets(10));

        // --- Botões ---
        Button btnCalcular = new Button("Calcular IMC");
        Button btnSalvar = new Button("Salvar");
        Button btnCarregar = new Button("Carregar Dados");

        btnCalcular.setOnAction(e -> calcularImc());
        btnSalvar.setOnAction(e -> salvar());
        btnCarregar.setOnAction(e -> carregar());

        HBox botoes = new HBox(10, btnCalcular, btnSalvar, btnCarregar);
        botoes.setAlignment(Pos.CENTER_LEFT);
        botoes.setPadding(new Insets(0, 10, 10, 10));

        // --- Tabela ---
        criarTabela();

        VBox layout = new VBox(5, topo, botoes, tabela);
        javafx.scene.layout.VBox.setVgrow(tabela, javafx.scene.layout.Priority.ALWAYS);
        return layout;
    }

    @SuppressWarnings("unchecked")
    private void criarTabela() {
        tabela = new TableView<>(pessoas);
        tabela.setPlaceholder(new Label("Não há conteúdo na tabela"));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pessoa, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Pessoa, String> colNome = new TableColumn<>("NOME");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Pessoa, Double> colAltura = new TableColumn<>("ALTURA");
        colAltura.setCellValueFactory(new PropertyValueFactory<>("altura"));
        colAltura.setCellFactory(c -> formatoDuasCasas());

        TableColumn<Pessoa, Double> colPeso = new TableColumn<>("PESO");
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colPeso.setCellFactory(c -> formatoDuasCasas());

        TableColumn<Pessoa, Double> colImc = new TableColumn<>("IMC");
        colImc.setCellValueFactory(new PropertyValueFactory<>("imc"));
        colImc.setCellFactory(c -> formatoDuasCasas());

        TableColumn<Pessoa, String> colClass = new TableColumn<>("CLASSIFICAÇÃO");
        colClass.setCellValueFactory(new PropertyValueFactory<>("classificacao"));

        tabela.getColumns().addAll(colId, colNome, colAltura, colPeso, colImc, colClass);
    }

    /**
     * Célula que mostra números com 2 casas decimais.
     */
    private TableCell<Pessoa, Double> formatoDuasCasas() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double valor, boolean vazio) {
                super.updateItem(valor, vazio);
                setText(vazio || valor == null ? null : String.format(Locale.US, "%.2f", valor));
            }
        };
    }

    // ------------------------------------------------------------------
    // Ações dos botões
    // ------------------------------------------------------------------

    /**
     * Lê altura e peso, aceitando vírgula ou ponto como separador decimal.
     */
    private double lerNumero(TextField campo, String nomeCampo) {
        String texto = campo.getText().trim().replace(",", ".");
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido para " + nomeCampo + ".");
        }
    }

    private void calcularImc() {
        try {
            double altura = lerNumero(txtAltura, "altura");
            double peso = lerNumero(txtPeso, "peso");
            if (altura <= 0 || peso <= 0) {
                throw new IllegalArgumentException("Altura e peso devem ser maiores que zero.");
            }
            double imc = Pessoa.calcularImc(peso, altura);
            lblResultado.setText(String.format(Locale.US, "%.2f", imc));
            lblClassificacao.setText(Pessoa.classificar(imc));
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Entrada inválida", e.getMessage());
        }
    }

    /**
     * Adiciona a pessoa do formulário à tabela e grava todos os registros no arquivo.
     */
    private void salvar() {
        try {
            double altura = lerNumero(txtAltura, "altura");
            double peso = lerNumero(txtPeso, "peso");
            Pessoa pessoa = new Pessoa(proximoId(), txtNome.getText(), altura, peso);

            pessoas.add(pessoa);
            ArquivoUtil.salvar(pessoas);

            lblResultado.setText(String.format(Locale.US, "%.2f", pessoa.getImc()));
            lblClassificacao.setText(pessoa.getClassificacao());
            limparCampos();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                    "Dados salvos em \"" + ArquivoUtil.NOME_ARQUIVO + "\".");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Entrada inválida", e.getMessage());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao salvar", e.getMessage());
        }
    }

    private void carregar() {
        try {
            pessoas.setAll(ArquivoUtil.carregar());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao carregar", e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    private int proximoId() {
        return pessoas.stream().mapToInt(Pessoa::getId).max().orElse(0) + 1;
    }

    private void limparCampos() {
        txtNome.clear();
        txtAltura.clear();
        txtPeso.clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}