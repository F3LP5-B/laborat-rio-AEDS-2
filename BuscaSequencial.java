import java.util.NoSuchElementException;
import java.util.function.Function;

public class BuscaSequencial<T, K extends Comparable<K>> implements IBuscador<T, K> {

	private T[] dados;
	private int quantidade;
	private Function<T, K> extratorDeChave;
	private long comparacoes;
	private long inicio;
	private long termino;

	/**
	 * @param dados Vetor pesquisado (não precisa estar ordenado).
	 * @param quantidade Quantidade de posições válidas no vetor.
	 * @param extratorDeChave Função que extrai, de cada elemento, a chave usada na busca.
	 */
	public BuscaSequencial(T[] dados, int quantidade, Function<T, K> extratorDeChave) {
		this.dados = dados;
		this.quantidade = quantidade;
		this.extratorDeChave = extratorDeChave;
	}

	@Override
	public T buscar(K chave) {
		inicio = System.nanoTime();
		comparacoes = 0;

		for (int i = 0; i < quantidade; i++) {
			comparacoes++;
			K chaveAtual = extratorDeChave.apply(dados[i]);
			if (chaveAtual.compareTo(chave) == 0) {
				termino = System.nanoTime();
				return dados[i];
			}
		}

		termino = System.nanoTime();
		throw new NoSuchElementException("Elemento não encontrado.");
	}

	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return (termino - inicio) / 1_000_000.0;
	}
}
