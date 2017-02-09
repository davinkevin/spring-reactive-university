package com.example;

import com.example.dashboard.ReactorPerson;
import com.example.dashboard.ReactorPersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Created by kevin on 09/02/2017
 */
@Component
@RequiredArgsConstructor
public class CliExecutionAtStartUp implements CommandLineRunner {

    private final ReactorPersonRepository repository;

    @Override
    public void run(String... args) throws Exception {
        Flux<ReactorPerson> people = Flux.just(
                new ReactorPerson("smaldini", "Stephane Maldini"),
                new ReactorPerson("simonbasle", "Simon Basle"),
                new ReactorPerson("akarnokd", "David Karnok"),
                new ReactorPerson("rstoya05", "Rossen Stoyanchev"),
                new ReactorPerson("sdeleuze", "Sebastien Deleuze"),
                new ReactorPerson("poutsma", "Arjen Poutsma"),
                new ReactorPerson("bclozel", "Brian Clozel")
        );
        repository.deleteAll()
                .thenMany(() -> repository.save(people))
                .blockLast();
    }
}
