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

    private static final String actuatorEndpoint = "/actuator/health";

    @Bean
    public SecurityWebFilterChain springSecurityFilterChainForDev(final ServerHttpSecurity http,
                                                                  @Value("${spring.security.oauth2.client.provider.elixir.success-url}") final String successRedirectURL,
                                                                  @Value("${spring.security.oauth2.client.logout-uri}") final URI logoutURI,
                                                                  @Value("${web-client.base-url}") final String baseURL,
                                                                  @Value("${whitelist.uri}") final URI whiteListURI) throws URISyntaxException {
        final ServerHttpSecurity serverHttpSecurity = configureMatchers(http, whiteListURI.getPath(), actuatorEndpoint);
        return securityConfig(serverHttpSecurity,
                successRedirectURL,
                redirectServerLogoutSuccessHandler(baseURL),
                logoutURI.getPath())
                .build();
    }

    private ServerHttpSecurity configureMatchers(final ServerHttpSecurity http,
                                                 final String... whiteListURI) {
        return corsCsrfConfig(http)
                .authorizeExchange()
                .pathMatchers(whiteListURI)
                .permitAll()
                .and();
    }

    private ServerHttpSecurity corsCsrfConfig(final ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity
                .cors().disable()
                .csrf().disable();
    }

    private ServerHttpSecurity securityConfig(final ServerHttpSecurity serverHttpSecurity,
                                              final String successRedirectURL,
                                              final RedirectServerLogoutSuccessHandler rslSuccessHandler,
                                              final String logoutURI) {
        return serverHttpSecurity
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
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(UNAUTHORIZED)));
    }

    private RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler(final String baseURL) throws URISyntaxException {
        final RedirectServerLogoutSuccessHandler rslSuccessHandler = new RedirectServerLogoutSuccessHandler();
        rslSuccessHandler.setLogoutSuccessUrl(new URI(baseURL));
        return rslSuccessHandler;
    }
}
