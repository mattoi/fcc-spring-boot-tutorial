package dev.mattoi.runnerz.run;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RunControllerIntTest {
    @LocalServerPort
    int port;
    private RestClient restClient;

    @BeforeEach
    void setup(){
        restClient = RestClient.create("http://localhost:" + port);
    }

    @Test
    void shouldFindAllRuns() {
        List<Run> runs = restClient.get()
                .uri("/api/runs")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        assertEquals(1, runs.size());
    }

    @Test
    void shouldFindRunById() {
        Run run = restClient.get()
                .uri("/api/runs/3")
                .retrieve()
                .body(Run.class);

        assertAll(
                () -> assertEquals(3, run.id()),
                () -> assertEquals("Wednesday Afternoon Run", run.title()),
                () -> assertEquals("2022-12-12T15:00:12.497660", run.startedOn().toString()),
                () -> assertEquals("2022-12-12T15:20", run.completedOn().toString()),
                () -> assertEquals(2, run.miles()),
                () -> assertEquals(Location.INDOOR, run.location()));
    }

    @Test
    void shouldCreateNewRun() {
        Run run = new Run(11, "Evening Run", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 10, Location.OUTDOOR, null);

        ResponseEntity<Void> newRun = restClient.post()
                .uri("/api/runs")
                .body(run)
                .retrieve()
                .toBodilessEntity();

        assertEquals(201, newRun.getStatusCodeValue());
    }

    @Test
    void shouldUpdateExistingRun() {
        Run run = restClient.get().uri("/api/runs/3").retrieve().body(Run.class);

        ResponseEntity<Void> updatedRun = restClient.put()
                .uri("/api/runs/3")
                .body(run)
                .retrieve()
                .toBodilessEntity();

        assertEquals(204, updatedRun.getStatusCodeValue());
    }

    @Test
    void shouldDeleteRun() {
        ResponseEntity<Void> run = restClient.delete()
                .uri("/api/runs/3")
                .retrieve()
                .toBodilessEntity();

        assertEquals(204, run.getStatusCodeValue());
    }

}