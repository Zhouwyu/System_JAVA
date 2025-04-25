package com.geekzhou.crm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geekzhou.crm.dto.CustomerQueryDto;
import com.geekzhou.crm.dto.UserQueryDto;
import com.geekzhou.crm.entity.Customer;
import com.geekzhou.crm.entity.User;
import com.geekzhou.crm.vo.UserLoginInfoVo;
import com.geekzhou.crm.vo.UserShowInfoVo;

import java.util.List;

public interface UserService extends IService<User> {
    UserLoginInfoVo getUserInfoByUsername(User userFromQd);

    User getUserInfoByUserId(Integer userId);

    Page<UserShowInfoVo> getUserByPage(UserQueryDto queryDTO);

    int createNewUser(User user);

    boolean checkUsernameAvailability(String username, Long userId);

    int updateUser(User user);

    int batchDelUser(List<Integer> ids);
}
