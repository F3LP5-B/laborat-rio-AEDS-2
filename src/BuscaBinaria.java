import java.util.NoSuchElementException;
import java.util.function.Function;

public class BuscaBinaria<T, K extends Comparable<K>> implements IBuscador<T, K> {

	private T[] dados;
	private int quantidade;
	private Function<T, K> extratorDeChave;
	private long comparacoes;
	private long inicio;
	private long termino;

	/**
	 * @param dados Vetor pesquisado. Pré-condição: ordenado crescentemente pela MESMA chave
	 *              que extratorDeChave extrai.
	 * @param quantidade Quantidade de posições válidas no vetor.
	 * @param extratorDeChave Função que extrai, de cada elemento, a chave usada na busca.
	 */
	public BuscaBinaria(T[] dados, int quantidade, Function<T, K> extratorDeChave) {
		this.dados = dados;
		this.quantidade = quantidade;
		this.extratorDeChave = extratorDeChave;
	}

	@Override
	public T buscar(K chave) {
		inicio = System.nanoTime();
		comparacoes = 0;

		int esquerda = 0;
		int direita = quantidade - 1;

		while (esquerda <= direita) {
			int meio = esquerda + (direita - esquerda) / 2;
			comparacoes++;
			K chaveMeio = extratorDeChave.apply(dados[meio]);
			int comparacao = chave.compareTo(chaveMeio);

			if (comparacao == 0) {
				termino = System.nanoTime();
				return dados[meio];
			}
			if (comparacao < 0) {
				direita = meio - 1;
			} else {
				esquerda = meio + 1;
			}
		}

		termino = System.nanoTime();
		throw new NoSuchElementException("Elemento não encontrado.");
	}

	/**
	 * Busca binária pela primeira posição cuja chave é maior ou igual à chave informada.
	 * Pré-condição: vetor ordenado crescentemente pela mesma chave.
	 * @return A primeira posição i (0 <= i <= quantidade) tal que a chave de dados[i] >= chave;
	 *         quantidade, se todas as chaves forem menores.
	 */
	public int primeiraPosicaoMaiorOuIgual(K chave) {
		int esquerda = 0;
		int direita = quantidade;

		while (esquerda < direita) {
			int meio = esquerda + (direita - esquerda) / 2;
			K chaveMeio = extratorDeChave.apply(dados[meio]);
			comparacoes++;
			if (chaveMeio.compareTo(chave) < 0) {
				esquerda = meio + 1;
			} else {
				direita = meio;
			}
		}

		return esquerda;
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
