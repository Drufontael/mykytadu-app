# Contrato Operacional para Agentes

## Propósito e alcance

Este arquivo orienta agentes que analisam, implementam, testam ou documentam o
MykytaDu. Ele se aplica recursivamente a todo o repositório.

Instruções mais específicas fornecidas pelo usuário ou por um `AGENTS.md` em um
diretório descendente complementam ou substituem estas regras dentro do escopo
correspondente. Na ausência delas, este arquivo prevalece como orientação local.

## Fontes de verdade

Use esta ordem para determinar o estado real do projeto:

1. Código atual.
2. Builds e testes efetivamente executados.
3. Implementações e validações recentemente aceitas.
4. Documento Mestre.
5. Modelagem.
6. Roadmap.
7. Documentação histórica.

Planejamento não comprova implementação. Código sem build ou teste não deve ser
descrito como validado. Relate divergências documentais e não escolha
silenciosamente uma versão quando as fontes divergirem.

Consulte `gradle/libs.versions.toml` e os arquivos Gradle para conhecer as
versões efetivas. Não use versões lembradas de conversas ou documentos como
substitutas dessa inspeção.

## Inspeção inicial obrigatória

Antes de editar, o agente deve:

- ler as instruções aplicáveis;
- executar `git status` e identificar a branch atual;
- identificar alterações preexistentes;
- localizar os arquivos reais envolvidos;
- verificar a estrutura existente antes de criar arquivos ou abstrações;
- diferenciar evidência, inferência e item ainda não validado.

Leia a documentação pertinente ao escopo antes de decidir arquitetura. Confirme
tasks Gradle reais antes de sugerir ou executar validações.

## Proteção do working tree

Alterações preexistentes pertencem ao usuário. Não as sobrescreva, reverta,
remova, reformate ou misture a mudanças não relacionadas.

Não use comandos destrutivos para limpar o repositório e não altere line endings
em massa. Diferencie no relatório as mudanças próprias das preexistentes.

Quando mudanças sobrepostas não puderem ser preservadas, interrompa e peça
orientação antes de editar.

## Arquitetura do projeto

O projeto usa Kotlin Multiplatform e Compose Multiplatform. Compartilhe em
`commonMain` tudo que não depender legitimamente da plataforma e mantenha código
específico no source set mais estreito possível.

Use `webMain` para código compartilhado entre JavaScript e WasmJS. Mantenha
regras de negócio fora da UI e faça comunicação externa por repositories.

DTOs remotos não podem escapar da camada de dados. Use `expect`/`actual` somente
em fronteiras reais de plataforma e verifique abstrações existentes antes de
criar alternativas.

Não antecipe modelos, camadas ou serviços sem consumidor atual. Preserve o
cancelamento de coroutines. Mantenha IDs locais, AniList e backend
semanticamente distintos.

## Targets

Os targets atuais são Android, Desktop/JVM, iOS Arm64, iOS Simulator Arm64,
JavaScript e WasmJS. WasmJS é o target Web principal; JavaScript é o fallback de
compatibilidade.

Isole limitações de plataforma no source set adequado. A compilação de metadata
iOS não substitui o build nativo em macOS com Xcode.

## Regras específicas do Web

Mantenha `App` e Navigation 3 compartilhados sempre que possível. Isole APIs do
navegador na fronteira Web e não crie frontend paralelo sem decisão arquitetural
explícita.

Não presuma que CORS funciona sem teste em navegador. Diferencie navegação
interna de browser history e não declare URL, reload, voltar/avançar, PWA ou
offline como implementados sem validação.

Não armazene tokens sensíveis em `localStorage` e evite logs sensíveis no
console. Mudanças de políticas de repositórios Gradle exigem justificativa e
registro na documentação afetada após o aceite.

## Dependências e configuração

Mantenha versões centralizadas no Version Catalog. Novas dependências exigem
necessidade concreta e justificativa. Não misture upgrades com funcionalidades
sem necessidade comprovada.

Prefira documentação oficial ao avaliar compatibilidade. Não remova dependências
ou configurações apenas porque parecem não utilizadas sem comprovar todos os
targets. Trate mudanças globais de Gradle como decisões arquiteturais.

## Fluxo de implementação (Autonomia com trava no push)

A solicitação do usuário confere ao agente autorização para executar o ciclo
completo de desenvolvimento de ponta a ponta na branch de trabalho local.

Siga esta sequência de forma contínua:

1. Inspecionar o repositório, branch e alterações preexistentes.
2. Delimitar o escopo autorizado (suporta metas compostas ou múltiplas subtasks quando solicitado).
3. Implementar o código e testes necessários.
4. Compilar e executar testes automatizados pertinentes no Gradle.
5. Atualizar a documentação contemporaneamente (`docs/sprints/`, `docs/modelagem.md`, etc.), registrando o estado real e evidências.
6. Criar commit local atômico contendo apenas as alterações pertinentes do escopo.
7. Apresentar relatório final consolidado com resumo, validações e hash do commit local.
8. Aguardar comando para `git push` ou instruções para a próxima task.

O agente tem autonomia para encadear tarefas solicitadas em lote sem paradas
artificiais intermediárias, gerando commits atômicos por marco lógico. Interrompa
a execução autônoma apenas se houver falha de build/teste irresolúvel,
ambiguidade material de produto ou risco a alterações preexistentes.

## Testes e validações

Descubra as tasks reais disponíveis no Gradle; não invente comandos. Valide
primeiro a área afetada e execute testes dos targets aplicáveis antes de
concluir o ciclo da task.

Para máxima agilidade e redução de processos repetitivos, **agrupe as validações dos
múltiplos targets em uma única invocação composta do Gradle** (por exemplo:
`.\gradlew.bat :composeApp:desktopTest :composeApp:testDebugUnitTest :composeApp:assembleDebug :composeApp:jsBrowserTest :composeApp:wasmJsBrowserTest :composeApp:compileIosMainKotlinMetadata`),
evitando invocações fragmentadas sucessivas.

Registre comandos e resultados no relatório. Diferencie build, teste automatizado,
smoke test e validação manual. Registre limitações do ambiente e não mascare testes
ausentes como sucesso. Separe avisos LF/CRLF de erros reais.

## Documentação e encerramento

A documentação técnica e de sprints deve ser atualizada de forma contemporânea
à implementação, dentro do mesmo ciclo de trabalho, antes da criação do commit local.

- Atualize `docs/sprints/` marcando subtasks concluídas com suas evidências reais.
- O Documento Mestre muda somente diante de especificação ou direcionamento durável.
- O roadmap recebe alterações de estado ou critérios quando a sprint for concluída.
- A modelagem (`docs/modelagem.md`) recebe alterações estruturais realizadas.
- Não documente funcionalidades ainda não implementadas ou não testadas.

Caso o usuário solicite correções ou revisões após a entrega, aplique os ajustes,
revalide, atualize a documentação e gere um novo commit local.

## Git e ações externas

- **Commits locais:** pré-autorizados na branch de trabalho para tarefas que
  tenham passado nas validações pertinentes. Os commits devem ser atômicos,
  pequenos, coerentes e com mensagens claras. Execute as operações de stage e
  commit de forma integrada dentro do ciclo da task.
- **Push para upstream (Trava no Push):** executar `git push`, abrir pull request,
  fazer deploy ou publicação em serviços externos exige comando ou autorização
  explícita do usuário (ex.: `$mykytadu-git-publish`, "faça push", "publique").
- **Proteção e segurança:** não faça push com `--force` ou `--force-with-lease`.
  Não use comandos destrutivos (`git reset --hard`, `git clean -f`, `git checkout --`).
  Não adicione ao stage arquivos não relacionados, temporários ou com dados sensíveis.

## Segurança

Nunca registre secrets, tokens, cookies ou credenciais. Higienize logs e não
exponha mensagens técnicas diretamente à UI.

Autenticação Web futura deve preferir mecanismos protegidos contra acesso por
JavaScript quando o contrato do backend permitir. Avalie permissões, CORS e
armazenamento seguro por plataforma. Não introduza credenciais fictícias para
fazer testes passarem.

## Relatório final

Evite mensagens intermediárias redundantes no chat durante execuções assíncronas;
conduza o fluxo e apresente diretamente o relatório consolidado ao final do ciclo.

Ao concluir o ciclo autônomo da task, apresente um relatório direto e objetivo
contendo:

- **Alterações realizadas:** resumo do código implementado ou corrigido;
- **Validações e resultados:** comandos Gradle executados e status dos testes;
- **Documentação atualizada:** arquivos em `docs/` modificados;
- **Commit local:** hash e mensagem do commit criado;
- **Estado do Git:** branch atual, commits locais pendentes de push;
- **Próximo passo:** informe que as alterações estão commitadas localmente e
  pergunte se o usuário deseja realizar o `git push` ou prosseguir para a próxima task.
