package dev.mattoi.runnerz.run;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@JdbcTest
@Import(JdbcClientRunRepository.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class JdbcClientRunRepositoryTest {
    @Autowired
    JdbcClientRunRepository repository;
    //Creates an instance of the repository for each test
    @BeforeEach
    void setup(){
        repository.create(new Run(40,
                "Monday Morning Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES),
                3,
                Location.INDOOR, null));

        repository.create(new Run(41,
                "Wednesday Evening Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(60, ChronoUnit.MINUTES),
                6,
                Location.INDOOR, null));
    }

    // ! These tests won't actually work unless the mounted database is empty. The teacher dropped the table in the schema, which I found clunky.
    @Test
    void shouldFindAllRuns() {
        List<Run> runs = repository.findAll();
        assertEquals(4, runs.size());
    }

    @Test
    void shouldFindRunWithValidId() {
        var run = repository.findById(40).get();
        assertEquals("Monday Morning Run", run.title());
        assertEquals(3, run.miles());
    }

    @Test
    void shouldNotFindRunWithInvalidId() {
        var run = repository.findById(0);
        assertTrue(run.isEmpty());
    }

    @Test
    void shouldCreateNewRun() {
        repository.create(new Run(42,
                "Friday Morning Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES),
                3,
                Location.INDOOR, null));
        List<Run> runs = repository.findAll();
        assertEquals(5, runs.size());
    }

    @Test
    void shouldUpdateRun() {
        repository.update(new Run(3,
                "Monday Morning Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES),
                5,
                Location.OUTDOOR, null), 1);
        var run = repository.findById(1).get();
        assertEquals("Monday Morning Run", run.title());
        assertEquals(5, run.miles());
        assertEquals(Location.OUTDOOR, run.location());
    }

    @Test
    void shouldDeleteRun() {
        repository.delete(40);
        List<Run> runs = repository.findAll();
        assertEquals(3, runs.size());
    }


}