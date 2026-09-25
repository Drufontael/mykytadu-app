# MykytaDu — Roadmap de Desenvolvimento

## Objetivo

Organizar a evolução funcional do MykytaDu em fatias verificáveis. Este
documento contém planejamento e estado consolidado. Execução, evidências,
bloqueios e encerramentos ficam nos registros em
[`docs/sprints/`](sprints/README.md).

## Princípios de planejamento

- cada sprint entrega um resultado observável;
- infraestrutura surge junto de um consumidor real;
- critérios de aceite não são relaxados silenciosamente;
- planejamento não comprova implementação;
- alterações de escopo são registradas no arquivo da sprint;
- decisões arquiteturais relevantes apontam para ADRs;
- biblioteca e persistência seguem direção local-first;
- IDs de catálogo, IDs nativos de provedores, IDs locais e IDs de backend permanecem distintos;
- autenticação habilita sincronização, mas não condiciona o uso local.

## Estado consolidado

O roadmap possui 16 sprints funcionais, uma sprint técnica adicional, W1,
inserida entre as Sprints 5 e 6 sem renumeração, e registros de manutenção
arquitetural identificados por `M`.

| Sprint | Objetivo | Estado | Registro |
|---|---|---|---|
| S1 | Fundação do Projeto | Concluída | [S1](sprints/S1.md) |
| S2 | Design System | Concluída | [S2](sprints/S2.md) |
| S3 | Navegação | Concluída | [S3](sprints/S3.md) |
| S4 | Camada de Comunicação | Concluída | [S4](sprints/S4.md) |
| S5 | Domínio do Catálogo | Concluída | [S5](sprints/S5.md) |
| W1 | Fundação Web | Concluída | [W1](sprints/W1.md) |
| S6 | Busca de Animes End-to-End | Concluída | [S6](sprints/S6.md) |
| M1 | Desacoplamento da identidade do catálogo | Concluída | [M1](sprints/M1.md) |
| S7 | Detalhes do Anime End-to-End | Em andamento | [S7](sprints/S7.md) |
| S8 | Persistência e Biblioteca Local | Planejada | a criar |
| S9 | Biblioteca End-to-End | Planejada | a criar |
| S10 | Home | Planejada | a criar |
| S11 | Configurações e Preferências | Planejada | a criar |
| S12 | Backend e Autenticação | Planejada | a criar |
| S13 | Sincronização e Perfil | Planejada | a criar |
| S14 | Cache e Experiência Offline | Planejada | a criar |
| S15 | Localização e Tradução | Planejada | a criar |
| S16 | Preparação para Lançamento | Planejada | a criar |

## Diretrizes transversais

### Fatias verticais

Uma funcionalidade incorpora somente os modelos, repositories, estados,
ViewModels, interface e testes necessários para produzir seu resultado. Não se
preparam camadas completas sem consumidor.

### Modelagem sob demanda

Modelos nascem de casos de uso concretos. Campos de provedores externos não são
copiados automaticamente para o domínio.

### Repositories por funcionalidade

A UI consome contratos de domínio. Detalhes de rede, cache e persistência ficam
atrás dos repositories correspondentes.

### Biblioteca local-first

A biblioteca funciona e persiste localmente sem autenticação. O backend adiciona
sincronização quando existir sessão válida, sem substituir o estado local como
base da experiência.

---
# Sprint 6 — Busca de Animes End-to-End

**Status:** Concluída — validação manual em Android, Desktop e Web; iOS nativo pendente de ambiente

## Objetivo

Entregar a primeira funcionalidade completa do aplicativo: pesquisar animes na AniList e navegar para o item selecionado.

## Decomposição operacional

### S6.1 — Auditoria e plano de implementação

- [x] Base de domínio, repository, navegação, UI e dependências auditada
- [x] Estado, paginação, imagens e navegação com ID planejados
- [x] Divisão entre S6.2 e S6.6 definida e aceita

### S6.2 — Estado e primeira pesquisa funcional

**Estado:** Concluída e aceita

- [x] Integrar lifecycle e Koin, com testes em Desktop, Android, JS e WasmJS; iOS nativo pendente
- [x] Implementar estado específico da busca e `SearchViewModel`
- [x] Entregar consulta inicial observável com debounce de 800 ms, cancelamento, estados e retry
- [x] Invalidar e cancelar a consulta anterior ao mudar a consulta normalizada, exceto durante cooldown não vazio, que preserva o contador e usa o texto mais recente
- [x] Pesquisa automática a partir de três caracteres e submissão explícita de consultas curtas não vazias
- [x] Cooldown local de 15 segundos com uma tentativa automática, validado por tempo virtual

### S6.3 — Navegação de detalhes com identidade do catálogo

**Estado:** Concluída e aceita

- [x] Transportar e restaurar o `CatalogAnimeId` na rota de detalhes
- [x] Atualizar serialização, callbacks, deep links e histórico Web
- [x] Validar IDs ausentes, inválidos ou fora do intervalo sem criar IDs fictícios

### S6.4 — Resultados responsivos e imagens

**Estado:** Concluída e aceita

- [x] Implementar cards e grade responsiva com títulos e capas
- [x] Auditar o carregador: Coil 3 com Ktor 3 é o candidato para validação
- [x] Consolidar Coil 3.4.0 com Ktor 3 após compilação Android, Desktop, metadata iOS, JS e WasmJS
- [x] Criar componente específico de resultado com capa opcional e título
- [x] Manter as políticas padrão habilitadas de memória e disco do Coil, sem cache HTTP próprio
- [x] Cobrir URL de capa, grade compacta, seleção e restauração de scroll com testes automatizados
- [x] Validar visualmente imagens, grade compacta e expandida, seleção e retorno no Web WasmJS

### S6.5 — Paginação e recuperação incremental

**Estado:** Concluída e aceita — S6.5.1 a S6.5.7

- [x] Definir estado incremental e transportar `PageInfo` nos resultados
- [x] Solicitar a próxima página por `PageInfo.hasNextPage`, com bloqueio e invalidação de respostas obsoletas
- [x] Deduplicar páginas por `CatalogAnimeId`, preservando a primeira ocorrência
- [x] Repetir a página que falhou sem descartar resultados
- [x] Preservar resultados em loading e erro incrementais e rejeitar respostas de consultas anteriores
- [x] Integrar carregamento automático, estado de erro e retry à grade responsiva
- [x] Cobrir o gatilho, loading incremental, erro e retry em teste automatizado de UI
- [x] Validar visualmente a grade, capas e rolagem no Web WasmJS

### S6.6 — Validação End-to-End

**Estado:** Concluída — S6.6.1 a S6.6.8 concluídas e aceitas; iOS nativo pendente de ambiente

- [x] Confirmar a matriz de tasks e executar builds/testes locais dos targets aplicáveis
- [x] Compilar Desktop, Android debug, metadata comum/iOS, JavaScript e WasmJS
- [x] Executar testes de navegador JS e WasmJS
- [x] Executar smoke test manual Web WasmJS
- [x] Executar smoke tests manuais Android, Desktop e Web
- [x] Validar pesquisa, imagens, paginação, responsividade, teclado e navegação em Android, Desktop e Web
- [x] Verificar separadamente `SearchViewModel` e os modelos de domínio por regressão automatizada
- [x] Verificar resultados e posição de scroll ao retornar dos detalhes
- [x] Consolidar evidências, alcance e limitações por target
- [x] Remodelar o registro da sprint no padrão documental aprovado
- [x] Encerrar documentalmente a Sprint 6 após aceite

### Escopo

- [x] Estado assíncrono específico da busca
- [x] `SearchViewModel`
- [x] Campo de pesquisa
- [x] Normalização da consulta
- [x] Debounce
- [x] Cancelamento ou invalidação da pesquisa anterior
- [x] Lista de títulos da primeira página com capa opcional
- [x] Carregamento de imagens remotas
- [x] Cache padrão de imagens do Coil, sem política HTTP ou offline
- [x] Paginação baseada em `PageInfo.hasNextPage`
- [x] Loading inicial
- [x] Loading incremental
- [x] Estado vazio
- [x] Erro com nova tentativa
- [x] Navegação com a identidade real do catálogo

### Critérios de aceite

- [x] Consultas vazias não geram requisições.
- [x] Uma nova consulta não mistura resultados da anterior.
- [x] A paginação não duplica itens.
- [x] Falha ao carregar nova página não elimina resultados já exibidos.
- [x] Loading inicial e loading incremental são visualmente distintos.
- [x] Estados de erro e vazio utilizam o Design System.
- [x] O usuário consegue pesquisar e visualizar resultados.
- [x] O usuário consegue abrir a rota de detalhes com o ID correto.
- [x] Nenhum DTO remoto chega à UI.
- [x] O comportamento é validado nos targets disponíveis no ambiente atual.

### Divisão com a Sprint 7

A Sprint 6 transporta e restaura a identidade de catálogo selecionada na navegação. A Sprint 7 consumirá essa identidade para carregar e apresentar os detalhes completos do anime. A tradução para o ID nativo da AniList pertence exclusivamente à camada de dados.

Lifecycle e Koin estão integrados conforme [ADR-007](adr/ADR-007-escopar-viewmodels-por-entrada-navigation3.md), com evidências e limites no [registro S6](sprints/S6.md). A S6.3 transporta e restaura `CatalogAnimeId`, inclusive no histórico Web, e preserva a pesquisa no retorno. A S6.4 consolidou Coil 3.4.0 com rede Ktor 3 após compilar Android, Desktop, metadata iOS, JS e WasmJS. A grade usa duas ou quatro colunas conforme a largura disponível do conteúdo, com breakpoint de 600dp. Mantêm-se somente as políticas padrão habilitadas de memória e disco do Coil, sem cache HTTP, expiração, invalidação ou offline. A S6.4.5 cobriu URL de capa, grade compacta, seleção e restauração de scroll, e a S6.4.6 validou visualmente imagens, responsividade e navegação no Web WasmJS. A S6.5.1 definiu o estado incremental e passou a transportar `PageInfo` nos resultados. A S6.5.2 passou a solicitar `currentPage + 1` somente quando `hasNextPage` permite, bloqueando concorrência e rejeitando respostas obsoletas. A S6.5.3 passou a deduplicar resultados por `CatalogAnimeId`, preservando a primeira ocorrência. A S6.5.4 passou a preservar resultados durante falhas incrementais e repetir a página pendente. A S6.5.5 integrou o carregamento automático e o rodapé de loading, erro e retry à grade responsiva. A S6.5.6 adicionou cobertura automatizada de UI para o gatilho, loading, erro e retry. A S6.5.7 validou visualmente a grade, as capas e a rolagem no Web WasmJS. A S6.6 confirmou builds, testes automatizados e smoke tests manuais em Android, Desktop e Web. A manutenção [M1](sprints/M1.md) desacoplou essa identidade da AniList conforme [ADR-008](adr/ADR-008-desacoplar-identidade-do-catalogo-do-provedor.md) e removeu `idMal` sem consumidor conforme [ADR-009](adr/ADR-009-nao-solicitar-identificadores-externos-sem-consumidor.md). A validação nativa iOS permanece pendente de macOS/Xcode e aberta para contribuição. O próximo passo funcional é a Sprint 7.

---

# Sprint 7 — Detalhes do Anime End-to-End

**Status:** Em andamento — S7.1, S7.2 e S7.3 concluídas e aceitas

## Objetivo

Transformar a rota de detalhes em uma funcionalidade completa baseada no anime selecionado.

### Escopo

- [x] Consumo do ID obrigatório transportado pela rota implementada na Sprint 6
- [x] Deep Link de detalhes por fragmento Web
- [x] `AnimeDetailsViewModel`
- [x] Carregamento pelo `AnimeRepository`
- [x] Capa
- [x] Banner
- [x] Título principal com fallback
- [x] Sinopse
- [x] Gêneros
- [x] Nota principal
- [x] Quantidade de episódios
- [x] Estúdios
- [x] Temporada e ano
- [x] Trailer quando disponível e reconhecido
- [x] Apresentação resumida de relações e link externo validado de trailer
- [x] Loading
- [x] Erro e retry
- [x] Adaptação visual dos campos principais ausentes
- [x] Normalização segura da descrição para texto

### Critérios de aceite

- [x] A tela carrega o anime pelo ID recebido.
- [x] Nenhum DTO remoto é exposto à UI.
- [x] Campos nulos ou coleções vazias não quebram o layout principal.
- [x] Seções sem conteúdo são omitidas ou adaptadas.
- [ ] O retorno preserva adequadamente o contexto da pesquisa.
- [x] Links externos somente são apresentados quando válidos.
- [x] Loading, erro e retry estão implementados.
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
- [ ] IDs de catálogo, IDs nativos da AniList, IDs locais e futuros IDs de backend não são confundidos.
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
