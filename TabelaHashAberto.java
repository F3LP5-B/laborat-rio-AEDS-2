import java.util.NoSuchElementException;

public class TabelaHashAberto<K, V> implements IMapeamento<K, V> {

	private Entrada<K, V>[] tabelaHash;
	private int capacidade;
	private long comparacoes;
	private long inicio;
	private long termino;

	@SuppressWarnings("unchecked")
	public TabelaHashAberto(int capacidade) {
		if (capacidade < 1)
			throw new IllegalArgumentException("A capacidade da tabela hash não pode ser menor que 1.");
		this.capacidade = capacidade;
		tabelaHash = (Entrada<K, V>[]) new Entrada[capacidade];
	}

	private int funcaoHash(K chave, int tentativas) {
		return (Math.floorMod(chave.hashCode(), capacidade) + tentativas) % capacidade;
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
			int primeiraLivre = -1;
			for (int tentativa = 0; tentativa < capacidade; tentativa++) {
				int posicao = funcaoHash(chave, tentativa);
				Entrada<K, V> entrada = tabelaHash[posicao];
				if (entrada == null) {
					if (primeiraLivre < 0)
						primeiraLivre = posicao;
					break;
				}
				if (entrada.isRemovida()) {
					if (primeiraLivre < 0)
						primeiraLivre = posicao;
					continue;
				}
				comparacoes++;
				if (entrada.getChave().equals(chave))
					throw new IllegalArgumentException("O item já foi inserido anteriormente na tabela.");
			}
			if (primeiraLivre < 0)
				throw new IllegalStateException("A tabela hash está cheia.");
			tabelaHash[primeiraLivre] = new Entrada<>(chave, item);
			return primeiraLivre;
		} finally {
			finalizarMedicao();
		}
	}

	@Override
	public V pesquisar(K chave) {
		iniciarMedicao();
		try {
			for (int tentativa = 0; tentativa < capacidade; tentativa++) {
				int posicao = funcaoHash(chave, tentativa);
				Entrada<K, V> entrada = tabelaHash[posicao];
				if (entrada == null)
					throw new NoSuchElementException("O item não foi localizado na tabela.");
				if (entrada.isRemovida())
					continue;
				comparacoes++;
				if (entrada.getChave().equals(chave))
					return entrada.getValor();
			}
			throw new NoSuchElementException("O item não foi localizado na tabela.");
		} finally {
			finalizarMedicao();
		}
	}

	@Override
	public V remover(K chave) {
		iniciarMedicao();
		try {
			for (int tentativa = 0; tentativa < capacidade; tentativa++) {
				int posicao = funcaoHash(chave, tentativa);
				Entrada<K, V> entrada = tabelaHash[posicao];
				if (entrada == null)
					throw new NoSuchElementException("O item não foi localizado na tabela.");
				if (entrada.isRemovida())
					continue;
				comparacoes++;
				if (entrada.getChave().equals(chave)) {
					entrada.setRemovida(true);
					return entrada.getValor();
				}
			}
			throw new NoSuchElementException("O item não foi localizado na tabela.");
		} finally {
			finalizarMedicao();
		}
	}

	@Override
	public int tamanho() {
		int total = 0;
		for (Entrada<K, V> entrada : tabelaHash)
			if (entrada != null && !entrada.isRemovida()) total++;
		return total;
	}

	@Override
	public String percorrer() {
		StringBuilder texto = new StringBuilder();
		for (int i = 0; i < capacidade; i++) {
			texto.append("Posição[").append(i).append("]: ");
			if (tabelaHash[i] == null || tabelaHash[i].isRemovida())
				texto.append("vazia\n");
			else
				texto.append(tabelaHash[i]).append("\n");
		}
		return texto.toString();
	}

	@Override
	public String toString() { return percorrer(); }
}
