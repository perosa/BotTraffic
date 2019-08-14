package com.perosa.bot.traffic.http.server.request;

import com.perosa.bot.traffic.core.BotProxyRequest;
import com.perosa.bot.traffic.core.rule.worker.RuleWorkerImpl;
import com.perosa.bot.traffic.core.service.Consumable;
import com.perosa.bot.traffic.http.client.Router;
import com.perosa.bot.traffic.http.client.wrap.Post;
import com.networknt.client.Http2Client;
import io.undertow.client.ClientResponse;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostRequest extends ParentRequest implements Request {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostRequest.class);

    public void handle(HttpServerExchange exchange) {

        try {

            BotProxyRequest request = initBotProxyRequest(exchange);

            Consumable consumable = new RuleWorkerImpl(request).process();

            Post post = initPost(consumable, request);

            ClientResponse clientResponse = new Router().post(post);

            String clientResponseAsString = clientResponse.getAttachment(Http2Client.RESPONSE_BODY);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, getContentType(clientResponse.getResponseHeaders()));
            exchange.getResponseSender().send(clientResponseAsString);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    private BotProxyRequest initBotProxyRequest(HttpServerExchange exchange) {

        BotProxyRequest request = new BotProxyRequest();

        request.setUrl(exchange.getRequestURL());
        request.setHeaders(extractHeaders(exchange));
        request.setBody(extractBody(exchange));

        return request;

    }

    private Post initPost(Consumable consumable, BotProxyRequest request) {
        Post post = new Post(getUrl(consumable.getUrl()), getPath(consumable.getUrl()));

        if (!request.getBody().isEmpty()) {
            post.setBody(request.getBody());
        }
        if (!request.getHeaders().isEmpty()) {
            post.setHeaders(request.getHeaders());
        }

        LOGGER.debug(post.toString());

        return post;
    }

}
