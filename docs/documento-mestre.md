# MykytaDu — Documento Mestre

> Especificação central de produto, direção arquitetural e princípios duráveis.
> Planejamento, execução e histórico possuem documentos próprios.

## 1. Propósito

MykytaDu é uma aplicação multiplataforma para controle e acompanhamento de
animes. O produto combina catálogo, biblioteca pessoal e progresso em uma
experiência organizada, moderna e coerente entre plataformas.

A filosofia central é:

> **Anime na personalidade, produto de software na execução.**

Este documento define o que deve permanecer estável enquanto o projeto evolui.
Ele não registra o diário das sprints nem substitui código, builds, testes,
modelagem ou contratos técnicos.

## 2. Visão do produto

A aplicação deve permitir:

- pesquisar e descobrir animes;
- consultar informações relevantes sobre cada obra;
- manter uma biblioteca pessoal;
- registrar estado e progresso;
- continuar utilizável localmente sem autenticação;
- sincronizar dados quando autenticação e backend estiverem disponíveis;
- preservar uma experiência consistente em Android, Desktop, iOS e Web.

O conteúdo é o protagonista. Capas, títulos, informações e progresso devem ter
mais destaque que elementos decorativos.

## 3. Direção de experiência

O MykytaDu deve ser organizado, moderno, imersivo e pessoal. Referências ao
universo de anime devem ser sutis e não comprometer clareza ou usabilidade.

Evitar:

- excesso de neon ou decoração;
- fundos completamente pretos;
- gradientes indiscriminados;
- tipografia temática em excesso;
- aparência de portal genérico;
- cópia visual de serviços existentes;
- regras de negócio ou detalhes técnicos expostos na interface.

Dark Mode é a principal referência visual, e Light Mode deve preservar a mesma
identidade. Cores, tipografia, shapes, componentes e critérios visuais vigentes
pertencem à [`identidade-visual.md`](identidade-visual.md).

## 4. Plataformas

O projeto usa Kotlin Multiplatform e Compose Multiplatform para compartilhar a
aplicação entre:

- Android;
- Desktop/JVM;
- iOS Arm64;
- iOS Simulator Arm64;
- JavaScript;
- WasmJS.

WasmJS é o target Web principal. JavaScript é uma alternativa de compatibilidade
com distribuição separada; não existe seleção automática de fallback.

Compartilhar em `commonMain` tudo que não depender legitimamente da plataforma.
Código específico permanece no source set mais estreito; `webMain` reúne o que é
comum a JS e WasmJS.

A decisão completa está no
[`ADR-001`](adr/ADR-001-compartilhar-aplicacao-kotlin-multiplatform.md) e a
estratégia Web no
[`ADR-003`](adr/ADR-003-adotar-wasmjs-com-js-alternativo.md).

## 5. Arquitetura

O projeto evolui por fatias verticais. Cada funcionalidade incorpora somente as
camadas necessárias para produzir um resultado observável.

Fluxo conceitual:

```text
UI
 ↓
ViewModel / estado da funcionalidade
 ↓
Repository de domínio
 ↓
Fontes de dados
```

Princípios:

- regras de negócio não pertencem à UI;
- comunicação externa ocorre por repositories;
- DTOs e protocolos remotos não atravessam a camada de dados;
- estados e ViewModels surgem com consumidores reais;
- `expect`/`actual` representa apenas fronteiras reais de plataforma;
- abstrações existentes são verificadas antes da criação de alternativas;
- cancelamentos de coroutines são preservados;
- exceções inesperadas não são silenciosamente transformadas em resultado
  esperado;
- módulos adicionais exigem benefício concreto de isolamento.

A arquitetura e os modelos vigentes estão em
[`modelagem.md`](modelagem.md). Decisões com consequências duráveis são
registradas em [`docs/adr/`](adr/README.md).

## 6. Domínio e identificadores

O domínio representa conceitos do MykytaDu e não o formato de uma API externa.

- IDs locais, AniList e backend possuem significados distintos;
- `AniListAnimeId` identifica obras no catálogo AniList;
- um futuro ID local identifica registros persistidos pelo aplicativo;
- um futuro ID do backend identifica recursos controlados pelo MykytaDu API;
- ausência legítima permanece nula, sem sentinelas como zero ou texto de
  interface;
- coleções vazias são válidas quando o domínio permitir;
- valores externos desconhecidos devem ser tratados explicitamente.

Os contratos do catálogo são independentes de GraphQL, Ktor e DTOs. A decisão
está no [`ADR-002`](adr/ADR-002-isolar-dominio-dos-contratos-anilist.md).

## 7. Catálogo e apresentação

A AniList é a fonte externa inicial para pesquisa e detalhes. Seus contratos são
convertidos para modelos do domínio antes de chegarem aos consumidores.

Quando a apresentação precisar escolher um título, a ordem é:

1. inglês;
2. romaji;
3. nativo;
4. primeiro sinônimo;
5. recurso localizado indicando título indisponível.

Essa regra pertence à apresentação; os modelos preservam os títulos disponíveis
sem selecionar um deles.

O contrato técnico da integração está em
[`api-externa-anilist.md`](api-externa-anilist.md). O acesso direto no Web e suas
condições estão no
[`ADR-005`](adr/ADR-005-consumir-anilist-diretamente-no-web.md).

## 8. Navegação

Navigation 3, `AppRoute`, `NavDisplay`, back stack e regras de navegação devem
permanecer compartilhados sempre que possível.

- rotas transportam somente dados necessários ao destino;
- identificadores obrigatórios devem ser representados por contratos tipados;
- restauração não deve inventar dados ausentes;
- ações de retorno preservam a coerência do back stack;
- classificação de acesso não substitui autenticação;
- deep links devem validar parâmetros antes de construir rotas.

No Web, uma fronteira específica sincroniza Navigation 3 com URL e History API.
Fragment routing é a estratégia atual porque funciona em hospedagem estática sem
rewrite de paths. A decisão e seus critérios de revisão estão no
[`ADR-004`](adr/ADR-004-integrar-navigation3-ao-historico-com-fragmentos.md).

## 9. Web

O Web reutiliza a aplicação compartilhada e isola APIs do navegador.

- `webMain` concentra entrypoint, engine HTTP e lógica Web compartilhável;
- `jsMain` e `wasmJsMain` contêm somente interoperabilidade específica;
- o transporte HTTP usa Fetch por meio da engine Ktor apropriada;
- logging HTTP sensível permanece desabilitado;
- CORS precisa ser comprovado em navegador real;
- navegação interna e histórico do navegador são responsabilidades distintas;
- tokens sensíveis não devem ser armazenados em `localStorage`;
- modos de diagnóstico não se tornam funcionalidades de produto.

PWA, service worker, offline, deploy, SSR e hospedagem definitiva exigem escopo e
validação próprios.

## 10. Biblioteca e persistência

A biblioteca seguirá uma estratégia local-first:

- funciona sem autenticação;
- persiste alterações localmente;
- permanece utilizável quando backend ou rede estiverem indisponíveis;
- habilita sincronização somente quando houver sessão válida;
- não confunde dados locais com dados externos ou remotos.

Persistência deve ser modelada quando existirem casos de uso e consultas reais.
Modelos persistidos não precisam reproduzir modelos de domínio nem DTOs.

## 11. Backend e integrações futuras

O `mykytadu-api` fornecerá capacidades que não devem residir exclusivamente no
cliente, como identidade, autenticação, sincronização e tradução controlada.

O frontend deve depender de contratos HTTP estáveis, não de classes internas do
backend. Autenticação e tradução somente serão introduzidas quando os respectivos
contratos estiverem disponíveis e validados.

Credenciais, tokens, cookies e segredos nunca devem ser registrados em logs,
documentação ou código-fonte.

## 12. Design System e acessibilidade

Material 3 é a base. Componentes próprios devem centralizar tokens,
comportamento ou contratos compartilhados, e precisam de consumidor real.

- telas não definem cores, espaçamentos ou raios arbitrários;
- controles somente com ícone possuem nome acessível;
- elementos informativos não simulam ações;
- estados de loading, erro, progresso e vazio devem ser distinguíveis;
- responsividade parte das constraints disponíveis;
- teclado, foco, mouse e semântica são validados nos targets aplicáveis;
- validação parcial não autoriza declarar conformidade WCAG completa.

O foco de botões no tema escuro está funcional, porém seu refinamento visual foi
reservado para a Sprint 16.

## 13. Qualidade e validação

Uma entrega requer evidência proporcional ao impacto:

- testes determinísticos para regras e transições de estado;
- builds dos targets afetados;
- regressão compartilhada quando contratos comuns mudarem;
- validação visual para alterações de interface;
- testes reais no navegador para transporte, CORS, histórico e recursos Web;
- build nativo iOS em macOS/Xcode quando essa evidência for necessária.

Metadata iOS não equivale ao build nativo. Bundle gerado não comprova que a
aplicação renderizou. Avisos devem ser separados de falhas.

## 14. Stack e dependências

O projeto utiliza Kotlin, Compose Multiplatform, Material 3, Navigation 3, Koin,
Ktor, Kotlinx Serialization e Coroutines.

Versões efetivas pertencem a `gradle/libs.versions.toml` e aos arquivos Gradle.
Novas dependências exigem necessidade concreta, compatibilidade comprovada e
versão centralizada. Upgrades não devem ser misturados com funcionalidades sem
necessidade técnica.

## 15. Fontes de verdade

Use a prioridade definida no `AGENTS.md`:

1. código atual;
2. builds e testes efetivamente executados;
3. implementações e validações recentemente aceitas;
4. este Documento Mestre;
5. modelagem;
6. roadmap;
7. documentação histórica.

Responsabilidades documentais:

| Documento | Responsabilidade |
|---|---|
| [`README.md`](../README.md) | entrada, execução e visão resumida |
| este documento | produto, especificações e direção estável |
| [`roadmap.md`](roadmap.md) | planejamento e estado consolidado |
| [`modelagem.md`](modelagem.md) | arquitetura e modelos vigentes ou propostos |
| [`identidade-visual.md`](identidade-visual.md) | identidade e Design System |
| [`api-externa-anilist.md`](api-externa-anilist.md) | contrato da integração AniList |
| [`sprints/`](sprints/README.md) | execução, evidências e encerramentos |
| [`adr/`](adr/README.md) | decisões arquiteturais e consequências |

Quando documentos divergirem, corrigir a fonte responsável. Não duplicar
informação apenas para manter arquivos aparentemente completos.

## 16. Limites de manutenção

- este documento não recebe status de sprint, checklists ou próximo passo;
- resultados de execução pertencem ao registro da sprint;
- decisões arquiteturais relevantes apontam para ADR;
- modelagem descreve o estado vigente e identifica propostas;
- roadmap não funciona como diário de implementação;
- documentação técnica acompanha contratos reais;
- planejamento nunca é descrito como capacidade implementada.
