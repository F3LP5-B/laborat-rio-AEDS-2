import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProdutoPerecivel extends Produto {

    private static final double DESCONTO = 0.25;
    private static final int PRAZO_DESCONTO = 7;
    private LocalDate dataDeValidade;

    /**
     * Construtor do produto perecível.
     * @param desc Descrição do produto
     * @param precoCusto Preço de custo
     * @param margemLucro Margem de lucro
     * @param validade Data de validade (não pode ser anterior à data atual)
     */
    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
        super(desc, precoCusto, margemLucro);
        
        // Requisito: Não pode ser cadastrado com data de validade anterior ao dia atual
        if (validade == null || validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de validade não pode ser anterior à data atual.");
        }
        this.dataDeValidade = validade;
    }

    /**
     * Calcula o valor de venda do produto perecível aplicando as regras de validade e desconto.
     * @return Valor de venda ajustado
     */
    @Override
    public double valorVenda() {
        LocalDate hoje = LocalDate.now();

        // Requisito: Não pode ser solicitado seu valor de venda se estiver fora da data de validade
        if (dataDeValidade.isBefore(hoje)) {
            throw new IllegalStateException("Produto fora da data de validade! Venda não permitida.");
        }

        double precoBase = super.valorVenda();
        long diasParaVencer = ChronoUnit.DAYS.between(hoje, dataDeValidade);

        // Requisito: Desconto de 25% se o vencimento for em até 7 dias a partir de hoje
        if (diasParaVencer <= PRAZO_DESCONTO) {
            precoBase -= (precoBase * DESCONTO);
        }

        return precoBase;
    }

    @Override
    public String toString() {
        return super.toString() + " (Validade: " + dataDeValidade + ")";
    }
}