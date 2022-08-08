package ru.job4j.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.handlers.Operation;
import ru.job4j.chat.model.Role;
import ru.job4j.chat.service.RoleService;

import javax.validation.Valid;
import java.util.List;

/**
 * Rest controller for working with objects of Role
 */
@RestController
@Validated
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    /**
     * Business login for working with object of Role
     */
    private final RoleService roleService;

    /**
     * GET method for finding all roles
     * @return List of role
     */
    @GetMapping("/")
    public List<Role> findAll() {
        return roleService.findAllRoles();
    }

    /**
     * GET method for finding Role object by role ID
     * @param id role ID
     * @return Role object
     */
    @GetMapping("/{id}")
    public Role findById(@PathVariable int id) {
        return roleService.findRoleById(id);
    }

    /**
     * POST method for creating new Role
     * @param role Role object
     * @return created Role object
     */
    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public Role create(@Valid @RequestBody Role role) {
        return roleService.addRole(role);
    }

    /**
     * PATCH method for updating Role object
     * @param role Role object
     */
    @PatchMapping("/")
    @Validated(Operation.OnCreate.class)
    public void update(@RequestBody Role role) {
        roleService.addRole(role);
    }

    /**
     * DELETE method for deleting Role from database by role ID
     * @param id role ID
     */
    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public void delete(@PathVariable int id) {
        roleService.deleteRoleById(id);
    }
}
