---
name: mykytadu-task-acceptance
description: Orienta o encerramento documental e validação de tasks no MykytaDu de forma integrada e contemporânea à implementação, gerando commit local e preservando a trava no push.
---

# MykytaDu Task Acceptance

Este fluxo padroniza o encerramento técnico e documental das tarefas no repositório MykytaDu, alinhado ao modelo de **Autonomia com trava no push**.

## Encerramento integrado da task

Ao concluir a implementação técnica e passar nas validações do Gradle, o agente deve, no mesmo turno de trabalho:

1. **Atualizar a documentação da sprint (`docs/sprints/`):**
   - Marcar a subtask como concluída (`[x]`).
   - Registrar as evidências reais dos testes executados (ex.: classes de teste, suites validadas).
   - Atualizar a tabela de subtasks com o estado e evidência.
2. **Atualizar documentos de arquitetura e modelo (quando afetados):**
   - Atualizar `docs/modelagem.md` se contratos, entidades ou fluxos sofreram mudanças.
   - Atualizar o Documento Mestre somente diante de decisões ou especificações duráveis.
   - Atualizar `docs/roadmap.md` se um marco ou sprint foi finalizado.
3. **Criar commit local atômico:**
   - Adicionar ao stage os arquivos de código, testes e documentação pertencentes ao escopo da task.
   - Criar um commit local claro e conciso na branch de trabalho.
4. **Apresentar relatório final consolidado:**
   - Informar o que foi implementado, os testes validados, a documentação atualizada e o hash do commit local.
   - Informar que as alterações estão commitadas localmente e aguardar comando para `git push` ou início da próxima task.

## Processar correções ou ajustes posteriores

Se o usuário revisar o trabalho e solicitar correções, melhorias ou apontar divergências:

1. Aplique os ajustes solicitados no código ou documentação, respeitando o escopo.
2. Reexecute as validações necessárias no Gradle.
3. Atualize a documentação afetada com as novas evidências.
4. Crie um novo commit local (ou amend se explicitamente solicitado).
5. Apresente um resumo objetivo dos ajustes realizados e o novo commit gerado.

## Restrições

- **Trava no push:** Não execute `git push`, não abra pull request e não faça deploy sem autorização explícita do usuário.
- Não documente itens que não foram efetivamente implementados e validados.
- Preserve arquivos preexistentes e alterações não relacionadas no working tree.
