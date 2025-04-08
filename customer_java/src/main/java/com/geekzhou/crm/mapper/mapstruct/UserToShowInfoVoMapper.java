package com.geekzhou.crm.mapper.mapstruct;


import com.geekzhou.crm.entity.Order;
import com.geekzhou.crm.entity.User;
import com.geekzhou.crm.vo.OrderShowInfoVo;
import com.geekzhou.crm.vo.UserShowInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实体类映射为VO用于前端展示（用户管理页面）
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {LocalDateTime.class},
uses = {})
public interface UserToShowInfoVoMapper {
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "userName", source = "username")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "phone", source = "mobile")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "roleId", source = "roleId")
    @Mapping(target = "roleName", ignore = true)
    UserShowInfoVo entityToShowVo(User entity);
    List<UserShowInfoVo> entityToShowVoList(List<User> users);
}
