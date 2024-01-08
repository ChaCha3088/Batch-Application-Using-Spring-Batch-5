package com.delicious.batch.job.querydsl;

import com.delicious.batch.entity.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QueryDslPagingReaderJobConfig {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job queryDslPagingReaderJob() {
        return new JobBuilder("queryDslPagingReaderJob", jobRepository)
            .start(queryDslPagingReaderJob_step1())
            .build();
    }

    @Bean
    @JobScope
    public Step queryDslPagingReaderJob_step1() {
        return new StepBuilder("queryDslPagingReaderJob_step1", jobRepository)
            .<Article, Article>chunk(60, transactionManager)
            .reader(queryDslPagingReaderJob_reader())
            .processor(queryDslPagingReaderJob_processor())
            .writer(queryDslPagingReaderJob_writer())
            .build();
    }

    @Bean
    public QuerydslPagingItemReader<Article> queryDslPagingReaderJob_reader() {
        return new QuerydslPagingItemReader<>(transactionManager, 60, queryFactory -> queryFactory
            .selectFrom(QArticle.article)
            .where(QArticle.article.b
            .orderBy(QArticle.article.id.asc())
        );
}
