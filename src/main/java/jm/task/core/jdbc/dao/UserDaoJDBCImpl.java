package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
            "name VARCHAR(50), " +
            "lastName VARCHAR(50), " +
            "age TINYINT);";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS users";
    private static final String INSERT_USER = "INSERT INTO users (name, lastName, age) VALUES (?, ?, ?);";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?;";
    private static final String SELECT_USERS = "SELECT * FROM users;";
    private static final String TRUNCATE_TABLE = "TRUNCATE TABLE users;";

    public void createUsersTable() {
        try (Connection connection = Util.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE);
            System.out.println("Таблица пользователей создана.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания таблицы: " + e.getMessage(), e);
        }
    }

    public void dropUsersTable() {
        try (Connection connection = Util.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(DROP_TABLE);
            System.out.println("Таблица пользователей удалена.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления таблицы: " + e.getMessage(), e);
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        Connection connection = null;

        try {
            connection = Util.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, lastName);
                preparedStatement.setByte(3, age);
                preparedStatement.executeUpdate();
            }

            connection.commit();
            System.out.println("Пользователь '" + name + "' добавлен.");

        } catch (SQLException e) {
            try {
                connection.rollback();
                System.out.println("Rollback при добавлении пользователя '" + name + "'.");
            } catch (SQLException rollbackEx) {
                System.err.println("Ошибка отката: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Ошибка добавления пользователя: " + e.getMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Ошибка закрытия соединения: " + closeEx.getMessage());
            }
        }
    }

    public void removeUserById(long id) {
        Connection connection = null;

        try {
            connection = Util.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
            }

            connection.commit();
            System.out.println("Пользователь с id " + id + " удалён.");

        } catch (SQLException e) {
            try {
                connection.rollback();
                System.out.println("Rollback при удалении пользователя с id " + id + ".");
            } catch (SQLException rollbackEx) {
                System.err.println("Ошибка отката: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Ошибка удаления пользователя: " + e.getMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Ошибка закрытия соединения: " + closeEx.getMessage());
            }
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = Util.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERS);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("name"),
                        rs.getString("lastName"),
                        rs.getByte("age"));
                user.setId(rs.getLong("id"));
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения пользователей: " + e.getMessage(), e);
        }

        return users;
    }

    public void cleanUsersTable() {
        try (Connection connection = Util.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(TRUNCATE_TABLE);
            System.out.println("Таблица очищена.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка очистки таблицы: " + e.getMessage(), e);
        }
    }
}