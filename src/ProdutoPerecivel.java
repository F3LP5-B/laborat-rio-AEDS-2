import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ProdutoPerecivel extends Produto {

    private static final double DESCONTO = 0.25;
    private static final int PRAZO_DESCONTO = 7;
    private LocalDate dataValidade;

    /**
     * Construtor do produto perecível.
     * @param desc Descrição do produto
     * @param precoCusto Preço de custo
     * @param margemLucro Margem de lucro
     * @param validade Data de validade (não pode ser anterior à data atual)
     */
    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
        super(desc, precoCusto, margemLucro);

        // Validação: não pode ser cadastrado vencido
        if (validade == null || validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de validade não pode ser anterior à data atual.");
        }
        this.dataValidade = validade;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    /**
     * Calcula o preço de venda do produto perecível aplicando as regras de validade e desconto.
     */
    @Override
    public double precoVenda() {
        LocalDate hoje = LocalDate.now();

        // Não permite venda se estiver vencido
        if (dataValidade.isBefore(hoje)) {
            throw new IllegalStateException("Produto fora da data de validade! Venda não permitida.");
        }

        double precoBase = super.precoVenda();
        long diasParaVencer = ChronoUnit.DAYS.between(hoje, dataValidade);

        // Aplicar desconto de 25% se vencer em 7 dias ou menos
        if (diasParaVencer <= PRAZO_DESCONTO) {
            precoBase -= (precoBase * DESCONTO);
        }

        return precoBase;
    }

    // TAREFA 2: Método exigido pelo guia do laboratório
    @Override
    public String gerarDadosTexto() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format(Locale.US, "2;%s;%.2f;%.2f;%s", 
                descricao, precoCusto, margemLucro, dataValidade.format(formatador));
    }

    @Override
    public String toString() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("%s (R$ %.2f) - Validade: %s", 
                descricao, precoVenda(), dataValidade.format(formatador));
    }
}
