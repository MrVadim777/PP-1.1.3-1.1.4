package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        userService.createUsersTable();
        userService.saveUser("Vadim", "Droshenkov", (byte) 25);
        userService.saveUser("Darya", "Zhelezniakova", (byte) 24);
        userService.saveUser("Andrei", "Koncevoi", (byte) 26);
        userService.saveUser("Sergei", "Griboedov", (byte) 34);
        System.out.println(userService.getAllUsers().toString());
        userService.cleanUsersTable();
        userService.dropUsersTable();
    }
}
