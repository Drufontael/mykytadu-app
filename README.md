# MykytaDu

> Sua jornada pelos animes.

MykytaDu é um aplicativo multiplataforma para organizar e acompanhar animes. O produto está sendo desenvolvido de forma incremental, com foco em uma arquitetura desacoplada, componentes reutilizáveis e uma experiência visual consistente.

Atualmente, a fundação multiplataforma, o Design System e a infraestrutura de navegação estão concluídos. A aplicação apresenta placeholders conectados pelo Navigation 3; busca, autenticação, biblioteca e as demais funcionalidades de produto ainda fazem parte das próximas sprints.

## Plataformas

- Android
- Desktop (JVM)
- iOS (dispositivo e simulador)

## Estado atual

| Etapa | Estado |
|---|---|
| Sprint 1 — Fundação | Concluída |
| Sprint 2 — Design System | Concluída |
| Sprint 3 — Navegação | Concluída |
| Sprint 4 — Comunicação | Próxima etapa |
| Sprints 5 a 16 | Planejadas |

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
- estrutura compartilhada para resolução de Deep Links.

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
│       └── iosMain/       # Engine HTTP do iOS
├── gradle/
│   └── libs.versions.toml # Catálogo central de dependências
└── docs/                  # Produto, identidade, modelagem e roadmap
```

O código compartilhado usa o namespace `br.com.mykytadu`. A arquitetura planejada separa apresentação, domínio e dados, mantendo regras de negócio fora da UI e o acesso externo atrás de repositories.

## Design System

A identidade visual adota Poppins, Material 3 e temas claro e escuro baseados em tokens. O modo escuro é a principal referência da marca, com violeta como cor primária e turquesa como destaque.

Entre os elementos implementados estão `AppButton`, `AppTextField`, `AppCard`, `AppTopBar`, `AppSearchBar`, `AppChip`, `AppIconButton`, `AppDialog`, `AppLoading`, `AppProgressBar`, `AppError`, `AppEmptyState`, `AppDivider` e `AppIcons`.

## Roadmap resumido

O desenvolvimento está organizado em 16 sprints:

1. Fundação do projeto;
2. Design System;
3. Navegação;
4. Comunicação;
5. Modelagem;
6. Camada de dados;
7. Gerenciamento de estado;
8. Busca de animes;
9. Detalhes do anime;
10. Autenticação;
11. Biblioteca;
12. Perfil;
13. Configurações;
14. Cache;
15. Traduções;
16. Polimento.

Consulte o [`docs/roadmap.md`](docs/roadmap.md) para entregas e critérios de aceite de cada sprint.

## Documentação

- [`docs/README.md`](docs/README.md): índice da documentação;
- [`docs/documento-mestre.md`](docs/documento-mestre.md): visão consolidada, decisões técnicas e estado do projeto;
- [`docs/api-externa-anilist.md`](docs/api-externa-anilist.md): decisão arquitetural e contrato inicial da integração com a AniList;
- [`docs/identidade-visual.md`](docs/identidade-visual.md): identidade, cores, tipografia e princípios de UX;
- [`docs/modelagem.md`](docs/modelagem.md): diagramas e propostas de navegação, domínio e fluxos entre camadas;
- [`docs/roadmap.md`](docs/roadmap.md): planejamento completo das sprints.

Quando houver divergências, o código atual representa a fonte de verdade, seguido pelas decisões validadas e pelo Documento Mestre.

## Princípios de desenvolvimento

- criar componentes reutilizáveis antes de componentes específicos;
- manter regras de negócio fora da UI;
- abstrair comunicação externa por repositories;
- usar estados de tela padronizados;
- favorecer código desacoplado, testável e sem duplicação;
- evoluir incrementalmente, sem antecipar funcionalidades futuras.

---

**MykytaDu — anime na personalidade, produto de software na execução.**
