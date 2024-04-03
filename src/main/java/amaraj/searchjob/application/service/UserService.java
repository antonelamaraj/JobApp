package amaraj.searchjob.application.service;

import amaraj.searchjob.application.dto.PageDTO;
import amaraj.searchjob.application.dto.UserDto;
import amaraj.searchjob.application.entity.ChangePasswordRequest;
import amaraj.searchjob.application.entity.User;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.Optional;

public interface UserService {

    UserDto saveUser(User userDto, String role);
    Optional<User> findUserById(Long id);
    PageDTO<UserDto> findAll(Pageable pageable);

    Optional<User> findUserByEmail(String email);
    void changePassword(ChangePasswordRequest request, Principal connectedUser);

}
