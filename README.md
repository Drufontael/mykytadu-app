# MykytaDu

> Sua jornada pelos animes.

MykytaDu é um aplicativo multiplataforma para organizar e acompanhar animes. O produto está sendo desenvolvido de forma incremental, com foco em uma arquitetura desacoplada, componentes reutilizáveis e uma experiência visual consistente.

Atualmente, as cinco primeiras sprints estão concluídas: fundação multiplataforma, Design System, navegação, camada de comunicação com a AniList e domínio do catálogo. A primeira pesquisa funcional da S6.2 está aceita, com estados, debounce e lista de títulos conectados à AniList. A Sprint 6 segue em andamento; imagens, paginação incremental e navegação de detalhes com ID ainda estão planejadas, assim como autenticação e biblioteca.

## Plataformas

- Android
- Desktop (JVM)
- iOS (dispositivo e simulador)
- Web (WasmJS e JavaScript)

## Estado atual

| Etapa | Estado |
|---|---|
| Sprint 1 — Fundação | Concluída |
| Sprint 2 — Design System | Concluída |
| Sprint 3 — Navegação | Concluída |
| Sprint 4 — Comunicação | Concluída |
| Sprint 5 — Domínio do Catálogo | Concluída |
| Sprint W1 — Fundação Web | Concluída |
| Sprint 6 — Busca de Animes | Em andamento — S6.1 e S6.2 concluídas e aceitas |
| Sprints 7 a 16 | Planejadas |

Já estão disponíveis:

- configuração Kotlin e Compose Multiplatform;
- injeção de dependências com Koin;
- cliente HTTP Ktor com engine específica por plataforma;
- serialização JSON com Kotlinx Serialization;
- temas claro e escuro, tipografia, formas e dimensões;
- componentes reutilizáveis de entrada, ação, navegação e feedback;
- showcase para validação visual do Design System;
- oito rotas tipadas e serializáveis com Navigation 3;
- fluxo de entrada, navegação principal e rotas secundárias;
- back stack compartilhado entre as plataformas;
- classificação declarativa de rotas públicas e protegidas;
- estrutura compartilhada para resolução de Deep Links;
- comunicação HTTP robusta e padronizada;
- pesquisa paginada e consulta de detalhes por meio do `AnimeApi` da AniList;
- modelos de domínio, mapeadores AniList, `AnimeRepository` e registro no Koin para pesquisa e detalhes.
- shell Web com transporte Ktor baseado em Fetch e comunicação direta validada com a AniList.
- navegação Web por fragmentos, integrada ao histórico do navegador em JS e WasmJS.
- shell Web responsivo e acessível no escopo validado, com distribuições de produção separadas para WasmJS e JavaScript.

A busca já apresenta a primeira página de títulos com debounce, cancelamento,
estados e retry, usando ViewModel por entrada do Navigation 3.

## Stack

- Kotlin 2.3.20
- Compose Multiplatform 1.10.3
- Material 3
- Gradle 8.14
- JDK 21
- Koin 4.2.2
- Ktor Client 3.5.2
- Kotlinx Serialization 1.11.0
- Navigation 3
- Lifecycle 2.10.0, com ViewModel e coleta de estado por lifecycle

As versões efetivamente utilizadas são centralizadas em [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Pré-requisitos

- JDK 21;
- Android Studio com Android SDK 36 para executar a versão Android;
- macOS com Xcode para compilar e executar os targets iOS.

Não é necessário instalar o Gradle separadamente: o repositório inclui o Gradle Wrapper.

## Como executar

Clone o repositório e acesse a pasta do projeto. No Windows, use `gradlew.bat`; em macOS ou Linux, use `./gradlew`.

### Desktop

```powershell
.\gradlew.bat :composeApp:desktopRun
```

Em macOS ou Linux:

```bash
./gradlew :composeApp:desktopRun
```

### Android

Abra o projeto no Android Studio, selecione a configuração `composeApp` e execute em um dispositivo ou emulador. Também é possível instalar o build de debug por linha de comando:

```powershell
.\gradlew.bat :composeApp:installDebug
```

### iOS

Os targets `iosArm64` e `iosSimulatorArm64` estão configurados. A compilação e a integração do framework iOS exigem macOS e Xcode.

### Web

WasmJS é o target Web principal e JavaScript é a alternativa de compatibilidade. Os dois são executados e distribuídos separadamente; não existe seleção automática de fallback.

Para executar os servidores de desenvolvimento no Windows:

```powershell
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun
```

Para gerar as distribuições de produção:

```powershell
.\gradlew.bat :composeApp:wasmJsBrowserDistribution
.\gradlew.bat :composeApp:jsBrowserDistribution
```

Os artefatos são gerados, respectivamente, em:

```text
composeApp/build/dist/wasmJs/productionExecutable
composeApp/build/dist/js/productionExecutable
```

Sirva cada diretório por HTTP estático; abrir o `index.html` diretamente pelo sistema de arquivos não representa a execução validada. Por exemplo, em um ambiente com Python:

```powershell
python -m http.server 8080 --directory composeApp\build\dist\wasmJs\productionExecutable
```

A navegação atual usa fragmentos (`#/...`) e não depende de rewrite de paths no servidor. Os parâmetros `?network-smoke=true` e `?design-system-showcase=true` ativam modos técnicos incluídos nas distribuições; o diagnóstico de rede tem precedência quando ambos estão ativos.

## Testes

Para executar os testes do target Desktop:

```powershell
.\gradlew.bat :composeApp:desktopTest
```

Em macOS ou Linux:

```bash
./gradlew :composeApp:desktopTest
```

## Estrutura do projeto

```text
mykytadu-app/
├── composeApp/
│   └── src/
│       ├── commonMain/    # UI, tema, componentes, domínio e DI compartilhados
│       ├── commonTest/    # Testes compartilhados
│       ├── androidMain/   # Entrada e engine HTTP do Android
│       ├── desktopMain/   # Entrada e engine HTTP do Desktop
│       ├── iosMain/       # Engine HTTP do iOS
│       ├── webMain/       # Entrada, engine Fetch e diagnóstico técnico Web
│       ├── jsMain/        # APIs específicas do JavaScript
│       └── wasmJsMain/    # APIs específicas do WasmJS
├── gradle/
│   └── libs.versions.toml # Catálogo central de dependências
└── docs/                  # Produto, identidade, modelagem e roadmap
```

O código compartilhado usa o namespace `br.com.mykytadu`. A arquitetura planejada separa apresentação, domínio e dados, mantendo regras de negócio fora da UI e o acesso externo atrás de repositories.

## Design System

A identidade visual adota Poppins, Material 3 e temas claro e escuro baseados em tokens. O modo escuro é a principal referência da marca, com violeta como cor primária e turquesa como destaque.

Entre os elementos implementados estão `AppButton`, `AppTextField`, `AppCard`, `AppTopBar`, `AppSearchBar`, `AppChip`, `AppIconButton`, `AppDialog`, `AppLoading`, `AppProgressBar`, `AppError`, `AppEmptyState`, `AppDivider` e `AppIcons`.

## Roadmap resumido

O desenvolvimento está organizado em 16 sprints funcionais e uma sprint técnica adicional, W1, inserida entre as Sprints 5 e 6:

1. Fundação do projeto;
2. Design System;
3. Navegação;
4. Comunicação;
5. Domínio do catálogo;
W1. Fundação Web;
6. Busca de animes end-to-end;
7. Detalhes do anime end-to-end;
8. Persistência e biblioteca local;
9. Biblioteca end-to-end;
10. Home;
11. Configurações e preferências;
12. Backend e autenticação;
13. Sincronização e perfil;
14. Cache e experiência offline;
15. Localização e tradução;
16. Preparação para lançamento.

Consulte o [`docs/roadmap.md`](docs/roadmap.md) para entregas e critérios de aceite de cada sprint.

A próxima etapa é a **S6.3 — Navegação de detalhes com ID AniList**.

## Documentação

- [`docs/README.md`](docs/README.md): índice da documentação;
- [`docs/documento-mestre.md`](docs/documento-mestre.md): especificações de produto e direção arquitetural estável;
- [`docs/api-externa-anilist.md`](docs/api-externa-anilist.md): decisão arquitetural e contrato inicial da integração com a AniList;
- [`docs/identidade-visual.md`](docs/identidade-visual.md): identidade, cores, tipografia e princípios de UX;
- [`docs/modelagem.md`](docs/modelagem.md): arquitetura, modelos e fluxos vigentes ou explicitamente propostos;
- [`docs/roadmap.md`](docs/roadmap.md): planejamento e estado consolidado das sprints;
- [`docs/sprints/`](docs/sprints/README.md): execução, evidências e encerramentos;
- [`docs/adr/`](docs/adr/README.md): decisões arquiteturais e consequências.

Quando houver divergências, aplique a prioridade definida no `AGENTS.md` e corrija a fonte documental responsável pela informação.

## Princípios de desenvolvimento

- criar componentes reutilizáveis antes de componentes específicos;
- manter regras de negócio fora da UI;
- abstrair comunicação externa por repositories;
- usar estados de tela padronizados;
- favorecer código desacoplado, testável e sem duplicação;
- evoluir incrementalmente, sem antecipar funcionalidades futuras.
- entregar fatias verticais e modelar somente a partir de casos de uso concretos;
- manter a biblioteca local-first, independente de autenticação e backend para seu funcionamento principal.

---

**MykytaDu — anime na personalidade, produto de software na execução.**
