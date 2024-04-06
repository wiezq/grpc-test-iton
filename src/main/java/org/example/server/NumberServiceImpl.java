package org.example.server;

import com.example.grpc.numbers.NumberRequest;
import com.example.grpc.numbers.NumberResponse;
import com.example.grpc.numbers.NumbersServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {
    public static final Logger log = LoggerFactory.getLogger(NumberServiceImpl.class.getName());

    @Override
    public void number(NumberRequest request, StreamObserver<NumberResponse> responseObserver) {
        log.info("Recieved sequence: firstValue={} lastValue={}", request.getFirstValue(), request.getLastValue());

        AtomicInteger atomicInteger = new AtomicInteger(request.getFirstValue());

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            responseObserver.onNext(NumberResponse.newBuilder().setValue(atomicInteger.incrementAndGet()).build());
            if (atomicInteger.get() == request.getLastValue()) {
                responseObserver.onCompleted();
                executor.shutdown();
                log.info("Sequence completed!");
            }
        }, 0, 2, TimeUnit.SECONDS);



    }
}