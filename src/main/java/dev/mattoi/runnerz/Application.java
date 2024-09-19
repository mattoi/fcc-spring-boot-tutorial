package dev.mattoi.runnerz;

import dev.mattoi.runnerz.run.Location;
import dev.mattoi.runnerz.run.Run;
import dev.mattoi.runnerz.run.JdbcClientRunRepository;
import dev.mattoi.runnerz.user.UserHttpClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.LocalDateTime;

@SpringBootApplication
public class Application {

	// private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		//log.info("Application started successfully.");
	}

	// How to use the UserHttpClient interface
	@Bean
	UserHttpClient userHttpClient(){
		RestClient restClient = RestClient.create("https://jsonplaceholder.typicode.com");
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
		return factory.createClient(UserHttpClient.class);
	}



}
