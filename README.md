# MykytaDu

> Sua jornada pelos animes.

MykytaDu é um aplicativo multiplataforma moderno para organizar e acompanhar animes. O produto foi construído de forma incremental com arquitetura desacoplada, design system consistente, persistência *local-first*, sincronização opcional em nuvem, resiliência offline, internacionalização e conformidade para distribuição.

Todas as **16 sprints** do roadmap funcional (S1 a S16), além das manutenções arquiteturais W1 e M1, estão **concluídas**, com a versão **1.0.0** empacotada e validada nos targets ativos. A compilação de metadata do target iOS está comprovada e funcional, dependendo apenas de ambiente físico macOS e Xcode para o build nativo final.

## Plataformas

- Android (APK e AAB)
- Desktop / JVM (Distribuição nativa executável)
- iOS (Dispositivo e simulador — compilação de metadata validada)
- Web (WasmJS principal e JavaScript alternativo)

## Estado atual

| Etapa | Estado |
|---|---|
| Sprint 1 — Fundação | Concluída |
| Sprint 2 — Design System | Concluída |
| Sprint 3 — Navegação | Concluída |
| Sprint 4 — Comunicação | Concluída |
| Sprint 5 — Domínio do Catálogo | Concluída |
| Sprint W1 — Fundação Web | Concluída |
| Sprint 6 — Busca de Animes | Concluída |
| Sprint M1 — Identidade do Catálogo | Concluída |
| Sprint 7 — Detalhes do Anime | Concluída |
| Sprint 8 — Persistência e Biblioteca Local | Concluída |
| Sprint 9 — Biblioteca End-to-End | Concluída |
| Sprint 10 — Home | Concluída |
| Sprint 11 — Configurações e Preferências | Concluída |
| Sprint 12 — Backend e Autenticação | Concluída |
| Sprint 13 — Sincronização e Perfil | Concluída |
| Sprint 14 — Cache e Experiência Offline | Concluída |
| Sprint 15 — Localização e Tradução | Concluída |
| Sprint 16 — Preparação para Lançamento | Concluída (Versão 1.0.0) |

## Capacidades disponíveis

- **Fundação Multiplataforma:** Configuração completa Kotlin Multiplatform, Compose Multiplatform, Material 3, injeção de dependências com Koin, cliente HTTP Ktor com engines por plataforma e serialização com Kotlinx Serialization;
- **Design System e Acessibilidade:** Tema escuro prioritário e tema claro baseados em tokens, tipografia Poppins, componentes reutilizáveis, modais informativos com rolagem vertical (`AppInfoDialog`), semântica assistiva (`semantics { selected = ... }`) e suporte a leitores de tela;
- **Navegação Declarativa:** Navigation 3 compartilhado com back stack persistente, tipagem segura, rotas protegidas, Deep Links e shell Web integrado ao histórico do navegador por fragmentos (`#/anime/{id}`);
- **Catálogo e Busca:** Integração robusta com a API GraphQL da AniList, debounce inteligente, paginação incremental, trailer, relações navegáveis e layout responsivo;
- **Biblioteca Local-First:** Gerenciamento de status (Assistindo, Concluído, etc.), notas, contagem de episódios com incremento rápido (+1), ordenação, filtros e persistência local no dispositivo sem obrigar login;
- **Home Híbrida:** Continuidade rápida ("Continuar Assistindo"), títulos recentemente atualizados e tendências populares da temporada com isolamento defensivo de falhas de rede;
- **Configurações e Preferências:** Seleção de tema (Claro, Escuro, Sistema), idioma dinâmico, feedback tátil, limpeza de cache e exclusão segura de dados locais;
- **Autenticação e Nuvem Opcional:** Sessão desacoplada (*local-first*) com suporte a backend MykytaDu/Supabase, sincronização em nuvem bidirecional idempotente e perfil de usuário com avatar e estatísticas;
- **Resiliência e Cache Offline:** Estratégia de Cache-First para catálogo e detalhes, permitindo navegação fluida mesmo sem conectividade com a internet;
- **Internacionalização (i18n):** Suporte multilíngue nativo em Português (pt-BR), Inglês (en-US) e Espanhol (es-ES) com troca em tempo de execução e tradução sob demanda de sinopses com cache LRU;
- **Release v1.0.0 e Conformidade:** Identidade visual refinada, versão `1.0.0`, política de privacidade transparente (*local-first*), licenças de código aberto (Apache 2.0), atribuição à API AniList e empacotamento validado para Desktop, Web e Android.

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
- Android Studio com Android SDK 36 para executar o target Android;
- macOS com Xcode para compilar e executar nativamente os targets iOS.

Não é necessário instalar o Gradle separadamente: o repositório inclui o Gradle Wrapper.

## Como executar

Clone o repositório e acesse a pasta do projeto. No Windows, use `gradlew.bat`; em macOS ou Linux, use `./gradlew`.

### Desktop

```powershell
.\gradlew.bat :composeApp:desktopRun
```

Ou através da task do Compose Multiplatform:

```powershell
.\gradlew.bat :composeApp:run
```

Em macOS ou Linux:

```bash
./gradlew :composeApp:desktopRun
# ou ./gradlew :composeApp:run
```

### Android

Abra o projeto no Android Studio, selecione a configuração `composeApp` e execute em um dispositivo ou emulador. Também é possível instalar o build de debug por linha de comando:

```powershell
.\gradlew.bat :composeApp:installDebug
```

### iOS

Os targets `iosArm64` e `iosSimulatorArm64` estão configurados e suas tasks de metadata compilam com sucesso (`:composeApp:compileIosMainKotlinMetadata`). A compilação e a integração final do framework nativo exigem macOS e Xcode.

O projeto está aberto a contribuições com ambiente macOS/Xcode para validar o empacotamento nativo em iOS.

### Web

WasmJS é o target Web principal e JavaScript é a alternativa de compatibilidade. Os dois são executados e distribuídos separadamente; não existe seleção automática de fallback.

Para executar os servidores de desenvolvimento no Windows:

```powershell
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun
```

A navegação Web usa fragmentos (`#/...`) e não depende de rewrite de paths no servidor.

## Empacotamento de distribuição (Release v1.0.0)

O projeto está preparado e validado para a geração de pacotes distribuíveis em cada plataforma:

### Desktop

Gera o binário executável empacotado da aplicação com todos os metadados de versão (`1.0.0`), vendor e descrição:

```powershell
.\gradlew.bat :composeApp:createDistributable
```

O artefato é gerado em:
```text
composeApp/build/compose/binaries/main/app
```

### Web (WasmJS e JS)

Gera os pacotes otimizados de produção:

```powershell
.\gradlew.bat :composeApp:wasmJsBrowserDistribution
.\gradlew.bat :composeApp:jsBrowserDistribution
```

Os artefatos são gerados em:
```text
composeApp/build/dist/wasmJs/productionExecutable
composeApp/build/dist/js/productionExecutable
```

Para testar localmente via HTTP estático:

```powershell
python -m http.server 8080 --directory composeApp\build\dist\wasmJs\productionExecutable
```

### Android

Gera o pacote APK para instalação:

```powershell
.\gradlew.bat :composeApp:assembleDebug
```

O APK é gerado em:
```text
composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

## Testes

Para garantir a máxima integridade e redução de processos repetitivos, recomenda-se executar a **bateria composta de testes multiplataforma**, que valida os 5 targets simultaneamente:

```powershell
.\gradlew.bat :composeApp:desktopTest :composeApp:testDebugUnitTest :composeApp:compileIosMainKotlinMetadata :composeApp:jsBrowserTest :composeApp:wasmJsBrowserTest
```

Em macOS ou Linux:

```bash
./gradlew :composeApp:desktopTest :composeApp:testDebugUnitTest :composeApp:compileIosMainKotlinMetadata :composeApp:jsBrowserTest :composeApp:wasmJsBrowserTest
```

Para executar testes unitários do Desktop individualmente:

```powershell
.\gradlew.bat :composeApp:desktopTest
```

## Estrutura do projeto

```text
mykytadu-app/
├── composeApp/
│   └── src/
│       ├── commonMain/    # UI, temas, navegação, componentes, domínio e DI compartilhados
│       ├── commonTest/    # Testes unitários compartilhados
│       ├── androidMain/   # Entrada, recursos e engines do Android
│       ├── desktopMain/   # Entrada e engines do Desktop
│       ├── desktopTest/   # Testes de UI em Desktop Compose
│       ├── iosMain/       # Engines e pontos de entrada do iOS
│       ├── webMain/       # Entrada, engine Fetch e recursos Web
│       ├── jsMain/        # APIs específicas do JavaScript
│       └── wasmJsMain/    # APIs específicas do WasmJS
├── gradle/
│   └── libs.versions.toml # Catálogo central de dependências
└── docs/                  # Produto, identidade, modelagem, sprints e ADRs
```

O código compartilhado utiliza o namespace `br.com.mykytadu`. A arquitetura separa estritamente apresentação, domínio e dados, mantendo regras de negócio fora da UI e o acesso externo protegido atrás de repositories e fontes locais.

## Design System

A identidade visual adota Poppins, Material 3 e temas claro e escuro baseados em tokens. O modo escuro é a principal referência da marca, com violeta como cor primária e turquesa como destaque.

Entre os componentes implementados estão `AppButton`, `AppTextField`, `AppCard`, `AppTopBar`, `AppSearchBar`, `AppChip`, `AppIconButton`, `AppDialog`, `AppInfoDialog`, `AppLoading`, `AppProgressBar`, `AppError`, `AppEmptyState`, `AppDivider` e `AppIcons`.

## Roadmap e evolução

O desenvolvimento do MykytaDu foi estruturado em 16 sprints funcionais e manutenções técnicas integradas:

1. Fundação do projeto;
2. Design System;
3. Navegação;
4. Comunicação;
5. Domínio do catálogo;
W1. Fundação Web;
6. Busca de animes end-to-end;
M1. Identidade do catálogo;
7. Detalhes do anime end-to-end;
8. Persistência e biblioteca local;
9. Biblioteca end-to-end;
10. Home;
11. Configurações e preferências;
12. Backend e autenticação;
13. Sincronização e perfil;
14. Cache e experiência offline;
15. Localização e tradução;
16. Preparação para lançamento (v1.0.0).

Todas as sprints acima estão **100% concluídas**. Consulte o [`docs/roadmap.md`](docs/roadmap.md) para o detalhamento consolidado dos critérios e a pasta [`docs/sprints/`](docs/sprints/README.md) para os registros contemporâneos de execução de cada entrega.

## Documentação

- [`docs/README.md`](docs/README.md): índice geral da documentação;
- [`docs/documento-mestre.md`](docs/documento-mestre.md): especificações de produto e direção arquitetural estável;
- [`docs/api-externa-anilist.md`](docs/api-externa-anilist.md): decisão arquitetural e contrato da integração com a AniList;
- [`docs/identidade-visual.md`](docs/identidade-visual.md): identidade, cores, tipografia e princípios de UX;
- [`docs/modelagem.md`](docs/modelagem.md): arquitetura, diagramas Mermaid, modelos e fluxos vigentes;
- [`docs/roadmap.md`](docs/roadmap.md): planejamento e estado consolidado das sprints;
- [`docs/sprints/`](docs/sprints/README.md): registros de execução, evidências e encerramentos de sprints;
- [`docs/adr/`](docs/adr/README.md): decisões arquiteturais, contexto e consequências.

## Princípios de desenvolvimento

- criar componentes reutilizáveis antes de componentes específicos;
- manter regras de negócio fora da UI;
- abstrair comunicação externa por repositories;
- usar estados de tela padronizados;
- favorecer código desacoplado, testável e sem duplicação;
- entregar fatias verticais e modelar somente a partir de casos de uso concretos;
- manter a biblioteca *local-first*, independente de autenticação e backend para seu funcionamento principal;
- transparência com o usuário: respeito à privacidade e dados locais sem rastreamento indesejado.

---

**MykytaDu — anime na personalidade, produto de software na execução.**
