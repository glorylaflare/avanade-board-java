package br.com.dio.board.ui;

import br.com.dio.board.dto.BoardDetailsDTO;
import br.com.dio.board.dto.BoardInfoDTO;
import br.com.dio.board.persistence.entity.BoardColumnEntity;
import br.com.dio.board.persistence.entity.BoardColumnTypeEnum;
import br.com.dio.board.persistence.entity.BoardEntity;
import br.com.dio.board.service.BoardQueryService;
import br.com.dio.board.service.BoardService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static br.com.dio.board.persistence.config.ConnectionConfig.getConnection;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao gerenciador de boards, escolha a opção desejada");
        int option = -1;
        while (true){
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Mostrar todos os boards");
            System.out.println("3 - Selecionar um board existente");
            System.out.println("4 - Excluir um board");
            System.out.println("5 - Sair");

            option = scanner.nextInt();
            switch (option){
                case 1 -> createBoard();
                case 2 -> showAllBoards();
                case 3 -> selectBoard();
                case 4 -> deleteBoard();
                case 5 -> {
                    System.out.println("Saindo...");
                    System.exit(0);
                }
                default -> System.out.println("Opção inválida, informe uma opção do menu");
            }
        }
    }

    private void createBoard() throws SQLException {
        BoardEntity entity = new BoardEntity();
        System.out.println("Informe o nome do seu board");
        entity.setName(scanner.next());

        System.out.println("Seu board terá colunas além das 3 padrões? Se sim informe quantas, senão digite '0'");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial do board");
        var initialColumnName = scanner.next();
        BoardColumnEntity initialColumn = createColumn(
                initialColumnName,
                BoardColumnTypeEnum.INITIAL,
                0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna de tarefa pendente do board");
            var pendingColumnName = scanner.next();
            BoardColumnEntity pendingColumn = createColumn(
                    pendingColumnName,
                    BoardColumnTypeEnum.PENDING,
                    i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final");
        var finalColumnName = scanner.next();
        BoardColumnEntity finalColumn = createColumn(
                finalColumnName,
                BoardColumnTypeEnum.FINAL,
                additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento do baord");
        var cancelColumnName = scanner.next();
        BoardColumnEntity cancelColumn = createColumn(
                cancelColumnName,
                BoardColumnTypeEnum.CANCEL,
                additionalColumns + 2);
        columns.add(cancelColumn);
        entity.setBoardColumns(columns);

        try(Connection connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void showAllBoards() throws SQLException {
        try(Connection connection = getConnection()){
            BoardQueryService queryService = new BoardQueryService(connection);
            List<BoardEntity> boards = queryService.getAllBoards();
            boards.forEach(b -> {
                System.out.println("[Board (ID: " + b.getId() + ", Nome: \"" + b.getName() + "\")]");
            });
        } catch (SQLException e) {
            System.out.println("Erro ao listar os boards: " + e.getMessage());
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar");
        var id = scanner.nextLong();

        try(Connection connection = getConnection()){
            BoardQueryService queryService = new BoardQueryService(connection);
            Optional<BoardEntity> optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Não foi encontrado um board com id %s\n", id)
            );
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do board que será excluido");
        var id = scanner.nextLong();
        scanner.nextLine();

        try(Connection connection = getConnection()){
            BoardService service = new BoardService(connection);
            BoardInfoDTO dto = service.getInfo(id);

            System.out.println("Digite o nome do board \"" + dto.name() + "\" para confirmar a exclusão");
            var name = scanner.nextLine();
            if(!name.equals(dto.name())) {
                System.out.println("O nome digitado não está correto");
                return;
            }

            if (service.delete(id)){
                System.out.printf("O board %s foi excluido\n", id);
            } else {
                System.out.printf("Não foi encontrado um board com id %s\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnTypeEnum type, final Integer order) {
        BoardColumnEntity boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setType(type);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
