---
name: mykytadu-git-publish
description: Revisa, prepara, documenta, commita e publica as alterações do repositório MykytaDu desde o último commit. Use somente quando o usuário invocar explicitamente $mykytadu-git-publish.
---

# MykytaDu Git Publish

Conduza a publicação completa das alterações claramente pertencentes ao trabalho atual. A invocação explícita autoriza atualizar a documentação diretamente afetada, adicionar ao stage, criar um commit novo e executar push. Não amplia o escopo para alterações duvidosas ou não relacionadas.

## Inspeção e classificação

Antes de editar, adicionar ao stage ou publicar:

1. Identifique a branch atual, os remotes, o upstream e a relação da branch local com o remoto.
2. Inspecione o status, alterações staged e unstaged, arquivos não rastreados, o diff completo contra `HEAD` e commits locais ainda não publicados. Busque instruções do repositório aplicáveis aos arquivos envolvidos.
3. Classifique cada arquivo ou grupo coerente como:
   - pertinente ao commit atual;
   - legítimo, mas não relacionado ao commit;
   - gerado ou temporário;
   - possível segredo ou dado sensível;
   - duvidoso e dependente de confirmação.
4. Determine a autoria e a finalidade pelo contexto, histórico e conteúdo. Não presuma que toda alteração presente foi produzida na sessão atual.

Interrompa e peça orientação se encontrar possível segredo ou credencial, mudança claramente não relacionada, autoria ou finalidade indeterminável, ou dúvida material sobre o escopo do commit. Informe concretamente os arquivos e a classificação que causaram a pausa.

## Preservação do trabalho

- Nunca descarte, restaure ou sobrescreva alterações do usuário.
- Não inclua mudanças não relacionadas por conveniência.
- Não use `git reset --hard`, `git checkout --`, `git clean`, force push ou equivalentes destrutivos.
- Não faça merge ou rebase automaticamente.
- Não altere commits anteriores nem use `--amend`, salvo solicitação explícita.
- Não desabilite hooks ou verificações.

## Documentação e validação

Verifique se o código ou comportamento alterado exige atualização do Documento Mestre, roadmap, `modelagem.md` ou `MykytaDu_Modelagem.md` conforme o nome existente no repositório, `identidade-visual.md`, `README.md` ou outro documento afetado. Atualize somente o que for diretamente sustentado pelas mudanças. Não declare como concluído o que não estiver comprovado pelo código e pelas validações.

Escolha tarefas existentes do Gradle proporcionais ao conteúdo alterado. Execute as compilações e os testes relevantes e registre cada comando e resultado. Se uma validação relevante falhar, interrompa antes do commit e peça autorização explícita para prosseguir; não trate falha preexistente como irrelevante sem evidência.

## Stage e commit

1. Adicione somente os arquivos classificados como pertinentes, preferindo caminhos explícitos em `git add`.
2. Não use `git add .` nem `git add -A`, exceto se a inspeção comprovar que absolutamente todas as alterações pertencem ao mesmo commit.
3. Revise `git diff --cached` e confirme que o stage não contém segredo, credencial, configuração local, artefato gerado ou alteração fora do escopo.
4. Se não houver alterações pertinentes staged, interrompa sem criar commit.
5. Determine a convenção pelo histórico do repositório. Use Conventional Commits somente se o histórico ou a documentação a adotar.
6. Crie uma mensagem fiel ao conteúdo staged, com assunto conciso no imperativo e corpo apenas quando contexto, impacto ou decisão relevante precisarem de explicação.
7. Crie um commit novo, mantendo hooks e verificações ativos.

## Publicação

Faça push somente da branch atual para o upstream existente, sem `--force` ou `--force-with-lease`. Se não houver upstream, apresente o comando exato proposto e solicite confirmação antes de configurá-lo.

Interrompa sem tentar corrigir automaticamente se houver rejeição, divergência remota, conflito, branch protegida ou necessidade de alterar upstream, fazer merge, rebase ou force push. Explique o estado e solicite orientação.

## Relatório final

Apresente:

- arquivos incluídos;
- arquivos deixados de fora e o motivo;
- documentação atualizada;
- comandos de validação e resultados;
- hash e mensagem do commit;
- branch, remote e upstream utilizados;
- resultado do push;
- pendências restantes no working tree.

Se o fluxo for interrompido antes da publicação, entregue o mesmo relatório com o que foi concluído, o ponto de parada e a decisão necessária.
