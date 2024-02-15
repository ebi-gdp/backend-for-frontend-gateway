/*
 *
 * Copyright 2022 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.gdp.intervene.igs4eu.bff.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class StripBasePathGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Value("${spring.webflux.base-path}")
    private String basePath;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            final ServerHttpRequest serverHttpRequest = exchange.getRequest();
            final String path = serverHttpRequest.getURI().getRawPath();
            final String newPath = path.replaceFirst(basePath, "");

            final ServerHttpRequest modifiedServerHttpRequest = serverHttpRequest
                    .mutate()
                    .path(newPath)
                    .contextPath(null)
                    .build();
            return chain
                    .filter(exchange
                            .mutate()
                            .request(modifiedServerHttpRequest)
                            .build());
        };
    }
}
