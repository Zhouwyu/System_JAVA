package com.geekzhou.crm.mapper.mapstruct;

import com.geekzhou.crm.entity.User;
import com.geekzhou.crm.vo.UserShowInfoVo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-19T10:26:32+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17 (Oracle Corporation)"
)
@Component
public class UserToShowInfoVoMapperImpl implements UserToShowInfoVoMapper {

    @Override
    public UserShowInfoVo entityToShowVo(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserShowInfoVo userShowInfoVo = new UserShowInfoVo();

        if ( entity.getUserId() != null ) {
            userShowInfoVo.setUserId( entity.getUserId().intValue() );
        }
        if ( entity.getUsername() != null ) {
            userShowInfoVo.setUserName( entity.getUsername() );
        }
        if ( entity.getFullName() != null ) {
            userShowInfoVo.setFullName( entity.getFullName() );
        }
        if ( entity.getMobile() != null ) {
            userShowInfoVo.setPhone( entity.getMobile() );
        }
        if ( entity.getEmail() != null ) {
            userShowInfoVo.setEmail( entity.getEmail() );
        }
        if ( entity.getStatus() != null ) {
            userShowInfoVo.setStatus( entity.getStatus().name() );
        }
        if ( entity.getRoleId() != null ) {
            userShowInfoVo.setRoleId( entity.getRoleId() );
        }

        return userShowInfoVo;
    }

    @Override
    public List<UserShowInfoVo> entityToShowVoList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserShowInfoVo> list = new ArrayList<UserShowInfoVo>( users.size() );
        for ( User user : users ) {
            list.add( entityToShowVo( user ) );
        }

        return list;
    }
}
