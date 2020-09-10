package com.hxz.test.login.data.service;

import com.hxz.test.login.common.Result;
import com.hxz.test.login.data.common.CommonRepository;
import com.hxz.test.login.utils.CopyUtil;
import com.hxz.test.login.utils.UUIDUtil;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CommonServiceImpl<V,E,T> implements CommonService<V,E,T> {

    private Class<V> entityVoClass;//实体类Vo

    private Class<E> entityClass;//实体类

    @Autowired
    private CommonRepository<E, T> commonRepository;//注入实体类仓库

    public CommonServiceImpl() {
        Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        this.entityVoClass = (Class<V>) types[0];
        this.entityClass = (Class<E>) types[1];
    }


    @Override
    public Result<V> get(T id) {
        Optional<E> optionalE = commonRepository.findById(id);
        if (!optionalE.isPresent()) {
            return Result.custom(100,"ID不存在！",null);
        }
        return Result.success(CopyUtil.copy(optionalE.get(), entityVoClass));
    }

    @Override
    public Result<V> save(V entityVo) {
        E copy = CopyUtil.copy(entityVo, entityClass);
        E save = commonRepository.save(copy);
        return Result.success(CopyUtil.copy(save,entityVoClass));
    }

    @Override
    public Result<T> delete(T id) {
        commonRepository.deleteById(id);
        return Result.success(id);
    }

    @Override
    public Result<List<V>> list() {
        List<E> all = commonRepository.findAll();
        List<V> vs = CopyUtil.copyList(all, entityVoClass);
        return Result.success(vs);
    }
}
