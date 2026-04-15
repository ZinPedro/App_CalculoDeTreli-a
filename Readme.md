# Calculadora de Treliças Planas

Projeto desenvolvido para a disciplina de **Mecânica dos Sólidos** da **PUC-Campinas**.

O objetivo do projeto é criar um aplicativo capaz de **analisar treliças planas simples**, permitindo ao usuário inserir os dados da estrutura e obter automaticamente as forças internas nas barras e reações nos apoios.

---

# 📌 Objetivo

Desenvolver um **programa computacional** que resolva problemas de **Estática e Resistência dos Materiais**, especificamente para **treliças planas simples (rígidas)**.

O aplicativo deve permitir ao usuário modelar uma treliça fornecendo:

* Coordenadas dos nós
* Conexões entre os nós (elementos)
* Condições de apoio
* Forças aplicadas

A partir dessas informações, o sistema deve calcular as forças internas e indicar o comportamento estrutural das barras.

---

# 🧩 Funcionalidades

O aplicativo deverá permitir:

### Entrada de dados

* Inserção das **coordenadas dos nós da treliça**
* Definição dos **elementos (barras)** conectando os nós
* Identificação de **elementos que se cruzam**, exibindo erro ao usuário
* Definição dos **vínculos estruturais**, como:

  * Apoio tipo **pino**
  * Apoio tipo **rolete**
* Inserção de **forças aplicadas nos nós**, incluindo:

  * intensidade
  * direção
  * sentido ou componentes (Fx, Fy)

---

### Processamento

O sistema deverá realizar:

* Cálculo das **reações nos vínculos**
* Cálculo das **forças internas nas barras**
* Identificação se cada elemento está em:

  * **Tração**
  * **Compressão**
* Identificação de **elementos de força zero**

---

### Saída de resultados

O aplicativo deve apresentar ao usuário:

* Forças internas em cada barra
* Reações nos apoios
* Classificação das barras (tração ou compressão)
* Indicação de barras com **força nula**

---

# 🏗 Estrutura do Projeto

Estrutura inicial sugerida:

```
src/
 ├── model/
 │   ├── Node
 │   ├── Element
 │   ├── Force
 │   └── Support
 │
 ├── solver/
 │   └── TrussSolver
 │
 ├── validation/
 │   └── IntersectionChecker
 │
 └── ui/
     └── Interface do usuário
```

---

# 🧮 Método de cálculo

A análise estrutural da treliça será baseada em princípios de **equilíbrio estático**, utilizando:

* Equações de equilíbrio nos nós
* Sistemas de equações lineares
* Determinação de forças internas nas barras

Possíveis métodos:

* **Método dos Nós**
* **Método das Seções**
* Resolução matricial do sistema estrutural

---

# 🧪 Etapas do Projeto

O projeto será desenvolvido em três etapas principais.

### 1. Levantamento de aplicativos existentes

Pesquisa de ferramentas já disponíveis no mercado para análise de treliças, avaliando:

* funcionalidades
* interface
* facilidade de uso

---

### 2. Implementação do aplicativo

Desenvolvimento do software com:

* entrada de dados da estrutura
* processamento dos cálculos
* apresentação dos resultados

---

### 3. Testes com público-alvo

O aplicativo será testado por estudantes de engenharia para:

* validar a usabilidade
* identificar melhorias
* corrigir possíveis erros

---

# 🎤 Apresentação

O projeto será apresentado em formato de **pitch**, com duração máxima de **5 minutos**, abordando:

* método de cálculo utilizado
* estrutura computacional
* algoritmo implementado
* demonstração do aplicativo

Após a apresentação, será realizado um **teste prático**, onde o programa deverá resolver corretamente um problema proposto pela banca.

---

# 🚀 Possíveis melhorias

Funcionalidades adicionais que podem ser implementadas:

* Interface gráfica para criação da treliça
* Visualização gráfica das forças
* Exportação de resultados
* Versão multiplataforma
* Publicação em lojas de aplicativos

---

# 👥 Equipe

Projeto desenvolvido por estudantes da disciplina **Mecânica dos Sólidos** da **PUC-Campinas**.
Pedro Henrique Coan Zin

---

# 📄 Licença

Projeto acadêmico desenvolvido para fins educacionais.
