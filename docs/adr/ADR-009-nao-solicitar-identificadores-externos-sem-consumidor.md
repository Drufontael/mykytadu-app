# ADR-009 — Não solicitar identificadores externos sem consumidor

> **Estado:** Aprovado
> **Data:** 2026-09-23
> **Sprints relacionadas:** correção arquitetural anterior à S7

## Contexto

O ADR-006 adotou a AniList como fonte inicial do catálogo e decidiu preservar
`Media.idMal` como referência externa auxiliar e opcional. As consultas de
pesquisa e detalhes passaram a solicitar o campo, os DTOs remotos o
representavam e os modelos de domínio o transportavam.

A revisão de identidade do catálogo removeu `idMal` do domínio porque não existe
consumidor funcional. Mantê-lo nas queries e nos DTOs ainda aumentaria o
contrato remoto e o volume de dados sem atender um caso de uso.

## Drivers

- Solicitar da API GraphQL somente campos consumidos pelo aplicativo.
- Evitar contratos e testes mantidos para necessidades hipotéticas.
- Não transformar um identificador de outro catálogo em identidade do domínio.
- Permitir que uma referência externa seja modelada com origem explícita quando
  surgir um consumidor real.

## Opções consideradas

1. Manter `idMal` somente nos DTOs e nas queries, sem expô-lo ao domínio.
2. Remover `idMal` das queries, DTOs e testes enquanto não houver consumidor.
3. Criar agora um modelo genérico de referências externas.

## Decisão

Adotar a opção 2. As operações atuais da AniList deixam de solicitar
`Media.idMal`, e os DTOs remotos deixam de representá-lo.

Esta decisão substitui somente a preservação de `Media.idMal` definida no
ADR-006. A escolha da AniList, o uso de `Media.id` como chave nativa atual e as
demais consequências daquele ADR permanecem vigentes.

Se um caso de uso futuro precisar de MyAnimeList ou outro catálogo, a referência
será introduzida junto ao consumidor por um contrato tipado que identifique a
origem e o valor. Não se presume que essa referência será a identidade principal
do catálogo ou de uma entidade local.

## Consequências

- as queries ficam menores e alinhadas ao uso efetivo;
- DTOs, mapeadores e modelos deixam de manter um campo sem consumidor;
- nenhum dado persistido exige migração, pois ainda não existe persistência do
  catálogo ou da biblioteca;
- adicionar links ou reconciliação com outro catálogo exigirá uma nova mudança
  explícita de contrato;
- exemplos e documentação da API precisam distinguir observações históricas do
  contrato efetivamente solicitado pelo aplicativo.

## Evidências atuais

- não existe leitura funcional de `idMal` fora de testes e documentação;
- a busca, os detalhes, a navegação e a deduplicação usam `Media.id`, traduzido
  para `CatalogAnimeId` na camada de dados;
- GraphQL permite omitir `idMal` sem afetar os demais campos das operações.

As operações e os DTOs foram atualizados, e a ausência do campo nas queries foi
coberta por testes. A regressão aplicável foi aprovada antes do encerramento da
decisão.

## Critérios de revisão

Reavaliar quando existir um consumidor concreto para:

- abrir links do MyAnimeList;
- importar ou exportar dados entre catálogos;
- reconciliar obras de provedores diferentes;
- sincronizar uma referência externa exigida pelo backend.
