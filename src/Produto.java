import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class Produto {

    private static final double MARGEM_PADRAO = 0.2;
    private static int seqId = 0;
    
    private int id;
    protected String descricao;
    protected double precoCusto;
    protected double margemLucro;

    public Produto(String descricao, double precoCusto, double margemLucro) {
        this.id = ++seqId;
        this.descricao = descricao;
        this.precoCusto = precoCusto;
        this.setMargemLucro(margemLucro);
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }

    public double getMargemLucro() {
        return margemLucro;
    }

    private void setMargemLucro(double margemLucro) {
        if (margemLucro < 0) {
            this.margemLucro = MARGEM_PADRAO;
        } else {
            this.margemLucro = margemLucro;
        }
    }

    public double precoVenda() {
        return precoCusto * (1 + margemLucro);
    }

    // Tarefa 1: Sobrescrita do equals comparando por descrição (case-insensitive)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Produto)) return false;
        Produto outro = (Produto) obj;
        if (this.descricao == null || outro.descricao == null) return false;
        return this.descricao.equalsIgnoreCase(outro.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descricao != null ? descricao.toLowerCase() : 0);
    }

    // Tarefa 2: Assinatura do método abstrato
    /**
     * Gera uma linha de texto a partir dos dados do produto.
     * @return String no formato "tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
     */
    public abstract String gerarDadosTexto();

    // Tarefa 2: Método para instanciar Produto a partir de uma linha de texto
    /**
     * Cria um produto a partir de uma linha de dados em formato texto.
     * @param linha Linha com os dados do produto.
     * @return Produto instanciado (Perecivel ou NaoPerecivel)
     */
    public static Produto criarDoTexto(String linha) {
        if (linha == null || linha.trim().isEmpty()) {
            return null;
        }

        String[] partes = linha.split(";");
        int tipo = Integer.parseInt(partes[0].trim());
        String descricao = partes[1].trim();
        double precoCusto = Double.parseDouble(partes[2].trim().replace(",", "."));
        double margemLucro = Double.parseDouble(partes[3].trim().replace(",", "."));

        if (tipo == 1) {
            return new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
        } else if (tipo == 2) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataValidade = LocalDate.parse(partes[4].trim(), fmt);
            return new ProdutoPerecivel(descricao, precoCusto, margemLucro, dataValidade);
        }

        return null;
    }

    @Override
    public String toString() {
        return String.format("%s (R$ %.2f)", descricao, precoVenda());
    }
}
