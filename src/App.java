import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

public class App {

    static Scanner teclado;
    static Produto[] produtos = new Produto[0];
    static final String NOME_ARQUIVO = "dadosProdutos.csv";

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("Pressione Enter para continuar...");
        teclado.nextLine();
    }

    static int menu() {
        limparTela();
        System.out.println("XABUMBA - SISTEMA DE VENDAS");
        System.out.println("=========================");
        System.out.println("1 - Carregar produtos");
        System.out.println("2 - Cadastrar produto");
        System.out.println("3 - Localizar produto");
        System.out.println("4 - Listar todos os produtos");
        System.out.println("5 - Salvar produtos");
        System.out.println("0 - Sair");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(teclado.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }

    public static void main(String[] args) throws Exception {
        teclado = new Scanner(System.in);
        int opcao = -1;

        // Tenta carregar produtos automaticamente ao iniciar
        produtos = lerProdutos(NOME_ARQUIVO);

        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> {
                    produtos = lerProdutos(NOME_ARQUIVO);
                    System.out.println(produtos.length + " produto(s) carregado(s) com sucesso!");
                }
                case 2 -> cadastrarProduto();
                case 3 -> localizarProdutos();
                case 4 -> listarTodosOsProdutos();
                case 5 -> salvarProdutos(NOME_ARQUIVO);
                case 0 -> {
                    salvarProdutos(NOME_ARQUIVO);
                    System.out.println("Saindo do sistema...");
                }
                default -> System.out.println("Opção inválida!");
            }
            if (opcao != 0) {
                pausa();
            }
        } while (opcao != 0);

        teclado.close();
    }

    // ==========================================
    // TAREFA 3: Métodos exigidos pelo enunciado
    // ==========================================

    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
        File arquivo = new File(nomeArquivoDados);
        if (!arquivo.exists()) {
            return new Produto[0];
        }

        try (Scanner leitor = new Scanner(arquivo)) {
            if (!leitor.hasNextLine()) {
                return new Produto[0];
            }

            int n = Integer.parseInt(leitor.nextLine().trim());
            Produto[] vetor = new Produto[n];
            int i = 0;

            while (leitor.hasNextLine() && i < n) {
                String linha = leitor.nextLine().trim();
                if (!linha.isEmpty()) {
                    vetor[i] = Produto.criarDoTexto(linha);
                    i++;
                }
            }
            return vetor;

        } catch (Exception e) {
            System.err.println("Erro ao ler o arquivo de dados: " + e.getMessage());
            return new Produto[0];
        }
    }

    /**
     * Localiza um produto no vetor por descrição (não sensível ao caso).
     */
    static void localizarProdutos() {
        if (produtos.length == 0) {
            System.out.println("Nenhum produto cadastrado no vetor.");
            return;
        }

        System.out.print("Informe o nome do produto para busca: ");
        String termo = teclado.nextLine().trim();

        boolean encontrado = false;
        for (Produto p : produtos) {
            if (p != null && p.getDescricao().equalsIgnoreCase(termo)) {
                System.out.println("Produto encontrado:");
                System.out.println(p);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            System.out.println("Produto não encontrado.");
        }
    }

    /**
     * Salva os produtos cadastrados no arquivo informado.
     */
    static void salvarProdutos(String nomeArquivo) {
        try (PrintWriter escritor = new PrintWriter(nomeArquivo)) {
            escritor.println(produtos.length);
            for (Produto p : produtos) {
                if (p != null) {
                    escritor.println(p.gerarDadosTexto());
                }
            }
            System.out.println("Dados salvos em '" + nomeArquivo + "' com sucesso!");
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    // ==========================================
    // TAREFA 4: Métodos exigidos pelo enunciado
    // ==========================================

    /**
     * Lista todos os produtos cadastrados numerados, um por linha.
     */
    static void listarTodosOsProdutos() {
        if (produtos.length == 0) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }

        System.out.println("\n--- LISTA DE PRODUTOS CADASTRADOS ---");
        for (int i = 0; i < produtos.length; i++) {
            System.out.println((i + 1) + ". " + produtos[i]);
        }
    }

    /**
     * Cadastra um novo produto perguntando os dados ao usuário.
     */
    static void cadastrarProduto() {
        System.out.println("\n--- CADASTRO DE PRODUTO ---");
        System.out.print("Tipo do produto (1 - Não Perecível | 2 - Perecível): ");
        int tipo = Integer.parseInt(teclado.nextLine().trim());

        if (tipo != 1 && tipo != 2) {
            System.out.println("Tipo inválido! Cadastro cancelado.");
            return;
        }

        System.out.print("Descrição: ");
        String descricao = teclado.nextLine().trim();

        System.out.print("Preço de custo: ");
        double precoCusto = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));

        System.out.print("Margem de lucro (ex: 0.25 para 25%): ");
        double margemLucro = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));

        Produto novo = null;
        if (tipo == 1) {
            novo = new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
        } else {
            System.out.print("Data de validade (dd/mm/aaaa): ");
            String dataStr = teclado.nextLine().trim();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataValidade = LocalDate.parse(dataStr, fmt);
            novo = new ProdutoPerecivel(descricao, precoCusto, margemLucro, dataValidade);
        }

        // Insere o produto no vetor expandindo o tamanho do array em 1
        produtos = Arrays.copyOf(produtos, produtos.length + 1);
        produtos[produtos.length - 1] = novo;

        System.out.println("Produto cadastrado com sucesso!");
    }
}
