# AEDs II - Oficinas e Atividade 3 - Busca em Sistemas de software

Oficinas e atividade pontuada realizada em AEDs II, com foco em estruturas de dados e análise de desempenho de algoritmos de busca em sistemas de software.

## Aluno

* Felipe Bragança Godinho

## Objetivo da atividade

O projeto simula um sistema de cadastro de produtos e implementa diferentes formas de busca e ordenação para analisar o comportamento de algoritmos em estruturas de dados lineares.

A atividade contempla:
- busca sequencial
- busca binária
- ordenação por quicksort
- busca por prefixo de descrição
- comparação de desempenho entre tipos de busca

## Estrutura do projeto

Os arquivos principais do sistema são:

- App.java: menu principal e fluxo da aplicação
- Produto.java: abstração base dos produtos
- ProdutoNaoPerecivel.java: produto sem validade
- ProdutoPerecivel.java: produto com validade
- BuscaSequencial.java: implementação da busca sequencial
- BuscaBinaria.java: implementação da busca binária
- Quicksort.java: algoritmo de ordenação utilizado
- IBuscador.java: contrato para buscas
- IOrdenator.java: contrato para ordenação
- IMedicao.java: interface para medição de comparações e tempo
- produtos.txt: base de dados dos produtos

## Funcionalidades implementadas

1. Ordenar produtos por código ou descrição.
2. Embaralhar o vetor de produtos.
3. Realizar busca sequencial por código ou descrição.
4. Realizar busca binária por código ou descrição.
5. Listar todos os produtos cadastrados.
6. Localizar produtos pelo início do nome/descrição.
7. Comparar desempenho entre busca sequencial e busca binária.

## Regras de busca e ordenação

- A busca sequencial percorre o vetor da esquerda para a direita.
- A busca binária exige que o vetor esteja ordenado pela mesma chave usada na busca.
- O quicksort é usado para ordenar os produtos por código ou descrição.
- O sistema também mede:
  - número de comparações realizadas
  - tempo de execução em milissegundos

## Como executar

A aplicação foi desenvolvida em Java e pode ser executada a partir da linha de comando:

```bash
javac *.java
java App
```

> Importante: o projeto foi compilado com Java 17+, então a execução deve ser feita com uma versão compatível do JDK.

## Observação

Este projeto tem caráter acadêmico e foi desenvolvido para demonstrar a diferença prática entre algoritmos de busca em termos de eficiência, custo computacional e comportamento em vetores ordenados e não ordenados.

## Tarefa 5 – Comparação de desempenho das buscas

A aplicação inclui a opção de comparar o desempenho das buscas sequencial e binária para cada critério (código e descrição). A bateria executa as mesmas buscas para todos os produtos cadastrados e para 1.000 chaves inexistentes.

### Tabela de resultados

| Critério | Método | Encontrados | Comparações (total) | Comparações por busca (média) | Tempo (ms) |
|---|---|---:|---:|---:|---:|
| Código | Sequencial |  |  |  |  |
| Código | Binária |  |  |  |  |
| Descrição | Sequencial |  |  |  |  |
| Descrição | Binária |  |  |  |  |

### Tempo de criação do índice por descrição

- Tempo de criação do índice por descrição: ___ ms

### Comparação com a complexidade esperada

Para n = 7.750 elementos:

- Busca sequencial:
  - caso médio para chave existente: $\frac{n}{2} = 3.875$
  - pior caso: $n = 7.750$
  - complexidade: $O(n)$

- Busca binária:
  - caso médio e pior caso: $\lceil \log_2(n+1) \rceil \approx 13$
  - complexidade: $O(\log n)$

A comparação deve ser feita separando chaves existentes e inexistentes:

- Chaves existentes:
  - sequencial: média aproximada de 3.875 comparações
  - binária: cerca de 13 comparações

- Chaves inexistentes:
  - sequencial: em média, próxima de 3.875 a 7.750 comparações, dependendo da posição da primeira falha
  - binária: ainda em torno de 13 comparações, pois percorre o intervalo de busca até concluir que a chave não existe

### Cálculo do ponto de equilíbrio do índice por descrição

A criação do índice ordenado por descrição compensa quando o número de buscas por nome é grande o suficiente para amortizar o custo inicial de ordenação.

Se:

- $T_{indice}$ = tempo de criação do índice por descrição
- $T_{seq}$ = tempo da busca sequencial por nome
- $T_{bin}$ = tempo da busca binária no índice por descrição

então o índice compensa quando:

$$
T_{indice} + k \times T_{bin} < k \times T_{seq}
$$

onde $k$ é o número de buscas por nome realizadas.

Logo:

$$
k > \frac{T_{indice}}{T_{seq} - T_{bin}}
$$

Esse valor representa o número mínimo de buscas por nome necessário para que o uso do índice ordenado e da busca binária seja mais eficiente do que repetir a busca sequencial no vetor original.

### Observação sobre o método existeNoPedido

O método existeNoPedido da classe Pedido usa busca sequencial no vetor de itens do pedido. Essa estratégia é aceitável para pedidos pequenos, mas, quando o número de itens cresce, a busca binária pode passar a ser interessante. A troca só compensa caso o vetor de itens do pedido seja frequentemente consultado e tenha tamanho suficiente para justificar o custo de manter uma ordenação adicional. Em geral, para listas pequenas, a busca sequencial é simples e eficiente; para volumes maiores, a busca binária pode reduzir o número de comparações de forma significativa.


