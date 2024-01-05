package com.delicious.crawler.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class BatchConfig {
    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("job", jobRepository)
            .start(step1(null, jobRepository, transactionManager))
            .next(step2(null, jobRepository, transactionManager))
            .build();
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters[requestDate]}") String requestDate, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step", jobRepository)
                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                    throw new IllegalArgumentException("step1에서 실패!");
                }, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step step2(@Value("#{jobParameters[requestDate]}") String requestDate, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step", jobRepository)
            .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                log.info(">>>>> This is Step2");
                log.info(">>>>> requestDate: {}", requestDate);

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
