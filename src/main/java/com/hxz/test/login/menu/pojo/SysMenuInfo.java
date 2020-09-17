package com.hxz.test.login.menu.pojo;

import lombok.Data;

import java.util.List;

@Data
public class SysMenuInfo {

    private String sysPath;

    private String name;

    private String menuId;

    private List<SysMenuInfo> children;
}
