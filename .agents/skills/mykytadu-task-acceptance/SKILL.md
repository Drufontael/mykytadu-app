---
name: mykytadu-task-acceptance
description: Gerencia o aceite de tasks implementadas no repositório MykytaDu e documenta seu encerramento somente após aprovação inequívoca do usuário. Use após implementar e validar uma task do MykytaDu ou ao processar a resposta à pergunta de aceite.
---

# MykytaDu Task Acceptance

Use este fluxo somente no repositório MykytaDu. Ele separa a entrega técnica do aceite e da documentação de encerramento.

## Solicitar aceite

Depois de implementar uma task, executar as validações pertinentes e apresentar o relatório final, pergunte exatamente:

> A task está aceita?

Nesse momento:

- não atualize a documentação de encerramento;
- não considere nem marque a task como concluída;
- não realize commit, push ou pull request por causa desta skill;
- aguarde uma resposta explícita do usuário.

Silêncio, ausência de objeção ou mudança de assunto não constituem aceite.

## Processar resposta afirmativa

Considere inequívocas respostas como `sim`, `ok`, `aceito`, `aprovado`, `pode concluir` e equivalentes claros no contexto.

Após o aceite:

1. Inspecione as alterações implementadas e o estado atual da documentação.
2. Atualize o Documento Mestre com um resumo sucinto da task aceita.
3. Atualize roadmap, modelagem, documentação de APIs, rede, persistência, identidade visual ou outros documentos técnicos somente quando forem realmente afetados.
4. Preserve o formato, a estrutura e a linguagem existentes.
5. Registre apenas resultados comprovados pela implementação e pelas validações; não replique todo o relatório no Documento Mestre nem o transforme em changelog.
6. Revise o diff documental e confirme que contém somente alterações relacionadas à task aceita.
7. Informe quais documentos foram atualizados e por quê.

O aceite autoriza somente a documentação pertinente ao encerramento. Não altere código depois dele sem nova solicitação explícita e não faça commit, push ou pull request por causa desta skill.

## Processar correções ou ausência de aceite

Se a resposta trouxer observações, divergências ou instruções de correção:

1. Não atualize a documentação de encerramento nem marque a task como concluída.
2. Aplique somente as correções solicitadas, sem ampliar o escopo.
3. Reexecute as validações afetadas.
4. Apresente um novo relatório destacando as mudanças.
5. Pergunte novamente:

> A task está aceita?

Se a resposta for ambígua e não contiver instruções executáveis, peça confirmação objetiva antes de agir.

## Restrições

- Não documente funcionalidades não implementadas.
- Não altere documentos sem relação direta com a task aceita.
- Não interprete aprovação parcial como aceite integral quando houver ressalvas pendentes.
- Não use esta skill como autorização para commit, push, pull request ou ampliação de escopo.
- Preserve alterações preexistentes e não relacionadas no working tree.
