public class No<K, V> {

    private K chave;
    private V item;
    private No<K, V> direita;
    private No<K, V> esquerda;
    private int altura;

    public No(K chave, V item) {
        setChave(chave);
        setItem(item);
        setDireita(null);
        setEsquerda(null);
        altura = 1;
    }

    public V getItem() { return item; }
    public void setItem(V item) { this.item = item; }
    public K getChave() { return chave; }
    public void setChave(K chave) { this.chave = chave; }
    public No<K, V> getDireita() { return direita; }
    public void setDireita(No<K, V> direita) { this.direita = direita; }
    public No<K, V> getEsquerda() { return esquerda; }
    public void setEsquerda(No<K, V> esquerda) { this.esquerda = esquerda; }

    private static int altura(No<?, ?> no) {
        return (no == null) ? 0 : no.altura;
    }

    public int getAltura() { return altura; }

    public void setAltura() {
        altura = 1 + Math.max(altura(esquerda), altura(direita));
    }

    public int getFatorBalanceamento() {
        return altura(esquerda) - altura(direita);
    }
}
