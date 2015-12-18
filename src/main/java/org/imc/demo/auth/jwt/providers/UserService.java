package org.imc.demo.auth.jwt.providers;

import org.imc.demo.auth.jwt.models.User;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    List<User> users = new ArrayList<User>() {{
        add(new User(UUID.randomUUID().toString(), "demo", "demo", "demo@auth-demo.com"));
        add(new User(UUID.randomUUID().toString(), "admin", "admin", "admin@auth-demo.com"));
    }};

    public User getUser(@NotNull String username, @NotNull String password) {
        for (User user : users) {
            if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                return user;
            }
        }

        return null;
    }

    public User getById(String id) {
        for (User user : users) {
            if (id.equals(user.getId())) {
                return user;
            }
        }
        return null;
    }
}
