import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;

    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    static IOrdenator<Produto> ordenador;

    /** Critérios de busca e ordenação */
    static final int SEM_ORDEM = 0;
    static final int CODIGO = 1;
    static final int DESCRICAO = 2;

    /** Extratores de chave: definem por qual atributo os produtos são buscados ou ordenados */
    static final Function<Produto, Integer> POR_CODIGO = p -> p.hashCode();
    static final Function<Produto, String> POR_DESCRICAO = p -> p.descricao.toLowerCase();

    /** Critério pelo qual o vetor de produtos está ordenado no momento (SEM_ORDEM, CODIGO ou DESCRICAO) */
    static int criterioOrdenacao = SEM_ORDEM;

    static ABB<String, Produto> produtosCadastradosPorNome;
    static ABB<Integer, Produto> produtosCadastradosPorId;
    static final int CAPACIDADE_HASH = 7753;
    static final int CAPACIDADE_HASH_ABERTO = 15511;

    static AVL<String, Produto> produtosPorNome;
    static AVL<Integer, Produto> produtosPorId;
    static TabelaHash<Integer, Produto> produtosPorIdHash;
    static TabelaHashAberto<Integer, Produto> produtosPorIdHashAberto;

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {

    	T valor;

    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    /**
     * Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário (-1, se a opção digitada for inválida).
    */
    static int menu() {
        cabecalho();
        System.out.println("Produtos ordenados por: " + nomeCriterio(criterioOrdenacao));
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar produto, por nome");
        System.out.println("3 - Procurar produto, por id");
        System.out.println("4 - Remover produto, por nome");
        System.out.println("5 - Remover produto, por id");
        System.out.println("6 - Recortar a lista de produtos, por nome");
        System.out.println("7 - Recortar a lista de produtos, por id");
        System.out.println("0 - Finalizar");

        Integer opcao = lerOpcao("Digite sua opção: ", Integer.class);
        return (opcao == null) ? -1 : opcao;
    }

    static String nomeCriterio(int criterio) {
    	return switch (criterio) {
    		case CODIGO -> "código";
    		case DESCRICAO -> "descrição";
    		default -> "nenhum critério (vetor embaralhado)";
    	};
    }

    /** Pergunta ao usuário o critério (atributo) de busca ou ordenação. */
    static int lerCriterio() {

    	Integer criterio;
    	do {
    		criterio = lerOpcao("Critério: 1 - código | 2 - descrição", Integer.class);
    	} while (criterio == null || (criterio != CODIGO && criterio != DESCRICAO));
    	return criterio;
    }

    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {

    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;

    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];

    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;

    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		if (arquivo != null) {
    			arquivo.close();
    		}
    	}

    	return produtosCadastrados;
    }

    /** Mostra o produto encontrado (ou a mensagem de não encontrado) e as medições da busca. */
    private static void mostrarProduto(Produto produto, IMedicao medicao) {

        StringBuilder mensagem = new StringBuilder("Produto não encontrado.\n");

        if (produto != null) {
            mensagem = new StringBuilder(String.format("%s\n", produto));
        }

        mensagem.append(String.format("\nTotal de comparações: %d\n", medicao.getComparacoes()));
        mensagem.append(String.format("Tempo gasto: %.4f milissegundos\n", medicao.getTempo()));

        System.out.println(mensagem.toString());
    }

    /** Ordena o vetor de produtos pelo critério informado, usando o Quicksort fornecido. */
    static void ordenarProdutos(int criterio) {

    	Comparator<Produto> comparador = (criterio == CODIGO)
    			? Comparator.comparing(POR_CODIGO)
    			: Comparator.comparing(POR_DESCRICAO);

    	ordenador = new Quicksort<>();
        produtosCadastrados = ordenador.ordenar(produtosCadastrados, comparador);
        criterioOrdenacao = criterio;
    }

    static void ordenarProdutos() {

    	cabecalho();
    	int criterio = lerCriterio();
    	ordenarProdutos(criterio);
    	System.out.println(String.format("Produtos ordenados por %s: %d comparações, %d movimentações, %.2f ms.",
    			nomeCriterio(criterio), ordenador.getComparacoes(), ordenador.getMovimentacoes(), ordenador.getTempo()));
    }

    private static void embaralharVetor(Produto[] vetor) {

    	Random sorteio = new Random();
    	for (int i = vetor.length - 1; i > 0; i--) {
    		int j = sorteio.nextInt(i + 1);
    		Produto temp = vetor[i];
    		vetor[i] = vetor[j];
    		vetor[j] = temp;
    	}
    }

    /** Embaralha o vetor de produtos (algoritmo de Fisher-Yates). */
    static void embaralharProdutos() {
    	embaralharVetor(produtosCadastrados);
    	criterioOrdenacao = SEM_ORDEM;
    }

    private static boolean vetorOrdenadoPor(int criterio) {
    	Comparator<Produto> comparador = (criterio == CODIGO)
    			? Comparator.comparing(POR_CODIGO)
    			: Comparator.comparing(POR_DESCRICAO);

    	for (int i = 1; i < quantosProdutos; i++) {
    		if (comparador.compare(produtosCadastrados[i - 1], produtosCadastrados[i]) > 0) {
    			return false;
    		}
    	}
    	return true;
    }

    private static void ordenarVetor(Produto[] vetor, int criterio) {
    	Comparator<Produto> comparador = (criterio == CODIGO)
    			? Comparator.comparing(POR_CODIGO)
    			: Comparator.comparing(POR_DESCRICAO);

    	ordenador = new Quicksort<>();
    	ordenador.ordenar(vetor, comparador);
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {

        cabecalho();
        System.out.println("\nProdutos cadastrados: ");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - [%d] %s", (i + 1), produtosCadastrados[i].hashCode(), produtosCadastrados[i].toString()));
        }
    }

    /** Executa a busca com o buscador informado e mostra o resultado e as medições. */
    static <K extends Comparable<K>> void buscar(IBuscador<Produto, K> buscador, K chave) {

    	Produto encontrado = null;
    	try {
    		encontrado = buscador.buscar(chave);
    	} catch (NoSuchElementException excecao) {
    		System.out.println(excecao.getMessage());
    	}
    	mostrarProduto(encontrado, buscador);
    }

    /**
     * Pergunta o critério (código ou descrição) e a chave, e localiza o produto por busca sequencial,
     * mostrando o resultado e as medições.
     */
    static void fazerBuscaSequencial() {
    	cabecalho();
    	int criterio = lerCriterio();

    	if (criterio == CODIGO) {
    		Integer codigo = lerOpcao("Digite o código do produto: ", Integer.class);
    		if (codigo == null) {
    			return;
    		}
    		IBuscador<Produto, Integer> buscador = new BuscaSequencial<>(produtosCadastrados, quantosProdutos, POR_CODIGO);
    		buscar(buscador, codigo);
    		return;
    	}

    	System.out.println("Digite a descrição do produto:");
    	String descricao = teclado.nextLine().trim().toLowerCase();
    	IBuscador<Produto, String> buscador = new BuscaSequencial<>(produtosCadastrados, quantosProdutos, POR_DESCRICAO);
    	buscar(buscador, descricao);
    }

    /**
     * Pergunta o critério (código ou descrição) e a chave, e localiza o produto por busca binária,
     * mostrando o resultado e as medições. Avisa o usuário se o vetor não estiver ordenado pelo critério escolhido.
     */
    static void fazerBuscaBinaria() {
    	cabecalho();
    	int criterio = lerCriterio();

    	if (!vetorOrdenadoPor(criterio)) {
    		System.out.println("Atenção: o vetor precisa estar ordenado por " + nomeCriterio(criterio) + ".");
    		return;
    	}

    	if (criterio == CODIGO) {
    		Integer codigo = lerOpcao("Digite o código do produto: ", Integer.class);
    		if (codigo == null) {
    			return;
    		}
    		IBuscador<Produto, Integer> buscador = new BuscaBinaria<>(produtosCadastrados, quantosProdutos, POR_CODIGO);
    		buscar(buscador, codigo);
    		return;
    	}

    	System.out.println("Digite a descrição do produto:");
    	String descricao = teclado.nextLine().trim().toLowerCase();
    	IBuscador<Produto, String> buscador = new BuscaBinaria<>(produtosCadastrados, quantosProdutos, POR_DESCRICAO);
    	buscar(buscador, descricao);
    }

    /**
     * Lista os produtos cuja descrição começa com o texto informado (sem diferenciar maiúsculas).
     * Exige o vetor ordenado por descrição: busca binária pela primeira posição candidata e,
     * a partir dela, varredura sequencial que para na primeira descrição que não começa com o texto.
     */
    static void localizarProdutosPorInicioDoNome() {
    	cabecalho();
    	System.out.println("Digite o início da descrição:");
    	String prefixo = teclado.nextLine().trim().toLowerCase();

    	if (!vetorOrdenadoPor(DESCRICAO)) {
    		System.out.println("Atenção: ordene os produtos por descrição antes de procurar por prefixo.");
    		return;
    	}

    	BuscaBinaria<Produto, String> buscador = new BuscaBinaria<>(produtosCadastrados, quantosProdutos, POR_DESCRICAO);
    	int posicaoInicial = buscador.primeiraPosicaoMaiorOuIgual(prefixo);
    	int encontrados = 0;

    	System.out.println("Produtos com descrição iniciando por \"" + prefixo + "\":");
    	for (int i = posicaoInicial; i < quantosProdutos; i++) {
    		String descricao = produtosCadastrados[i].descricao.toLowerCase();
    		if (!descricao.startsWith(prefixo)) {
    			break;
    		}
    		System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].descricao));
    		encontrados++;
    	}

    	if (encontrados == 0) {
    		System.out.println("Nenhum produto encontrado.");
    	}
    }

    /**
     * Compara busca sequencial e binária, por código e por descrição.
     * Chaves: todos os produtos cadastrados e 1000 chaves inexistentes (códigos 1 a 1000;
     * descrições "inexistente 1" a "inexistente 1000"). Antes de cada bateria de busca binária,
     * o vetor é embaralhado e ordenado pelo critério correspondente, e o custo dessa ordenação é exibido
     * (embaralhar antes evita o pior caso do quicksort com pivô na última posição: vetor já ordenado).
     */
    static void compararDesempenhoBuscas() {
    	cabecalho();
    	System.out.println("Comparação de desempenho de buscas\n");

    	for (int criterio : new int[] { CODIGO, DESCRICAO }) {
    		Produto[] sequencial = Arrays.copyOf(produtosCadastrados, quantosProdutos);
    		long comparacoesSequencial = 0;
    		double tempoSequencial = 0.0;

    		if (criterio == CODIGO) {
    			for (Produto produto : sequencial) {
    				BuscaSequencial<Produto, Integer> buscador = new BuscaSequencial<>(sequencial, quantosProdutos, POR_CODIGO);
    				buscador.buscar(produto.hashCode());
    				comparacoesSequencial += buscador.getComparacoes();
    				tempoSequencial += buscador.getTempo();
    			}
    			for (int chave = 1; chave <= 1000; chave++) {
    				BuscaSequencial<Produto, Integer> buscador = new BuscaSequencial<>(sequencial, quantosProdutos, POR_CODIGO);
    				try {
    					buscador.buscar(chave);
    				} catch (NoSuchElementException ignored) {
    				}
    				comparacoesSequencial += buscador.getComparacoes();
    				tempoSequencial += buscador.getTempo();
    			}
    		} else {
    			for (Produto produto : sequencial) {
    				BuscaSequencial<Produto, String> buscador = new BuscaSequencial<>(sequencial, quantosProdutos, POR_DESCRICAO);
    				buscador.buscar(produto.descricao.toLowerCase());
    				comparacoesSequencial += buscador.getComparacoes();
    				tempoSequencial += buscador.getTempo();
    			}
    			for (int indice = 1; indice <= 1000; indice++) {
    				String chave = "inexistente " + indice;
    				BuscaSequencial<Produto, String> buscador = new BuscaSequencial<>(sequencial, quantosProdutos, POR_DESCRICAO);
    				try {
    					buscador.buscar(chave);
    				} catch (NoSuchElementException ignored) {
    				}
    				comparacoesSequencial += buscador.getComparacoes();
    				tempoSequencial += buscador.getTempo();
    			}
    		}

    		System.out.println(String.format("Busca sequencial por %s: %d comparações, %.4f ms.",
    				nomeCriterio(criterio), comparacoesSequencial, tempoSequencial));

    		Produto[] binaria = Arrays.copyOf(produtosCadastrados, quantosProdutos);
    	embaralharVetor(binaria);
    		ordenarVetor(binaria, criterio);
    		System.out.println(String.format("Ordenação para busca binária por %s: %d comparações, %d movimentações, %.2f ms.",
    				nomeCriterio(criterio), ordenador.getComparacoes(), ordenador.getMovimentacoes(), ordenador.getTempo()));

    		long comparacoesBinaria = 0;
    		double tempoBinaria = 0.0;

    		if (criterio == CODIGO) {
    			for (Produto produto : binaria) {
    				BuscaBinaria<Produto, Integer> buscador = new BuscaBinaria<>(binaria, quantosProdutos, POR_CODIGO);
    				buscador.buscar(produto.hashCode());
    				comparacoesBinaria += buscador.getComparacoes();
    				tempoBinaria += buscador.getTempo();
    			}
    			for (int chave = 1; chave <= 1000; chave++) {
    				BuscaBinaria<Produto, Integer> buscador = new BuscaBinaria<>(binaria, quantosProdutos, POR_CODIGO);
    				try {
    					buscador.buscar(chave);
    				} catch (NoSuchElementException ignored) {
    				}
    				comparacoesBinaria += buscador.getComparacoes();
    				tempoBinaria += buscador.getTempo();
    			}
    		} else {
    			for (Produto produto : binaria) {
    				BuscaBinaria<Produto, String> buscador = new BuscaBinaria<>(binaria, quantosProdutos, POR_DESCRICAO);
    				buscador.buscar(produto.descricao.toLowerCase());
    				comparacoesBinaria += buscador.getComparacoes();
    				tempoBinaria += buscador.getTempo();
    			}
    			for (int indice = 1; indice <= 1000; indice++) {
    				String chave = "inexistente " + indice;
    				BuscaBinaria<Produto, String> buscador = new BuscaBinaria<>(binaria, quantosProdutos, POR_DESCRICAO);
    				try {
    					buscador.buscar(chave);
    				} catch (NoSuchElementException ignored) {
    				}
    				comparacoesBinaria += buscador.getComparacoes();
    				tempoBinaria += buscador.getTempo();
    			}
    		}

    		System.out.println(String.format("Busca binária por %s: %d comparações, %.4f ms.\n",
    				nomeCriterio(criterio), comparacoesBinaria, tempoBinaria));
    	}
    }

    static <K> ABB<K, Produto> lerProdutosArvore(String nomeArquivoDados, Function<Produto, K> extratorDeChave) {
        ABB<K, Produto> arvore = new ABB<>();
        try (Scanner arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"))) {
            int numProdutos = Integer.parseInt(arquivo.nextLine());
            for (int i = 0; i < numProdutos; i++) {
                Produto produto = Produto.criarDoTexto(arquivo.nextLine());
                arvore.inserir(extratorDeChave.apply(produto), produto);
            }
            quantosProdutos = numProdutos;
        } catch (IOException | RuntimeException excecao) {
            System.out.println("Erro ao carregar os produtos: " + excecao.getMessage());
        }
        return arvore;
    }

    static <K> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K procurado) {
        Produto encontrado = produtosCadastrados.pesquisar(procurado);
        System.out.printf("Comparações realizadas: %d%n", produtosCadastrados.getComparacoes());
        System.out.printf("Tempo de execução: %.4f milissegundos%n", produtosCadastrados.getTempo());
        return encontrado;
    }

    static Produto localizarProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        System.out.println("Digite o identificador do produto:");
        Integer id = lerOpcao("ID:", Integer.class);
        if (id == null)
            return null;
        return localizarProduto(produtosCadastrados, id);
    }

    static Produto localizarProdutoNome(ABB<String, Produto> produtosCadastrados) {
        System.out.println("Digite o nome do produto:");
        String nome = teclado.nextLine().trim().toLowerCase();
        if (nome.isEmpty())
            return null;
        return localizarProduto(produtosCadastrados, nome);
    }

    static <K> Produto removerProduto(ABB<K, Produto> produtosCadastrados, K chave) {
        try {
            Produto removido = produtosCadastrados.remover(chave);
            System.out.printf("Comparações realizadas: %d%n", produtosCadastrados.getComparacoes());
            System.out.printf("Tempo de execução: %.4f milissegundos%n", produtosCadastrados.getTempo());
            return removido;
        } catch (NoSuchElementException excecao) {
            System.out.println(excecao.getMessage());
            System.out.printf("Comparações realizadas: %d%n", produtosCadastrados.getComparacoes());
            System.out.printf("Tempo de execução: %.4f milissegundos%n", produtosCadastrados.getTempo());
            return null;
        }
    }

    static Produto removerProdutoNome(ABB<String, Produto> produtosCadastrados) {
        System.out.println("Digite o nome do produto:");
        String nome = teclado.nextLine().trim().toLowerCase();
        if (nome.isEmpty())
            return null;
        return removerProduto(produtosCadastrados, nome);
    }

    static Produto removerProdutoId(ABB<Integer, Produto> produtosCadastrados) {
        System.out.println("Digite o identificador do produto:");
        Integer id = lerOpcao("ID:", Integer.class);
        if (id == null)
            return null;
        return removerProduto(produtosCadastrados, id);
    }

    static Produto removerProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        return removerProdutoId(produtosCadastrados);
    }

    private static <K> void recortarProduto(AVL<K, Produto> produtosCadastrados, K deOnde, K ateOnde) {
        Lista<Produto> recorte = produtosCadastrados.recortar(deOnde, ateOnde);
        System.out.println("Produtos encontrados no recorte: " + recorte.tamanho());
        System.out.println("Comparações realizadas: " + produtosCadastrados.getComparacoes());
        System.out.printf("Tempo de execução: %.4f milissegundos%n", produtosCadastrados.getTempo());
        System.out.println(recorte);
    }

    private static void recortarProdutosNome(AVL<String, Produto> produtosCadastrados) {
        System.out.println("Digite o nome inicial do recorte:");
        String inicio = teclado.nextLine().trim().toLowerCase();
        System.out.println("Digite o nome final do recorte:");
        String fim = teclado.nextLine().trim().toLowerCase();
        recortarProduto(produtosCadastrados, inicio, fim);
    }

    private static void recortarProdutosId(AVL<Integer, Produto> produtosCadastrados) {
        Integer inicio = lerOpcao("Digite o primeiro identificador:", Integer.class);
        Integer fim = lerOpcao("Digite o último identificador:", Integer.class);
        if (inicio != null && fim != null)
            recortarProduto(produtosCadastrados, inicio, fim);
    }

    private static void mostrarProduto(Produto produto) {
        cabecalho();
        if (produto == null)
            System.out.println("Produto não encontrado.");
        else
            System.out.println(produto);
    }

    static Produto[] lerProdutosParaEstruturas(String nomeArquivo) {
        try (Scanner arquivo = new Scanner(new File(nomeArquivo), Charset.forName("UTF-8"))) {
            Produto[] produtos = new Produto[Integer.parseInt(arquivo.nextLine().trim())];
            for (int i = 0; i < produtos.length; i++)
                produtos[i] = Produto.criarDoTexto(arquivo.nextLine());
            quantosProdutos = produtos.length;
            return produtos;
        } catch (IOException | RuntimeException excecao) {
            System.out.println("Não foi possível ler os produtos: " + excecao.getMessage());
            return new Produto[0];
        }
    }

    static <K> void inserirProdutos(IMapeamento<K, Produto> estrutura, Produto[] produtos, Function<Produto, K> extrator) {
        for (Produto produto : produtos)
            estrutura.inserir(extrator.apply(produto), produto);
    }

    static <K> Produto pesquisarEstrutura(String nome, IMapeamento<K, Produto> estrutura, K chave) {
        try {
            Produto produto = estrutura.pesquisar(chave);
            System.out.printf("%-32s comparações: %d | tempo: %.4f ms%n",
                    nome, estrutura.getComparacoes(), estrutura.getTempo());
            return produto;
        } catch (NoSuchElementException excecao) {
            System.out.printf("%-32s comparações: %d | tempo: %.4f ms%n",
                    nome, estrutura.getComparacoes(), estrutura.getTempo());
            return null;
        }
    }

    static void procurarPorNomeAVL() {
        System.out.println("Nome do produto:");
        String nome = teclado.nextLine().trim();
        Produto produto = pesquisarEstrutura("Arvore AVL por nome", produtosPorNome, nome);
        if (produto == null) {
            try {
                Produto seguinte = produtosPorNome.teto(nome);
                System.out.println("Produto não encontrado. Primeiro produto seguinte:");
                System.out.println(seguinte);
                System.out.printf("AVL teto - comparações: %d | tempo: %.4f ms%n",
                        produtosPorNome.getComparacoes(), produtosPorNome.getTempo());
            } catch (NoSuchElementException excecao) {
                System.out.println("Produto não encontrado e não existe próximo produto em ordem alfabética.");
            }
        } else {
            System.out.println(produto);
        }
    }

    static void procurarPorIdEstruturas() {
        Integer id = lerOpcao("Id do produto:", Integer.class);
        if (id == null) return;
        Produto produto = pesquisarEstrutura("Árvore AVL por id", produtosPorId, id);
        pesquisarEstrutura("Hash com endereçamento separado", produtosPorIdHash, id);
        pesquisarEstrutura("Hash com endereçamento aberto", produtosPorIdHashAberto, id);
        System.out.println(produto != null ? produto : "Produto não encontrado.");
    }

    static void recortarNome() {
        System.out.println("Nome inicial:");
        String inicio = teclado.nextLine().trim();
        System.out.println("Nome final:");
        String fim = teclado.nextLine().trim();
        try {
            Lista<Produto> recorte = produtosPorNome.recortar(inicio, fim);
            System.out.println("Produtos encontrados: " + recorte.tamanho());
            System.out.println("Comparações realizadas: " + produtosPorNome.getComparacoes());
            System.out.println(recorte);
        } catch (IllegalArgumentException excecao) {
            System.out.println(excecao.getMessage());
        }
    }

    static void recortarId() {
        Integer inicio = lerOpcao("Primeiro id:", Integer.class);
        Integer fim = lerOpcao("Último id:", Integer.class);
        if (inicio == null || fim == null) return;
        try {
            Lista<Produto> recorte = produtosPorId.recortar(inicio, fim);
            System.out.println("Produtos encontrados: " + recorte.tamanho());
            System.out.println("Comparações realizadas: " + produtosPorId.getComparacoes());
            System.out.println(recorte);
        } catch (IllegalArgumentException excecao) {
            System.out.println(excecao.getMessage());
        }
    }

    public static void main(String[] args) {
        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        Produto[] produtos = lerProdutosParaEstruturas(nomeArquivoDados);

        produtosPorNome = new AVL<>(String.CASE_INSENSITIVE_ORDER);
        produtosPorId = new AVL<>();
        produtosPorIdHash = new TabelaHash<>(CAPACIDADE_HASH);
        produtosPorIdHashAberto = new TabelaHashAberto<>(CAPACIDADE_HASH_ABERTO);

        inserirProdutos(produtosPorNome, produtos, p -> p.descricao);
        inserirProdutos(produtosPorId, produtos, p -> p.idProduto);
        inserirProdutos(produtosPorIdHash, produtos, p -> p.idProduto);
        inserirProdutos(produtosPorIdHashAberto, produtos, p -> p.idProduto);

        int opcao;
        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> System.out.println(produtosPorNome.percorrer());
                case 2 -> procurarPorNomeAVL();
                case 3 -> procurarPorIdEstruturas();
                case 4 -> System.out.println("Remoção por nome ainda não integrada ao menu final.");
                case 5 -> System.out.println("Remoção por id ainda não integrada ao menu final.");
                case 6 -> recortarNome();
                case 7 -> recortarId();
                case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
                default -> System.out.println("Opção inválida.");
            }
            if (opcao != 0) pausa();
        } while (opcao != 0);

        teclado.close();
    }
}
