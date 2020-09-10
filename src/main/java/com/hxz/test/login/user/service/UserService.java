package com.hxz.test.login.user.service;

import com.hxz.test.login.data.service.CommonService;
import com.hxz.test.login.user.pojo.User;
import com.hxz.test.login.user.vo.UserVo;

public interface UserService extends CommonService<UserVo, User,String> {

    UserVo findByUserName(String userName);

    UserVo register(String userName,String password);

    UserVo changePassword(String userId,String newPassword);
}
