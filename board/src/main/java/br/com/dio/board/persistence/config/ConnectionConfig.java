package br.com.dio.board.persistence.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionConfig {

    //Arquivo para acessar o banco de dados utilizando o Liquibase
    public static Connection getConnection() throws SQLException {
        Dotenv dotenv = Dotenv.load(); //Configuração das variáveis de ambiente

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        Connection connection = DriverManager.getConnection(
                url,
                user,
                password);
        connection.setAutoCommit(false);
        return connection;
    }
}
