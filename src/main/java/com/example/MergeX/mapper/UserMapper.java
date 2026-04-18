package com.example.MergeX.mapper;

import com.example.MergeX.Dto.UserDtoReq;
import com.example.MergeX.Dto.UserDtoRes;
import com.example.MergeX.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "college",ignore = true)
    User toEntity(UserDtoReq dto);


    @Mapping(target = "collegeName", source = "college.name")
    UserDtoRes todto(User user);
}
