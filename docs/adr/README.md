# Architecture Decision Records — ADRs

> **Status:** convenção ativa

## Finalidade

ADRs registram decisões arquiteturais relevantes, o contexto que as motivou e
suas consequências. O Documento Mestre descreve a direção vigente; o ADR
preserva por que uma escolha foi feita e quando deve ser revista.

## Quando criar um ADR

Criar ou substituir um ADR quando uma decisão afetar:

- targets, source sets ou fronteiras multiplataforma;
- dependências estruturais ou stack central;
- limites entre UI, domínio, dados e infraestrutura;
- navegação, persistência ou compatibilidade de contratos;
- segurança, autenticação ou armazenamento de credenciais;
- integração com serviços externos;
- build, distribuição ou implantação.

Detalhes locais, reversíveis e sem consequência arquitetural não precisam de
ADR.

## Identificação e estados

- nome: `ADR-NNN-titulo-curto.md`;
- numeração sequencial e nunca reutilizada;
- estados: Proposto, Aprovado, Rejeitado, Substituído ou Obsoleto;
- uma decisão aprovada não é reescrita para esconder mudança posterior;
- uma substituição aponta para o ADR anterior.

## Modelo

```markdown
# ADR-NNN — Título

> **Estado:** Proposto
> **Data:** AAAA-MM-DD
> **Sprints relacionadas:** —

## Contexto
## Drivers
## Opções consideradas
## Decisão
## Consequências
## Evidências
## Critérios de revisão
```

## Registro

| ADR | Estado | Decisão |
|---|---|---|
| [ADR-001](ADR-001-compartilhar-aplicacao-kotlin-multiplatform.md) | Aprovado | compartilhar aplicação em Kotlin Multiplatform |
| [ADR-002](ADR-002-isolar-dominio-dos-contratos-anilist.md) | Aprovado | isolar domínio dos contratos AniList |
| [ADR-003](ADR-003-adotar-wasmjs-com-js-alternativo.md) | Aprovado | WasmJS principal e JS alternativo |
| [ADR-004](ADR-004-integrar-navigation3-ao-historico-com-fragmentos.md) | Aprovado | Navigation 3 com History API e fragmentos |
| [ADR-005](ADR-005-consumir-anilist-diretamente-no-web.md) | Aprovado | acesso direto à AniList enquanto CORS for viável |
| [ADR-006](ADR-006-adotar-anilist-como-fonte-do-catalogo.md) | Aprovado | AniList como fonte externa inicial do catálogo |
| [ADR-007](ADR-007-escopar-viewmodels-por-entrada-navigation3.md) | Aprovado | Lifecycle e Koin com ViewModelStore por entrada do Navigation 3 |
