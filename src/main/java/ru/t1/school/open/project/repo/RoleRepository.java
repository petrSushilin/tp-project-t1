package ru.t1.school.open.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.school.open.project.domain.entity.Role;
import ru.t1.school.open.project.domain.enums.UserRoles;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(UserRoles role);
}
