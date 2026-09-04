# MykytaDu — Modelagem

> Documento vivo de modelagem do projeto MykytaDu.
>
> Este arquivo concentra os diagramas do sistema utilizando **Mermaid**, acompanhados de explicações sobre decisões de domínio, navegação, arquitetura e fluxos.
>
> O documento deve evoluir junto com as sprints, registrando apenas decisões suficientemente consolidadas para evitar antecipação desnecessária de arquitetura ou regras de negócio.

---

# 1. Objetivo

Este documento tem como objetivo registrar visualmente a evolução estrutural do **MykytaDu**, um aplicativo multiplataforma para controle e acompanhamento de animes.

Os diagramas aqui presentes devem servir como apoio para:

- compreender a estrutura do sistema;
- discutir decisões antes da implementação;
- registrar relações entre entidades;
- documentar fluxos importantes;
- visualizar estados e transições;
- manter alinhamento entre UI, domínio, dados e navegação;
- facilitar a retomada do projeto em novas sessões de desenvolvimento.

A modelagem deve acompanhar a evolução real do código e do roadmap.

---

# 2. Princípios de Modelagem

A documentação do MykytaDu segue os mesmos princípios gerais adotados no desenvolvimento:

- Evolução incremental;
- Evitar antecipação de funcionalidades futuras;
- Uma responsabilidade clara por elemento;
- Baixo acoplamento;
- Reutilização;
- Regras de negócio fora da UI;
- Comunicação externa abstraída por repositories;
- Modelos imutáveis sempre que possível;
- Código e implementação real têm prioridade sobre documentação desatualizada.

Quando houver divergência entre modelagem e implementação, a ordem de prioridade é:

```text
Código atual
    ↓
Implementações e validações recentes
    ↓
Documento Mestre
    ↓
Modelagem
    ↓
Roadmap
    ↓
Documentos históricos
```

Após uma decisão arquitetural relevante ser consolidada no código, este documento deve ser atualizado.

---

# 3. Convenções dos Diagramas

Os diagramas deste documento utilizam **Mermaid**.

## 3.1 Tipos previstos

Ao longo das sprints, poderão ser utilizados:

- `flowchart` — navegação e fluxos;
- `classDiagram` — modelos e relações de domínio;
- `stateDiagram-v2` — estados e transições;
- `sequenceDiagram` — interação entre camadas;
- `erDiagram` — persistência e estrutura de dados, quando necessário.

Diagramas arquiteturais de alto nível podem utilizar `flowchart` quando a representação for mais clara do que uma notação UML tradicional.

## 3.2 Diretrizes

Cada diagrama deve:

1. Ter um objetivo claro;
2. Representar apenas elementos relevantes naquele momento;
3. Possuir uma explicação textual;
4. Diferenciar decisões consolidadas de propostas;
5. Ser atualizado quando a implementação alterar o fluxo representado.

---

# 4. Sprint 3 — Navegação

> **Status:** Concluída.

## 4.1 Objetivo

A Sprint 3 tem como objetivo definir a navegação do aplicativo.

As rotas implementadas são:

- Splash;
- Login;
- Home;
- Pesquisa;
- Detalhes do Anime;
- Biblioteca;
- Perfil;
- Configurações.

Também fazem parte da sprint:

- preparação de rotas protegidas;
- estrutura para Deep Links.

A navegação principal da experiência é formada por:

- Home;
- Biblioteca;
- Pesquisa;
- Perfil.

`Splash`, `Login`, `Detalhes do Anime` e `Configurações` são tratados como destinos auxiliares ou externos à navegação principal.

---

## 4.2 Diagrama de Navegação

```mermaid
flowchart TD
    Splash["Splash"]

    Login["Login"]
    Home["Home"]
    Search["Pesquisa"]
    AnimeDetails["Detalhes do Anime"]
    Library["Biblioteca"]
    Profile["Perfil"]
    Settings["Configurações"]

    MainNavigation["MainNavigationBar"]

    Splash -->|limpa o back stack| Login
    Login -->|limpa o back stack| Home

    MainNavigation -.-> Home
    MainNavigation -.-> Search
    MainNavigation -.-> Library
    MainNavigation -.-> Profile

    Search --> AnimeDetails
    Library --> AnimeDetails

    Profile --> Settings

    AnimeDetails -->|voltar| Search
    AnimeDetails -->|voltar| Library
    Settings -->|voltar| Profile
```

---

## 4.3 Interpretação

O fluxo começa pela tela `Splash` e segue para `Login`. Após o avanço, cada uma dessas rotas é removida do back stack:

```text
Splash
  ↓
Login
  ↓
Home
```

Após o acionamento do placeholder de Login, o usuário entra na área principal do aplicativo.

Nesta sprint, o avanço é apenas um gatilho de navegação do placeholder. Não existe autenticação, validação de sessão ou regra de negócio. A infraestrutura apenas classifica as rotas para preparar a autenticação, atualmente planejada para a Sprint 12.

---

## 4.4 Área Principal

A navegação principal pode ser entendida conceitualmente como:

```text
Área autenticada
├── Home
├── Pesquisa
├── Biblioteca
└── Perfil
```

Esses quatro destinos representam as áreas de uso recorrente do aplicativo.

Eles são centralizados em `MainDestination` e apresentados por `MainNavigationBar`. A troca entre eles substitui a aba atual, evitando acumular todas as abas visitadas no back stack. A barra não é exibida em `Splash`, `Login`, `AnimeDetails` ou `Settings`.

O fluxo conceitual completo fica:

```text
Navegação raiz
├── Splash
├── Login
└── Área autenticada
    ├── Home
    ├── Pesquisa
    │   └── Detalhes do Anime
    ├── Biblioteca
    │   └── Detalhes do Anime
    └── Perfil
        └── Configurações
```

---

## 4.5 Detalhes do Anime

`Detalhes do Anime` é uma rota secundária que pode ser acessada a partir de diferentes pontos da aplicação.

Inicialmente:

```text
Pesquisa
   ↓
Detalhes do Anime
```

e:

```text
Biblioteca
    ↓
Detalhes do Anime
```

No futuro, outras áreas, como a Home, também poderão abrir diretamente os detalhes de um anime.

Essa expansão somente deve ser adicionada ao diagrama quando fizer parte da implementação real.

---

## 4.6 Configurações

A rota `Configurações` está relacionada ao contexto do usuário e é acessada inicialmente através de:

```text
Perfil
  ↓
Configurações
```

Essa organização evita transformar a navegação principal em uma lista excessiva de destinos.

---

## 4.7 Rotas públicas e protegidas

`RouteAccess` consolida a seguinte classificação:

```text
PUBLIC
├── Splash
└── Login

PROTECTED
├── Home
├── Search
├── AnimeDetails
├── Library
├── Profile
└── Settings
```

Essa classificação é somente declarativa. A validação de sessão será implementada na Sprint 12.

---

## 4.8 Deep Links

`AppDeepLink` fornece uma resolução compartilhada para:

```text
mykytadu://app/home
mykytadu://app/search
mykytadu://app/library
mykytadu://app/profile
mykytadu://app/settings
```

Trailing slash é aceito e endereços desconhecidos retornam `null`. `AnimeDetails` não possui Deep Link porque ainda não existe um identificador definitivo de anime. Integrações de entrada específicas para Android e iOS não fazem parte desta sprint.

---

# 5. Sprint 5 — Domínio do Catálogo

> **Status:** Em andamento — S5.1 e S5.2 concluídas.

A Sprint 5 modelará somente os conceitos exigidos pelos casos de uso de pesquisa e detalhes. O domínio do MykytaDu será separado dos DTOs da AniList, com conversões explícitas para campos opcionais, coleções vazias e enums externos desconhecidos.

O escopo previsto inclui:

- resultados de pesquisa;
- detalhes de anime;
- títulos alternativos;
- imagens;
- gêneros;
- estúdios;
- datas parciais;
- trailer;
- relações entre obras;
- paginação;
- enums do domínio;
- `AnimeRepository`.

Não fazem parte desta modelagem inicial `Character`, `User`, `LibraryEntry`, `Review` ou regras detalhadas de temporadas e episódios sem consumidor atual. Esses conceitos deverão surgir somente nas sprints em que forem necessários.

## 5.1 Auditoria dos contratos atuais

A S5.1 confirmou que a camada remota já encapsula GraphQL, DTOs, validações de entrada e falhas de rede, mas ainda não existem modelos de catálogo, mapeadores ou `AnimeRepository`. O único tipo no pacote `domain` é o `AnimeStatus` usado pelo Design System para estados da futura biblioteca; ele não deve ser reutilizado como status editorial do catálogo.

A direção proposta para as próximas tasks é:

- separar `AnimeSummary` de `AnimeDetails`;
- preservar títulos e campos opcionais sem escolher idioma no mapper;
- manter gêneros como `List<String>`;
- converter enums remotos no mapper, com fallback seguro para valores desconhecidos;
- distinguir semanticamente IDs AniList e MyAnimeList, sem antecipar IDs locais ou de backend;
- manter `NetworkResult` na camada remota e expor pelo repository um resultado independente de transporte;
- adiar campos e modelos sem consumidor comprovado nas Sprints 6 e 7.

## 5.2 Contratos fundamentais

A S5.2 implementou `AniListAnimeId` como identificador positivo e semanticamente específico, além de `RepositoryResult` e sete categorias de `RepositoryFailure` independentes da infraestrutura. `NetworkFailure` é convertido internamente na camada de dados, com preservação opcional da causa técnica.

`PageInfo` valida página atual, tamanho da página, última página e total. `PagedResult<T>` aceita páginas vazias, preserva `hasNextPage` sem inferência pelo número de itens e mantém um snapshot da lista recebida. Esses tipos não são serializáveis e o domínio não depende de rede, GraphQL, Ktor ou DTOs.

---

## 5.3 Diagrama de Classes

```mermaid
classDiagram
    class PageInfo{
        +int currentPage
        +int? lastPage
        +bool hasNextPage
        +int perPage
        +int? total
    }
    class PagedResult~out T~{
        +list~T~ items
        +PageInfo pageInfo
     }
```

---

## 5.4 Questões a validar

Durante a modelagem de domínio deverão ser respondidas somente questões necessárias aos casos de uso atuais, como:

- quais dados distinguem resultados de pesquisa de detalhes;
- como representar títulos, imagens, gêneros, estúdios e datas parciais;
- o que pertence ao domínio e o que é apenas DTO de API;
- como representar dados opcionais e coleções vazias vindos da AniList;
- como converter enums externos e preservar valores desconhecidos com segurança.

---

# 6. Sprint 8 — Estado da Biblioteca Local

> **Status:** Planejado.

`LibraryEntry` será modelado quando a persistência local for implementada. Ele representa conceitualmente a relação local com um anime dentro da biblioteca pessoal e não depende da existência de usuário autenticado ou backend.

A biblioteca seguirá a estratégia local-first:

- funciona sem autenticação;
- persiste alterações localmente;
- continua disponível sem conexão ou backend;
- habilita sincronização somente após autenticação futura;
- mantém IDs locais, IDs AniList e futuros IDs de backend separados.

Estados previstos:

- Planejando;
- Assistindo;
- Pausado;
- Concluído;
- Abandonado.

---

## 6.1 Diagrama de Estados de LibraryEntry

```mermaid
stateDiagram-v2
    [*] --> Planejando

    Planejando --> Assistindo : iniciar

    Assistindo --> Pausado : pausar
    Pausado --> Assistindo : retomar

    Assistindo --> Concluido : concluir

    Assistindo --> Abandonado : abandonar
    Pausado --> Abandonado : abandonar

    Concluido --> [*]
    Abandonado --> [*]
```

> As transições acima representam uma proposta inicial. Regras como reabrir um anime concluído, retomar um abandonado ou concluir automaticamente pelo número de episódios deverão ser decididas durante a Sprint 8 e validadas no fluxo da Sprint 9.

---

# 7. Fatias Verticais e Fluxos entre Camadas

> **Status:** Planejado.

Repositories, fontes de dados, ViewModels e estados serão criados por funcionalidade, quando necessários para entregar um resultado observável. Não serão preparados antecipadamente para todas as telas.

Arquitetura conceitual esperada:

```text
UI
 ↓
ViewModel
 ↓
Repository
 ├── Remote Data Source
 └── Local Data Source
```

A UI não deverá acessar APIs diretamente.

---

## 7.1 Pesquisa de Anime

```mermaid
sequenceDiagram
    actor User as Usuário
    participant UI as SearchScreen
    participant VM as SearchViewModel
    participant Repo as AnimeRepository
    participant API as AnimeApi

    User->>UI: pesquisa por anime
    UI->>VM: enviar consulta
    VM->>Repo: buscar animes
    Repo->>API: requisitar dados
    API-->>Repo: resultados
    Repo-->>VM: modelos de domínio
    VM-->>UI: atualizar estado
```

> Diagrama conceitual da Sprint 6. Nomes finais de métodos e estados serão definidos durante a implementação da busca.

---

## 7.2 Atualização da Biblioteca

```mermaid
sequenceDiagram
    actor User as Usuário
    participant UI as AnimeDetails
    participant VM as LibraryViewModel
    participant Repo as LibraryRepository
    participant Local as LocalDataSource

    User->>UI: adiciona ou atualiza anime
    UI->>VM: solicita alteração
    VM->>Repo: atualizar LibraryEntry
    Repo->>Local: persistir alteração
    Local-->>Repo: registro persistido
    Repo-->>VM: LibraryEntry atualizada
    VM-->>UI: atualizar estado
```

> Diagrama conceitual das Sprints 8 e 9. A operação principal é local e independe do backend. A sincronização será modelada separadamente na Sprint 13, quando existirem autenticação e contratos reais.

---

# 8. Arquitetura de Alto Nível

> **Status:** Conceitual.

A estrutura atual do projeto é organizada em torno de responsabilidades como:

```text
br/com/mykytadu/

├── app/
├── core/
├── data/
├── di/
├── domain/
├── features/
└── presentation/
```

Uma visão simplificada da arquitetura pretendida é:

```mermaid
flowchart TD
    UI["Presentation / Compose"]
    VM["ViewModels"]
    Domain["Domain"]
    Repo["Repositories"]
    Remote["Remote Data Source"]
    Local["Local Data Source"]
    API["Backend / APIs externas"]

    UI --> VM
    VM --> Domain
    Domain --> Repo

    Repo --> Remote
    Repo --> Local

    Remote --> API
```

Essa visão deve ser refinada apenas quando as responsabilidades reais das camadas estiverem consolidadas no código.

---

# 9. ERD / Persistência

> **Status:** Não iniciado.

Um diagrama entidade-relacionamento será criado somente quando houver necessidade concreta de documentar persistência local, backend ou sincronização de dados.

Não devemos assumir que o modelo persistido será idêntico ao modelo de domínio ou aos DTOs das APIs.

```mermaid
erDiagram
    %% Estrutura será definida quando a camada de persistência for modelada.
```

---

# 10. Evolução Prevista por Sprint

| Sprint | Diagrama / Modelagem |
|---|---|
| 3 — Navegação | Diagrama de Navegação |
| 4 — Comunicação | Fluxos HTTP, se necessário |
| 5 — Domínio do Catálogo | Modelos e conversões necessários a pesquisa e detalhes |
| 6 — Busca End-to-End | Sequência UI → ViewModel → AnimeRepository → AnimeApi |
| 7 — Detalhes End-to-End | Fluxo de carregamento pelo ID AniList |
| 8 — Persistência e Biblioteca Local | Estado de `LibraryEntry` e estrutura persistida |
| 9 — Biblioteca End-to-End | Fluxos locais de alteração da biblioteca |
| 10 — Home | Composição e independência dos estados por seção |
| 11 — Configurações | Preferências locais e localização da interface |
| 12 — Backend e Autenticação | Fluxo e estados da sessão, após contrato real |
| 13 — Sincronização e Perfil | Sincronização, conflitos e vínculo local/remoto |
| 14 — Cache e Offline | Estratégia Remote / Cache e políticas de validade |
| 15 — Localização e Tradução | Separação entre interface e conteúdo externo |
| 16 — Preparação para Lançamento | Revisão geral da documentação |

Essa lista é orientativa e pode mudar de acordo com a evolução real do projeto.

---

# 11. Regras de Manutenção

Ao atualizar este documento:

1. Não adicionar uma arquitetura futura como se já estivesse implementada;
2. Marcar explicitamente diagramas conceituais ou propostas;
3. Atualizar diagramas quando uma decisão mudar;
4. Preferir diagramas pequenos e focados;
5. Evitar duplicar informações já explicadas no Documento Mestre;
6. Registrar principalmente relações, fluxos e decisões que se beneficiem de representação visual;
7. Manter nomes próximos aos utilizados no código;
8. Remover diagramas que tenham deixado de representar o sistema.

---

# 12. Fontes do Projeto

Este documento deve ser mantido em conjunto com:

- [`documento-mestre.md`](documento-mestre.md) — contexto e decisões consolidadas;
- [`identidade-visual.md`](identidade-visual.md) — direção de UX e Design System;
- [`roadmap.md`](roadmap.md) — planejamento das sprints;
- código atual do projeto — fonte definitiva do estado real da implementação.

---

# 13. Estado Atual da Modelagem

## Consolidado

- Sprint 3 concluída e diagrama atualizado para a implementação real;
- oito rotas tipadas e serializáveis;
- fluxo de entrada `Splash → Login → Home` com limpeza do histórico;
- quatro destinos irmãos na navegação principal;
- rotas secundárias e retorno pelo back stack;
- classificação declarativa de acesso;
- estrutura compartilhada e testada para Deep Links.

## Planejado

- Domínio do catálogo guiado por pesquisa e detalhes;
- Estados de `LibraryEntry`;
- Persistência e biblioteca local-first;
- Fluxos verticais entre UI, ViewModel, Repository e fontes de dados;
- Arquitetura refinada;
- autenticação e validação real de sessão;
- sincronização posterior com o backend;
- integração de Deep Links por plataforma, quando necessária;
- fluxos de Home, cache, localização e tradução.

---

> **Princípio do documento**
>
> Modelar o suficiente para reduzir ambiguidades e orientar a implementação, sem transformar a documentação em uma arquitetura imaginária do futuro.
