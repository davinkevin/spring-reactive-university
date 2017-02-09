package com.example.dashboard;

import com.example.DashboardProperties;
import com.example.integration.github.GithubClient;
import com.example.integration.github.GithubIssue;
import com.example.integration.gitter.GitterClient;
import com.example.integration.gitter.GitterMessage;
import com.example.integration.gitter.GitterUser;
import javaslang.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Brian Clozel
 */
@Service
@RequiredArgsConstructor
public class DefaultDashboardService implements DashboardService {

	private final DashboardProperties properties;
	private final GitterClient gitterClient;
	private final GithubClient githubClient;

	@Override
	public Flux<ReactorIssue> findReactorIssues() {
        Flux<GithubIssue> issues = this.githubClient.findOpenIssues("reactor", "reactor-core");
        Mono<java.util.List<GitterUser>> users = this.gitterClient
                .getUsersInRoom(this.properties.getReactor().getGitterRoomId(), 300)
                .collectList();

        return users
                .flatMap(
                        gitterUserList -> issues.map(i -> this.generateReactorIssue(i, gitterUserList))
                );
	}

    private ReactorIssue generateReactorIssue(GithubIssue issue, java.util.List<GitterUser> gitterUserList) {
        String login = issue.getUser().getLogin();

        Option<GitterUser> guser = javaslang.collection.List.ofAll(gitterUserList)
                .find(u -> u.getUsername().equals(login));

        return new ReactorIssue(issue, guser.isDefined());
    }

    @Override
	public Flux<GitterMessage> getLatestChatMessages(int limit) {
		return Flux.empty();
	}

	@Override
	public Flux<GitterMessage> streamChatMessages() {
		return Flux.empty();
	}
}
