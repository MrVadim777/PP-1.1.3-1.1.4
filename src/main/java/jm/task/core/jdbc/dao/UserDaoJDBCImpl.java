package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    private final Connection connection = Util.getConnection();
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

    @Override
    public void createUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE);
            System.out.println("Таблица пользователей создана.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания таблицы: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public void dropUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(DROP_TABLE);
            System.out.println("Таблица пользователей удалена.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления таблицы: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {
            connection.setAutoCommit(false);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.executeUpdate();

            connection.commit();
            System.out.println("Пользователь '" + name + "' добавлен.");
        } catch (SQLException rollbackEx) {
            try {
                connection.rollback();
                System.out.println("Rollback при добавлении пользователя '"
                        + name + "'.");
            } catch (SQLException e) {
                System.err.println("Ошибка при вызове Rollback: "
                        + e.getMessage());
            }

            throw new RuntimeException("Ошибка добавления пользователя: "
                    + rollbackEx.getMessage(), rollbackEx);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Ошибка возвращения setAutoCommit в true: "
                        + e.getMessage());
            }
        }
    }

    @Override
    public void removeUserById(long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)) {
            connection.setAutoCommit(false);

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

            connection.commit();
            System.out.println("Пользователь с id " + id + " удалён.");
        } catch (SQLException rollbackEx) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                System.err.println("Ошибка при вызове Rollback: "
                        + e.getMessage());
            }

            throw new RuntimeException("Ошибка удаления пользователя: "
                    + rollbackEx.getMessage(), rollbackEx);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Ошибка возвращения setAutoCommit в true: "
                        + e.getMessage());
            }
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERS);
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
            throw new RuntimeException("Ошибка получения пользователей: "
                    + e.getMessage(), e);
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(TRUNCATE_TABLE);
            System.out.println("Таблица очищена.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка очистки таблицы: "
                    + e.getMessage(), e);
        }
    }
}