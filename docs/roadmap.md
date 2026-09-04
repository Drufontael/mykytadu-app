# MykytaDu — Roadmap de Desenvolvimento (Frontend)

## Objetivo

Construir o frontend do **MykytaDu** de forma incremental, priorizando uma arquitetura sólida, reutilização de componentes e baixo acoplamento.

Cada sprint deve entregar uma funcionalidade completa ou uma evolução da infraestrutura do projeto. Nenhuma sprint deve depender de soluções improvisadas ou gerar dívida técnica desnecessária.

---

# Sprint 1 — Fundação do Projeto

**Status:** Concluída

## Objetivo

Preparar toda a infraestrutura necessária para o desenvolvimento do aplicativo.

### Entregas

- [x] Configuração do projeto Compose Multiplatform
- [x] Organização do Gradle
- [x] Version Catalog (`libs.versions.toml`)
- [x] Configuração das bibliotecas principais
- [x] Estrutura de pacotes
- [x] Configuração de DI
- [x] Configuração do cliente HTTP
- [x] Configuração da serialização
- [x] Configuração do projeto para múltiplas plataformas

### Critérios de aceite

- [x] O projeto compila.
- [x] Não existe código de negócio.
- [x] Não existe tela funcional.
- [x] Todas as dependências estão centralizadas.

---

# Sprint 2 — Design System

**Status:** Concluída

## Objetivo

Criar a identidade visual do aplicativo.

### Entregas

### Tema

- [x] Colors
- [x] Typography
- [x] Shapes
- [x] Dimensions
- [x] Spacing
- [x] Elevations

### Componentes reutilizáveis

- [x] AppButton
- [x] AppTextField
- [x] AppCard
- [x] AppTopBar
- [x] AppSearchBar
- [x] AppChip
- [x] AppDivider
- [x] AppDialog
- [x] AppLoading
- [x] AppProgressBar
- [x] AppError
- [x] AppEmptyState
- [x] AppIconButton

### Critérios de aceite

- [x] Nenhuma cor fixa utilizada nas telas.
- [x] Todos os componentes reutilizáveis.
- [x] Tema aplicado globalmente.

---

# Sprint 3 — Navegação

**Status:** Concluída

## Objetivo

Definir toda a navegação do aplicativo.

### Rotas

- [x] Splash
- [x] Login
- [x] Home
- [x] Pesquisa
- [x] Detalhes do Anime
- [x] Biblioteca
- [x] Perfil
- [x] Configurações

### Entregas

- [x] Navegação configurada
- [x] Rotas protegidas preparadas
- [x] Estrutura para Deep Links

### Critérios de aceite

- [x] Navegação funcionando entre todas as telas.
- [x] Cada tela pode conter apenas um texto identificando seu nome.

---

# Sprint 4 — Camada de Comunicação

**Status:** Concluída

## Objetivo

Preparar toda a comunicação com o backend e APIs externas.

### Entregas

- [x] Cliente HTTP
- [x] Configuração de Serialização
- [x] Tratamento global de erros
- [x] Timeouts
- [x] Logging
- [x] Interceptadores
- [x] Configuração de autenticação futura

### Serviços

- [x] AnimeApi

`AuthApi` e `TranslationApi` foram retiradas desta sprint porque dependem do backend ainda indisponível nesta etapa. Na revisão atual do roadmap, suas entregas estão planejadas para as Sprints 12 e 15, respectivamente.

### Critérios de aceite

- [x] Primeira chamada HTTP funcionando.
- [x] Erros tratados de forma padronizada.

---

# Princípios para as Próximas Sprints

## Fatias verticais

A partir da Sprint 5, cada funcionalidade deve incluir somente as camadas necessárias para produzir um resultado observável:

```text
API ou fonte de dados
    ↓
Repository
    ↓
ViewModel e estado
    ↓
Interface
    ↓
Testes e validação
```

Repositories, ViewModels e modelos não devem ser planejados antecipadamente para funcionalidades futuras. O padrão compartilhado de estado será definido na primeira funcionalidade e reutilizado incrementalmente.

## Modelagem sob demanda

Os modelos devem surgir quando houver um caso de uso concreto. DTOs da AniList permanecem na camada de dados e não são modelos de domínio.

## Repositories por funcionalidade

- `AnimeRepository` surge com o catálogo.
- `LibraryRepository` surge com a biblioteca.
- `AuthRepository` surge com a autenticação.

## Biblioteca local-first

A biblioteca deve funcionar localmente antes da existência do backend:

- funciona sem autenticação;
- persiste alterações localmente;
- autenticação futura habilita sincronização;
- indisponibilidade do backend não bloqueia a funcionalidade principal;
- IDs locais, IDs externos da AniList e futuros IDs do backend permanecem conceitualmente separados.

---

# Sprint 5 — Domínio do Catálogo

**Status:** Planejada

## Objetivo

Criar somente os modelos, conversões e abstrações necessários para pesquisa e detalhes de animes, estabelecendo a separação entre a AniList e o domínio do MykytaDu.

### Escopo

- [ ] Modelos de resultado de pesquisa
- [ ] Modelo de detalhes
- [ ] Títulos alternativos
- [ ] Capas, banners e imagens
- [ ] Gêneros
- [ ] Estúdios
- [ ] Datas parciais
- [ ] Trailer
- [ ] Relações entre obras
- [ ] Paginação
- [ ] Enums do domínio
- [ ] Mapeadores de DTOs AniList para domínio
- [ ] Contrato e implementação inicial de `AnimeRepository`

A inclusão de modelos auxiliares deve ser guiada pelos casos de uso existentes, não pela antiga lista de entidades.

### Fora do escopo

- `Character`
- `User`
- `LibraryEntry`
- `Review`
- Autenticação
- Persistência local
- ViewModels
- Telas
- Cache de catálogo
- Regras completas de episódios ou temporadas sem consumidor atual

### Critérios de aceite

- [ ] DTOs da AniList permanecem restritos à camada de dados.
- [ ] O repository devolve modelos de domínio.
- [ ] Pesquisa e detalhes podem ser representados sem perda indevida de dados.
- [ ] Campos opcionais e coleções vazias são preservados corretamente.
- [ ] Enums externos são convertidos explicitamente.
- [ ] Valores externos desconhecidos possuem tratamento seguro.
- [ ] Modelos são imutáveis sempre que possível.
- [ ] Mapeadores possuem testes.
- [ ] Serialização é aplicada somente onde existir necessidade técnica concreta.
- [ ] Nenhum modelo sem caso de uso atual é criado apenas para completar o roadmap.

---

# Sprint 6 — Busca de Animes End-to-End

**Status:** Planejada

## Objetivo

Entregar a primeira funcionalidade completa do aplicativo: pesquisar animes na AniList e navegar para o item selecionado.

### Escopo

- [ ] Padrão compartilhado de estado assíncrono
- [ ] `SearchViewModel`
- [ ] Campo de pesquisa
- [ ] Normalização da consulta
- [ ] Debounce
- [ ] Cancelamento ou invalidação da pesquisa anterior
- [ ] Lista ou grade de resultados
- [ ] Carregamento de imagens
- [ ] Cache básico de imagens
- [ ] Paginação baseada em `PageInfo.hasNextPage`
- [ ] Loading inicial
- [ ] Loading incremental
- [ ] Estado vazio
- [ ] Erro com nova tentativa
- [ ] Navegação com o ID AniList real

### Critérios de aceite

- [ ] Consultas vazias não geram requisições.
- [ ] Uma nova consulta não mistura resultados da anterior.
- [ ] A paginação não duplica itens.
- [ ] Falha ao carregar nova página não elimina resultados já exibidos.
- [ ] Loading inicial e loading incremental são visualmente distintos.
- [ ] Estados de erro e vazio utilizam o Design System.
- [ ] O usuário consegue pesquisar e visualizar resultados.
- [ ] O usuário consegue abrir a rota de detalhes com o ID correto.
- [ ] Nenhum DTO remoto chega à UI.
- [ ] O comportamento é validado nos targets disponíveis.

---

# Sprint 7 — Detalhes do Anime End-to-End

**Status:** Planejada

## Objetivo

Transformar a rota de detalhes em uma funcionalidade completa baseada no anime selecionado.

### Escopo

- [ ] ID obrigatório na rota de detalhes
- [ ] Deep Link de detalhes, caso seja tecnicamente apropriado
- [ ] `AnimeDetailsViewModel`
- [ ] Carregamento pelo `AnimeRepository`
- [ ] Capa
- [ ] Banner
- [ ] Títulos
- [ ] Sinopse
- [ ] Gêneros
- [ ] Notas
- [ ] Quantidade de episódios
- [ ] Estúdios
- [ ] Temporada
- [ ] Trailer quando disponível
- [ ] Relações e links externos quando houver caso de uso
- [ ] Loading
- [ ] Erro e retry
- [ ] Adaptação visual para campos ausentes
- [ ] Normalização segura da descrição

### Critérios de aceite

- [ ] A tela carrega o anime pelo ID recebido.
- [ ] Nenhum DTO remoto é exposto à UI.
- [ ] Campos nulos ou coleções vazias não quebram o layout.
- [ ] Seções sem conteúdo são omitidas ou adaptadas.
- [ ] O retorno preserva adequadamente o contexto da pesquisa.
- [ ] Links externos somente são apresentados quando válidos.
- [ ] Loading, erro e retry estão implementados.
- [ ] O layout é validado nos temas claro e escuro.
- [ ] O comportamento é validado nos targets disponíveis.

---

# Sprint 8 — Persistência e Biblioteca Local

**Status:** Planejada

## Objetivo

Criar a base local-first da biblioteca pessoal sem depender de autenticação ou backend.

### Escopo

- [ ] Seleção fundamentada da solução de persistência multiplataforma
- [ ] Esquema local
- [ ] Migração inicial
- [ ] Modelo `LibraryEntry`
- [ ] Enum de status
- [ ] Progresso
- [ ] Favorito
- [ ] Nota pessoal
- [ ] Avaliação, somente se houver regra consolidada
- [ ] `LibraryRepository`
- [ ] Fonte de dados local
- [ ] Testes de persistência e migração
- [ ] Separação entre modelo persistido e modelo de domínio

### Status previstos

- Planejando
- Assistindo
- Pausado
- Concluído
- Abandonado

### Critérios de aceite

- [ ] A biblioteca funciona sem conexão e sem autenticação.
- [ ] Dados permanecem após reiniciar o aplicativo.
- [ ] Progresso negativo não é aceito.
- [ ] Progresso acima do total conhecido possui tratamento explícito.
- [ ] IDs locais, AniList e futuros IDs de backend não são confundidos.
- [ ] O modelo persistido não é automaticamente tratado como modelo de domínio.
- [ ] Migrações e operações principais possuem testes.
- [ ] Nenhum contrato fictício de backend é introduzido.

---

# Sprint 9 — Biblioteca End-to-End

**Status:** Planejada

## Objetivo

Entregar ao usuário o gerenciamento completo da biblioteca local.

### Escopo

- [ ] Adicionar anime
- [ ] Remover anime
- [ ] Alterar status
- [ ] Favoritar
- [ ] Atualizar progresso
- [ ] Registrar notas pessoais
- [ ] Avaliação, se consolidada na Sprint 8
- [ ] Filtros por status
- [ ] Ordenação quando necessária
- [ ] Estados vazio e erro
- [ ] Confirmação de ações destrutivas
- [ ] Navegação para detalhes

### Critérios de aceite

- [ ] O fluxo completo funciona offline.
- [ ] Alterações aparecem imediatamente na interface.
- [ ] Dados sobrevivem à reinicialização.
- [ ] Filtros preservam estado adequadamente.
- [ ] Remoção exige confirmação.
- [ ] A biblioteca permite abrir os detalhes do anime.
- [ ] As regras de progresso possuem testes.
- [ ] Nenhuma operação depende do backend.

---

# Sprint 10 — Home

**Status:** Planejada

## Objetivo

Criar uma Home híbrida, priorizando a continuidade pessoal e complementando-a com descoberta de conteúdo.

### Direção de produto

A Home deve priorizar:

1. Continuar assistindo
2. Atividade ou atualizações recentes da biblioteca
3. Descoberta por tendências, temporada atual ou lançamentos

### Escopo

- [ ] Definição dos requisitos da Home
- [ ] Seções pessoais baseadas na biblioteca local
- [ ] Consultas AniList adicionais estritamente necessárias
- [ ] Estados independentes por seção
- [ ] Navegação para detalhes
- [ ] Comportamento quando a biblioteca estiver vazia
- [ ] Tratamento de indisponibilidade parcial

### Critérios de aceite

- [ ] Conteúdo pessoal tem prioridade visual.
- [ ] A Home continua útil quando a biblioteca está vazia.
- [ ] Falha em uma seção não derruba toda a tela.
- [ ] Novas operações AniList são isoladas na camada de dados.
- [ ] A Home permite abrir detalhes.
- [ ] Estados vazio, loading e erro são tratados por seção.

---

# Sprint 11 — Configurações e Preferências

**Status:** Planejada

## Objetivo

Permitir personalização local e preparar a aplicação para localização.

### Escopo

- [ ] Tema claro
- [ ] Tema escuro
- [ ] Seguir o sistema
- [ ] Idioma da interface
- [ ] Preferência de título
- [ ] Preferência sobre conteúdo adulto
- [ ] Outras preferências justificadas
- [ ] Persistência local
- [ ] Tela Sobre
- [ ] Recursos de string da aplicação

Logout não pertence a esta sprint porque ainda não existe autenticação.

### Critérios de aceite

- [ ] Preferências permanecem após reiniciar o aplicativo.
- [ ] Alterações de tema são aplicadas globalmente.
- [ ] Textos da interface utilizam recursos apropriados.
- [ ] A preferência de título é aplicada de forma consistente.
- [ ] Configurações inválidas possuem fallback seguro.
- [ ] Logout não é exibido sem sessão autenticada.

---

# Sprint 12 — Backend e Autenticação

**Status:** Planejada

## Objetivo

Integrar autenticação somente após existirem contratos reais do backend próprio.

### Pré-condição

Esta sprint não deve começar sem contrato documentado e backend disponível ou mockado de forma oficial.

### Escopo

- [ ] `AuthApi`
- [ ] `AuthRepository`
- [ ] Modelo `User`
- [ ] Cadastro
- [ ] Login
- [ ] Logout
- [ ] Armazenamento seguro de credenciais
- [ ] Refresh token
- [ ] Restauração da sessão
- [ ] Expiração da sessão
- [ ] Aplicação efetiva de `RouteAccess`
- [ ] Erros de autenticação
- [ ] Testes dos fluxos principais

### Critérios de aceite

- [ ] O usuário pode cadastrar-se, entrar e sair.
- [ ] A sessão é restaurada após reiniciar o aplicativo.
- [ ] Tokens não são armazenados em preferências comuns.
- [ ] Dados sensíveis não aparecem em logs.
- [ ] Refresh e expiração possuem comportamento definido.
- [ ] Rotas protegidas verificam a sessão real.
- [ ] Falhas de autenticação não apagam indevidamente dados locais.

---

# Sprint 13 — Sincronização e Perfil

**Status:** Planejada

## Objetivo

Associar a experiência local ao usuário autenticado e sincronizar a biblioteca com o backend.

### Escopo

- [ ] Estratégia de sincronização
- [ ] Vínculo entre registros locais e remotos
- [ ] Fila de operações pendentes
- [ ] Resolução de conflitos
- [ ] Retry
- [ ] Estados de sincronização
- [ ] Comportamento offline
- [ ] Perfil
- [ ] Avatar
- [ ] Nome
- [ ] Quantidade de animes
- [ ] Horas assistidas
- [ ] Favoritos
- [ ] Outras estatísticas comprovadamente disponíveis

### Critérios de aceite

- [ ] Biblioteca local continua utilizável offline.
- [ ] Alterações pendentes são sincronizadas posteriormente.
- [ ] Conflitos possuem política explícita.
- [ ] Falhas de sincronização não causam perda silenciosa.
- [ ] O usuário consegue identificar o estado da sincronização.
- [ ] Perfil usa dados reais do backend ou agregações locais documentadas.
- [ ] Logout preserva ou remove dados locais conforme política explícita.

---

# Sprint 14 — Cache e Experiência Offline

**Status:** Planejada

## Objetivo

Aprimorar desempenho, resiliência e uso offline do catálogo.

### Escopo

- [ ] Cache de pesquisas
- [ ] Cache de detalhes
- [ ] Revisão do cache de imagens
- [ ] Política de expiração
- [ ] Invalidação
- [ ] Fallback offline
- [ ] Diferenciação entre dado atual, cache válido e cache expirado
- [ ] Limpeza controlada
- [ ] Limites de armazenamento
- [ ] Testes de política de cache

### Critérios de aceite

- [ ] Dados armazenados possuem política de validade explícita.
- [ ] O aplicativo apresenta conteúdo disponível quando estiver offline.
- [ ] Dados expirados não são tratados silenciosamente como atuais.
- [ ] Limpeza não remove dados permanentes da biblioteca.
- [ ] Cache não replica indiscriminadamente a base da AniList.
- [ ] Falhas de atualização preservam conteúdo útil já disponível.

---

# Sprint 15 — Localização e Tradução

**Status:** Planejada

## Objetivo

Localizar a interface e integrar tradução opcional do conteúdo externo.

### Separação obrigatória

Distinguir:

- Localização dos textos da interface
- Tradução de sinopses e outros conteúdos vindos da API

### Escopo

- [ ] Idiomas suportados pela interface
- [ ] Revisão dos recursos de string
- [ ] `TranslationApi`
- [ ] Repository ou serviço apropriado
- [ ] Cache de traduções
- [ ] Fallback para conteúdo original
- [ ] Preferência de idioma
- [ ] Preferência de título
- [ ] Estados de carregamento e falha da tradução

### Critérios de aceite

- [ ] A interface pode trocar de idioma sem textos fixos relevantes.
- [ ] Falha de tradução não impede a exibição do conteúdo original.
- [ ] Traduções são reutilizadas quando disponíveis.
- [ ] Conteúdo original permanece preservado.
- [ ] Tradução não altera os DTOs nem o contrato da AniList.
- [ ] Idiomas não suportados possuem fallback definido.

---

# Sprint 16 — Preparação para Lançamento

**Status:** Planejada

## Objetivo

Preparar a primeira versão pública sem concentrar nesta sprint toda a qualidade que deveria ter sido construída anteriormente.

### Escopo

- [ ] Revisão de UX e UI
- [ ] Acessibilidade final
- [ ] Performance
- [ ] Testes de regressão
- [ ] Correção de bugs
- [ ] Refatorações justificadas
- [ ] Documentação
- [ ] Licenças e atribuições
- [ ] Privacidade
- [ ] Revisão de logs
- [ ] Ícones e splash definitivos
- [ ] Empacotamento por plataforma
- [ ] Remoção ou isolamento do showcase no fluxo de produção
- [ ] Checklist de release

### Critérios de aceite

- [ ] Não existem falhas críticas conhecidas.
- [ ] Fluxos principais possuem testes e validação manual.
- [ ] Logs de produção não expõem dados sensíveis.
- [ ] Acessibilidade básica foi revisada.
- [ ] Performance dos fluxos principais é aceitável.
- [ ] Ícones, splash, versão e metadados estão corretos.
- [ ] Licenças e atribuições necessárias estão documentadas.
- [ ] Documentação representa o estado real do projeto.
- [ ] Builds de distribuição aplicáveis são gerados com sucesso.

---

# Definition of Done Transversal

Aplicável a todas as próximas sprints:

- [ ] O incremento possui resultado observável.
- [ ] Regras de negócio não ficam na UI.
- [ ] Tipos remotos não escapam da camada de dados.
- [ ] Loading, sucesso, vazio e erro são tratados quando aplicáveis.
- [ ] Cancelamento de coroutines é preservado.
- [ ] Regras, conversões e estados relevantes possuem testes.
- [ ] Desktop e Android são validados.
- [ ] Metadata iOS é compilada quando aplicável.
- [ ] Limitações da validação nativa iOS são registradas.
- [ ] Dados sensíveis não aparecem em logs.
- [ ] Componentes respeitam o Design System.
- [ ] Temas claro e escuro são verificados quando houver interface.
- [ ] Acessibilidade básica é considerada durante a implementação.
- [ ] A documentação registra apenas entregas comprovadas.
- [ ] Commits permanecem pequenos e descritivos.
- [ ] Não são antecipadas abstrações sem consumidor real.

---

# Critérios Gerais do Projeto

Todos os novos recursos deverão seguir as seguintes diretrizes:

- [ ] Componentes reutilizáveis antes de componentes específicos.
- [ ] Nenhuma regra de negócio dentro da UI.
- [ ] Toda comunicação externa passa pelos Repositories.
- [ ] Estados padronizados em todas as telas.
- [ ] Código desacoplado e testável.
- [ ] Commits pequenos e descritivos.
- [ ] Uma responsabilidade por classe.
- [ ] Evitar duplicação de código.
- [ ] Evolução incremental, sem antecipar funcionalidades futuras.

---

# Visão de Longo Prazo

Após a conclusão deste roadmap, o MykytaDu estará preparado para evoluir com recursos como:

- [ ] Recomendações personalizadas
- [ ] Sistema de amigos
- [ ] Feed de atividades
- [ ] Estatísticas avançadas
- [ ] Conquistas
- [ ] Notificações
- [ ] Sincronização com serviços externos
- [ ] Modo offline aprimorado
- [ ] Suporte completo a múltiplas plataformas
