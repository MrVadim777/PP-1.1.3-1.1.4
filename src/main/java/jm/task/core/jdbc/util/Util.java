package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    private final static String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static String DIALECT = "org.hibernate.dialect.MySQL5Dialect";
    private static final String URL = "jdbc:mysql://localhost:3306/mydbtest";
    private static final String NAME = "root";
    private static final String PASSWORD = "root";
    private static Connection connection = null;
    private static SessionFactory sessionFactory = null;

    public static Connection getConnection() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection(URL, NAME, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка соединения с БД", e);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка закрытия соединения с БД", e);
            }
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                Properties setting = new Properties();

                setting.put(Environment.DRIVER, DRIVER);
                setting.put(Environment.URL, URL);
                setting.put(Environment.USER, NAME);
                setting.put(Environment.PASS, PASSWORD);
                setting.put(Environment.DIALECT, DIALECT);
                setting.put(Environment.SHOW_SQL, "true");
                setting.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                setting.put(Environment.HBM2DDL_AUTO, "");

                configuration.setProperties(setting);
                configuration.addAnnotatedClass(User.class);

                ServiceRegistry serviceRegistry =
                        new StandardServiceRegistryBuilder()
                                .applySettings(configuration.getProperties())
                                .build();

                sessionFactory = configuration.
                        buildSessionFactory(serviceRegistry);
            } catch (HibernateException e) {
                throw new RuntimeException("Не удалось создать SessionFactory: "
                        + e.getMessage(), e);
            }
        }
        return sessionFactory;
    }
    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
