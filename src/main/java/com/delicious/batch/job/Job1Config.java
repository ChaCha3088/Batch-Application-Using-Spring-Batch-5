package com.delicious.batch.job;

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

@Slf4j
@Configuration
public class Job1Config {
    @Bean
    public Job job1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("job1", jobRepository)
            .start(job1_step1(null, jobRepository, transactionManager))
            .next(job1_step2(null, jobRepository, transactionManager))
            .next(job1_step3(null, jobRepository, transactionManager))
            .build();
    }

    @Bean
    @JobScope
    public Step job1_step1(@Value("#{jobParameters[version]}") String version, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                    log.info(">>>>> This is Job1 Step1");

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step job1_step2(@Value("#{jobParameters[version]}") String version, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
            .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                log.info(">>>>> This is Job1 Step2");

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @JobScope
    public Step job1_step3(@Value("#{jobParameters[version]}") String version, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
            .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                log.info(">>>>> This is Job1 Step3");

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
