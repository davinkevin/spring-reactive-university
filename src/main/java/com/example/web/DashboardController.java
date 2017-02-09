package com.example.web;

import com.example.dashboard.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Brian Clozel
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;
	private final ReactorPersonRepository repository;

    @GetMapping("/")
    public String home() {
        return "home";
    }


    @GetMapping("/reactor/people")
    @ResponseBody
    public Flux<ReactorPerson> findReactorPeople() {
        return this.repository.findAll();
    }

    @GetMapping("/reactor/people/{id}")
    @ResponseBody
    public Mono<ReactorPerson> findReactorPerson(@PathVariable String id) {
        return this.repository
                .findOne(id)
                .otherwiseIfEmpty(Mono.error(new ReactorPersonNotFoundException(id)));
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/issues")
    @ResponseBody
    public Flux<ReactorIssue> findIssues() {
        return this.dashboardService.findReactorIssues();
    }

}
