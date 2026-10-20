---
name: mykytadu-git-publish
description: Revisa commits locais ou alterações pendentes e realiza a publicação (git push) no upstream com segurança. Use quando o usuário invocar $mykytadu-git-publish ou solicitar ações de push/publicação.
---

# MykytaDu Git Publish

Conduza a publicação (push) das alterações e commits locais para o repositório remoto.
Esta skill é a chave de destravamento da **trava no push** do repositório MykytaDu.

## Inspeção e preparação

Antes de publicar:

1. **Inspecione a relação com o remoto:**
   - Verifique branch atual, remote configurado, upstream e commits locais pendentes de envio (`git status`, `git log @{u}..HEAD --oneline` ou equivalente).
2. **Inspecione o working tree:**
   - Se houver alterações locais ainda não commitadas pertinentes ao trabalho, adicione ao stage e crie o commit local correspondente antes do push.
   - Confirme que o stage não contém segredos, credenciais, arquivos temporários ou alterações fora de escopo.
   - Se os testes pertinentes já foram executados e aprovados no ciclo da task e não houve novas alterações de código, evite reexecuções redundantes. Se houver novas mudanças de código não validadas, execute os testes Gradle aplicáveis antes de publicar.

## Publicação (Push)

1. Faça o `git push` da branch atual para o upstream configurado.
2. **Nunca** utilize `--force` ou `--force-with-lease`.
3. Se não houver upstream configurado, apresente o comando proposto (`git push -u origin <branch>`) e solicite confirmação.
4. Se houver rejeição remota, conflitos ou necessidade de merge/rebase, **interrompa** imediatamente sem tentar resolver com força bruta e informe o usuário.

## Relatório de publicação

Apresente um resumo claro e conciso:

- Commits enviados (hashes e mensagens);
- Branch e upstream utilizados;
- Status do push (sucesso/falha);
- Estado final do working tree (limpo ou com itens preservados).
