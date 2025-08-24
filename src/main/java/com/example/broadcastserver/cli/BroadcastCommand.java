package com.example.broadcastserver.cli;

import com.example.broadcastserver.BroadcastserverApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.socket.WebSocketHandler;
import picocli.CommandLine;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

@CommandLine.Command(name = "broadcast-server", mixinStandardHelpOptions = true, version = "1.0",
        description = "Start or connect to the broadcast server.")
public class BroadcastCommand implements Runnable {
    @CommandLine.Option(names = {"-p", "--port"}, description = "Port")
    private int port = 8080;

    @CommandLine.Parameters(index = "0", description = "Action: start or connect")
    private String action;

    @Override
    public void run () {
        if (this.action.equals ("start")) {
            SpringApplication.run (BroadcastserverApplication.class);
        } else if (this.action.equals ("connect")) {
            connectClient ();
        } else {
            System.err.println ("Invalid action");
        }
    }

    private void connectClient() {
        String url = "ws://localhost:" + port + "/chat";

        java.util.concurrent.CountDownLatch exitLatch = new java.util.concurrent.CountDownLatch (1);
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient ();

        java.util.concurrent.CompletableFuture<java.net.http.WebSocket> wsFuture =
                client.newWebSocketBuilder ()
                        .buildAsync (java.net.URI.create (url), new java.net.http.WebSocket.Listener () {
                            @Override
                            public void onOpen (java.net.http.WebSocket webSocket) {
                                System.out.println ("Connected to " + url);
                                System.out.println ("Type messages and press Enter");
                                System.out.println ("Type 'exit' to quit");
                                webSocket.request (1);
                            }

                            @Override
                            public java.util.concurrent.CompletionStage <?> onText (java.net.http.WebSocket webSocket, CharSequence data, boolean last) {
                                System.out.println ("Received: " + data);
                                webSocket.request (1);
                                return null;
                            }

                            @Override
                            public java.util.concurrent.CompletionStage <?> onClose (java.net.http.WebSocket webSocket, int statusCode, String reason) {
                                System.out.println ("Disconnected");
                                exitLatch.countDown( );
                                return java.util.concurrent.CompletableFuture.completedFuture (null);
                            }

                            @Override
                            public void onError (java.net.http.WebSocket webSocket, Throwable error) {
                                System.err.println( "WebSocket error: " + error.getMessage ());
                                exitLatch.countDown ();
                            }
                        });

        java.net.http.WebSocket ws = wsFuture.join ();

        try (java.io.BufferedReader reader = new java.io.BufferedReader (new java.io.InputStreamReader (System.in))) {
            String line;
            while ((line = reader.readLine ()) != null) {
                if ("exit".equalsIgnoreCase (line)) {
                    ws.sendClose (java.net.http.WebSocket.NORMAL_CLOSURE, "bye").join ();
                    break;
                }
                ws.sendText (line, true).join ();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace ();
        }

        try {
            exitLatch.await ();
        } catch (InterruptedException e) {
            Thread.currentThread ().interrupt ();
        }
    }

}
