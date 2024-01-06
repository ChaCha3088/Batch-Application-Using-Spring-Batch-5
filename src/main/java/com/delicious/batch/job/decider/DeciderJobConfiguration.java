package com.delicious.batch.job.decider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeciderJobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job deciderJob() {
        return new JobBuilder("deciderJob", jobRepository)
            .start(startStep())

            .next(decider()) // 홀수 | 짝수 구분

            .from(decider()) // decider의 상태가
                .on(OddDecider.ODD) // ODD라면
                    .to(oddStep()) // oddStep로 간다.

            .from(decider()) // decider의 상태가
                .on(OddDecider.EVEN) // ODD라면
                    .to(evenStep()) // evenStep로 간다.

            .end() // builder 종료
            .build();
    }

    @Bean
    @JobScope
    public Step startStep() {
        return new StepBuilder("startStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> Start!");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @JobScope
    public Step evenStep() {
        return new StepBuilder("evenStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> 짝수입니다.");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @JobScope
    public Step oddStep() {
        return new StepBuilder("oddStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> 홀수입니다.");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new OddDecider();
    }

    public static class OddDecider implements JobExecutionDecider {
        private final static String EVEN = "EVEN";
        private final static String ODD = "ODD";

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            int randomNumber = (int)(Math.random() * 10);

            log.info("랜덤숫자: {}", randomNumber);

            if (randomNumber % 2 == 0) {
                return new FlowExecutionStatus(EVEN);
            } else {
                return new FlowExecutionStatus(ODD);
            }
        }
    }
}
