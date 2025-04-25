package com.geekzhou.crm.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geekzhou.crm.dto.UserQueryDto;
import com.geekzhou.crm.entity.*;
import com.geekzhou.crm.exception.CustomException;
import com.geekzhou.crm.mapper.mapstruct.UserToShowInfoVoMapper;
import com.geekzhou.crm.mapper.mybatis.RoleMapper;
import com.geekzhou.crm.mapper.mybatis.UserMapper;
import com.geekzhou.crm.service.UserService;
import com.geekzhou.crm.utils.PasswordUtils;
import com.geekzhou.crm.utils.TokenUtils;
import com.geekzhou.crm.vo.OrderShowInfoVo;
import com.geekzhou.crm.vo.UserLoginInfoVo;
import com.geekzhou.crm.vo.UserShowInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserToShowInfoVoMapper userToShowInfoVoMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserLoginInfoVo getUserInfoByUsername(User userFromQd) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userFromQd.getUsername());
        User dbUserInfo = userMapper.selectOne(queryWrapper);
        if (dbUserInfo == null) {
            throw new CustomException("401", "用户名错误");
        }
        if (!userFromQd.getRoleId().equals(dbUserInfo.getRoleId())) {
            throw new CustomException("402", "没有权限");
        }
        if (!PasswordUtils.matches(userFromQd.getPassword(), dbUserInfo.getPassword())) {
            throw new CustomException("401", "密码错误");
        }
        String token = TokenUtils.createToken(dbUserInfo.getUserId() + "-" + roleMapper.selectById(dbUserInfo.getRoleId()).getRoleCode(),
                dbUserInfo.getPassword());
        dbUserInfo.setAccessToken(token);
        UserLoginInfoVo userLoginInfoVo = new UserLoginInfoVo();
        userLoginInfoVo.setUserName(dbUserInfo.getUsername());
        userLoginInfoVo.setRoleId(dbUserInfo.getRoleId());
        userLoginInfoVo.setUserId(dbUserInfo.getUserId());
        userLoginInfoVo.setAccessToken(token);
        userLoginInfoVo.setStatus(dbUserInfo.getStatus().getCode());
        return userLoginInfoVo;
    }

    @Override
    public User getUserInfoByUserId(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public Page<UserShowInfoVo> getUserByPage(UserQueryDto queryDto) {
        // 1. 构建分页参数（使用User实体类接收）
        Page<User> entityPage = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 构建动态查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 基础条件：未删除
        queryWrapper.eq(User::getIsDeleted, 0);
        // 匹配用户名
        if (StrUtil.isNotBlank(queryDto.getUserName())) {
            queryWrapper.like(User::getUsername, queryDto.getUserName());
        }

        // 4. 排序规则（示例按创建时间升序）
        queryWrapper.orderByAsc(User::getCreatedAt);

        // 5. 执行原始分页查询（获取User实体分页）
        Page<User> userPage = baseMapper.selectPage(entityPage, queryWrapper);

        // 6. 转换为VO分页对象
        return convertToVoPage(userPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createNewUser(User user) {
        Long count = lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .count();
        if (count > 0) {
            throw new CustomException("4001", "用户已存在！");
        }
        // 密码加密存储
        user.setPassword(PasswordUtils.encode(user.getPassword()));
        // 软删除标记设置
        user.setIsDeleted(0);
        return userMapper.insert(user);
    }

    @Override
    public boolean checkUsernameAvailability(String username, Long userId) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 精准匹配用户名
        userLambdaQueryWrapper.eq(User::getUsername, username);
        if (userId != null) {
            userLambdaQueryWrapper.ne(User::getUserId, userId);
        }
        // 大于0表示不可用
        return !(count(userLambdaQueryWrapper) > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(User user) {
        return userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelUser(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            StaticLog.warn("批量删除传入空ID列表");
            return 0;
        }

        return userMapper.update(new User(),
                Wrappers.<User>lambdaUpdate()
                        .set(User::getIsDeleted, 1)
                        .in(User::getUserId, ids)
        );
    }

    /**
     * 转换分页对象方法
     */
    private Page<UserShowInfoVo> convertToVoPage(Page<User> entityPage) {
        // 创建VO分页对象
        Page<UserShowInfoVo> voPage = new Page<>();
        BeanUtils.copyProperties(entityPage, voPage, "records");

        // 批量获取角色名称
        Map<Integer, String> roleNameMap = getRoleNames(entityPage.getRecords());

        // 转换记录（手动设置roleName）
        List<UserShowInfoVo> voRecords = entityPage.getRecords().stream()
                .map(user -> {
                    UserShowInfoVo vo = userToShowInfoVoMapper.entityToShowVo(user);
                    vo.setRoleName(roleNameMap.get(user.getRoleId()));
                    return vo;
                })
                .collect(Collectors.toList());

        voPage.setRecords(voRecords);
        return voPage;
    }

    /**
     * 批量查询角色名称
     */
    private Map<Integer, String> getRoleNames(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyMap();
        }

        // 提取所有角色ID并去重
        Set<Integer> roleIds = users.stream()
                .map(User::getRoleId)
                .collect(Collectors.toSet());

        // 批量查询角色
        List<Role> roles = roleMapper.selectBatchIds(roleIds);

        // 构建角色ID到名称的映射
        return roles.stream()
                .collect(Collectors.toMap(
                        Role::getRoleId,
                        Role::getRoleName,
                        (existing, replacement) -> existing
                ));
    }

}
