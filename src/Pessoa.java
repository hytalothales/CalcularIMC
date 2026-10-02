import java.util.Locale;

/**
 * Representa uma pessoa cadastrada, com nome, altura, peso e IMC.
 */
public class Pessoa {

    private final int id;
    private final String nome;
    private final double altura; // em metros
    private final double peso;   // em kg
    private final double imc;

    public Pessoa(int id, String nome, double altura, double peso) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        if (altura <= 0) {
            throw new IllegalArgumentException("A altura deve ser maior que zero.");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("O peso deve ser maior que zero.");
        }
        this.id = id;
        this.nome = nome.trim();
        this.altura = altura;
        this.peso = peso;
        this.imc = calcularImc(peso, altura);
    }

    /** IMC = peso / altura² */
    public static double calcularImc(double peso, double altura) {
        return peso / (altura * altura);
    }

    /** Retorna a classificação do IMC conforme a tabela do exercício. */
    public static String classificar(double imc) {
        if (imc < 18.5) {
            return "Abaixo do Peso";
        } else if (imc < 25) {
            return "Peso Normal";
        } else if (imc < 30) {
            return "Sobrepeso";
        } else if (imc < 35) {
            return "Obesidade Grau 1";
        } else if (imc < 40) {
            return "Obesidade Grau 2";
        } else {
            return "Obesidade Grau 3";
        }
    }

    // Getters (usados também pelo PropertyValueFactory da TableView)
    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getAltura() { return altura; }
    public double getPeso() { return peso; }
    public double getImc() { return imc; }
    public String getClassificacao() { return classificar(imc); }

    /** Converte para uma linha CSV: id,nome,altura,peso */
    public String toCsv() {
        String nomeSeguro = nome.replace(",", " ");
        return String.format(Locale.US, "%d,%s,%.2f,%.2f", id, nomeSeguro, altura, peso);
    }

    /** Cria uma Pessoa a partir de uma linha CSV: id,nome,altura,peso */
    public static Pessoa fromCsv(String linha) {
        String[] partes = linha.split(",");
        if (partes.length != 4) {
            throw new IllegalArgumentException("Linha inválida: " + linha);
        }
        int id = Integer.parseInt(partes[0].trim());
        String nome = partes[1].trim();
        double altura = Double.parseDouble(partes[2].trim());
        double peso = Double.parseDouble(partes[3].trim());
        return new Pessoa(id, nome, altura, peso);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Pessoa[id=%d, nome=%s, altura=%.2f, peso=%.2f, imc=%.2f]",
                id, nome, altura, peso, imc);
    }
}
