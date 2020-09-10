package com.hxz.test.login.data.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 *
 * JpaSpecificationExecutor 复杂条件查询  分页查询  Papgable
 *
 * @param <E>  返回结果的实体类
 * @param <T>  查询 条件  一般为 id
 */
@NoRepositoryBean  // 该注解 告诉JPA不要创建对应接口的bean对象
public interface CommonRepository<E,T> extends JpaRepository<E,T>, JpaSpecificationExecutor<E> {
}
