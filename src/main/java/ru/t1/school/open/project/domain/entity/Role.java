package ru.t1.school.open.project.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.school.open.project.domain.enums.UserRoles;

@Entity
@Table(name = "roles")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Role extends AbstractPersistable<Long> {
    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private UserRoles name;

    public Role() {
    }

    public Role(Long id, UserRoles name) {
        this.setId(id);
        this.name = name;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public UserRoles getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", name=" + name +
                '}';
    }
}