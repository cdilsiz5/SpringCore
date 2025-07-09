package com.epam.springcore.service;


import com.epam.springcore.model.User;

import java.util.Collection;

/**
 * Service interface for managing users in the system.
 */
public interface IUserService {

    /**
     * Saves a user to the storage.
     *
     * @param user the user to save
     */
    void save(User user);

    /**
     * Finds a user by their unique ID.
     *
     * @param id the user ID
     * @return the user with the given ID, or null if not found
     */
    User findById(String id);

    /**
     * Returns all users in the storage.
     *
     * @return a collection of all users
     */
    Collection<User> findAll();

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     */
    void delete(String id);
}
