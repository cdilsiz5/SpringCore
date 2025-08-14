package com.epam.gymcrm.mapper.impl;

import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.mapper.UserMapper;
import com.epam.gymcrm.model.User;
import com.epam.gymcrm.request.user.CreateUserRequest;
import com.epam.gymcrm.request.user.UpdateUserRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toUserDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userActive(user.isUserActive())
                .build();
    }

    @Override
    public List<UserDto> toUserDtoList(List<User> users) {
        if (users == null) return null;

        List<UserDto> dtoList = new ArrayList<>();
        for (User user : users) {
            dtoList.add(toUserDto(user));
        }
        return dtoList;
    }

    @Override
    public User createUser(CreateUserRequest request) {
        if (request == null) return null;

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userActive(true)
                .build();
    }

    @Override
    public void updateUserRequest(UpdateUserRequest request, User user) {
        if (request == null || user == null) return;

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
    }


}
