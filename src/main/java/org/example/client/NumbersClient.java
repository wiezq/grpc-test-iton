package org.example.client;

import com.example.grpc.numbers.NumberRequest;
import com.example.grpc.numbers.NumbersServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


public class NumbersClient {

    private static final Logger log = LoggerFactory.getLogger(NumbersClient.class);

    private static final int FROM = 0;
    private static final int TO = 30;
    private static final int LOOP_COUNT = 50;

    public static void main(String[] args) {
        // Create a channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8081)
                .usePlaintext()
                .build();

        // Create a stub
        NumbersServiceGrpc.NumbersServiceStub stub = NumbersServiceGrpc.newStub(channel);

        // Create a request
        NumberRequest request = NumberRequest.newBuilder()
                .setFirstValue(FROM)
                .setLastValue(TO)
                .build();

        // Create a client stream observer
        ClientStreamObserver clientStreamObserver = new ClientStreamObserver();

        // Call the service
        stub.number(request, clientStreamObserver);

        // Create a scheduled executor
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        // Create a value and count
        AtomicInteger value = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(0);


        executor.scheduleAtFixedRate(() -> {
            if (count.incrementAndGet() >  LOOP_COUNT){
                System.out.println("Final value: " + value.get());
                executor.shutdown();
            } else {
                value.set(value.get() + clientStreamObserver.getValue() + 1);
                log.info("Current value: {}", value.get());
            }

        }, 0, 1, java.util.concurrent.TimeUnit.SECONDS);

        channel.shutdown();
    }



}