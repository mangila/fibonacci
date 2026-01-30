package com.github.mangila.fibonacci.jobrunr.job;

import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.stereotype.Component;

@Component
public class FibonacciProduceJobHandler implements JobRequestHandler<FibonacciProduceJobRequest> {
    @Override
    public void run(FibonacciProduceJobRequest jobRequest) throws Exception {

    }
}
