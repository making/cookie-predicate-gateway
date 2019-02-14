package com.example.demogateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.util.UUID;
import java.util.function.Predicate;

@Component
public class CookieWeightedRoutePredicateFactory extends AbstractRoutePredicateFactory<Object> {

    private final Logger log = LoggerFactory.getLogger(CookieWeightedRoutePredicateFactory.class);

    public CookieWeightedRoutePredicateFactory() {
        super(Object.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Object config) {
        return exchange -> {
            MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
            return this.routeToNewApi(cookies);
        };
    }

    boolean routeToNewApi(MultiValueMap<String, HttpCookie> cookies) {
        HttpCookie b = cookies.getFirst("B");
        if (b == null) {
            log.debug("B cookie is not found.");
            return false;
        }
        int hash;
        try {
            hash = UUID.fromString(b.getValue()).hashCode(); // TODO
        } catch (IllegalArgumentException e) {
            log.info("Illegal cookie value detected : {}", b);
            return false;
        }
        int x = hash % 10;
        log.debug("hash = {}({})", hash, x);
        return x < 5; // hopefully 50%
    }
}
