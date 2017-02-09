package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import javaslang.jackson.datatype.JavaslangModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kevin on 09/02/2017
 */
@Configuration
public class JacksonConfig {

    @Bean
    ObjectMapper mapper() {
        return new ObjectMapper()
                .registerModules(new JavaslangModule());

    }

}
