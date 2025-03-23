# 📌 Board Management System

Um sistema de gerenciamento de boards e cards para organizar tarefas de forma eficiente.

## 🚀 Funcionalidades

- Criar, mover, bloquear e desbloquear cards dentro de um board
- Registro de data e hora das movimentações dos cards
- Interface interativa no terminal para manipulação dos boards e cards

##  🎯️ Informações sobre o projeto

**Mudanças do projeto base:**

- Alteração em algumas chamadas de variáveis
- Adição de novos métodos
- Alguns textos foram alterados para deixar as repostas do menu mais legíveis
- Projeto usando Maven e PostgreSQL
    - Optei por usar o Maven e o PostgreSQL por ser algo que já tenho mais hábito e também para me desafiar em fazer algo diferente daquilo que foi visto na aula, mas não teria problemas de usar Gradle e MySQL.
- Adicionado uma nova dependência Jackson Databind
    - Dependência utilizada para gerar os arquivos json para os relatórios.
- Adição de dois novo DTOs
    - Adição do BoardInfoDTO usado para resgatar as informações na hora da checagem de confirmação do Board após o usuário escolher a opção de deletar.
    - Adição do CardMovementDTO usado para resgatar as informações da tabela CARD_MOVEMENT_HISTORY e repassar para o relatório.
- Adição de uma nova tabela CARD_MOVEMENT_HISTORY
    - Tabela que armazena os dados de entrada e saída de cada coluna de um card.
- Adicionado uma nova Entity, DAO e Service relacionado as movimentações do card
    - Novos arquivos, CardMovementEntity, CardMovementDAO e ReportService, utilizados para gerar o relatório em json, através da dependência Jackson.

**Novas features:**

- Adição de uma mensagem de “Saindo…” quando o usuário digitar para sair
    - Apenas um feedback do comando avisando que o scanner será finalizado.
- Verifica se o Card está desbloqueado antes de perguntar o motivo
    - Antes o comando perguntava o motivo do desbloqueio antes de verificar se o Card estava ou não bloqueado, achei que se fizesse a verificação primeiro, seria mais útil.
- Pede uma confirmação antes de deletar um Board
    - Ajuda ao evitar que o usuário delete um board sem querer.
- Lista todos os Boards do banco de dados
    - Ajuda ao usuário a saber quantos boards tem no banco de dados e quais ele pode manipular através do ID.

**Desafios propostos:**

- Desafio 1: Adição de uma tabela que registra os horários de entrada e saída de um card
    - Agora existe uma tabela que registra o tempo de entrada e saída de um card de suas respectivas colunas.
- Desafio 2: Gerar relatório em json sobre as movimentações de um card em específico
    - Agora existe uma opção no BoardMenu capaz de gerar um relatório com todas as movimentações e o tempo que durou em cada coluna.

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Maven**
- **Liquibase** (para controle de versão do banco de dados)
- **PostgreSQL** (banco de dados relacional)
- **Dotenv** (variáveis de ambiente)

## 📂 Estrutura do Projeto

```
board/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── br/com/dio/board/
│   │   │   │   ├── dto/
│   │   │   │   ├── exception/
│   │   │   │   ├── persistence/ 
│   │   │   │   │   ├── config/
│   │   │   │   │   ├── converter/
│   │   │   │   │   ├── dao/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── migration/
│   │   │   │   ├── service/ 
│   │   │   │   ├── ui/ 
│   │   ├── resources/
│   │   │   ├── db/ 
│   │   │   │   ├── changelog/
│   │   │   │   │   ├── migrations/
│   │   │   ├── reports/            
│   │   │   ├── application.properties
│   │   │   ├── liquibase.properties 
├── pom.xml 
├── .env 
```

## ⚙️ Como Rodar o Projeto

1. Clone este repositório:
   ```sh
   git clone https://github.com/seu-usuario/board-management.git
   cd board-management
   ```
2. Configure o banco de dados PostgreSQL e edite `.env`.
    ```declarative
    DB_URL=jdbc:postgresql://localhost:5432/board_db
    DB_USER=seu_usuario
    DB_PASSWORD=sua_senha
    ```
### Como Usar a Aplicação

1. Após iniciar o programa, você verá um menu no terminal com as opções disponíveis.
2. Escolha a opção desejada digitando o número correspondente e pressionando **Enter**.
3. As principais funcionalidades incluem:
    - Criar um novo quadro (board)
    - Criar um novo cartão (card)
    - Mover um cartão entre colunas
    - Gerar um relatório de movimentações de um cartão

### Exemplo de Uso

- Criar um novo board:
  ```sh
  Selecione a opção 1 no menu principal: "Criar um novo board"
  Escolha o nome do board: "Aulas pendentes do Bootcamp"
  Responda se o board terá mais colunas além das 3 padrões (INITIAL, PENDENT, FINAL), se sim, digite o número de colunas, se não, digite 0
  Informe o nome das colunas do board
  "O Board foi criado com sucesso"
  ```

- Excluir um board:
  ```sh
  Selecione a opção 4: "Excluir um board"
  Escolha o id do board que você deseja deletar
  Confirme o procedimento digitando o nome do board que você deseja deletar
  ```
  
- Criar um card:
  ```sh
  Selecione a opção 3 no menu principal: "Selecionar um board existente"
  Informe o id do board que você deseja navegar
  Selecione a opção 1 no menu do board: "Criar um card"
  Informe o título e descrição do card
  ```
- Bloquear um card:
  ```sh
  Selecione a opção 3 no menu do board: "Bloquear um card"
  Informe o id do card que deseja bloquear
  Informe o motivo do porque deseja bloquear o card
  ```
Card que estiverem na coluna _FINAL_ não poderão ser bloqueados.

- Desbloquear um card:
  ```sh
  Selecione a opção 4 no menu do board: "Desbloquear um card"
  Informe o id do card que deseja desbloquear
  Informe o motivo do porque deseja desbloquear o card
  ```
  
- Gerar um relatório:
  ```sh
  No menu do board, selecione a opção 9: "Gerar relatório de um card"
  Digite o id do card que deseja um relatório detalhado
  "Relatório gerado com sucesso src/main/resources/reports/card_1_report.json"
  ```

O relatório gerado conterá informações detalhadas sobre as movimentações do card, incluindo tempos de permanência em cada coluna.

---
Agora você está pronto para usar o projeto! Caso tenha dúvidas, sinta-se à vontade para perguntar. 🚀

