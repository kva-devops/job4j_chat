package ru.job4j.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.chat.model.Role;
import ru.job4j.chat.repository.RoleRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Logic for working with object of Role
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {
    /**
     * DAO for Role models
     */
    private final RoleRepository roleRepository;

    /**
     * Method for finding all roles
     * @return List of roles
     */
    public List<Role> findAllRoles() {
        String anchor = UUID.randomUUID().toString();
        List<Role> roleList = StreamSupport.stream(
                this.roleRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
        if (roleList == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return roleList;
    }

    /**
     * Method for finding Role object by role ID
     * @param roleId - role ID
     * @return object of Role
     */
    public Role findRoleById(int roleId) {
        String anchor = UUID.randomUUID().toString();
        Optional<Role> role = this.roleRepository.findById(roleId);
        if (role.isEmpty()) {
            throw new IllegalArgumentException("Role not found. Actual parameters: role ID - " + roleId + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return role.get();
    }

    /**
     * Method for adding new role
     * @param role - object of Role
     * @return object of Role
     */
    public Role addRole(Role role) {
        String anchor = UUID.randomUUID().toString();
        if (role.getName() == null) {
            throw new IllegalArgumentException("Name of role is empty. Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        Role addedRole = this.roleRepository.save(Role.of(role.getName()));
        if (addedRole == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return addedRole;
    }

    /**
     * Method for updating Role
     * @param role - object of Role
     */
    public void updateRole(Role role) {
        String anchor = UUID.randomUUID().toString();
        var current = roleRepository.findById(role.getId());
        if (current.isEmpty()) {
            throw new IllegalArgumentException("Role not found. Actual parameters: role ID - " + role.getId() + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        var buffRole = current.get();
        var methods = buffRole.getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method : methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                var getMethod = namePerMethod.get(name);
                var setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
                }
                Object newValue = null;
                try {
                    newValue = getMethod.invoke(role);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (newValue != null) {
                    try {
                        setMethod.invoke(buffRole, newValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        roleRepository.save(buffRole);
    }

    /**
     * Method for deleting Role by role ID
     * @param roleId - role ID
     */
    public void deleteRoleById(int roleId) {
        String anchor = UUID.randomUUID().toString();
        Optional<Role> foundRole = roleRepository.findById(roleId);
        if (foundRole.isEmpty()) {
            throw new IllegalArgumentException("Role not found. Actual parameters: role ID - " + roleId + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        Role role = new Role();
        role.setId(roleId);
        roleRepository.delete(role);
    }

}
