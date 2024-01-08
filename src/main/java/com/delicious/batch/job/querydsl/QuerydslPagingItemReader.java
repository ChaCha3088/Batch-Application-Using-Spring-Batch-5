package com.delicious.batch.job.querydsl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.batch.item.database.orm.JpaQueryProvider;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class QuerydslPagingItemReader<T> extends AbstractPagingItemReader<T> {
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private final Map<String, Object> jpaPropertyMap = new HashMap();
    private String queryString;
    private JpaQueryProvider queryProvider;
    private Map<String, Object> parameterValues;
    private boolean transacted = true;
    private Function<JPAQueryFactory, JPAQuery<T>> queryFunction;

    protected QuerydslPagingItemReader() {
        setName(ClassUtils.getShortName(QuerydslPagingItemReader.class));
    }

    public QuerydslPagingItemReader(EntityManagerFactory entityManagerFactory, int pageSize, Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        this();

        this.entityManagerFactory = entityManagerFactory;
        this.queryFunction = queryFunction;

        setPageSize(pageSize);
    }

    protected JPAQuery<T> createQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        return queryFunction.apply(queryFactory);
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setParameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (this.queryProvider == null) {
            Assert.state(this.entityManagerFactory != null, "EntityManager is required when queryProvider is null");
            Assert.state(StringUtils.hasLength(this.queryString), "Query string is required when queryProvider is null");
        }

    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setQueryProvider(JpaQueryProvider queryProvider) {
        this.queryProvider = queryProvider;
    }

    protected void doOpen() throws Exception {
        super.doOpen();
        this.entityManager = this.entityManagerFactory.createEntityManager(this.jpaPropertyMap);
        if (this.entityManager == null) {
            throw new DataAccessResourceFailureException("Unable to obtain an EntityManager");
        } else {
            if (this.queryProvider != null) {
                this.queryProvider.setEntityManager(this.entityManager);
            }

        }
    }

    protected void doReadPage() {
        EntityTransaction tx = null;
        if (this.transacted) {
            this.entityManager.clear();
        }

        JPAQuery<T> query = this.createQuery().offset(this.getPage() * this.getPageSize()).limit(this.getPageSize());

        if (this.results == null) {
            this.results = new CopyOnWriteArrayList();
        } else {
            this.results.clear();
        }

        if (!this.transacted) {
            List<T> queryResult = query.fetch();
            Iterator var7 = queryResult.iterator();

            while(var7.hasNext()) {
                T entity = (T) var7.next();
                this.entityManager.detach(entity);
                this.results.add(entity);
            }
        } else {
            this.results.addAll(query.fetch());
        }

    }

    protected void doClose() throws Exception {
        this.entityManager.close();
        super.doClose();
    }
}
