package com.acme.pointbarbackend.platform.iam.application.internal.commandservices;

import com.acme.pointbarbackend.platform.iam.domain.model.commands.SeedRolesCommand;
import com.acme.pointbarbackend.platform.iam.domain.model.entities.Role;
import com.acme.pointbarbackend.platform.iam.domain.model.valueobjects.Roles;
import com.acme.pointbarbackend.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.acme.pointbarbackend.platform.iam.domain.services.RoleCommandService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Implementation of {@link RoleCommandService}
 * <p>
 *     This class is responsible for handling the commands for {@link Role} entity
 * </p>
 */
@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values()).forEach(role -> {
            if(!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(Roles.valueOf(role.name())));
            }
        });


    }
}

