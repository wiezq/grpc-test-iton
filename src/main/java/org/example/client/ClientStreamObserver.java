package org.example.client;

import com.example.grpc.numbers.NumberResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientStreamObserver implements StreamObserver<NumberResponse> {

    private final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);

    private int value = 0;

    @Override
    public void onNext(NumberResponse numberResponse) {
        log.info("Received value: {}", numberResponse.getValue());
        setValue(numberResponse.getValue());
    }

    private synchronized void setValue(int value) {
        this.value = value;
    }

    public synchronized int getValue() {
        int returnValue = this.value;
        this.value = 0;
        return returnValue;
    }
    @Override
    public void onError(Throwable t) {
        log.info("Error: {}", t.getMessage());
    }

    @Override
    public void onCompleted() {
        log.info("request completed!");
    }
}
