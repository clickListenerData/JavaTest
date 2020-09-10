package com.hxz.test.login.user.repository;

import com.hxz.test.login.data.common.CommonRepository;
import com.hxz.test.login.user.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CommonRepository<User,String> {
    User findByUserName(String userName);
}
