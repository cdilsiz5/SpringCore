package com.epam.springcore.mapper;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.model.User;

import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.request.user.UpdateUserRequest;
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
