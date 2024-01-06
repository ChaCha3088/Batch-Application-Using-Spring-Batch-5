package com.delicious.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import static org.springframework.batch.core.BatchStatus.FAILED;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConditionalJobConfig {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job conditionalJob1() {
        return new JobBuilder("conditionalJob1", jobRepository)
            .start(conditionalJob1Step1())
                .on(FAILED.toString()) // FAILED 일 경우
                .to(conditionalJob1Step3()) // step3으로 이동한다.
                .on("*") // step3의 결과 관계 없이
                .end() // step3으로 이동하면 Flow가 종료된다.

            .from(conditionalJob1Step1()) // step1로부터
                .on("*") // FAILED 외에 모든 경우
                .to(conditionalJob1Step2()) // step2로 이동한다.
                .next(conditionalJob1Step3()) // step2가 정상 종료되면 step3으로 이동한다.
                .on("*") // step3의 결과 관계 없이
                .end() // step3으로 이동하면 Flow가 종료된다.
            .end() // Job 종료

            .build();
    }

    @Bean
    @JobScope
    public Step conditionalJob1Step1() {
        return new StepBuilder("conditionalJob1Step1", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> This is conditionalJob1Step1");

                /**
                 ExitStatus를 FAILED로 지정한다.
                 해당 status를 보고 flow가 진행된다.
                 **/
                contribution.setExitStatus(ExitStatus.FAILED);

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @JobScope
    public Step conditionalJob1Step2() {
        return new StepBuilder("conditionalJobStep2", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> This is conditionalJob1Step2");

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @JobScope
    public Step conditionalJob1Step3() {
        return new StepBuilder("conditionalJob1Step3", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> This is conditionalJob1Step3");

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
