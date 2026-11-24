import java.util.Objects;

public class Entrada<K, V> {

	private final K chave;
	private V valor;
	private boolean removida;

	public Entrada(K chave, V item) {
		this.chave = chave;
		this.valor = item;
		this.removida = false;
	}

	public K getChave() { return chave; }
	public V getValor() { return valor; }
	public void setValor(V valor) { this.valor = valor; }
	public boolean isRemovida() { return removida; }
	public void setRemovida(boolean removida) { this.removida = removida; }

	@Override
	public boolean equals(Object outro) {
		if (this == outro) return true;
		if (outro == null || this.getClass() != outro.getClass()) return false;
		Entrada<?, ?> outraEntrada = (Entrada<?, ?>) outro;
		return this.chave.equals(outraEntrada.chave);
	}

	@Override
	public int hashCode() { return Objects.hash(chave); }

	@Override
	public String toString() { return chave + "\n" + valor; }
}
