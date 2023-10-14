package com.uhu.mvc.util;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class MybatisPlusUtil {

    private final SqlSession sqlSession;

    public MybatisPlusUtil(Environment environment) {
        SqlSessionFactory factory = new MybatisSqlSessionFactoryBuilder().build(new Configuration(environment));
        sqlSession = factory.openSession(true);
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        sqlSession.getConfiguration().addInterceptor(mybatisPlusInterceptor);
        sqlSession.getConfiguration().setCacheEnabled(false);
    }

    public static UtilBuilder build() {
        return new UtilBuilder();
    }



    /**
     * 获取mapper对象
     * @param clazz
     * @param <M>
     * @return
     */
    public <M extends BaseMapper<?>> M getMapper(Class<M> clazz) {
        return sqlSession.getMapper(clazz);
    }

    /**
     * 获取service
     * @param baseMapperClass
     * @param entityClass
     * @param <M>
     * @param <E>
     * @return
     */
    public <M extends BaseMapper<E>, E> ServiceImpl<M, E> getService(Class<M> baseMapperClass, Class<E> entityClass) {
        M mapper = getMapper(baseMapperClass);
        ServiceImpl<M, E> service = new ServiceImpl<>();
        try {
            Field baseMapperF = service.getClass().getDeclaredField("baseMapper");
            baseMapperF.setAccessible(true);
            baseMapperF.set(service, mapper);

            Field mapperClass = service.getClass().getDeclaredField("mapperClass");
            mapperClass.setAccessible(true);
            mapperClass.set(service, baseMapperClass);

            Field entityClassF = service.getClass().getDeclaredField("entityClass");
            entityClassF.setAccessible(true);
            entityClassF.set(service, entityClass);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return service;
    }

    @Accessors(chain = true)
    @Data
    static class UtilBuilder {

        private String id = IdUtil.fastUUID();
        private TransactionFactory transactionFactory;
        private DataSourceFactory dataSourceFactory = new PooledDataSourceFactory();

        private String url;
        private String driver;
        private String username;
        private String password;

        public UtilBuilder setTransactionFactory(Class<? extends TransactionFactory> transactionFactoryClass) {
            try {
                transactionFactory = transactionFactoryClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return this;
        }

        public MybatisPlusUtil build() {
            Properties properties = new Properties();
            properties.setProperty("driver", driver);
            properties.setProperty("url", url);
            properties.setProperty("username", username);
            properties.setProperty("password", password);
            dataSourceFactory.setProperties(properties);
            Environment environment = new Environment(id, transactionFactory, dataSourceFactory.getDataSource());
            return new MybatisPlusUtil(environment);
        }
    }
}

