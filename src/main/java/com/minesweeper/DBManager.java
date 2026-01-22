package com.minesweeper;

import java.sql.*;

public class DBManager {

    private final String url;

    public DBManager(String databasePath) {
        // databasePath = "data/leaderboard.db"
        this.url = "jdbc:sqlite:" + databasePath;
        ensureTableExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    // создаём таблицу, если её нет
    private void ensureTableExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS leaderboard (
                name TEXT NOT NULL,
                time INTEGER NOT NULL,
                difficulty TEXT NOT NULL
            )
            """;

        try (Connection c = getConnection();
             Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(String name) {
        String query = "SELECT COUNT(*) FROM leaderboard WHERE name = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void insertPlayer(String name, int time, String difficulty) {
        String query = "INSERT INTO leaderboard (name, time, difficulty) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, time);
            statement.setString(3, difficulty);
            statement.executeUpdate();
            System.out.println("Result saved!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerScore(String name, int time, String difficulty) {
        String query = """
            UPDATE leaderboard
            SET time = ?
            WHERE name = ? AND difficulty = ? AND time > ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, time);
            statement.setString(2, name);
            statement.setString(3, difficulty);
            statement.setInt(4, time);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                insertPlayer(name, time, difficulty);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
