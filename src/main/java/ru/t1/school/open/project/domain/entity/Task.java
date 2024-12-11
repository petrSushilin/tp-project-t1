package ru.t1.school.open.project.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.school.open.project.domain.enums.TaskStatus;

@Entity
@Table(name = "tasks")
public class Task extends AbstractPersistable<Long> {
    @NotNull
    private String title;

    @NotNull
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @NotNull
    private long userId;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = TaskStatus.CREATED;
        }
    }
  
    public Task() {
    }

    public Task(long id, String title, String description, TaskStatus status, long userId) {
        this.setId(id);
        this.title = title;
        this.description = description;
        this.status = status;
        this.userId = userId;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + this.getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", userId=" + userId +
                '}';
    }
}
