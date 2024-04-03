package amaraj.searchjob.application.service.impl;

import amaraj.searchjob.application.dao.RoleRepository;
import amaraj.searchjob.application.dao.UserRepository;
import amaraj.searchjob.application.dto.PageDTO;
import amaraj.searchjob.application.dto.UserDto;
import amaraj.searchjob.application.entity.ChangePasswordRequest;
import amaraj.searchjob.application.entity.Role;
import amaraj.searchjob.application.entity.User;
import amaraj.searchjob.application.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

import static amaraj.searchjob.application.mapper.UserMapper.USER_MAPPER;
import static amaraj.searchjob.application.utils.PageUtils.toPageImpl;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
    }

//    @Transactional
//    @Override
//    public UserDto saveUser(User userDto) {
//        Role roleCompany = roleRepository.findByName("COMPANY").orElse(null);
//
//        if (roleCompany == null) {
//            roleCompany = Role.builder()
//                    .name("COMPANY")
//                    .build();
//            roleCompany = roleRepository.save(roleCompany);
//        }
//
//        // Create the user
//        User userCompany = User.builder()
//                .userName(userDto.getUsername())
//                .email(userDto.getEmail())
//                .password(passwordEncoder.encode(userDto.getPassword()))
//                .build();
//
//        userCompany.setRoles(new HashSet<>(Arrays.asList(roleCompany)));
//        userCompany = userRepository.save(userCompany);
//
//        return USER_MAPPER.toDTO(userRepository.save(userCompany));
//    }

//    @Transactional
//    @Override
//    public UserDto saveUser(User userDto) {
//        Role roleCompany = roleRepository.findByName("COMPANY").orElse(null);
//        Role roleEmployee = roleRepository.findByName("EMPLOYEE").orElse(null);
//        Role roleAdmin = roleRepository.findByName("ADMIN").orElse(null);
//        if (roleCompany == null) {
//            roleCompany = Role.builder()
//                    .name("COMPANY")
//                    .build();
//            roleCompany = roleRepository.save(roleCompany);
//        }
//        if (roleEmployee == null) {
//            roleEmployee = Role.builder()
//                    .name("EMPLOYEE")
//                    .build();
//            roleEmployee = roleRepository.save(roleEmployee);
//        }
//        if (roleAdmin == null) {
//            roleAdmin = Role.builder()
//                    .name("ADMIN")
//                    .build();
//            roleAdmin = roleRepository.save(roleAdmin);
//        }
//
//        Set<Role> userRoles = new HashSet<>();
//        if (userDto.getUsername()!= null && userDto.getEmail() != null) {
//            userRoles.add(roleCompany);
//        } else if (userDto.getEmail() != null) {
//            userRoles.add(roleEmployee);
//        } else {
//            userRoles.add(roleAdmin);
//        }
//
//        // Create the user
//        User user = User.builder()
//                .userName(userDto.getUsername())
//                .email(userDto.getEmail())
//                .password(passwordEncoder.encode(userDto.getPassword()))
//                .build();
//
//        User savedUser = userRepository.save(user);
//
//        return USER_MAPPER.toDTO(savedUser);
//    }

    @Transactional
    @Override
    public UserDto saveUser(User userDto, String role) {
        Role userRole = roleRepository.findByName( "ROLE_" + role.toUpperCase()).orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_" + role.toUpperCase()).build()));

        // Create the user
        User userCompany = User.builder()
                .userName(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();

        userCompany.setRoles(new HashSet<>(Arrays.asList(userRole)));
        userCompany = userRepository.save(userCompany);

        return USER_MAPPER.toDTO(userRepository.save(userCompany));
    }
    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public PageDTO<UserDto> findAll(Pageable pageable) {
        return toPageImpl(userRepository.findAll(pageable), USER_MAPPER);
    }

    private Role checkRoleExist(String roleName) {
        Role role = new Role();
        role.setName("ROLE_" + roleName.toUpperCase());
        return roleRepository.save(role);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
