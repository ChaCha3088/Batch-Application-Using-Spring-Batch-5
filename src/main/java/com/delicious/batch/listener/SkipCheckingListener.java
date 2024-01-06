package com.delicious.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import static org.springframework.batch.core.ExitStatus.FAILED;

public class SkipCheckingListener implements StepExecutionListener {
    public static final String skipCheck = "SKIP WHEN SUCCESS";

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String exitCode = stepExecution.getExitStatus().getExitCode();

        if (!exitCode.equals(FAILED.getExitCode()) && stepExecution.getSkipCount() > 0) {
            return new ExitStatus("SKIP WHEN SUCCESS");
        }
        else {
            return null;
        }
    }
}
