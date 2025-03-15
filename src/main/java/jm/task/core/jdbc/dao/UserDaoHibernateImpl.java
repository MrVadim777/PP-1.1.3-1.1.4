package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private final SessionFactory sessionFactory = Util.getSessionFactory();
    private Transaction transaction = null;
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
            "name VARCHAR(50), " +
            "last_name VARCHAR(50), " +
            "age TINYINT);";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS users";
    private static final String SELECT_USERS = "FROM User";
    private static final String CLEAR_TABLE = "DELETE FROM User";

    public UserDaoHibernateImpl() {

    }

    @Override
    public void createUsersTable() {
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createSQLQuery(CREATE_TABLE).executeUpdate();
            transaction.commit();
            System.out.println("Таблица пользователей успешно создана.");
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new HibernateException("Не удалось создать таблицу: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public void dropUsersTable() {
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createSQLQuery(DROP_TABLE).executeUpdate();
            transaction.commit();
            System.out.println("Таблица пользователей успешно удалена.");
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new HibernateException("Не удалось удалить таблицу: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(new User(name, lastName, age));
            transaction.commit();
            System.out.println("Пользователь '" + name + "' добавлен.");
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new HibernateException("Не удалось добавить '" + name
                    + "' в таблицу: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeUserById(long id) {
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                transaction.commit();
                System.out.println("Пользователь с id " + id + " удалён.");
            } else {
                transaction.rollback();
                System.out.println("Пользователь с id " + id + " не найден.");
            }
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new HibernateException("Не удалось удалить пользователя по id '"
                    + id + "' в таблицу: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            List<User> users = session.createQuery(SELECT_USERS, User.class).list();
            System.out.println("Список пользователей успешно получен.");
            return users;
        } catch (HibernateException e) {
            throw new HibernateException("Не удалось получить список пользователей."
                    + e.getMessage(), e);
        }

    }

    @Override
    public void cleanUsersTable() {
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery(CLEAR_TABLE).executeUpdate();
            transaction.commit();
            System.out.println("Таблица была успешно очищена.");
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new HibernateException("Не удалось очистить таблицу: "
                    + e.getMessage(), e);
        }
    }
}
