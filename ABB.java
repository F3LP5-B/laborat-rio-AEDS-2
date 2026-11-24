import java.util.Comparator;
import java.util.NoSuchElementException;

public class ABB<K, V> implements IMapeamento<K, V> {

	protected No<K, V> raiz;
	protected Comparator<K> comparador;
	protected int tamanho;
	private long comparacoes;
	private long inicio;
	private long termino;

	@SuppressWarnings("unchecked")
	public ABB() {
		this((chave1, chave2) -> ((Comparable<K>) chave1).compareTo(chave2));
	}

	public ABB(Comparator<K> comparador) {
		if (comparador == null)
			throw new IllegalArgumentException("O comparador não pode ser nulo.");
		this.raiz = null;
		this.comparador = comparador;
		this.tamanho = 0;
	}

	public boolean vazia() {
		return raiz == null;
	}

	protected void iniciarMedicao() {
		comparacoes = 0;
		inicio = System.nanoTime();
	}

	protected void finalizarMedicao() {
		termino = System.nanoTime();
	}

	protected int comparar(K chave1, K chave2) {
		comparacoes++;
		return comparador.compare(chave1, chave2);
	}

	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return (termino - inicio) / 1_000_000.0;
	}

	@Override
	public V pesquisar(K chave) {
		iniciarMedicao();
		try {
			return pesquisar(raiz, chave);
		} finally {
			finalizarMedicao();
		}
	}

	protected V pesquisar(No<K, V> raizArvore, K chave) {
		No<K, V> atual = raizArvore;
		while (atual != null) {
			int resultado = comparar(chave, atual.getChave());
			if (resultado == 0)
				return atual.getItem();
			atual = resultado < 0 ? atual.getEsquerda() : atual.getDireita();
		}
		throw new NoSuchElementException("O item não foi localizado na árvore!");
	}

	@Override
	public int inserir(K chave, V item) {
		iniciarMedicao();
		try {
			raiz = inserir(raiz, chave, item);
			tamanho++;
			return tamanho;
		} finally {
			finalizarMedicao();
		}
	}

	protected No<K, V> inserir(No<K, V> raizArvore, K chave, V item) {
		if (raizArvore == null)
			return new No<>(chave, item);
		int resultado = comparar(chave, raizArvore.getChave());
		if (resultado < 0)
			raizArvore.setEsquerda(inserir(raizArvore.getEsquerda(), chave, item));
		else if (resultado > 0)
			raizArvore.setDireita(inserir(raizArvore.getDireita(), chave, item));
		else
			throw new IllegalArgumentException("O item já foi inserido anteriormente na árvore.");
		return raizArvore;
	}

	@Override
	public V remover(K chave) {
		iniciarMedicao();
		try {
			V removido = pesquisarItem(raiz, chave);
			raiz = remover(raiz, chave);
			tamanho--;
			return removido;
		} finally {
			finalizarMedicao();
		}
	}

	private V pesquisarItem(No<K, V> raizArvore, K chave) {
		No<K, V> atual = raizArvore;
		while (atual != null) {
			int resultado = comparar(chave, atual.getChave());
			if (resultado == 0)
				return atual.getItem();
			atual = resultado < 0 ? atual.getEsquerda() : atual.getDireita();
		}
		throw new NoSuchElementException("O item a ser removido não foi localizado na árvore!");
	}

	protected No<K, V> remover(No<K, V> raizArvore, K chaveRemover) {
		if (raizArvore == null)
			throw new NoSuchElementException("O item a ser removido não foi localizado na árvore!");
		int resultado = comparar(chaveRemover, raizArvore.getChave());
		if (resultado < 0)
			raizArvore.setEsquerda(remover(raizArvore.getEsquerda(), chaveRemover));
		else if (resultado > 0)
			raizArvore.setDireita(remover(raizArvore.getDireita(), chaveRemover));
		else {
			if (raizArvore.getEsquerda() == null)
				return raizArvore.getDireita();
			if (raizArvore.getDireita() == null)
				return raizArvore.getEsquerda();
			return removerNoAntecessor(raizArvore, raizArvore.getEsquerda());
		}
		return raizArvore;
	}

	protected No<K, V> removerNoAntecessor(No<K, V> itemRetirar, No<K, V> raizArvore) {
		if (raizArvore.getDireita() != null) {
			raizArvore.setDireita(removerNoAntecessor(itemRetirar, raizArvore.getDireita()));
			return raizArvore;
		}
		itemRetirar.setChave(raizArvore.getChave());
		itemRetirar.setItem(raizArvore.getItem());
		return raizArvore.getEsquerda();
	}

	@Override
	public int tamanho() {
		return tamanho;
	}

	@Override
	public String percorrer() {
		StringBuilder conteudo = new StringBuilder();
		percorrer(raiz, conteudo);
		return conteudo.toString();
	}

	private void percorrer(No<K, V> raizArvore, StringBuilder conteudo) {
		if (raizArvore != null) {
			percorrer(raizArvore.getEsquerda(), conteudo);
			conteudo.append(raizArvore.getItem()).append("\n");
			percorrer(raizArvore.getDireita(), conteudo);
		}
	}

	@Override
	public String toString() {
		return percorrer();
	}
}
