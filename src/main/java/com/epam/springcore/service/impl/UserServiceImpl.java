package com.epam.springcore.service.impl;

import com.epam.springcore.model.User;
import com.epam.springcore.service.IUserService;
import com.epam.springcore.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserServiceImpl implements IUserService {

    private UserStorage userStorage;

    @Autowired
    public void setUserStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void save(User user) {
        userStorage.getUserMap().put(user.getId(), user);
    }

    @Override
    public User findById(String id) {
        return userStorage.getUserMap().get(id);
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.getUserMap().values();
    }

    @Override
    public void delete(String id) {
        userStorage.getUserMap().remove(id);
    }
}
