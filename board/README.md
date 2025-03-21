# 📌 Board Management System

Um sistema de gerenciamento de boards e cards para organizar tarefas de forma eficiente.

## 🚀 Funcionalidades

- Criar, mover, bloquear e desbloquear cards dentro de um board
- Registro de data e hora das movimentações dos cards
- Interface interativa no terminal para manipulação dos boards e cards

##  🎯️ Informações sobre o projeto

**Mudanças do projeto base:**

- Projeto usando Maven e PostgreSQL
    - Optei por usar o Maven e o PostgreSQL por ser algo que já tenho mais hábito e também para me desafiar em fazer algo diferente daquilo que foi visto na aula, mas não teria problemas de usar Gradle e MySQL.
- Alteração em algumas chamadas de variáveis
- Adição de novos métodos
- Adição de um novo DTO
    - Adição do BoardInfoDTO, usado para resgatar as informações na hora da checagem de confirmação do Board após o usuário escolher a opção de deletar.
- Adição de variáveis de ambiente no arquivo de config do liquibase

**Novas features:**

- Adição de uma mensagem de “Saindo…” quando o usuário digitar para sair
    - Apenas um feedback do comando avisando que o scanner será finalizado.
- Adição das colunas de registro de movimentação e criação dos Cards (date_created, date_moved) → desafio proposto (1)
    - As colunas de data de criação do card e data da última movimentação do card foram adicionadas.
- Verifica se o Card está desbloqueado antes de perguntar o motivo
    - Antes o comando perguntava o motivo do desbloqueio antes de verificar se o Card estava ou não bloqueado, achei que se fizesse a verificação primeiro, seria mais útil.
- Pede uma confirmação antes de deletar um Board
    - Ajuda ao evitar que o usuário delete um board sem querer.
- Lista todos os Boards do banco de dados
    - Ajuda ao usuário a saber quantos boards tem no banco de dados e quais ele pode manipular através do ID.

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Maven**
- **Liquibase** (para controle de versão do banco de dados)
- **PostgreSQL** (banco de dados relacional)
- **Dotenv** (variáveis de ambiente)

## 📂 Estrutura do Projeto

```
board-management/
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
3. Execute as migrações do Liquibase:
   ```sh
   mvn liquibase:update
   ```
4. Compile e execute o projeto:
   ```sh
   mvn clean package
   java -jar target/board-management.jar
   ```
