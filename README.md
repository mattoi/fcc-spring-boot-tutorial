# FreeCodeCamp Spring Boot 3 Tutorial - Summary

This is where I'm taking notes regarding a course on backend development, ["Building web applications in Java with Spring Boot 3"](https://www.youtube.com/watch?v=31KTdfRH6nY), hosted by [FreeCodeCamp](https://www.freecodecamp.org/). 
Yes, it's a free course with no certificate, but I tried it out just to see if I enjoyed the Spring ecosystem, or backend development at all.

Why Java? Honestly, I'm badly in need of a job. 
At the time of this writing, it's been over a year since I've graduated and I have little more than some experience with Flutter, which doesn't seem like the hottest tech right now. 
In the meantime, I've tried a couple courses on stuff like React and Data Science (which I do want to go back to), but I didn't feel like pushing onwards just yet. 
Now, I picked a framework that is always in high demand but not a lot of people really mind learning, and that seemed like I could quickly develop something meaningful. 
I don't mind it's "verbose" syntax or how it forces OOP, so I figured I would get along well with it. 

I've been painstakingly going through this tutorial for the last couple weeks (not because of the language or anything), but now I got my ADHD meds and it helped me tear through the last stretch and start writing this document. 
I wasn't even going to write anything at first. I don't like depending on it, but it really makes me function and I'm glad I'm back on my treatment.

So why am I writing this? I don't want to be stuck in tutorial hell, just copying tutorials on the internet and unable to show anyone what I'm really learning, so I figured this would be a good way. 
I started writing this during the last section then went on to document the remaining chapters.

This course was made by Dan Vega, a veteran Spring developer with over 23 years of Java experience and an instructor at Spring Academy, the "official" website for learning. 
He takes an approach where first he shows you the "usual" method of writing the code, and then he brings in some of Spring's built-in features to achieve the same results with way less typing. 
And that's the part I found the most interesting about Spring, so kudos to Dan for showcasing that.

I'm organizing the remainder of this document in 6 sections: Setup, Spring MVC, Database, REST Client, Testing and then my final thoughts.

## How to run this application
(I'm not sure this is how it works)

From what I understand. you need: 
- JDK 17 or later
- Docker Desktop

1. Clone the repository
2. In the project folder, run `mvnw clean install` to install the dependencies
3. Run `docker compose up` to launch the PostgreSQL database. This is needed when running tests, otherwise Spring will try to compose when running the main app.
4. Run `mvnw spring-boot:run` to launch the main application. I don't know how to run the tests via the command line yet. 

## 1. Course Setup
For this course, it's recommended to know the basics of Java and its OOP paradigm. 
To be honest, even encapsulation becomes a breeze with Spring's tooling. 

JDK 17 or later is required, as well as either Maven or Gradle for building. 
I'm sticking to Maven for the sake of following the tutorial, but I might try Gradle next time I go for a personal project.

I'm using IntelliJ IDEA as it seems to have the best integration with Java environments, but I'm sure VS Code will work just as fine.
For the API testing, I used Postman. 
And to set up the PostgreSQL database, I had to install Docker Desktop.

As for the project itself, it's a backend for a fictional fitness tracking app called Runnerz. 
We'll be setting the MVC architecture with Rest endpoints, database calls and testing. 
I've decided to keep the same theme as the course, because after this I'll create an app of my own to consolidate my learning.

The [Spring Initializr](https://start.spring.io/) will generate a project folder with the settings we choose. 
For the sake of consistency, I stayed as close as possible to the tutorial. 
That means Maven, Java, 3.3.3 because it's the last stable version, and Java 17. 

The instructor messes around with the dependencies a lot throughout the course, adding and removing them manually when needed, which I didn't really like. 
The dependencies in the final project are Spring Web, Spring Boot DevTools, Spring Data JDBC, PostgreSQL Driver, Docker Compose Support and JDBC API.

After generating the project, you can download the zip file, extract to a folder and then open the new folder in your IDE of choice. 
There are many ways of launching the application on IntelliJ, but we'll stick to entering `mvnw spring-boot:run` in the command line. 
Since it's a Maven project, we can find the application settings and dependencies in the [pom.xml](pom.xml) file. 
And that's pretty much all we need to begin writing the MVC architecture for our app.

## 2. Spring MVC
One thing to note: in Spring, you usually shouldn't create `new` instances of objects; dependency injection and the application context will take care of it. 
You just need to use the correct annotations so that Spring knows what to do. 
This is the first abstraction that I still need to understand 100% so that I know what I am doing.

Before setting up the run model, Dan mentions two common ways of structuring our code: package by layer and package by feature. 
The former separates MVC files into 3 packages, Model, View and Controller, while the latter groups together all the files related to a model or feature.
For example, the Run model, Run repository and Run controller would all be part of the `Run` package. 
I prefer this approach, not only because I think it's easier to find the files, but also because all the related files in the same package will have full access to each other without necessarily using `public` elements. 

We begin with the [Run model](src/main/java/dev/mattoi/runnerz/run/Run.java). This is the basic outline:
```
id: Integer
title: String
startedOn: LocalDateTime
completedOn: LocalDataTime
miles: Integer
location: Enum {INDOORS, OUTDOORS}
```

Dan begins by writing a normal class, with constructor, getters, setters and all. 
And that's just so we can see how much boilerplate we can avoid, as he then swaps that out with a record. 
I didn't know this was a thing, but thank god it is. 
A record will have its constructor and getters generated automatically, but the fields are read-only. 
This is perfect for data structures that are loaded from a database.

There are also a couple annotations to help with validation, as well as a constructor for that too. 

Next up, the [Run controller](src/main/java/dev/mattoi/runnerz/run/RunController.java).
Strictly speaking, the "controller" of our architecture consists on both the Controller and the Repository classes, while the "view" would be the http responses in the controller.
The `@RestController` sets the class up for Spring, and then we can use annotations such as `@GetMapping` and `@PostMapping` to set up the endpoints. 
At `@RequestMapping` we can setup the base URL for all the methods. 
`@PathVariable` allows the use of dynamic URIs using the method's parameters. 
`@RequestBody` specifies that an object must be input to serve as the request's body.
`@Valid` will check the run against the annotations in the class file, and will throw an exception if the run is invalid.
`@ResponseStatus` provides an enum to easily return a response code.
We have also set up a custom exception for when a run is not found.

This class' only role is to provide endpoints, responses and make the appropriate method calls in the Run repository, which we'll setup next. 
Said repository needs to be passed down in the controller constructor so that dependency injection cant keep track of it.

Note that, throughout the course, we go from an in-memory List to a mock H2 database and then to a fully functional PostgreSQL database without making a single change to this class.
The way this is done, though, is by changing the entirety of the repository class, so if you're coding along it might get confusing. 
I'll try to keep all the different versions as separate files.

The first version of the repository is nothing special, just a standard List of Runs with a method for each basic CRUD operation. 
The `@PostConstruct` annotation ensures that piece of code only runs after the initial application context is set.

From this moment onwards, we can use Postman to test the API endpoints. 
By importing [this collection](api/runnerz-postman-collection.json) we have some requests ready to do some testing.

## 3. Database
The next step is rewriting the repository so it works with an actual database.
We do this by using the JDBC API, which is an interface that connects Java applications and relational databases.
With it, we can start writing our [JDBC client repository](src/main/java/dev/mattoi/runnerz/run/JdbcClientRunRepository.java).

You can see that we use standard SQL queries in the methods. 
At first the instructor uses the H2 library, which is an in-memory relational database, but I'm skipping the explanation on that.
The cool part, though, is when we set up a virtual, functioning PostgreSQL instance with Docker and very little configuration in the [compose.yaml](compose.yaml) file.

This iteration of the repository will work with any database that's running.
From my understanding, in this project, Spring will automatically connect to the PostgreSQL database specified in the docker compose file with no extra configuration.

When launching the standard Spring application, it will try to run the Docker compose file if it isn't running yet, but as we'll see later on testing, we might want to compose the database separately from the app.
This can be done by running `docker compose up` in the application folder, provided Docker Desktop is running.

To speed things up, Dan provides a [JSON file](src/main/resources/data/runs.json) with some runs and a [helper class](src/main/java/dev/mattoi/runnerz/run/RunJsonDataLoader.java) that imports the runs straight into the database if it isn't empty.

To check the database itself, the instructor used the database viewer built into IntelliJ IDEA Ultimate, but I don't have access to that. 
So I used a program called [PGAdmin](https://www.pgadmin.org/download/) instead. 

Next up, a really useful Spring feature that certainly gets rid of a lot of boilerplate. 
We set up yet [another iteration of the Run Repository](src/main/java/dev/mattoi/runnerz/run/RunRepository.java), but this time as an interface extending the `ListCrudRepository` class.
With this, all the basic CRUD functions are already implemented. 

However, we still have to add a `findAllByLocation()` method to satisfy our controller.
When we start typing that method's declaration, we can see that Intellisense gives us a lot of options for custom methods based on the Run class we're using. 
That's another Spring feature I thought was really cool and that can save a lot of work, but that's also where I felt maybe there was too much magic around.
Still, it's great to see it working right away.

Finally, if we just change the repository class used in the Run controller to the one we just wrote, it should work seamlessly.

This should be now a complete server application with a database and CRUD functionality, but at this point there are no tests yet.
Testing has its own section in the end, but realistically we should write tests as we implement each part of the architecture. 

## 4. Rest Client
A Rest client is a way to communicate our application with another API. 
It's necessary when using third-party microservices or when integrating with a larger backend.

For this app, we'll use a public API designed for testing/learning called [JSONPlaceholder](https://jsonplaceholder.typicode.com/).
We'll use the `/users` endpoint and pretend they're the users of our app.

First off, to mirror the "User" object from the API we'll create a package for user-related classes, then an [User record](src/main/java/dev/mattoi/runnerz/user/User.java), as well as one for Address, Company and Geo location. 

Now we can move on to the [User Rest Client](src/main/java/dev/mattoi/runnerz/user/UserRestClient.java).
There is no specific annotation for defining Rest clients, but by importing the `RestClient` we get the tools we need.
It has to be passed down into the constructor via a builder in order to be initialized, and then it gives us access to the common API functions.
The builder has several additional options if we need them, such as a different factory for the class, a specific timeout duration for requests or a default header.
The `body()` method specifies how the response body will be decoded.

Next up is a quicker way to set up a Rest client, similar to whet we did to the Run repository.
By creating a [User Client interface](src/main/java/dev/mattoi/runnerz/user/UserHttpClient.java) and using the exchange annotations, all we need is to type the contract for each method.
In both examples, the `@GetExchange` annotation with the desired URI is enough to make each method do what we expect.
One additional piece of code we need to write, though, can be found in the [main Application](src/main/java/dev/mattoi/runnerz/Application.java) class. 
There's where we set up a `@Bean` with the necessary configuration for the Rest client.

## 5. Testing
First, we check out the automatically generated test/java folder. There, we can find a [test file](src/test/java/dev/mattoi/runnerz/ApplicationTests.java) generated by Spring. 
We can keep this test as a basic "sanity test". It won't test anything yet, but it won't run if something is wrong with the project.
The `@SpringBootTest` annotation allows for some custom configuration, such as custom Environment properties or a TestRestTemplate for web tests, but it comes at the cost of bringing the entire application context into the tests we're running.

Next, by checking the [Spring Boot docs on testing](https://docs.spring.io/spring-boot/reference/testing/test-scope-dependencies.html), we can see that, by default, each project comes with quite a few common testing libraries enabled in the testing scope, such as JUnit (unit testing), Mockito (API mocking) and AssertJ (assertions).

Back to the app, currently we can't run any tests right away because the Docker compose file is not being launched automatically. 
We can launch it separately by running `docker compose up` in the project folder. The instructor mentions [Testcontainers](https://java.testcontainers.org/) as an interesting alternative for this use case, but we're doing it manually.

The tests themselves will be copied from the tutorial repository since they are pretty self explanatory; the syntax is the thing to note.

First we bring in some basic unit tests for the [in-memory repository](/src/main/java/dev/danvega/runnerz/run/InMemoryRunRepository.java). 
IntelliJ allows us to simply right-click the repository class and create a [test file](src/test/java/dev/mattoi/runnerz/run/InMemoryRunRepositoryTest.java) out of it, package and all. Then, we can create a couple tests to check the repository methods. 

Next up, [a couple similar unit tests for the JDBC repository](src/test/java/dev/mattoi/runnerz/run/JdbcClientRunRepositoryTest.java). The `@JdbcTest` annotation ensure only configuration relevant to JDBC tests is applied. 
This includes rolling back after each test, and optionally using an in-memory version of the database, but we don't want that last one. 
To prevent that, we use `@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)` and `@Import(JdbcClientRunRepository.class)` for the main class and  `@Autowired` when initializing the repository. 
These tests should create a temporary instance of the PostgreSQL database provided that it is running, and apply each test individually. 
However, Dan chose to run the tests as if the table is empty, and changed the sql schema to drop the table when the app launches, which I found rather clunky. 
Sure, it's a workflow he's not used to, but I'd rather write tests that can run in any existing table. Maybe I'll do that in the future.

Moving on, we write some tests for the Run Controller itself, starting with [unit tests](src/test/java/dev/mattoi/runnerz/run/RunControllerTest.java). 
As this is a web MVC test (as per the `@WebMvcTest` annotation), there's a couple extra elements we need to set up. 
Namely, the MVC test gives us a mock client and an object mapper for JSON.

Next, some [integration tests](src/test/java/dev/mattoi/runnerz/run/RunControllerIntTest.java). 
Actually, I'm not used to integration tests. 
These will test each endpoint, this time with the entire application context, no mock endpoints or beans, and try to get the expected results.

Finally, some integration tests on the [Rest Client](src/test/java/dev/mattoi/runnerz/user/UserRestClientTest.java). 
This one was just a quick overview to test the desired outputs of an external API through the Rest client.

## 6. Closing Thoughts 
Honestly, Spring was a pleasant surprise. The last time I had any contact with Java was at college in 2016 while learning about OOP. 
Since then, I never really tried anything involving Java, such as (now legacy) Android development or Spring, but I heard a lot about how people don't like the language for being verbose.

I can't really deny that, not that I mind, but at the same time, Spring saves you a lot of typing for the most common use cases. 
Setting up a record for keeping data and having Spring generate most of the common functions with just a couple annotations feels great. 
But at the same time, the configuration options are really extensive, so I believe you can take the magic away and do a lot with the framework.

I still think I need to get into the inner workings of a lot of the abstractions, but even as a beginner I already feel like I can create fully-functional web applications from what I learned from this course. 
I'd rather use Kotlin, though, and I'm glad that's an option with full interoperability in Spring.