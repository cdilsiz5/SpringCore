package com.epam.springcore.service.impl;

import com.epam.springcore.dao.UserDao;
import com.epam.springcore.model.User;
import com.epam.springcore.service.IUserService;
import com.epam.springcore.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserServiceImpl implements IUserService {

    private UserDao userDao;

    @Autowired
    public void setUserStorage(UserDao userDao) {
        this.userDao = userDao;
    }
    @Override
    public void save(User user) {
        UserValidator.validate(user);
        userDao.save(user);
    }

    @Override
    public User findById(String id) {
        return validateUserExistenceById(id);
    }

    @Override
    public Collection<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public void delete(String id) {
        validateUserExistenceById(id);
        userDao.delete(id);
    }

    private User validateUserExistenceById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }

        User user = userDao.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        return user;
    }
}
