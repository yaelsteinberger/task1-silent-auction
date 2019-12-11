package server;

import entity.User;

public interface AdminUser {
    User ADMIN = new User(
            "administrator",
            "Admin",
            "Manager");
}
