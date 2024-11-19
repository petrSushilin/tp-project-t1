package ru.t1.school.open.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.school.open.project.domain.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
