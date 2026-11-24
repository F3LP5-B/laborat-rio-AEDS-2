import java.util.Comparator;
import java.util.NoSuchElementException;

public class AVL<K, V> extends ABB<K, V> {

	public AVL() {
		super();
	}

	public AVL(Comparator<K> comparador) {
		super(comparador);
	}

	@Override
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

		// O balanceamento ocorre no retorno de cada chamada recursiva,
		// portanto todos os nós do caminho percorrido são atualizados.
		return balancear(raizArvore);
	}

	@Override
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
			raizArvore = removerNoAntecessor(raizArvore, raizArvore.getEsquerda());
		}

		return balancear(raizArvore);
	}

	@Override
	protected No<K, V> removerNoAntecessor(No<K, V> itemRetirar, No<K, V> raizArvore) {
		if (raizArvore.getDireita() != null) {
			raizArvore.setDireita(removerNoAntecessor(itemRetirar, raizArvore.getDireita()));
			return balancear(raizArvore);
		}
		itemRetirar.setChave(raizArvore.getChave());
		itemRetirar.setItem(raizArvore.getItem());
		return raizArvore.getEsquerda();
	}

	private No<K, V> balancear(No<K, V> raizArvore) {
		if (raizArvore == null)
			return null;

		int fator = raizArvore.getFatorBalanceamento();
		if (fator == 2) {
			if (raizArvore.getEsquerda().getFatorBalanceamento() == -1)
				raizArvore.setEsquerda(rotacionarEsquerda(raizArvore.getEsquerda()));
			return rotacionarDireita(raizArvore);
		}
		if (fator == -2) {
			if (raizArvore.getDireita().getFatorBalanceamento() == 1)
				raizArvore.setDireita(rotacionarDireita(raizArvore.getDireita()));
			return rotacionarEsquerda(raizArvore);
		}

		raizArvore.setAltura();
		return raizArvore;
	}

	private No<K, V> rotacionarDireita(No<K, V> p) {
		No<K, V> novaRaiz = p.getEsquerda();
		p.setEsquerda(novaRaiz.getDireita());
		p.setAltura();
		novaRaiz.setDireita(p);
		novaRaiz.setAltura();
		return novaRaiz;
	}

	private No<K, V> rotacionarEsquerda(No<K, V> p) {
		No<K, V> novaRaiz = p.getDireita();
		p.setDireita(novaRaiz.getEsquerda());
		p.setAltura();
		novaRaiz.setEsquerda(p);
		novaRaiz.setAltura();
		return novaRaiz;
	}

	public Lista<V> recortar(K deOnde, K ateOnde) {
		iniciarMedicao();
		try {
			if (comparar(deOnde, ateOnde) > 0)
				throw new IllegalArgumentException("O início do intervalo não pode ser maior que o fim.");
			Lista<V> resultado = new Lista<>();
			recortar(raiz, deOnde, ateOnde, resultado);
			return resultado;
		} finally {
			finalizarMedicao();
		}
	}

	private void recortar(No<K, V> no, K deOnde, K ateOnde, Lista<V> resultado) {
		if (no == null)
			return;

		if (comparar(deOnde, no.getChave()) < 0)
			recortar(no.getEsquerda(), deOnde, ateOnde, resultado);

		if (comparar(no.getChave(), deOnde) >= 0 && comparar(no.getChave(), ateOnde) <= 0)
			resultado.inserirFinal(no.getItem());

		if (comparar(no.getChave(), ateOnde) < 0)
			recortar(no.getDireita(), deOnde, ateOnde, resultado);
	}

	public V teto(K chave) {
		iniciarMedicao();
		try {
			No<K, V> atual = raiz;
			V candidato = null;
			while (atual != null) {
				int resultado = comparar(chave, atual.getChave());
				if (resultado == 0)
					return atual.getItem();
				if (resultado < 0) {
					candidato = atual.getItem();
					atual = atual.getEsquerda();
				} else {
					atual = atual.getDireita();
				}
			}
			if (candidato == null)
				throw new NoSuchElementException("Todas as chaves são menores que a chave informada.");
			return candidato;
		} finally {
			finalizarMedicao();
		}
	}
}
