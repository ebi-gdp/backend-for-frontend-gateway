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
package uk.ac.ebi.gdp.intervene.igs4eu.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.oauth2.client.R2dbcReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@EnableRedisWebSession
public class BFFConfig {

    @Bean
    public WebSessionIdResolver webSessionIdResolver(final WebSessionCookieProperties webSessionCookieProperties) {
        final CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        //resolver.setCookieName("SESSION");
        resolver.addCookieInitializer((builder) -> builder
                .path(webSessionCookieProperties.getPath())
                .sameSite(webSessionCookieProperties.getSameSite())
                .secure(webSessionCookieProperties.isSecure())
                .httpOnly(webSessionCookieProperties.isHttpOnly())
                .domain(webSessionCookieProperties.getDomain())
        );
        return resolver;
    }

    @Bean
    public R2dbcReactiveOAuth2AuthorizedClientService r2dbcReactiveOAuth2AuthorizedClientService(final DatabaseClient databaseClient,
                                                                                                 final ReactiveClientRegistrationRepository reactiveClientRegistrationRepository) {
        return new R2dbcReactiveOAuth2AuthorizedClientService(
                databaseClient,
                reactiveClientRegistrationRepository
        );
    }

    @Bean
    public WebClient webClient(final ReactiveClientRegistrationRepository clientRegistrations,
                               final ServerOAuth2AuthorizedClientRepository authorizedClients,
                               final @Value("${web-client.base-url}") String baseURL) {
        final ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrations,
                        authorizedClients);
        oauth.setDefaultOAuth2AuthorizedClient(true);
        return WebClient.builder()
                .baseUrl(baseURL)
                .filter(oauth)
                .build();
    }

    @ConfigurationProperties(prefix = "web-session.cookie")
    @Bean
    public WebSessionCookieProperties webSessionCookieProperties() {
        return new WebSessionCookieProperties();
    }
}
