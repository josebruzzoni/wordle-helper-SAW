package com.tacs2022.wordlehelper;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import java.util.Collections;
import java.util.List;

@Configuration
public class SpringHeadersConfig {

    /**
     * Adds to every endpoint with the method 'GET', the headers 'ETAG' (a hash of body) and "Content-Length" (its length).
     * The second one is compared with the minimum size to encrypt (setted in application.properties) and then decide
     * if it is worth to apply that process or ienot.
     */
    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean() {
        List<String> ALL_URLs = Collections.singletonList("*");

        FilterRegistrationBean<ShallowEtagHeaderFilter> filterBean = new FilterRegistrationBean<>();
            filterBean.setFilter(new ShallowEtagHeaderFilter());
            filterBean.setUrlPatterns(ALL_URLs);

        return filterBean;
    }

}
