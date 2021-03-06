package com.perosa.bot.traffic.http.client;

import com.perosa.bot.traffic.http.client.wrap.Get;
import com.perosa.bot.traffic.http.client.wrap.Post;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class JavaClientImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaClientImplTest.class);

    private static Undertow server = null;

    @BeforeAll
    static void start() {
        buildAndStartServer(8899, "localhost");
    }

    @AfterAll
    static void stop() {
        server.stop();
    }

    @Test
    void get() throws Exception {

        JavaClientImpl client = new JavaClientImpl();

        Get input = new Get("http://localhost:8899", "/");
        input.setHeaders(Map.ofEntries(entry("User-Agent", "facebook"), entry("Content-Type", "application/json")));
        input.setParameters(Map.ofEntries(entry("param1", new String[]{"val1"})));

        ForwarderResponse response = client.get(input);

        assertNotNull(response);
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
    }

    @Test
    void post() throws Exception {

        JavaClientImpl client = new JavaClientImpl();

        Post input = new Post("http://localhost:8899", "/");
        input.setHeaders(Map.ofEntries(entry("User-Agent", "facebook"), entry("Content-Type", "application/json")));
        input.setBody("payload");

        ForwarderResponse response = client.post(input);

        assertNotNull(response);
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("Ok", response.getBody());
        assertEquals("2", response.getContentLength());
    }


    private static void buildAndStartServer(int port, String host) {
        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        LOGGER.info("Ok");
                        exchange.setStatusCode(200);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send("Ok");
                    }
                })
                .build();
        server.start();
    }

}