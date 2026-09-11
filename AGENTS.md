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

## Fluxo de implementação

Siga esta sequência:

1. Inspecionar.
2. Delimitar o escopo autorizado.
3. Implementar incrementalmente.
4. Compilar e testar frequentemente.
5. Executar regressão proporcional.
6. Apresentar relatório.
7. Solicitar aceite.
8. Aguardar aceite inequívoco.
9. Atualizar documentação somente depois do aceite.

Uma task não está concluída apenas porque compilou. Faça validação visual quando
houver impacto de interface. Não inicie automaticamente a próxima task.

## Testes e validações

Descubra as tasks reais disponíveis no Gradle; não invente comandos. Valide
primeiro a área afetada e execute regressão dos targets aplicáveis antes do
aceite.

Registre comandos e resultados. Diferencie build, teste automatizado, smoke test
e validação manual. Registre limitações do ambiente e não mascare testes ausentes
como sucesso. Separe avisos LF/CRLF de erros reais.

## Aceite e documentação

Implementação, aceite humano e encerramento documental são etapas distintas.
Antes do aceite, não marque uma task como concluída na documentação.

Respostas afirmativas inequívocas constituem aceite. Observações com defeitos ou
pedidos de correção não constituem aceite. Após o aceite, atualize somente os
documentos realmente afetados.

Após o aceite, registre execução, evidências e encerramento em `docs/sprints/`. O Documento Mestre muda somente diante de especificação ou direcionamento durável,
sem receber diário de sprints. O roadmap recebe estado, escopo ou critérios; a
modelagem, alterações estruturais; e `docs/adr/`, decisões arquiteturais
relevantes. Documentação técnica acompanha contratos reais sem copiar relatórios, e intenções futuras permanecem marcadas como planejadas.

## Git e ações externas

Stage, commit, push, pull request, publicação e deploy exigem autorização
explícita. Quando autorizados, commits devem ser pequenos, coerentes e não
misturar mudanças não relacionadas.

Não reescreva histórico, não use comandos Git destrutivos e não envie mensagens
ou realize ações em serviços externos sem autorização.

## Segurança

Nunca registre secrets, tokens, cookies ou credenciais. Higienize logs e não
exponha mensagens técnicas diretamente à UI.

Autenticação Web futura deve preferir mecanismos protegidos contra acesso por
JavaScript quando o contrato do backend permitir. Avalie permissões, CORS e
armazenamento seguro por plataforma. Não introduza credenciais fictícias para
fazer testes passarem.

## Relatório final

Todo relatório de implementação ou correção apresentado antes do aceite deve informar:

- alterações realizadas;
- arquivos relevantes;
- validações e resultados;
- limitações e pendências conhecidas;
- alterações preexistentes preservadas;
- estado de stage, commit e push;
- próximo passo recomendado;
- pergunta final: **A task está aceita?**

Após o aceite inequívoco e a atualização documental, apresente um relatório de encerramento sem solicitar novo aceite para a mesma task.
