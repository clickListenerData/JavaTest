package com.hxz.test.login.utils;

/**
 * token  黑名单
 * token  如果是 允许多处登录  则 只要 客户端 做好 退出登录时删除token的存储  就可以了
 * 单登录  一个user  对应  一个token
 */
public class TokenBackList {

    /**
     * 方案一：  预黑名单
     */
    // 存储 token 的 刷新时间  即  之前时间创建的token 都会失效  同时 只允许单登录。
    // userId ： key   refreshTime ： value


    // 该 类中  保存  每一次user登录 时 的 刷新token 过期时间，可以用来做  移除 以前生成的token

    // 第一次登录时  无需保存，因为 token 只有一份。

    // 过期时间 到了之后  可以移除相应的数据

    /**
     * 方案二：
     */
    // token 存储 数据库中  则 拿到userId 后  用数据库中的 token 判断 是否有效

    /**
     * 方案三
     */
    // 登录时  生成token  加一个标识符 并 把标识符存储再 数据库中。
    // 登出时  修改数据库中的标识符
    // 校验token时 同时校验标识符


    /**
     * 总结 ： token 需要保存一份 或是原样  或是修改的时间，以其判断token 是否有效
     * 最终一定是存储了 某个跟token相关的值  才能做到 token 失效的处理逻辑
     */

}
