# MykytaDu — Identidade Visual e Direção de UX

## 1. Conceito

**MykytaDu** é um aplicativo para controle e acompanhamento de animes.

### Conceito central

> **Anime moderno + tecnologia + organização pessoal**

A experiência deve transmitir a sensação de um espaço pessoal para acompanhar a jornada do usuário pelos animes, combinando referências da cultura otaku com uma execução visual limpa e moderna.

### Direção visual

**Anime na personalidade, produto de software na execução.**

O aplicativo deve se aproximar visualmente de uma experiência de streaming/dashboard pessoal, sem parecer uma cópia de plataformas como Crunchyroll, MyAnimeList ou AniList.

---

## 2. Personalidade da marca

A identidade deve transmitir:

- 🎌 **Otaku** — referências sutis ao universo dos animes
- 🧠 **Organizado** — hierarquia visual clara e interface limpa
- ⚡ **Moderno** — linguagem tecnológica e contemporânea
- 🌌 **Imersivo** — experiência dark forte
- 😊 **Pessoal** — sensação de biblioteca própria

### Evitar

- Excesso de neon
- Fundos totalmente pretos
- Gradientes em todos os elementos
- Fontes excessivamente "anime"
- Excesso de elementos decorativos
- Aparência de portal genérico de animes
- Reprodução direta da identidade de outros serviços

---

# 3. Direção de cores

A identidade principal utiliza **violeta/índigo** como cor primária e **turquesa** como cor secundária.

A proposta é criar uma identidade sofisticada, tecnológica e adequada tanto ao dark quanto ao light mode.

## Dark Mode

| Token | Cor | Uso |
|---|---|---|
| `background` | `#0B0D12` | Fundo principal |
| `surface` | `#12151D` | Cards e superfícies |
| `surfaceVariant` | `#191D27` | Elementos elevados/secundários |
| `primary` | `#8B7CFF` | Ações principais |
| `primaryVariant` | `#6C5CE7` | Estados secundários |
| `secondary` | `#45D6C8` | Progresso e destaques |
| `textPrimary` | `#F4F4F7` | Texto principal |
| `textSecondary` | `#A7A9B4` | Texto secundário |
| `divider` | `#292D38` | Separadores |

O **Dark Mode é o modo de referência da marca**.

Ele deve transmitir uma atmosfera misteriosa, imersiva e tecnológica.

---

## Light Mode

O Light Mode não deve ser uma simples inversão das cores do Dark Mode.

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

O Light Mode deve transmitir uma sensação mais leve, limpa e organizada, mantendo a mesma identidade visual do produto.

---

# 4. Cores semânticas

Além da identidade principal, os status devem possuir cores próprias para facilitar o reconhecimento visual.

| Status | Cor sugerida |
|---|---|
| Assistindo | `#8B7CFF` |
| Concluído | `#3CCB7F` |
| Pausado | `#E8B84A` |
| Abandonado | `#E45B68` |
| Planejado | `#55A8FF` |

A cor não deve ser utilizada apenas como decoração. Ela deve possuir significado consistente em todo o aplicativo.

---

# 5. Tipografia

### Fonte principal

**Poppins**

Sugestão de utilização:

- `Bold` — títulos principais
- `SemiBold` — títulos de cards e seções
- `Medium` — botões e labels
- `Regular` — corpo de texto
- `Light` — informações auxiliares

### Fonte de destaque

Opcionalmente, uma segunda fonte mais estilizada pode ser utilizada exclusivamente em:

- Logo
- Splash Screen
- Títulos especiais
- Empty states

A fonte estilizada não deve ser utilizada na interface principal, preservando legibilidade e consistência.

---

# 6. Cards

Os cards devem ter aparência moderna, mas sem exagerar nos cantos arredondados.

### Valores sugeridos

- Cards: **16dp**
- Botões: **12dp**
- Chips: **8dp**
- Imagens: **12dp**

### Exemplo conceitual

```text
╭──────────────────────╮
│                      │
│     CAPA DO ANIME    │
│                      │
│                  ♡   │
╰──────────────────────╯
  Frieren
  24 episódios

  ███████████░░  18/24
```

O card deve priorizar:

1. Capa
2. Título
3. Informações essenciais
4. Progresso
5. Ação principal/favorito

---

# 7. Progresso de episódios

O acompanhamento de episódios é uma das funcionalidades centrais do aplicativo e deve possuir destaque visual.

Exemplo:

```text
████████████████░░░░
18 / 24 episódios
```

A cor secundária (`#45D6C8`) pode ser utilizada para indicar progresso, enquanto a cor primária permanece responsável pela identidade principal.

O objetivo é evitar que todos os elementos da interface utilizem a cor violeta.

---

# 8. Navegação

Para mobile, a navegação deve ser simples e baseada em quatro destinos principais.

### Estrutura sugerida

```text
┌──────────────────────────────┐
│  MykytaDu                    │
│                              │
│  Olá, 👋                     │
│                              │
│  Continue assistindo         │
│                              │
│  ┌────────┐ ┌────────┐       │
│  │ Anime  │ │ Anime  │       │
│  │        │ │        │       │
│  └────────┘ └────────┘       │
│                              │
│  Sua biblioteca              │
│                              │
│  ┌────────────────────────┐  │
│  │ Assistindo       12    │  │
│  │ Planejados       27    │  │
│  │ Concluídos       43    │  │
│  └────────────────────────┘  │
│                              │
├──────────────────────────────┤
│ 🏠     📚      🔍      👤   │
│ Início Lista  Buscar Perfil │
└──────────────────────────────┘
```

### Destinos

- **Início**
- **Biblioteca**
- **Buscar**
- **Perfil**

Evitar excesso de destinos na navegação principal.

---

# 9. Ícones

A linguagem de ícones deve ser consistente e minimalista.

Preferência:

- Ícones lineares no estado normal
- Ícones preenchidos no estado selecionado
- Estados visuais claros

Exemplo:

```text
♡ Favoritar
♥ Favoritado
```

Ícones devem reforçar a compreensão da interface sem substituir textos quando houver risco de ambiguidade.

---

# 10. Logo e símbolo

## 10.1 Conceito oficial

O símbolo oficial combina três ideias complementares:

> **M + Play + Progresso/Portal**

- **M** — inicial de MykytaDu e elemento de reconhecimento da marca;
- **Play** — representa assistir animes e continuar uma experiência;
- **Progresso** — comunica acompanhamento de episódios e evolução da jornada;
- **Portal** — representa a entrada no universo pessoal de animes do usuário.

O resultado deve transmitir:

> **Sua jornada pelos animes.**

<p align="center">
  <img src="./mykytadu-logo-symbol.svg" alt="Símbolo oficial do MykytaDu: M, play e círculo de progresso" width="320">
</p>

<p align="center"><em>Símbolo oficial do MykytaDu.</em></p>

A referência ao universo dos animes permanece sutil. O símbolo não utiliza personagens, olhos literais, ideogramas japoneses ou tipografia temática.

## 10.2 Anatomia do símbolo

O monograma `M` é construído por três peças vetoriais independentes:

1. **Perna esquerda** — peça vertical com chanfro inferior voltado para o arco de progresso;
2. **Diagonal descendente** — parte superior esquerda que termina no vértice central;
3. **Peça direita** — diagonal ascendente integrada à perna direita, também finalizada com chanfro.

O botão de play ocupa o espaço interno ao lado da perna esquerda. Ele deve permanecer visualmente alinhado, mas separado por um respiro óptico constante. O play não deve tocar nem parecer colado ao monograma.

O círculo de progresso contorna o `M` e é dividido em quatro trechos:

- arco superior com transição de violeta para turquesa;
- segmento turquesa à direita;
- segmento violeta à direita;
- arco violeta inferior.

O arco superior inicia-se alinhado à ponta superior esquerda do `M`. O arco inferior acompanha externamente os chanfros das duas pernas, sem tocar no monograma. Essa separação evita a sensação de elementos empilhados.

## 10.3 Construção e proporções

- O arquivo mestre utiliza `viewBox="0 0 1024 1024"`;
- O símbolo deve permanecer centralizado e preservar suas proporções originais;
- As três peças do `M` não devem ser fundidas em um único traço;
- O círculo de progresso é ligeiramente deslocado para a direita em relação ao centro do monograma;
- Cantos e terminações devem permanecer arredondados;
- Redimensionamentos devem ser uniformes, sem distorção horizontal ou vertical;
- A geometria oficial não deve ser redesenhada individualmente em cada plataforma.

O arquivo `mykytadu-logo-symbol.svg` é a fonte única da geometria da marca.

## 10.4 Cores do logo

| Elemento | Direção de cor |
|---|---|
| Perna esquerda | Lilás claro → violeta → leve recuperação de luminosidade |
| Diagonal descendente | Lilás → violeta profundo |
| Peça direita | Lilás superior → índigo inferior |
| Play | Turquesa → azul-ciano |
| Arco superior | Violeta → turquesa |
| Segmentos direitos | Turquesa e violeta |
| Arco inferior | Violeta/índigo |
| Fundo principal | `#0B0D12` |

As cores-base permanecem vinculadas à paleta do Design System:

- Violeta principal: `#8B7CFF`;
- Índigo: `#6C5CE7`;
- Turquesa: `#45D6C8`;
- Texto claro: `#F4F4F7`;
- Fundo dark: `#0B0D12`.

Gradientes podem ser utilizados dentro das peças oficiais. Brilhos e halos são recursos de composição para splash screens e materiais de divulgação; não fazem parte da geometria mestre.

## 10.5 Wordmark

O nome deve ser escrito exatamente como:

> **MykytaDu**

Regras do wordmark:

- `M` e `D` maiúsculos;
- Demais letras minúsculas;
- Fonte principal: **Poppins Bold** ou **Poppins SemiBold**;
- `Mykyta` em `#F4F4F7` sobre fundos escuros;
- `Du` em violeta/índigo;
- O espaçamento entre símbolo e nome deve permitir leitura independente dos dois elementos;
- Efeitos de brilho devem ser discretos e restritos a peças promocionais ou splash screens.

## 10.6 Área de proteção e tamanho mínimo

A marca deve possuir área livre suficiente para não competir com textos, bordas ou outros elementos.

- Ao redor do símbolo, preservar no mínimo a espessura visual de uma das pernas do `M`;
- Em avatares circulares, manter o símbolo dentro de aproximadamente `78%` da área;
- No launcher adaptativo, manter toda a geometria dentro da área segura central;
- Não aproximar o wordmark do símbolo a ponto de transformá-los em uma única massa visual.

Tamanhos mínimos recomendados:

| Aplicação | Tamanho mínimo |
|---|---:|
| Símbolo completo | `32 px` |
| Ícone convencional | `48 px` |
| Avatar | `40 px` |
| Favicon simplificado | `16 px` |

Em `16 px`, deve ser utilizada exclusivamente a variante de favicon, que possui símbolo ampliado e traços de progresso mais espessos.

## 10.7 Derivações oficiais

| Aplicação | Arquivo | Características |
|---|---|---|
| Mestre | `mykytadu-logo-symbol.svg` | Geometria oficial com fundo transparente |
| Launcher — foreground | `mykytadu-launcher-foreground.svg` | Símbolo dentro da área segura adaptativa |
| Launcher — background | `mykytadu-launcher-background.svg` | Fundo sólido `#0B0D12` |
| Ícone convencional | `mykytadu-app-icon.svg` | Tile arredondado autocontido |
| Favicon vetorial | `mykytadu-favicon.svg` | Variante ampliada para tamanhos reduzidos |
| Favicon legado | `mykytadu-favicon.ico` | Inclui `16`, `32` e `48 px` |
| Splash Screen | `mykytadu-splash.svg` / `.png` | Composição vertical `1080 × 1920` |
| Avatar | `mykytadu-avatar.svg` / `.png` | Fundo circular e margem segura |
| GitHub Social Preview | `mykytadu-github-social-preview.svg` / `.png` | Composição horizontal `1280 × 640` |

## 10.8 Usos incorretos

Não é permitido:

- Alterar as proporções do símbolo;
- Reposicionar individualmente as peças do `M`;
- Encostar o play na perna esquerda;
- Fundir o arco inferior aos chanfros do monograma;
- Girar, inclinar ou espelhar o símbolo;
- Substituir as cores oficiais por cores sem relação com o Design System;
- Aplicar sombras, contornos ou brilhos excessivos;
- Utilizar tipografia temática de anime ou caracteres japoneses como parte do logo;
- Recriar manualmente a geometria quando uma derivação oficial já existir.

---

# 11. Microinterações

As microinterações devem tornar a experiência mais agradável sem prejudicar a produtividade.

### Marcar episódio como assistido

```text
○ Episódio 18
```

pode transformar-se em:

```text
✓ Episódio 18
```

com uma pequena animação.

### Favoritar

```text
♡ → ♥
```

com uma animação breve de escala.

### Atualização de progresso

A barra de progresso deve animar suavemente quando o episódio é atualizado.

### Troca de tema

A mudança entre:

- 🌙 Dark
- ☀️ Light

deve ser suave sempre que possível, evitando uma troca visual abrupta.

---

# 12. Design System

Como o projeto utiliza **Kotlin Multiplatform + Compose Multiplatform**, a identidade visual deve ser implementada como um Design System desde o início.

Estrutura conceitual:

```text
Theme
 ├── Colors
 │    ├── Primary
 │    ├── Secondary
 │    ├── Background
 │    ├── Surface
 │    └── Text
 │
 ├── Typography
 │    ├── Display
 │    ├── Headline
 │    ├── Title
 │    ├── Body
 │    └── Label
 │
 ├── Shapes
 │    ├── Card
 │    ├── Button
 │    └── Chip
 │
 └── Dimensions
      ├── Spacing
      ├── Padding
      └── Radius
```

A implementação deve permitir que os componentes utilizem tokens em vez de valores de cor, espaçamento ou raio definidos diretamente em cada tela.

---

# 13. Princípios de UX

A identidade visual deve seguir alguns princípios:

### 1. Hierarquia antes de decoração

O usuário deve entender rapidamente:

- onde está;
- o que está acompanhando;
- qual é seu progresso;
- qual ação pode realizar.

### 2. Consistência

Um mesmo significado deve possuir o mesmo visual em todo o aplicativo.

### 3. Feedback

Toda ação importante deve possuir algum retorno visual.

### 4. Conteúdo como protagonista

As capas e informações dos animes devem ter destaque. A identidade do aplicativo deve complementar o conteúdo, não competir com ele.

### 5. Personalização

O usuário deve sentir que a biblioteca pertence a ele.

### 6. Acessibilidade

Cores, contraste, tamanho de texto e áreas de toque devem ser pensados para uso confortável.

---

# 14. Resumo da identidade

## MykytaDu

**Conceito:**

> Sua jornada pelos animes.

**Direção visual:**

> Anime moderno + tecnologia + organização pessoal

**Personalidade:**

> Geek, moderno, elegante e pessoal.

**Cor principal:**

> Violet / Indigo

**Cor secundária:**

> Turquoise

**Dark Mode:**

> Identidade visual principal, imersiva e tecnológica.

**Light Mode:**

> Limpo, leve e organizado.

**Tipografia:**

> Poppins

**Cards:**

> Modernos, com 16dp de radius.

**Ícones:**

> Lineares, consistentes e minimalistas.

**Navegação:**

> Início · Biblioteca · Buscar · Perfil

**Filosofia:**

> **Anime na personalidade, produto de software na execução.**

---

# 15. Próximas etapas do Design System

A evolução recomendada para o projeto é:

1. [x] Definir o conceito final do logo
2. Validar a paleta Dark/Light
3. Definir tokens de Design System
4. Implementar `MykytaDuDarkColorScheme`
5. Implementar `MykytaDuLightColorScheme`
6. Definir tipografia
7. Criar componentes base
8. Criar a Home como tela de referência
9. Validar a experiência em Dark e Light
10. Expandir o Design System para as demais telas

A Home deve funcionar como a **tela-piloto da identidade visual**. Se a linguagem funcionar nela, o restante do aplicativo poderá seguir os mesmos tokens e componentes com muito menos retrabalho.
