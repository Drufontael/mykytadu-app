# Registro de evolução das sprints

> **Status:** convenção ativa

## Finalidade

Esta pasta mantém o histórico de execução das sprints do MykytaDu App. O
[`roadmap.md`](../roadmap.md) preserva o plano e o estado consolidado; cada
arquivo de sprint registra execução, evidências, bloqueios, mudanças de escopo e
encerramento.

O registro deve permitir identificar:

- o que foi planejado e entregue;
- quais critérios foram comprovados;
- quais decisões surgiram e onde foram formalizadas;
- quais limitações ou débitos permanecem;
- qual é o próximo passo autorizado.

## Fonte responsável por informação

| Informação | Documento responsável |
|---|---|
| visão, especificações e direções estáveis | `docs/documento-mestre.md` |
| planejamento e estado consolidado | `docs/roadmap.md` |
| execução e evidências | arquivo da sprint |
| decisão arquitetural e consequências | `docs/adr/ADR-NNN-*.md` |
| arquitetura e modelos vigentes | `docs/modelagem.md` |
| contratos técnicos específicos | documento técnico correspondente |

Planejamento não comprova implementação. Uma sprint somente é concluída após
implementação, validações aplicáveis, aceite humano e encerramento documental.

## Estados

| Estado | Significado |
|---|---|
| Não iniciada | existe somente no roadmap |
| Planejada | escopo preparado, sem execução iniciada |
| Em andamento | há trabalho autorizado em execução |
| Em validação | implementação pronta, aguardando validação ou aceite |
| Concluída | critérios comprovados, aceite concedido e encerramento registrado |
| Bloqueada | impedimento concreto impede progresso relevante |

## Convenções

- usar um arquivo por sprint: `S1.md`, `S6.md`, `W1.md`;
- manter subtasks dentro do registro da sprint;
- usar links relativos para documentos e ADRs;
- registrar datas somente quando comprovadas;
- marcar registros reconstruídos depois do encerramento como retrospectivos;
- não copiar logs extensos nem saídas de build;
- nunca registrar tokens, cookies, credenciais ou dados sensíveis;
- manter evidências pequenas, objetivas e verificáveis.

## Modelo

```markdown
# Sprint SX — Nome

> **Estado:** Planejada
> **Registro:** contemporâneo ou retrospectivo

## Objetivo
## Baseline
## Escopo e subtasks
## Critérios de aceite
## Decisões
## Bloqueios, riscos e mudanças de escopo
## Evidências
## Encerramento
### Entregue
### Não entregue
### Débitos e limitações
### Próximo passo
```

## Registros

| Sprint | Estado | Registro |
|---|---|---|
| S1 — Fundação | Concluída | [S1.md](S1.md) |
| S2 — Design System | Concluída | [S2.md](S2.md) |
| S3 — Navegação | Concluída | [S3.md](S3.md) |
| S4 — Comunicação | Concluída | [S4.md](S4.md) |
| S5 — Domínio do Catálogo | Concluída | [S5.md](S5.md) |
| W1 — Fundação Web | Concluída | [W1.md](W1.md) |
| S6 — Busca de Animes | Em andamento | [S6.md](S6.md) |
