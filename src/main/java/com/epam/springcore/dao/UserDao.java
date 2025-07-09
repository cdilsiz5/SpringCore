package com.epam.springcore.dao;

import com.epam.springcore.model.User;
import com.epam.springcore.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class UserDao {

    private UserStorage userStorage;

    @Autowired
    public void setUserStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void save(User user) {
        userStorage.getUserMap().put(user.getId(), user);
    }

    public User findById(String id) {
        return userStorage.getUserMap().get(id);
    }

    public Collection<User> findAll() {
        return userStorage.getUserMap().values();
    }
    public void delete(String id) {
        userStorage.getUserMap().remove(id);
    }
}
