package dev.mattoi.runnerz.user;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class UserRestClient {
    private final RestClient restClient;

    public UserRestClient(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("https://jsonplaceholder.typicode.com")
//                Some examples of how it's possible to customize the requests:
//                .requestFactory(new JdkClientHttpRequestFactory())
//                .defaultHeader("User", "Spring Webclient")
                .build();
    }

    public List<User> findAll(){
        return restClient.get()
                .uri("/users")
                .retrieve()
                .body(new ParameterizedTypeReference<>(){});
    }
    public User findById(Integer id){
        return restClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .body(User.class);
    }
}
