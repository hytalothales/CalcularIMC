import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para salvar e carregar pessoas em um arquivo CSV.
 * Formato de cada linha: id,nome,altura,peso
 */
public class ArquivoUtil {

    public static final String NOME_ARQUIVO = "dados_pessoas.txt";

    private ArquivoUtil() {
        // classe utilitária: não deve ser instanciada
    }

    /** Salva (sobrescrevendo) a lista de pessoas no arquivo. */
    public static void salvar(List<Pessoa> pessoas) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO, false))) {
            for (Pessoa p : pessoas) {
                writer.println(p.toCsv());
            }
        }
    }

    /** Carrega as pessoas do arquivo. Linhas inválidas são ignoradas. */
    public static List<Pessoa> carregar() throws IOException {
        List<Pessoa> pessoas = new ArrayList<>();
        File arquivo = new File(NOME_ARQUIVO);

        if (!arquivo.exists()) {
            throw new IOException("O arquivo \"" + NOME_ARQUIVO + "\" não foi encontrado.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                try {
                    pessoas.add(Pessoa.fromCsv(linha));
                } catch (IllegalArgumentException e) {
                    System.err.println("Linha ignorada (" + e.getMessage() + ")");
                }
            }
        }
        return pessoas;
    }
}