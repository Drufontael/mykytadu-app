# ADR-003 — Adotar WasmJS com JavaScript alternativo

> **Estado:** Aprovado
> **Registro:** retrospectivo
> **Sprint relacionada:** W1

## Contexto

O projeto precisava executar no navegador sem criar outro frontend. Compose
Multiplatform oferece targets JavaScript e WasmJS com capacidades e requisitos
de compatibilidade distintos.

## Decisão

Usar WasmJS como target Web principal e JavaScript como alternativa de
compatibilidade. Ambos permanecem no módulo `:composeApp`, compartilham
`webMain` e produzem distribuições independentes.

Não existe seleção automática de fallback entre os artefatos.

## Consequências

- código Web compartilhável fica em `webMain`;
- particularidades de interoperabilidade ficam em `jsMain` ou `wasmJsMain`;
- dependências novas precisam ser comprovadas separadamente nos dois targets;
- distribuição e hospedagem devem escolher explicitamente o artefato servido.

## Evidências

Builds, testes browser, execução em desenvolvimento e distribuições de produção
foram aprovados em JS e WasmJS durante a W1.

## Critérios de revisão

Reavaliar quando suporte dos navegadores, tamanho, desempenho ou manutenção
justificarem manter apenas um target ou introduzir seleção de fallback.
