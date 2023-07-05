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
package uk.ac.ebi.gdp.intervene.igs4eu.bff.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public class WebSessionServerLogoutHandler implements ServerLogoutHandler {

    @Override
    public Mono<Void> logout(final WebFilterExchange exchange,
                             final Authentication authentication) {
        // Invalidate current session
        return invalidateSession(exchange)
                /*.flatMap(unused -> exchange.getChain().filter(exchange.getExchange()))*/
                .then();
    }

    private Mono<Void> invalidateSession(final WebFilterExchange exchange) {
        return exchange
                .getExchange()
                .getSession()
                .flatMap(WebSession::invalidate);
    }
}
