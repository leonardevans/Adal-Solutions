package com.adalsolutions.services;

import com.adalsolutions.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> getRoleByName(String name);
    List<Role> getAllRoles();
    void saveRole(Role role);
}
