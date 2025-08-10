package com.epam.gymcrm.mapper;
import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.model.User;

import com.epam.gymcrm.request.user.CreateUserRequest;
import com.epam.gymcrm.request.user.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
@Mapper(componentModel = "spring")

public interface UserMapper {

    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> users);

    User createUser(CreateUserRequest request);

    void updateUserRequest(UpdateUserRequest request, @MappingTarget User user);

}
