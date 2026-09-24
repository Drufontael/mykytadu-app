# ADR-008 — Desacoplar a identidade do catálogo do provedor externo

> **Estado:** Aprovado
> **Data:** 2026-09-23
> **Sprints relacionadas:** correção arquitetural anterior à S7

## Contexto

O domínio atual usa `AniListAnimeId` como identidade principal de
`AnimeSummary`, `AnimeDetails` e `AnimeRelation`. O mesmo tipo atravessa o
contrato de `AnimeRepository`, a navegação, os deep links e a interface.

Essa escolha preserva a distinção entre o ID da AniList e futuros IDs locais ou
do backend, conforme o ADR-002, mas faz o contrato de domínio nomear o provedor
externo. Como a AniList é a fonte inicial do catálogo, e não a autoridade de
identidade do MykytaDu, substituir ou combinar provedores exigiria alterar o
domínio e seus consumidores em vez de concentrar a mudança na camada de dados.

`idMal: Int?` também atravessa os modelos de domínio sem um consumidor atual e
sem um tipo que explicite seu sistema de origem.

## Drivers

- Manter o domínio independente do fornecedor atual do catálogo.
- Preservar a distinção entre identidade de catálogo, identidade local da
  biblioteca e identidade futura do backend.
- Permitir que adapters traduzam os identificadores nativos de cada provedor.
- Evitar criar antecipadamente uma identidade local universal sem persistência,
  reconciliação de provedores ou autoridade responsável por atribuí-la.
- Manter compatíveis as URLs e os deep links de detalhes já publicados.
- Remover dos modelos de domínio referências externas sem consumidor concreto.

## Opções consideradas

1. **Manter `AniListAnimeId`:** representa com precisão a origem atual, mas
   mantém domínio, navegação e UI acoplados à AniList.
2. **Renomear para `AnimeId`:** remove o nome do fornecedor, mas confunde a
   identidade de uma entrada do catálogo com futuros IDs locais e do backend.
3. **Criar agora um ID local universal:** oferece identidade própria, mas exige
   uma política ainda inexistente de geração, persistência e reconciliação.
4. **Introduzir `CatalogAnimeId` opaco:** expressa o contexto ao qual o ID
   pertence sem expor o provedor e mantém identidades futuras separadas.

## Decisão

Adotar a opção 4. O domínio deve representar a identidade retornada pelo
catálogo por meio de `CatalogAnimeId`, um value class opaco baseado em `String`
não vazia. O tipo será usado por modelos do catálogo, repositories, navegação e
UI, sem expor como o provedor representa sua chave nativa.

A implementação `AniListAnimeRepository` será responsável por converter o
`Int` recebido da AniList em `CatalogAnimeId` e por validar a conversão inversa
antes de consultar detalhes. `AnimeApi` e seus DTOs continuarão usando o formato
nativo da AniList dentro da camada de dados.

`CatalogAnimeId` não será reutilizado como ID de uma entrada da biblioteca nem
como ID de recursos do backend. Esses conceitos receberão tipos próprios quando
existirem consumidores concretos.

As rotas Web e os deep links manterão o formato textual atual, como
`#/anime/21579` e `mykytadu://app/anime/21579`. A compatibilidade externa do
endereço não depende do nome ou da representação interna do tipo.

`idMal` será removido dos modelos de domínio enquanto não houver consumidor. Se
um caso de uso exigir referências externas auxiliares, elas serão introduzidas
por um contrato tipado que identifique o catálogo de origem e seu valor, em vez
de um campo específico e sem semântica própria.

Esta decisão refina somente a política de identidade do ADR-002. Permanecem
vigentes suas decisões de isolar DTOs, GraphQL, Ktor e resultados de rede. O
ADR-006 continua definindo a AniList como fonte externa inicial do catálogo.

## Consequências

- domínio, navegação e UI deixam de nomear a AniList em seus contratos;
- o repository concreto passa a ser responsável pela tradução da identidade;
- trocar de provedor não garante que IDs ou URLs antigos sejam resolvíveis; essa
  compatibilidade exigirá migração ou mapeamento explícito quando houver um caso
  real;
- usar `String` evita impor a todos os provedores uma chave numérica, mas exige
  validação no adapter quando o provedor atual aceitar somente inteiros;
- a migração altera modelos, repository, mapeadores, rotas, serializers, UI,
  diagnósticos e testes;
- remover `idMal` reduz dados antecipados no domínio e não impede que o DTO
  remoto continue representando a resposta da AniList;
- nenhum ID local ou do backend é criado por esta decisão.

## Evidências atuais

- `AniListAnimeId` está declarado em `domain.model` e é usado por modelos do
  catálogo e pelo contrato de `AnimeRepository`.
- `AppRoute.AnimeDetails`, o codec Web e a UI de busca transportam esse tipo.
- os mapeadores convertem diretamente `Media.id` para `AniListAnimeId`.
- `idMal` está presente em `AnimeSummary` e `AnimeDetails`, mas não possui
  consumidor funcional no código atual.
- a URL de detalhes já serializa somente o valor numérico, permitindo preservar
  seu formato durante a migração.

Essas evidências justificam a decisão proposta, mas não comprovam sua
implementação. A adoção somente estará concluída após migração e validação dos
targets aplicáveis.

## Critérios de revisão

Reavaliar esta decisão quando:

- o MykytaDu possuir uma autoridade própria para identidade canônica de anime;
- múltiplos provedores precisarem coexistir simultaneamente;
- URLs precisarem identificar explicitamente o provedor de origem;
- persistência ou sincronização exigir reconciliação estável entre catálogos;
- um consumidor concreto demandar referências externas auxiliares.
