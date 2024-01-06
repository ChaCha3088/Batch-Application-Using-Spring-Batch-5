package com.delicious.batch.job.customexitcode;

import static org.springframework.batch.core.BatchStatus.FAILED;

import com.delicious.batch.listener.SkipListener;
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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConditionalJobWithCustomExitCodeConfig {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job conditionalJobWithCustomExitCode1() {
        return new JobBuilder("conditionalJobWithCustomExitCode1", jobRepository)
            .start(conditionalJobWithCustomExitCode1Step1())
                .on(FAILED.toString()) // FAILED 일 경우
                    .to(errorPrint1()) // errorPrint1로 이동한다.
                        .on("*") // errorPrint1의 결과 관계 없이
                            .end() // errorPrint1으로 이동하면 Flow가 종료된다.

            .from(conditionalJobWithCustomExitCode1Step1()) // conditionalJobWithCustomExitCode1Step1로부터
                .on(SkipListener.skip) // skip 일 경우
                .to(conditionalJobWithCustomExitCode1Step3()) // conditionalJobWithCustomExitCode1Step3로 이동한다.
                .on("*") // conditionalJobWithCustomExitCode1Step3의 결과 관계 없이
                .end() // Flow가 종료된다.

            .from(conditionalJobWithCustomExitCode1Step1()) // conditionalJobWithCustomExitCode1Step1로부터
                .on("*") // 모든 경우에
                    .to(conditionalJobWithCustomExitCode1Step2()) // conditionalJobWithCustomExitCode1Step2로 이동한다.

            .next(conditionalJobWithCustomExitCode1Step3()) // 다음으로 conditionalJobWithCustomExitCode1Step3로 이동한다.
                .on("*") // conditionalJobWithCustomExitCode1Step3의 결과 관계 없이
                    .end() // Flow가 종료된다.

            .end() // Job 종료

            .build();
    }

    @Bean
    @JobScope
    public Step conditionalJobWithCustomExitCode1Step1() {
        return new StepBuilder("conditionalJobWithCustomExitCode1Step1", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> This is conditionalJobWithCustomExitCode1Step1");

                contribution.setExitStatus(new ExitStatus(SkipListener.skip));
//                contribution.setExitStatus(ExitStatus.FAILED);

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @JobScope
    public Step conditionalJobWithCustomExitCode1Step2() {
        return new StepBuilder("conditionalJobWithCustomExitCode1Step2", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> This is conditionalJobWithCustomExitCode1Step2");

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @JobScope
    public Step conditionalJobWithCustomExitCode1Step3() {
        return new StepBuilder("conditionalJobWithCustomExitCode1Step3", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> This is conditionalJobWithCustomExitCode1Step3");

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    @JobScope
    public Step errorPrint1() {
        return new StepBuilder("errorPrint1", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> This is errorPrint1");

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
