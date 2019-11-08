package com.perosa.bot.traffic.http.server.dispatch.request;

import com.perosa.bot.traffic.core.BotProxyRequest;
import com.perosa.bot.traffic.core.rule.RuleWorkflow;
import com.perosa.bot.traffic.core.rule.worker.RuleAnalyzer;
import com.perosa.bot.traffic.core.rule.worker.RuleWorker;
import com.perosa.bot.traffic.core.rule.worker.RuleWorkerImpl;
import com.perosa.bot.traffic.core.service.Consumable;
import com.perosa.bot.traffic.core.service.ConsumableService;
import com.perosa.bot.traffic.http.client.Forwarder;
import com.perosa.bot.traffic.http.client.ForwarderResponse;
import com.perosa.bot.traffic.http.client.wrap.Get;
import com.perosa.bot.traffic.http.server.dispatch.workflow.Router;
import com.perosa.bot.traffic.http.server.dispatch.workflow.Shadower;
import io.undertow.io.BlockingSenderImpl;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.cache.ResponseCachingSender;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRequestTest {

    @Mock
    HttpServerExchange exchange;

    @Mock
    Forwarder forwarder;

    @Mock
    RuleWorker ruleWorker;

    @Mock
    Router router;

    @Mock
    Shadower shadower;

    @Test
    void initBotProxyRequest() {

        when(exchange.getRequestURL()).thenReturn("http://localhost");
        when(exchange.getQueryString()).thenReturn("user=perosa&location=Amsterdam");

        HeaderMap headerMap = new HeaderMap();
        headerMap.add(new HttpString("host"), "localhost");
        headerMap.add(new HttpString("authorization"), "bldfsdf$%45");
        when(exchange.getRequestHeaders()).thenReturn(headerMap);

        BotProxyRequest request = new GetRequest().initBotProxyRequest(exchange);

        assertNotNull(request);
        assertEquals("http://localhost?user=perosa&location=Amsterdam", request.getUrl());
    }

    @Test
    void initGet() {

        BotProxyRequest request = new BotProxyRequest();
        request.setBody("body");
        RuleWorkerImpl mgr = new RuleWorkerImpl();
        mgr.setRuleAnalyzer(new RuleAnalyzer(request));


        Get get = new GetRequest().initGet(getRoutingConsumable(), request);

        assertNotNull(get);
        assertEquals("http://localhost:9090", get.getUrl());
        assertEquals("/path?param=value", get.getPath());
    }

    @Test
    void handle() throws Exception {

        when(exchange.getRequestURL()).thenReturn("http://localhost/svc1");
        when(exchange.getQueryString()).thenReturn("user=perosa&location=Amsterdam");

        HeaderMap headerMap = new HeaderMap();
        headerMap.add(new HttpString("host"), "localhost");
        headerMap.add(new HttpString("authorization"), "bldfsdf$%45");
        when(exchange.getRequestHeaders()).thenReturn(headerMap);

        when(ruleWorker.process(isA(BotProxyRequest.class))).thenReturn(getRoutingConsumable());

        when(router.get(isA(Get.class))).thenReturn(new ForwarderResponse());

        new GetRequest(router, shadower, ruleWorker).handle(exchange);

        verify(router, times(1)).get(isA(Get.class));
        verify(shadower, times(0)).get(isA(Get.class));
        verify(ruleWorker, times(1)).process(isA(BotProxyRequest.class));

    }

    @Test
    void handleWithShadowing() throws Exception {

        when(exchange.getRequestURL()).thenReturn("http://localhost/svc1");
        when(exchange.getQueryString()).thenReturn("user=perosa&location=Amsterdam");

        HeaderMap headerMap = new HeaderMap();
        headerMap.add(new HttpString("host"), "localhost");
        headerMap.add(new HttpString("authorization"), "bldfsdf$%45");
        when(exchange.getRequestHeaders()).thenReturn(headerMap);

        when(ruleWorker.process(isA(BotProxyRequest.class))).thenReturn(getShadowingConsumable());

        new GetRequest(router, shadower, ruleWorker).handle(exchange);

        verify(router, times(0)).get(isA(Get.class));
        verify(shadower, times(1)).get(isA(Get.class));
        verify(ruleWorker, times(1)).process(isA(BotProxyRequest.class));

    }

    private Consumable getRoutingConsumable() {
        ConsumableService consumable = new ConsumableService("01", "localhost", 9090);
        consumable.setUrl("http://localhost:9090/path?param=value");
        consumable.setWorkflow(RuleWorkflow.ROUTE);

        return consumable;
    }

    private Consumable getShadowingConsumable() {
        ConsumableService consumable = new ConsumableService("01", "localhost", 9090);
        consumable.setUrl("http://localhost:9090/path?param=value");
        consumable.setWorkflow(RuleWorkflow.SHADOW);

        return consumable;
    }

}