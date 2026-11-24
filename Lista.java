import java.util.NoSuchElementException;

public class Lista<E> {

	private Celula<E> primeiro;
	private Celula<E> ultimo;
	private int tamanho;
	private long comparacoes;

	public Lista() {
		Celula<E> sentinela = new Celula<>();
		this.primeiro = this.ultimo = sentinela;
		this.tamanho = 0;
		this.comparacoes = 0;
	}

	public boolean vazia() {
		return tamanho == 0;
	}

	public int tamanho() {
		return tamanho;
	}

	public void inserir(E novo, int posicao) {
		if (posicao < 0 || posicao > tamanho)
			throw new IndexOutOfBoundsException("Posição inválida!");
		Celula<E> anterior = primeiro;
		for (int i = 0; i < posicao; i++)
			anterior = anterior.getProximo();
		Celula<E> nova = new Celula<>(novo);
		Celula<E> proxima = anterior.getProximo();
		anterior.setProximo(nova);
		nova.setProximo(proxima);
		if (posicao == tamanho)
			ultimo = nova;
		tamanho++;
	}

	public void inserirFinal(E novo) {
		Celula<E> nova = new Celula<>(novo);
		ultimo.setProximo(nova);
		ultimo = nova;
		tamanho++;
	}

	private E removerProxima(Celula<E> anterior) {
		Celula<E> removida = anterior.getProximo();
		Celula<E> proxima = removida.getProximo();
		anterior.setProximo(proxima);
		removida.setProximo(null);
		if (removida == ultimo)
			ultimo = anterior;
		tamanho--;
		return removida.getItem();
	}

	public E remover(int posicao) {
		if (vazia())
			throw new IllegalStateException("A lista está vazia!");
		if (posicao < 0 || posicao >= tamanho)
			throw new IndexOutOfBoundsException("Posição inválida!");
		Celula<E> anterior = primeiro;
		for (int i = 0; i < posicao; i++)
			anterior = anterior.getProximo();
		return removerProxima(anterior);
	}

	public E remover(E elemento) {
		comparacoes = 0;
		Celula<E> anterior = primeiro;
		while (anterior.getProximo() != null && !igual(anterior.getProximo().getItem(), elemento))
			anterior = anterior.getProximo();
		if (anterior.getProximo() == null)
			throw new NoSuchElementException("Item não encontrado!");
		return removerProxima(anterior);
	}

	public E pesquisar(E procurado) {
		comparacoes = 0;
		Celula<E> atual = primeiro.getProximo();
		while (atual != null) {
			if (igual(atual.getItem(), procurado))
				return atual.getItem();
			atual = atual.getProximo();
		}
		throw new NoSuchElementException("Item não encontrado!");
	}

	private boolean igual(E elemento, E outro) {
		comparacoes++;
		return elemento.equals(outro);
	}

	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public String toString() {
		if (vazia())
			return "A lista está vazia!\n";
		StringBuilder texto = new StringBuilder();
		Celula<E> atual = primeiro.getProximo();
		while (atual != null) {
			texto.append(atual.getItem()).append("\n");
			atual = atual.getProximo();
		}
		return texto.toString();
	}
}
