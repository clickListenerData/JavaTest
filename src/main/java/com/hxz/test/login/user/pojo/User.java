package com.hxz.test.login.user.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "user")  // 数据库 对应的表名
@Data  // lombok  插件 自动为所有属性生成 setter/getter 方法
public class User {



    @Id  // 主键
    @Column(name = "userId",nullable = false)
    private String userId;
    @Column(name = "userName")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "createTime")
    private Date createTime;
}
