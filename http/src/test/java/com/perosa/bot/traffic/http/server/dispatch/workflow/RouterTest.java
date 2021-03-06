package com.perosa.bot.traffic.http.server.dispatch.workflow;

import com.perosa.bot.traffic.http.client.Forwarder;
import com.perosa.bot.traffic.http.client.ForwarderResponse;
import com.perosa.bot.traffic.http.client.wrap.Get;
import com.perosa.bot.traffic.http.client.wrap.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouterTest {

    @Mock
    private Forwarder forwarderMock;

    @Test
    void get() throws Exception {

        when(forwarderMock.get(isA(Get.class))).thenReturn(new ForwarderResponse());

        Router router = new Router(forwarderMock);

        Get input = new Get("http://localhost:9999", "/svc1");

        ForwarderResponse response = router.get(input);

        assertNotNull(response);
        verify(forwarderMock, times(1)).get(isA(Get.class));
    }

    @Test
    void post() throws Exception {

        when(forwarderMock.post(isA(Post.class))).thenReturn(new ForwarderResponse());

        Router router = new Router(forwarderMock);

        Post input = new Post("http://localhost:9999", "/svc1");

        ForwarderResponse response = router.post(input);

        assertNotNull(response);
        verify(forwarderMock, times(1)).post(isA(Post.class));
    }

    @Test
    void sanitizeHttpUrl() {
        Router router = new Router(forwarderMock);

        assertEquals("host_perosa_com", router.sanitize("http://host.perosa.com"));
    }

    @Test
    void sanitizeHttpsUrl() {
        Router router = new Router(forwarderMock);

        assertEquals("host_perosa_com", router.sanitize("https://host.perosa.com"));
    }

}