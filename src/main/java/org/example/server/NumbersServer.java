package org.example.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class NumbersServer {

    private final Logger log = LoggerFactory.getLogger(NumbersServer.class);

    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        NumbersServer numbersServer = new NumbersServer();
        numbersServer.start();
        numbersServer.blockUntilShutdown();
    }

    public void start() throws IOException, InterruptedException {
        log.info("Starting server...");
        server = ServerBuilder.forPort(8081)
                .addService(new NumberServiceImpl())
                .build()
                .start();
        log.info("Server started!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            try {
                this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("Server shut down");
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
