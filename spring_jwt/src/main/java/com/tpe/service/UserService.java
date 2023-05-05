package com.tpe.service;

import com.tpe.controller.dto.RegisterRequest;
import com.tpe.domain.Role;
import com.tpe.domain.User;
import com.tpe.domain.enums.UserRole;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.RoleRepository;
import com.tpe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest request) {

        //!!! userName unique mi?
        if (userRepository.existsByUserName(request.getUserName())) {

            throw new ConflictException("User is already exist");

        }
        //!!! Role bilgisini setliyoruz, default olarak STUDENT
        Role role = roleRepository.findByName(UserRole.ROLE_STUDENT).orElseThrow(() ->
                new ResourceNotFoundException("Role not found !!!"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        //!!! User objesi olusturup fieldlarini setliyoruz
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserName(request.getUserName());
        user.setRoles(roles);

        //password sifrelenecek
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }
}
