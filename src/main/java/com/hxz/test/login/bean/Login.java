package com.hxz.test.login.bean;

import lombok.Data;

@Data
public class Login {

    private String access_token;  // 短token
    private String refresh_token;  // 长token
    private Long accessExprTime;  // 短token 过期时间
    private Long refreshExprTime; // 长token 过期时间

    public Login(String access_token,String refresh_token) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }
}
