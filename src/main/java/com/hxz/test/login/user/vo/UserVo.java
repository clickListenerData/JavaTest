package com.hxz.test.login.user.vo;

import com.hxz.test.login.utils.UserUtils;
import lombok.Data;

import java.util.Date;

@Data
public class UserVo {

    private String userId;

    private String userName;
    private String password;
    private Date createTime;

    public UserVo() {}

    public UserVo(String userName,String password) {
        this.userName = userName;
        this.password = password;
        this.userId = UserUtils.buildUserId();
        createTime = new Date(System.currentTimeMillis());
    }
}
