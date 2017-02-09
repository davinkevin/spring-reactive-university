# Spring Reactive University

## Prepare the authentication tokens

* Get your gitter token from https://developer.gitter.im/apps
* Create a new Github personal access token here: https://github.com/settings/tokens

Add both values as properties in `application-secret.properties` with the configuration
keys `dashboard.github.username`, `dashboard.github.token` and `dashboard.gitter.token`.

##1) Add demo web endpoints

First, let's create a few web endpoints to understand how reactive types
can be used in a web application:

1. Synchronous return types can be still used and will be wrapped by the
framework. This is allowed as long as the controller method is not
blocking.

Mission: create a `GET` endpoint which return `"this is a test"`.

2. You can obviously return Reactive types.

Mission: create a `GET` endpoint which takes a `String` request parameter
`name` and returns `"Hello, " + name + "!"`.

2. With the use of `Mono.never()`, we can see that the reactive type not
only controls the HTTP response body, but also when that response is
written.

Mission: create a `GET /waitforit` endpoint which just wait indefinitely.

3. The HTTP contract is not based on Servlet here, but rather on the
`ServerWebExchange` contract and `DataBuffer`.

Mission: create a `GET /exchange` endpoint with `public Mono<Void> exchange(ServerWebExchange exchange)`
signature that writes `"Hello from exchange"` in the response body.

4. Errors can also flow through the reactive pipeline and those should
be handled by application code or by the framework itself.

Missions: generate an error when reaching `/error` with an handler method
that returns `Mono<String>`.

##2) Add view infrastructure and home view

In this step, we configure the view infrastructure to use the Freemarker
templating engine. Templates will be asynchronously resolved and the
framework will initiate the rendering phase and use the result for the
HTTP response.

Mission:
 - Add a new `@Configuration` class that implements `WebFluxConfigurer`
 - Add a `FreeMarkerConfigurer` bean configured to load templates from `"classpath:/templates/"` and the `ApplicationContext` as resource loader
 - Override `configureViewResolvers()` to configure freemarker view registry
 - Display an `"home"` view on `GET /` in `DashboardController`.

##3) Use a reactive Spring Data repository

With the right database driver and repository API, we can leverage our
datastore in our reactive pipeline.

The `CommandlineRunner` interface requires a blocking operation (i.e. it
should complete before the method returns). Since this is not done while
handling requests and only at startup, we can safely block here.

We can compose our pipeline with operators in order to handle exceptions
or empty values.

Mission:
 - Create a `CommandLineRunner` bean that delete all the `ReactorPerso` and then initialize the BDD with the following data:
   - id = "smaldini", name = "Stephane Maldini"
   - id = "simonbasle", name = "Simon Basle"
   - id = "akarnokd", name = "David Karnok"
   - id = "rstoya05", name = "Rossen Stoyanchev"
   - id = "sdeleuze", name = "Sebastien Deleuze"
   - id = "poutsma", name = "Arjen Poutsma"
   - id = "bclozel", name = "Brian Clozel"
 - Add a `GET /reactor/people` endpoint that display the all the `ReactorPerson`
 - Add a `GET /reactor/people/{id}` endpoint that emit an `ReactorPersonNotFoundException`
   when nobody is found with a related `@ExceptionHandler` that returns a 404 HTTP code.
 

##4) Call a remote REST API with the WebClient

In this step, we're using the reactive HTTP `WebClient` to call a remote
REST API. To do that, we first need to create an instance of that client
and configure it if filter functions (which in our case, will add HTTP
headers to the outgoing request). We can then compose two REST calls into a single Publisher of
`ReactorIssue`s.

Mission:
 - Modify `GithubClient` to create a private `WebClient` with `"https://api.github.com"`
 base URI, add a basic authentication using `properties.getGithub().getUsername()` username
  and `properties.getGithub().getToken()` password + add a filter which add a header with `"User-Agent"`
  name and "Spring Framework WebClient" value
 - Modify `GithubClient#findOpenIssues()` to send a `GET` request to `"/repos/{owner}/{repo}/issues?state=open"`
with `"application/vnd.github.v3+json"` accept media type and retreive the body as `Flux<GithubIssue>`
 - In `DefaultDashboardService`, implement `findReactorIssues()` by using the Github
client to find the issues from `https://github.com/reactor/reactor-core` then the Gitter
client to find the 300 first messages in the room ID coming from `this.properties.getReactor().getGitterRoomId()`
then combine these results to create a `ReactorIssue` for each issue matching with a user in the room.
- Add a `GET /issues` endpoint that returns a `issues` view with an `issues` attribute created using
`this.dashboardService.findReactorIssues()`


##5) Add Ajax and ServerSentEvents endpoints
This step demonstrates how `Flux`, within a web application, can not
only convey an "asynchronous list of items" but also a "infinite stream
of items delivered over SSE".

Note that Spring Web Reactive also provides smarter types to deal with
SSE in a more fine-grained way: see `ServerSentEvent`.

Mission:
 - Implement `getLatestChatMessages()` and `streamChatMessages()` in `DefaultDashboardService`
 - In `DashboardController`, add a `GET /chatMessages` endpoint producing JSON with an optional
  `limit` request parameter with a default value of `10` that the latest chat messages
 - In `DashboardController`, add a `GET /chatStream` Server-Sent Events endpoint that
  return a stream of chat messages
- Update `/src/main/resources/templates/chat.ftl` to fetch the default number of latest messages,
and use `appendChatMessage()` to display them
- Update `/src/main/resources/templates/chat.ftl` to subscribe on the SSE endpoint previously
created and use `appendChatMessage()` to display them