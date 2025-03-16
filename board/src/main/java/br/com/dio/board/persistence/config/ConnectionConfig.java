package br.com.dio.board.persistence.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionConfig {

    public static Connection getConnection() throws SQLException {
        var connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/board",
                "postgres",
                "admin");
        connection.setAutoCommit(false);
        return connection;
    }
}
