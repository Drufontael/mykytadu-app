# MykytaDu — Documento Mestre do Projeto

> Documento central de contexto, decisões técnicas, identidade visual, roadmap e estado de desenvolvimento.
>
> Este documento deve ser atualizado conforme o projeto evoluir e servir como referência para novos chats, sessões de desenvolvimento e documentação futura.

---

# 1. Visão Geral

## Nome

**MykytaDu**

## Propósito

MykytaDu é um aplicativo multiplataforma para **controle e acompanhamento de animes**.

O conceito do produto combina:

> **Anime moderno + tecnologia + organização pessoal**

A experiência deve funcionar como um espaço pessoal onde o usuário acompanha sua jornada pelos animes, sua biblioteca e seu progresso.

A filosofia central da interface é:

> **Anime na personalidade, produto de software na execução.**

---

# 2. Visão do Produto

A aplicação deve permitir ao usuário acompanhar sua experiência com animes de maneira organizada e pessoal.

A experiência deve transmitir:

- Biblioteca pessoal;
- Organização;
- Acompanhamento de progresso;
- Continuidade do que está sendo assistido;
- Facilidade para encontrar novos títulos;
- Personalização da experiência.

O conteúdo deve ser o protagonista da interface. Capas, títulos, informações e progresso dos animes devem receber mais destaque do que elementos puramente decorativos.

---

# 3. Direção de UX e Identidade

## Personalidade

O MykytaDu deve ser:

- Otaku — referências sutis ao universo dos animes;
- Organizado — hierarquia visual clara e interface limpa;
- Moderno — linguagem tecnológica e contemporânea;
- Imersivo — experiência dark forte;
- Pessoal — sensação de biblioteca própria.

A interface deve ser:

> Geek, moderna, elegante e pessoal.

## Evitar

- Excesso de neon;
- Fundos completamente pretos;
- Gradientes em todos os componentes;
- Tipografia excessivamente temática de anime;
- Elementos decorativos em excesso;
- Aparência de portal genérico de animes;
- Cópias visuais de serviços existentes.

---

# 4. Identidade Visual

## 4.1 Dark Mode

| Token | Cor | Uso |
|---|---|---|
| `background` | `#0B0D12` | Fundo principal |
| `surface` | `#12151D` | Cards e superfícies |
| `surfaceVariant` | `#191D27` | Superfícies secundárias |
| `primary` | `#8B7CFF` | Ações principais |
| `primaryVariant` | `#6C5CE7` | Estados secundários |
| `secondary` | `#45D6C8` | Progresso e destaques |
| `textPrimary` | `#F4F4F7` | Texto principal |
| `textSecondary` | `#A7A9B4` | Texto secundário |
| `divider` | `#292D38` | Separadores |

O Dark Mode é a principal referência da marca.

## 4.2 Light Mode

| Token | Cor | Uso |
|---|---|---|
| `background` | `#F7F7FB` | Fundo principal |
| `surface` | `#FFFFFF` | Cards e superfícies |
| `surfaceVariant` | `#F0F0F6` | Elementos secundários |
| `primary` | `#6355D9` | Ações principais |
| `primaryVariant` | `#5144C4` | Estados secundários |
| `secondary` | `#159E94` | Progresso e destaques |
| `textPrimary` | `#171820` | Texto principal |
| `textSecondary` | `#656875` | Texto secundário |
| `divider` | `#E2E3EA` | Separadores |

O Light Mode deve preservar a identidade visual e não ser apenas uma inversão do Dark Mode.

---

# 5. Cores Semânticas

| Status | Cor |
|---|---|
| Assistindo | `#8B7CFF` |
| Concluído | `#3CCB7F` |
| Pausado | `#E8B84A` |
| Abandonado | `#E45B68` |
| Planejado | `#55A8FF` |

Cada status deve possuir significado visual consistente em todo o aplicativo.

---

# 6. Tipografia

## Fonte principal

**Poppins**

Uso planejado:

- `Bold` — títulos principais;
- `SemiBold` — títulos de cards e seções;
- `Medium` — botões e labels;
- `Regular` — corpo de texto;
- `Light` — informações auxiliares.

A Poppins já foi integrada aos recursos compartilhados do Compose Multiplatform.

---

# 7. Design System

O MykytaDu utiliza Kotlin Multiplatform, Compose Multiplatform e Material 3.

A identidade é implementada através de um Design System baseado em tokens:

```text
Theme
├── Colors
├── Typography
├── Shapes
└── Dimensions
    ├── Spacing
    ├── Padding
    └── Radius
```

As telas não devem definir diretamente cores, espaçamentos ou raios arbitrários.

## 7.1 Material 3 e componentes próprios

Preferir Material 3 quando ele já resolve corretamente o problema.

Criar abstrações próprias quando elas:

- Centralizam comportamento;
- Centralizam tokens;
- Garantem consistência visual;
- Evitam duplicação;
- Permitem evolução coerente da API do Design System.

---

# 8. Arquitetura do Projeto

A fonte oficial para a arquitetura e a modelagem do projeto é o documento [`modelagem.md`](modelagem.md).

Estrutura conceitual principal:

```text
br/com/mykytadu/

├── app/
├── core/
│   ├── common/
│   ├── constants/
│   ├── extensions/
│   ├── navigation/
│   ├── theme/
│   └── utils/
├── data/
├── di/
├── domain/
├── features/
│   ├── anime/
│   ├── auth/
│   ├── home/
│   ├── library/
│   ├── profile/
│   ├── search/
│   ├── settings/
│   └── splash/
└── presentation/
```

Regra: antes de criar estruturas paralelas, verificar o que já existe no código atual.

---

# 9. Stack Técnica

- Kotlin: **2.3.20**
- JDK: **21**
- Gradle Wrapper: **8.14**
- Compose Multiplatform: **1.10.3**
- Material 3
- Koin: **4.2.2**
- Ktor Client
- Kotlinx Serialization

As versões efetivamente utilizadas devem sempre ser verificadas no `libs.versions.toml`.

---

# 11. Roadmap Geral do Frontend

O roadmap oficial organiza o desenvolvimento em **16 sprints**:

| Sprint | Objetivo | Estado |
|---|---|---|
| 1 | Fundação do Projeto | ✅ Concluída |
| 2 | Design System | ✅ Concluída |
| 3 | Navegação | ✅ Concluída |
| 4 | Camada de Comunicação | ⏳ Planejada |
| 5 | Modelagem | ⏳ Planejada |
| 6 | Camada de Dados | ⏳ Planejada |
| 7 | Gerenciamento de Estado | ⏳ Planejada |
| 8 | Busca de Animes | ⏳ Planejada |
| 9 | Tela de Detalhes | ⏳ Planejada |
| 10 | Autenticação | ⏳ Planejada |
| 11 | Biblioteca | ⏳ Planejada |
| 12 | Perfil | ⏳ Planejada |
| 13 | Configurações | ⏳ Planejada |
| 14 | Cache | ⏳ Planejada |
| 15 | Traduções | ⏳ Planejada |
| 16 | Polimento | ⏳ Planejada |

O roadmap é a fonte de planejamento. O código atual e as implementações validadas determinam o estado real do projeto.

---

# 12. Sprint 1 — Fundação

## Status

✅ **CONCLUÍDA**

Entregas consolidadas:

- Compose Multiplatform;
- Gradle e Version Catalog;
- Estrutura multiplataforma;
- Dependency Injection;
- Cliente HTTP;
- Serialização;
- Engines HTTP por plataforma.

## 12.1 Koin e Networking

A infraestrutura de Dependency Injection foi validada em runtime.

Fluxo conceitual:

```text
Application
    ↓
initializeKoin()
    ↓
NetworkModule
    ↓
HttpClient
```

O Ktor utiliza uma abstração de engine por plataforma através de `provideHttpClientEngine()`, com implementações específicas para Android, Desktop e iOS.

Não recriar essa abstração sem necessidade.

---

# 13. Sprint 2 — Design System

## Status

✅ **CONCLUÍDA**

## 13.1 Tema e tokens implementados

- `AppColors.kt`;
- `AppTypography.kt`;
- `AppShapes.kt`;
- `AppDimensions.kt`;
- `AppTheme.kt`;
- Tokens de spacing, padding, radius e ícones.

Dark e Light Theme foram validados.

## 13.2 Componentes originalmente previstos no roadmap

| Componente | Estado |
|---|---|
| `AppButton` | ✅ Implementado |
| `AppTextField` | ✅ Implementado e expandido |
| `AppCard` | ✅ Implementado |
| `AppTopBar` | ✅ Implementado |
| `AppSearchBar` | ✅ Implementado |
| `AppChip` | ✅ Implementado |
| `AppIconButton` | ✅ Implementado |
| `AppDialog` | ✅ Implementado |
| `AppLoading` | ✅ Implementado |
| `AppError` | ✅ Implementado |
| `AppEmptyState` | ✅ Implementado |

Os **11 componentes originalmente previstos no roadmap estão implementados**.

## 13.3 Elementos adicionais surgidos durante a implementação

| Elemento | Estado | Papel |
|---|---|---|
| `AppDivider` | ✅ Implementado | Padroniza divisores visuais |
| `AppProgressBar` | ✅ Implementado | Padroniza a exibição de progresso determinado |
| `AppIcons` | ✅ Implementado | Centraliza a linguagem iconográfica |
| `DesignSystemShowcase` | ✅ Implementado | Validação visual dos componentes |

`AppDivider` e `AppProgressBar` são componentes reutilizáveis adicionais que não estavam previstos originalmente na Sprint 2. `AppIcons` e `DesignSystemShowcase` também surgiram durante a implementação como infraestrutura complementar. Essas inclusões representam evoluções orgânicas do Design System e foram incorporadas sem alterar o escopo funcional das próximas sprints.

---

## 13.4 Decisões Recentes do Design System

### 13.4.1 AppDivider

`AppDivider` foi criado durante a implementação para centralizar e padronizar divisores visuais.

O componente foi validado visualmente quanto à linha e ao comportamento de padding.

### 13.4.2 AppIcons

Foi criada a abstração `AppIcons` para centralizar os ícones utilizados pelo Design System.

Objetivos:

- Evitar referências de ícones espalhadas pelos componentes;
- Criar linguagem iconográfica consistente;
- Facilitar substituições futuras;
- Fazer com que componentes reutilizáveis dependam da abstração do projeto em vez de escolhas locais de ícones.

### 13.4.3 AppTextField

O `AppTextField` teve sua API expandida durante a evolução dos componentes.

Essa decisão estabelece um princípio para o Design System:

> Componentes existentes podem ser ampliados quando novas necessidades reutilizáveis surgirem, desde que a mudança preserve coerência, reutilização e baixo acoplamento.

A preferência é evoluir uma abstração existente em vez de criar componentes redundantes.

### 13.4.4 AppTopBar

`AppTopBar` foi implementado e validado como parte da Sprint 2.

Ele deixa de fazer parte das pendências do Design System.

### 13.4.5 AppSearchBar

`AppSearchBar` foi implementado sobre o `SearchBar` do Material 3, mantendo uma API alinhada ao Design System.

Durante a validação visual, o shape precisou ser ajustado porque o radius inicialmente utilizado não correspondia ao esperado. A versão final utiliza o shape/radius definido pelo projeto.

O componente utiliza `AppIcons.Actions.Search` como ícone de busca padrão.

### 13.4.6 Componentes de feedback e estado

Foram implementados os componentes previstos `AppLoading`, `AppError` e `AppEmptyState`, cobrindo feedback de carregamento, falha e ausência de conteúdo.

Também foi criado `AppProgressBar`, não previsto originalmente, para representar progresso determinado de forma consistente com os tokens do tema. Com ele, o Design System diferencia carregamento indeterminado (`AppLoading`) de progresso mensurável (`AppProgressBar`).

### 13.4.7 AppDialog

`AppDialog` foi implementado como uma abstração reutilizável para confirmações e mensagens, com título, mensagem, ícone e ações configuráveis.

## 13.5 Critérios de Aceite da Sprint 2

Conforme o roadmap:

- [x] Nenhuma cor fixa utilizada nas telas;
- [x] Todos os componentes reutilizáveis;
- [x] Tema aplicado globalmente.

A revisão final confirmou que as telas não utilizam literais de cor, os componentes expõem APIs parametrizadas para reutilização e o tema está aplicado globalmente. A compilação e os testes do target Desktop foram executados com sucesso antes do encerramento da sprint.

Além dos critérios formais, a implementação vem seguindo o ciclo:

```text
Implementar
    ↓
Compilar
    ↓
Validar visualmente
    ↓
Ajustar
    ↓
Avançar
```

---

# 15. Sprint 3 — Navegação

## Status

✅ **CONCLUÍDA**

Entregas consolidadas:

- oito rotas tipadas, serializáveis e centralizadas em `AppRoute`;
- navegação compartilhada com Navigation 3, `rememberNavBackStack`, `NavDisplay` e `entryProvider`;
- serialização polimórfica do back stack com `SavedStateConfiguration`;
- fluxo de entrada `Splash → Login → Home`, removendo Splash e Login do histórico após o avanço;
- destinos principais Home, Biblioteca, Pesquisa e Perfil centralizados em `MainDestination`;
- `MainNavigationBar` exibida somente nos quatro destinos principais;
- troca entre destinos principais sem acumular as abas visitadas no back stack;
- transições Pesquisa → Detalhes do Anime, Biblioteca → Detalhes do Anime e Perfil → Configurações;
- classificação declarativa de rotas públicas e protegidas em `RouteAccess`;
- resolução compartilhada de Deep Links em `AppDeepLink`;
- placeholders compartilhados por meio de `NavigationPlaceholder`.

A classificação `PROTECTED` prepara a futura autenticação, mas não aplica validação de sessão ou regras de negócio. Essa responsabilidade permanece planejada para a Sprint 10.

Os Deep Links compartilhados atualmente reconhecem Home, Pesquisa, Biblioteca, Perfil e Configurações. `AnimeDetails` permanece sem Deep Link até que exista um identificador definitivo de anime. Não foi adicionada integração específica por plataforma nesta sprint.

O build completo do módulo e os testes Desktop foram executados com sucesso no encerramento da sprint.

---

# 16. Sprint 4 — Camada de Comunicação

## Status

🚧 **EM ANDAMENTO**

A Sprint 4 tem como objetivo preparar a comunicação com o backend e APIs externas. A infraestrutura HTTP básica já existente foi auditada antes do avanço das próximas tasks.

## 16.1 S4.1 — Auditoria da infraestrutura HTTP

✅ **CONCLUÍDA**

A auditoria confirmou que a infraestrutura HTTP básica está implementada de forma compartilhada e coerente com Kotlin Multiplatform:

- `HttpClient` registrado como singleton no Koin;
- `ContentNegotiation` configurado com Kotlinx Serialization e `ignoreUnknownKeys`;
- engines OkHttp no Android, CIO no Desktop e Darwin no iOS;
- configuração comum centralizada em `commonMain` por meio de `expect/actual`;
- inicialização do Koin confirmada no Android e Desktop;
- testes de registro, resolução e singleton do cliente aprovados.

A compilação Desktop, os testes Desktop, o assemble Android e a compilação da metadata de iOS foram executados com sucesso. A compilação nativa de iOS não foi realizada por exigir macOS e Xcode.

Ainda não estão implementados:

- tratamento global e padronizado de erros;
- timeouts;
- logging;
- configuração comum de requisições;
- preparação para autenticação;
- `AnimeApi`, `AuthApi` e `TranslationApi`;
- primeira chamada HTTP funcional.

Foram identificados como pontos de atenção para as próximas tasks:

- ausência da permissão `INTERNET` no manifesto Android;
- inicialização do Koin no iOS ainda não demonstrada pelo código existente;
- ausência de encerramento explícito do `HttpClient`;
- possível uso desnecessário da dependência `koin-android`;
- testes atuais limitados ao registro e ciclo singleton do cliente.

A infraestrutura existente pode ser preservada e o projeto está apto a avançar para a próxima task da Sprint 4, começando pela consolidação da configuração comum do cliente e do tratamento de comunicação.

## 16.2 S4.2 — Seleção da API externa de animes

✅ **CONCLUÍDA**

A **AniList GraphQL API v2** foi escolhida como fonte principal do catálogo de animes. As consultas públicas utilizam `POST` no endpoint `https://graphql.anilist.co`, retornam JSON e não exigem autenticação.

Foram definidas e validadas manualmente duas operações iniciais:

- `SearchAnime`, para pesquisa paginada com `PageInfo`;
- `GetAnimeDetails`, para obtenção dos dados previstos na tela de detalhes.

O identificador externo principal será `Media.id`, com `Media.idMal` preservado como referência opcional. Os DTOs remotos deverão respeitar campos anuláveis, coleções vazias e datas parciais observadas nas respostas.

A integração deverá distinguir falhas HTTP de erros GraphQL, inclusive respostas HTTP `200` com `errors` ou dados parciais. Também deverá tratar rate limit e HTTP `429`, respeitando `Retry-After` quando disponível.

O futuro `AnimeApi` encapsulará queries, variables e envelopes GraphQL, sem expor tipos externos diretamente ao domínio. A implementação do serviço, dos DTOs Kotlin, repositories, modelos de domínio, cache, tradução e OAuth permanece fora do escopo desta decisão.

A decisão completa, incluindo queries validadas, nulabilidade, alternativas avaliadas, riscos e condições de reavaliação, está registrada em [`api-externa-anilist.md`](api-externa-anilist.md).

---

# 17. Diretrizes Gerais do Projeto

- Componentes reutilizáveis antes de componentes específicos;
- Nenhuma regra de negócio dentro da UI;
- Toda comunicação externa passa pelos Repositories;
- Estados padronizados em todas as telas;
- Código desacoplado e testável;
- Commits pequenos e descritivos;
- Uma responsabilidade por classe;
- Evitar duplicação;
- Evolução incremental;
- Não antecipar funcionalidades futuras;
- Verificar a estrutura existente antes de criar novos arquivos ou abstrações.

---

# 18. Fontes Oficiais do Projeto

## 18.1 Identidade Visual

**[`identidade-visual.md`](identidade-visual.md)**

Fonte para:

- Conceito;
- Personalidade da marca;
- Dark/Light Mode;
- Cores semânticas;
- Tipografia;
- Cards;
- Navegação;
- Ícones;
- Microinterações;
- Princípios de UX;
- Direção do Design System.

## 18.2 Roadmap do Frontend

**[`roadmap.md`](roadmap.md)**

Fonte para:

- Estrutura das 16 sprints;
- Objetivos;
- Entregas;
- Critérios de aceite;
- Diretrizes gerais;
- Visão de longo prazo.

## 18.3 Arquitetura e Modelagem

**[`modelagem.md`](modelagem.md)**

Fonte para:

- Arquitetura do projeto;
- Organização das camadas;
- Modelagem das entidades;
- Relações e responsabilidades entre os componentes.

## 18.4 API Externa de Animes

**[`api-externa-anilist.md`](api-externa-anilist.md)**

Fonte para:

- escolha da AniList GraphQL API v2;
- endpoint, protocolo e autenticação;
- operações de pesquisa e detalhes;
- paginação, nulabilidade e envelopes GraphQL;
- limites de uso, riscos e critérios de reavaliação;
- fronteiras arquiteturais da integração externa.

## 18.5 Histórico Técnico de Desenvolvimento

Conversas e registros de implementação do projeto.

Fonte para:

- Estado real dos componentes;
- Decisões tomadas durante a implementação;
- Problemas encontrados e soluções;
- Validações de compilação e runtime;
- Evoluções que não estavam previstas originalmente no roadmap.

---

# 19. Regra de Prioridade das Fontes

Quando houver divergência sobre o estado do projeto:

```text
Código atual
    ↓
Implementações e validações recentes
    ↓
Documento Mestre
    ↓
Roadmap
    ↓
Documentos históricos
```

O roadmap define **para onde vamos**.

O Documento Mestre registra **onde estamos e por quê**.

O código determina **o que realmente existe**.

---

# 20. Regra para Novos Chats

Ao continuar o desenvolvimento em um novo chat:

- Utilizar este documento como contexto principal;
- Não reiniciar sprints concluídas;
- Verificar o código atual antes de alterar arquivos;
- Trabalhar incrementalmente;
- Compilar frequentemente;
- Validar visualmente os componentes;
- Não antecipar funcionalidades futuras;
- Atualizar este Documento Mestre após decisões relevantes.

---

# 21. Resumo Executivo

## Produto

**MykytaDu — Sua jornada pelos animes.**

## Stack

**Kotlin Multiplatform + Compose Multiplatform**

## Roadmap

**16 Sprints**

## Estado atual

- Sprint 1 — Fundação: ✅ Concluída
- Sprint 2 — Design System: ✅ Concluída
- Sprint 3 — Navegação: ✅ Concluída
- Sprint 4 — Camada de Comunicação: 🚧 Em andamento — S4.1 e S4.2 concluídas
- Sprints 5–16: ⏳ Planejadas

## Componentes do roadmap concluídos na Sprint 2

- `AppButton`
- `AppTextField`
- `AppCard`
- `AppTopBar`
- `AppSearchBar`
- `AppChip`
- `AppIconButton`
- `AppDialog`
- `AppLoading`
- `AppError`
- `AppEmptyState`

## Adições ao Design System

- `AppDivider`
- `AppProgressBar`
- `AppIcons`
- `DesignSystemShowcase`

## Próximo passo

> **Continuar a Sprint 4 com a configuração operacional da comunicação GraphQL.**

## Filosofia

> **Anime na personalidade, produto de software na execução.**
