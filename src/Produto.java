import java.text.NumberFormat;

public abstract class Produto {
    
    private static final double MARGEM_PADRAO = 0.2;
    private String descricao;
    protected double precoCusto;
    protected double margemLucro;
    
    /**
     * Inicializador privado.
     * @param desc Descrição do produto (mínimo de 3 caracteres)
     * @param precoCusto Preço do produto (mínimo 0.01)
     * @param margemLucro Margem de lucro (mínimo 0.01)
     */
    private void init(String desc, double precoCusto, double margemLucro) {
        if ((desc != null) && (desc.length() >= 3) && (precoCusto > 0.0) && (margemLucro > 0.0)) {
            this.descricao = desc;
            this.precoCusto = precoCusto;
            this.margemLucro = margemLucro;
        } else {
            throw new IllegalArgumentException("Valores inválidos para os dados do produto.");
        }
    }
    
    /**
     * Construtor completo protegido (conforme diagrama UML).
     */
    protected Produto(String desc, double precoCusto, double margemLucro) {
        init(desc, precoCusto, margemLucro);
    }
    
    /**
     * Construtor protegido com margem padrão (conforme diagrama UML).
     */
    protected Produto(String desc, double precoCusto) {
        init(desc, precoCusto, MARGEM_PADRAO);
    }
    
    /**
     * Retorna o valor de venda padrão do produto.
     * @return Valor de venda do produto
     */
    public double valorVenda() {
        return (precoCusto * (1.0 + margemLucro));
    }
    
    /**
     * Descrição, em string, do produto.
     */
    @Override
    public String toString() {
        NumberFormat moeda = NumberFormat.getCurrencyInstance();
        return descricao + ": " + moeda.format(valorVenda());
    }
}