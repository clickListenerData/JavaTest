package com.hxz.test.login.data.service;

import com.hxz.test.login.common.Result;

import java.util.List;

/**
 *  通用 service
 *
 *  @param <V> 实体类Vo
 *  @param <E> 实体类
 *  @param <T> id主键类型
 *
 *  简单的通用的三个 数据库 增删改查方法
 */
public interface CommonService<V,E,T> {

    Result<List<V>> list();

    Result<V> get(T id);

    Result<V> save(V entityVo);

    Result<T> delete(T id);
}
