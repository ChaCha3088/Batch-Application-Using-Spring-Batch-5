package com.delicious.batch.job.scope;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ScopeJobConfig {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job scopeJob() {
        return new JobBuilder("scopeJob", jobRepository)
            .start(scopeStep1(null))
            .next(scopeStep2())
            .build();
    }

    @Bean
    @JobScope
    public Step scopeStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return new StepBuilder("scopeStep1", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> This is scopeStep1");
                log.info(">>>>> requestDate = {}", requestDate);

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @JobScope
    public Step scopeStep2() {
        return new StepBuilder("scopeStep2", jobRepository)
            .tasklet(scopeStep2Tasklet(null), transactionManager)
            .build();
    }

    @Bean
    @StepScope
    public Tasklet scopeStep2Tasklet(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return (contribution, chunkContext) -> {
            log.info(">>>>> This is scopeStep2");
            log.info(">>>>> requestDate = {}", requestDate);

            return RepeatStatus.FINISHED;
        };
    }
}
