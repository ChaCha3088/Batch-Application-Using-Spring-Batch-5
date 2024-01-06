package com.delicious.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import static org.springframework.batch.core.ExitStatus.FAILED;

public class SkipListener implements StepExecutionListener {
    public static final String skip = "SKIP";

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String exitCode = stepExecution.getExitStatus().getExitCode();

        // 실패한게 아니라면
        if (!exitCode.equals(FAILED.getExitCode())) {
            return new ExitStatus(skip);
        }
        else {
            return null;
        }
    }
}
