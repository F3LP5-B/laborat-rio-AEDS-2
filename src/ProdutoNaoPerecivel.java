public class ProdutoNaoPerecivel extends Produto {

    /**
     * Construtor completo do produto não perecível.
     */
    public ProdutoNaoPerecivel(String desc, double precoCusto, double margemLucro) {
        super(desc, precoCusto, margemLucro);
    }

    /**
     * Construtor sem margem de lucro (utiliza margem padrão).
     */
    public ProdutoNaoPerecivel(String desc, double precoCusto) {
        super(desc, precoCusto);
    }

    /**
     * Retorna o valor de venda do produto não perecível.
     */
    @Override
    public double valorVenda() {
        return super.valorVenda();
    }
}