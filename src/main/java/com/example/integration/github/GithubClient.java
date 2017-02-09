package com.example.integration.github;

import com.example.DashboardProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * @author Brian Clozel
 */
@Component
public class GithubClient {

    private final WebClient client;
    private final DashboardProperties dashboardProperties;

    public GithubClient(DashboardProperties dashboardProperties) {
        this.dashboardProperties = dashboardProperties;
        this.client = WebClient
                .builder()
                    .baseUrl("https://api.github.com")
                    .defaultHeader("User-Agent", "Spring Framework WebClient")
                .build()
                .filter(authentication());
    }

    private ExchangeFilterFunction authentication() {
        return ExchangeFilterFunctions.basicAuthentication(
                        this.dashboardProperties.getGithub().getUsername(),
                        this.dashboardProperties.getGithub().getToken()
                );
    }


    public Flux<GithubIssue> findOpenIssues(String owner, String repo) {
        return this.client
                .get()
                .uri("/repos/{owner}/{repo}/issues?state=open", owner, repo)
                .accept(MediaType.valueOf("application/vnd.github.v3+json"))
                .exchange()
                .flatMap(response -> response.bodyToFlux(GithubIssue.class));
    }

}
