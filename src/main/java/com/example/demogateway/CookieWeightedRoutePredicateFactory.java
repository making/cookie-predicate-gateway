package com.example.demogateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Component
public class CookieWeightedRoutePredicateFactory extends AbstractRoutePredicateFactory<CookieWeightedRoutePredicateFactory.Config> {

    private final Logger log = LoggerFactory.getLogger(CookieWeightedRoutePredicateFactory.class);

    public CookieWeightedRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("weight");
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> {
            MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
            return this.routeToNewApi(cookies, config);
        };
    }

    boolean routeToNewApi(MultiValueMap<String, HttpCookie> cookies, Config config) {
        HttpCookie b = cookies.getFirst("B");
        if (b == null) {
            log.debug("B cookie is not found.");
            return false;
        }
        int hash;
        try {
            hash = Math.abs(UUID.fromString(b.getValue()).hashCode()); // TODO
        } catch (IllegalArgumentException e) {
            log.info("Illegal cookie value detected : {}", b);
            return false;
        }
        int x = hash % 10;
        log.debug("hash = {}({})", hash, x);
        return config.isOk(x);
    }

    static class Config {

        private double weight; // 0.0-1.0

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = Math.min(Math.max(weight, 0.0), 1.0);
        }

        /**
         * @param n 0-9
         */
        public boolean isOk(int n) {
            return n < this.weight * 10;
        }
    }
}
