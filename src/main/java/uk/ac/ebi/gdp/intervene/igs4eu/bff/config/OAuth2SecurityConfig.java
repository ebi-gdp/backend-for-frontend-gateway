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
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import uk.ac.ebi.gdp.intervene.igs4eu.bff.handler.WebSessionServerLogoutHandler;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@EnableWebFluxSecurity
public class OAuth2SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http,
                                                            final @Value("${spring.security.oauth2.client.provider.elixir.success-url}") String successRedirectURL,
                                                            final @Value("${spring.security.oauth2.client.logout-uri}") String logoutURI,
                                                            final @Value("${web-client.base-url}") String baseURL) throws URISyntaxException {
        final RedirectServerLogoutSuccessHandler rslSuccessHandler = new RedirectServerLogoutSuccessHandler();
        rslSuccessHandler.setLogoutSuccessUrl(new URI(baseURL));

        // @formatter:off
        return http
                .cors().disable()
                .csrf().disable()
                   .authorizeExchange()
                   .anyExchange()
                   .authenticated()
                .and()
                   .oauth2Login()
                   .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler(successRedirectURL))
                .and()
                   .logout()
                   .logoutUrl(logoutURI)
                   .logoutHandler(new WebSessionServerLogoutHandler())
                   .logoutSuccessHandler(rslSuccessHandler)
                .and()
                   .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(UNAUTHORIZED)))
                .build();
        // @formatter:on
    }
}
