package com.hxz.test.login.user.service;

import com.hxz.test.login.data.service.CommonServiceImpl;
import com.hxz.test.login.user.pojo.User;
import com.hxz.test.login.user.repository.UserRepository;
import com.hxz.test.login.user.vo.UserVo;
import com.hxz.test.login.utils.CopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service  // 装载 bean  交给 spring 管理
@Transactional  // spring  的 事务管理
public class UserServiceImpl extends CommonServiceImpl<UserVo, User,String> implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserVo findByUserName(String userName) {
        // login  登录时通过这个方法查询 是否存在用户
        User user = userRepository.findByUserName(userName);
        return CopyUtil.copy(user,UserVo.class);
    }

    @Override
    public UserVo changePassword(String userId, String newPassword) {
        User one = userRepository.getOne(userId);
        one.setPassword(newPassword);
        User save = userRepository.save(one);
        return CopyUtil.copy(save,UserVo.class);
    }

    @Override
    public UserVo register(String userName, String password) {
        return null;
    }
}
