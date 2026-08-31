# MykytaDu — Roadmap de Desenvolvimento (Frontend)

## Objetivo

Construir o frontend do **MykytaDu** de forma incremental, priorizando uma arquitetura sólida, reutilização de componentes e baixo acoplamento.

Cada sprint deve entregar uma funcionalidade completa ou uma evolução da infraestrutura do projeto. Nenhuma sprint deve depender de soluções improvisadas ou gerar dívida técnica desnecessária.

---

# Sprint 1 — Fundação do Projeto

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

## Objetivo

Definir toda a navegação do aplicativo.

### Rotas

- [ ] Splash
- [ ] Login
- [ ] Home
- [ ] Pesquisa
- [ ] Detalhes do Anime
- [ ] Biblioteca
- [ ] Perfil
- [ ] Configurações

### Entregas

- [ ] Navegação configurada
- [ ] Rotas protegidas preparadas
- [ ] Estrutura para Deep Links

### Critérios de aceite

- [ ] Navegação funcionando entre todas as telas.
- [ ] Cada tela pode conter apenas um texto identificando seu nome.

---

# Sprint 4 — Camada de Comunicação

## Objetivo

Preparar toda a comunicação com o backend e APIs externas.

### Entregas

- [x] Cliente HTTP
- [x] Configuração de Serialização
- [ ] Tratamento global de erros
- [ ] Timeouts
- [ ] Logging
- [ ] Interceptadores
- [ ] Configuração de autenticação futura

### Serviços

- [ ] AnimeApi
- [ ] AuthApi
- [ ] TranslationApi

### Critérios de aceite

- [ ] Primeira chamada HTTP funcionando.
- [ ] Erros tratados de forma padronizada.

---

# Sprint 5 — Modelagem

## Objetivo

Criar os modelos utilizados pelo frontend.

### Modelos

- [ ] Anime
- [ ] Genre
- [ ] Character
- [ ] Studio
- [ ] Season
- [ ] Episode
- [ ] User
- [ ] LibraryEntry
- [ ] Review

### Critérios de aceite

- [ ] Todos os modelos serializáveis.
- [ ] Objetos imutáveis sempre que possível.

---

# Sprint 6 — Camada de Dados

## Objetivo

Criar a abstração de acesso aos dados.

### Entregas

Repositories

- [ ] AnimeRepository
- [ ] AuthRepository
- [ ] LibraryRepository

Data Sources

- [ ] Remote
- [ ] Local

### Critérios de aceite

- [ ] UI não conhece a API diretamente.
- [ ] Toda comunicação passa pelos repositories.

---

# Sprint 7 — Gerenciamento de Estado

## Objetivo

Padronizar o fluxo de estados da aplicação.

### Estados

- [ ] Loading
- [ ] Success
- [ ] Empty
- [ ] Error

### ViewModels

- [ ] Preparar os ViewModels para todas as telas.

### Critérios de aceite

- [ ] Todas as telas seguem o mesmo padrão de estados.
- [ ] Nenhuma tela realiza chamadas HTTP diretamente.

---

# Sprint 8 — Busca de Animes

## Objetivo

Implementar a primeira funcionalidade completa.

### Funcionalidades

- [ ] Campo de pesquisa
- [ ] Busca na API
- [ ] Lista de resultados
- [ ] Paginação
- [ ] Tratamento de erros
- [ ] Estado vazio

### Critérios de aceite

O usuário consegue:

- [ ] Pesquisar um anime.
- [ ] Visualizar os resultados.
- [ ] Abrir os detalhes.

---

# Sprint 9 — Tela de Detalhes

## Objetivo

Exibir todas as informações de um anime.

### Informações

- [ ] Capa
- [ ] Banner
- [ ] Sinopse
- [ ] Gêneros
- [ ] Nota
- [ ] Episódios
- [ ] Estúdio
- [ ] Temporada
- [ ] Trailer (quando disponível)

### Critérios de aceite

- [ ] Todos os dados vêm da API.

---

# Sprint 10 — Autenticação

## Objetivo

Implementar autenticação do usuário.

### Funcionalidades

- [ ] Login
- [ ] Cadastro
- [ ] Logout
- [ ] Persistência da sessão
- [ ] Refresh Token

### Critérios de aceite

- [ ] O usuário permanece autenticado após reiniciar o aplicativo.

---

# Sprint 11 — Biblioteca

## Objetivo

Criar a biblioteca pessoal.

### Funcionalidades

- [ ] Adicionar anime
- [ ] Remover anime
- [ ] Alterar status
- [ ] Favoritar
- [ ] Atualizar progresso
- [ ] Notas pessoais

### Status

- [ ] Planejando
- [ ] Assistindo
- [ ] Pausado
- [ ] Concluído
- [ ] Abandonado

### Critérios de aceite

- [ ] Toda alteração sincroniza com o backend.

---

# Sprint 12 — Perfil

## Objetivo

Criar a área do usuário.

### Informações

- [ ] Avatar
- [ ] Nome
- [ ] Quantidade de animes
- [ ] Horas assistidas
- [ ] Favoritos

### Critérios de aceite

- [ ] Dados carregados da API.

---

# Sprint 13 — Configurações

## Funcionalidades

- [ ] Tema claro/escuro
- [ ] Idioma
- [ ] Preferências
- [ ] Sobre
- [ ] Logout

---

# Sprint 14 — Cache

## Objetivo

Melhorar desempenho.

### Funcionalidades

- [ ] Cache de pesquisas
- [ ] Cache de detalhes
- [ ] Cache de imagens
- [ ] Cache offline

---

# Sprint 15 — Traduções

## Objetivo

Integrar o sistema de tradução.

### Fluxo

- [ ] Solicitar dados do anime.
- [ ] Verificar tradução existente.
- [ ] Caso não exista, solicitar tradução ao backend.
- [ ] Exibir conteúdo traduzido.

---

# Sprint 16 — Polimento

## Objetivo

Preparar a primeira versão pública.

### Atividades

- [ ] Melhorias de UX
- [ ] Melhorias de UI
- [ ] Acessibilidade
- [ ] Performance
- [ ] Testes
- [ ] Correção de bugs
- [ ] Refatorações
- [ ] Documentação

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
