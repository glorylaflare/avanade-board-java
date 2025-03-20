package br.com.dio.board;

import br.com.dio.board.persistence.migration.MigrationStrategy;
import br.com.dio.board.ui.MainMenu;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.SQLException;

import static br.com.dio.board.persistence.config.ConnectionConfig.getConnection;

@SpringBootApplication
public class BoardApplication {

	public static void main(String[] args) throws SQLException {
		try(Connection connection = getConnection()){
			new MigrationStrategy(connection).executeMigration();
		}
		new MainMenu().execute();
	}

}
