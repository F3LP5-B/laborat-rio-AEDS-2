import java.util.NoSuchElementException;

public class TabelaHash<K, V> implements IMapeamento<K, V> {

	private Lista<Entrada<K, V>>[] tabelaHash;
	private int capacidade;
	private long comparacoes;
	private long inicio;
	private long termino;

	@SuppressWarnings("unchecked")
	public TabelaHash(int capacidade) {
		if (capacidade < 1)
			throw new IllegalArgumentException("A capacidade da tabela hash não pode ser menor que 1.");
		this.capacidade = capacidade;
		tabelaHash = (Lista<Entrada<K, V>>[]) new Lista[capacidade];
		for (int i = 0; i < capacidade; i++)
			tabelaHash[i] = new Lista<>();
	}

	private int funcaoHash(K chave) {
		return Math.floorMod(chave.hashCode(), capacidade);
	}

	private void iniciarMedicao() {
		comparacoes = 0;
		inicio = System.nanoTime();
	}

	private void finalizarMedicao() {
		termino = System.nanoTime();
	}

	@Override
	public long getComparacoes() { return comparacoes; }

	@Override
	public double getTempo() { return (termino - inicio) / 1_000_000.0; }

	@Override
	public int inserir(K chave, V item) {
		iniciarMedicao();
		try {
			int posicao = funcaoHash(chave);
			Lista<Entrada<K, V>> lista = tabelaHash[posicao];
			try {
				lista.pesquisar(new Entrada<>(chave, null));
				throw new IllegalArgumentException("O item já foi inserido anteriormente na tabela.");
			} catch (NoSuchElementException ignorada) {
			}
			lista.inserirFinal(new Entrada<>(chave, item));
			comparacoes = lista.getComparacoes();
			return posicao;
		} finally {
			finalizarMedicao();
		}
	}

	@Override
	public V pesquisar(K chave) {
		iniciarMedicao();
		try {
			int posicao = funcaoHash(chave);
			Entrada<K, V> encontrada = tabelaHash[posicao].pesquisar(new Entrada<>(chave, null));
			comparacoes = tabelaHash[posicao].getComparacoes();
			return encontrada.getValor();
		} finally {
			finalizarMedicao();
		}
	}

	@Override
	public V remover(K chave) {
		iniciarMedicao();
		try {
			int posicao = funcaoHash(chave);
			Entrada<K, V> removida = tabelaHash[posicao].remover(new Entrada<>(chave, null));
			comparacoes = tabelaHash[posicao].getComparacoes();
			return removida.getValor();
		} finally {
			finalizarMedicao();
		}
	}

	@Override
	public int tamanho() {
		int total = 0;
		for (Lista<Entrada<K, V>> lista : tabelaHash)
			total += lista.tamanho();
		return total;
	}

	@Override
	public String percorrer() {
		StringBuilder texto = new StringBuilder();
		texto.append("Tabela com ").append(capacidade).append(" posições e ").append(tamanho()).append(" itens\n");
		for (int i = 0; i < capacidade; i++) {
			texto.append("Posição[").append(i).append("]: ");
			if (tabelaHash[i].vazia()) texto.append("vazia\n");
			else texto.append(tabelaHash[i]);
		}
		return texto.toString();
	}

	@Override
	public String toString() { return percorrer(); }
}
