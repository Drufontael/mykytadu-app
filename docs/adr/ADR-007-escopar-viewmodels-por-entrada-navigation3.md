# ADR-007 — Escopar ViewModels por entrada do Navigation 3

> **Estado:** Aprovado
> **Data:** 2026-09-12
> **Sprints relacionadas:** S6.2

## Contexto

A primeira pesquisa funcional precisa manter estado observável e cancelar
trabalho assíncrono sem colocar regras de pesquisa na UI. A aplicação já usa
Navigation 3 e Koin em código compartilhado. A integração introduz dependências
estruturais de lifecycle e define a propriedade do estado por entrada; por isso
merece ADR conforme a [convenção](README.md).

## Drivers

- Compartilhar a funcionalidade em `commonMain` nos targets atuais.
- Injetar `AnimeRepository` sem expor DTOs à UI.
- Associar o tempo de vida do ViewModel à entrada de navegação.
- Evitar instância global de busca e trabalho sem proprietário explícito.
- Observar estado respeitando lifecycle, com cancelamento em `viewModelScope`.

## Opções consideradas

1. Manter estado e jobs diretamente em `SearchScreen`: menos dependências,
   mas mistura coordenação da pesquisa e composição, sem propriedade explícita
   pelo store da entrada.
2. Registrar `SearchViewModel` como singleton no Koin: resolução simples, mas
   estado e jobs sobrevivem à remoção da entrada e podem ser compartilhados
   indevidamente entre entradas.
3. Criar store e lifecycle próprios ou por plataforma: permite controle local,
   com infraestrutura adicional e risco de divergência entre targets.
4. Usar Lifecycle ViewModel, definição Koin e store por entrada do Navigation 3:
   integra os mecanismos existentes e mantém a pesquisa compartilhada.

As opções documentam os tradeoffs da decisão; não representam protótipos ou
experimentos executados para cada alternativa.

## Decisão

Adotar a opção 4, implementada e aceita na S6.2. `SearchViewModel` recebe
`AnimeRepository` e publica `StateFlow<SearchUiState>`, executando os jobs em
`viewModelScope`. `ViewModelModule` registra `viewModel`, não `single`, e integra
a inicialização do Koin. `SearchScreen` usa `koinViewModel()` e
`collectAsStateWithLifecycle()`.

No `NavDisplay`, aplicar `rememberSaveableStateHolderNavEntryDecorator()` antes
de `rememberViewModelStoreNavEntryDecorator()`. O store pertence à entrada e
fornece o proprietário usado na resolução do ViewModel; não é um store global
da aplicação.

O catálogo e o Gradle atuais declaram Lifecycle 2.10.0 para
`lifecycle-viewmodel-compose`, `lifecycle-runtime-compose` e
`lifecycle-viewmodel-navigation3`, além de `koin-compose-viewmodel` 4.2.2 em
`commonMain`, com Navigation 3 1.1.1. Desktop recebe
`kotlinx-coroutines-swing` 1.10.2 para o dispatcher Main. Não se estende essa
decisão à adoção de Coil ou de um estado assíncrono genérico.

## Consequências

- Regras de pesquisa ficam fora da UI e podem ser testadas com repository
  controlado e dispatcher de teste.
- A instância acompanha a entrada: recomposição não implica singleton nem
  recriação arbitrária; remoção encerra o store e o escopo do ViewModel.
- Trocar destinos principais atualmente remove a entrada anterior. Não há
  promessa de preservar a pesquisa entre abas.
- Lifecycle da coleta não implica cancelamento da requisição ao ocultar a
  tela. Mudança de consulta, limpeza e encerramento do ViewModel governam o
  cancelamento; o cooldown possui regras próprias de atualização de consulta.
- Estado em memória não equivale a restauração após morte de processo ou
  reload. Preservar ViewModel, resultados e scroll no retorno dos detalhes
  ainda precisa de validação quando esse fluxo for implementado.
- A stack passa a depender da compatibilidade conjunta de Lifecycle, Koin e
  Navigation 3. iOS nativo continua dependente de validação em macOS/Xcode.

## Evidências

- [AppNavigation.kt](../../composeApp/src/commonMain/kotlin/br/com/mykytadu/core/navigation/AppNavigation.kt): ordem dos decorators e mutações da back stack.
- [ViewModelModule.kt](../../composeApp/src/commonMain/kotlin/br/com/mykytadu/di/ViewModelModule.kt) e [Koin.kt](../../composeApp/src/commonMain/kotlin/br/com/mykytadu/di/Koin.kt): definição e inclusão do módulo.
- [SearchScreen.kt](../../composeApp/src/commonMain/kotlin/br/com/mykytadu/features/search/SearchScreen.kt) e [SearchViewModel.kt](../../composeApp/src/commonMain/kotlin/br/com/mykytadu/features/search/SearchViewModel.kt): resolução, coleta e escopo assíncrono.
- [Version Catalog](../../gradle/libs.versions.toml) e [Gradle do app](../../composeApp/build.gradle.kts): dependências efetivas.
- [ViewModelModuleTest.kt](../../composeApp/src/commonTest/kotlin/br/com/mykytadu/di/ViewModelModuleTest.kt): resolução não singleton; não testa sozinho a retenção ou destruição da entrada real.
- [Registro S6.2](../sprints/S6.md#validações-e-alcance-das-evidências): relatórios automatizados de Desktop, Android, JS e WasmJS sem falhas; confirmação manual em Web/Desktop/Android informada no aceite. Não houve nova execução no encerramento documental, nem comprovação de build/execução nativos iOS.

## Critérios de revisão

Reavaliar ao mudar a política de back stack, precisar de estado compartilhado
entre entradas ou persistente após recriação, alterar versões estruturais ou
encontrar incompatibilidade em target. Validar retenção, remoção e cancelamento
no fluxo de detalhes antes de prometer preservação completa da experiência.
