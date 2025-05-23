package com.function.graph1.resolver;

import java.util.List;

import com.function.graph1.model.User;
import com.function.graph1.model.UserRole;
import com.function.graph1.service.UserRoleService;
import com.function.graph1.service.UserService;

import graphql.schema.DataFetcher;

public class UserDataFetcher {

    private final UserService userService = new UserService();
    private final UserRoleService userRoleService = new UserRoleService();

    public DataFetcher<List<User>> getAllUsersFetcher() {
        return environment -> userService.getAllUsers();
    }

    public DataFetcher<String> createUserFetcher() {
        return environment -> {
            String name = environment.getArgument("name");
            String email = environment.getArgument("email");
            String password = environment.getArgument("password");
            User user = new User(name, email, password);
            Long id = userService.createUser(user);
            System.out.println(id);
            UserRole a = new UserRole(id, 2L);

            userRoleService.assignRoleToUser(a);

            return "User created successfully";
        };
    }

    public DataFetcher<String> updateUserFetcher() {
        return environment -> {
            Long id = Long.valueOf(environment.getArgument("id"));
            String name = environment.getArgument("name");
            String email = environment.getArgument("email");
            String password = environment.getArgument("password");
            User user = new User(id, name, email, password);
            userService.updateUser(id, user);
            return "User updated successfully";
        };
    }

    public DataFetcher<String> deleteUserFetcher() {
        return environment -> {
            Long id = Long.valueOf(environment.getArgument("id"));
            userService.deleteUser(id);
            return "User deleted successfully";
        };
    }

    public DataFetcher<List<User>> getUsersByEmailFetcher() {
        return environment -> {
            String email = environment.getArgument("email");
            return userService.getUsersByEmail(email); // este método lo defines tú
        };
    }

    public DataFetcher<User> getUserByIdFetcher() {
        return environment -> {
            Long id = Long.valueOf(environment.getArgument("id"));
            return userService.getUserById(id);
        };
    }
}