import java.util.Locale;

public class ProdutoNaoPerecivel extends Produto {

    public ProdutoNaoPerecivel(String descricao, double precoCusto, double margemLucro) {
        super(descricao, precoCusto, margemLucro);
    }

    // Tarefa 2: Implementação do gerarDadosTexto
    @Override
    public String gerarDadosTexto() {
        return String.format(Locale.US, "1;%s;%.2f;%.2f", descricao, precoCusto, margemLucro);
    }
}
