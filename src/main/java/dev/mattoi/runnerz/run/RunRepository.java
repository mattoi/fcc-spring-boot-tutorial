package dev.mattoi.runnerz.run;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface RunRepository extends ListCrudRepository<Run, Integer> {
//Add a custom CRUD method
    List<Run> findAllByLocation(String location);
}
