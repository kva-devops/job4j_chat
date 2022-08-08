package ru.job4j.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.handlers.Operation;
import ru.job4j.chat.model.Role;
import ru.job4j.chat.repository.RoleRepository;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Rest controller for working with objects of Role
 */
@RestController
@Validated
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    /**
     * DAO for Role models
     */
    private final RoleRepository roleRepository;

    /**
     * GET method for finding all roles
     * @return List of role
     */
    @GetMapping("/")
    public List<Role> findAll() {
        return StreamSupport.stream(
                this.roleRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    /**
     * GET method for finding Role object by role ID
     * @param id role ID
     * @return Role object
     */
    @GetMapping("/{id}")
    public Role findById(@PathVariable int id) {
        var role = this.roleRepository.findById(id);
        if (role.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        return role.get();
    }

    /**
     * POST method for creating new Role
     * @param role Role object
     * @return created Role object
     */
    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public Role create(@Valid @RequestBody Role role) {
        if (role.getName() == null) {
            throw new NullPointerException("Name field is empty");
        }
        Role buff = Role.of(role.getName());
        return this.roleRepository.save(buff); // add validation
    }

    /**
     * PATCH method for updating Role object
     * @param role Role object
     */
    @PatchMapping("/")
    @Validated(Operation.OnCreate.class)
    public void update(@RequestBody Role role) throws InvocationTargetException, IllegalAccessException {
        var current = roleRepository.findById(role.getId());
        if (current.isEmpty()) {
            throw new NullPointerException("Role not found");
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
                    throw new NullPointerException("Invalid properties");
                }
                var newValue = getMethod.invoke(role);
                if (newValue != null) {
                    setMethod.invoke(buffRole, newValue);
                }
            }
        }
        roleRepository.save(buffRole);
    }

    /**
     * DELETE method for deleting Role from database by role ID
     * @param id role ID
     */
    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public void delete(@PathVariable int id) {
        if (this.roleRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        Role role = new Role();
        role.setId(id);
        this.roleRepository.delete(role);
    }
}
