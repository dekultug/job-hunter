package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdValidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // @GetMapping("/user/create")
    // public String createNewUser() {
    // User user = new User(0, "tunglv", "luutungcntt6k61@gmail.com", "123456");
    // userService.handleCreateUser(user);
    // return "Hello";
    // }

    @PostMapping("/user")
    @ApiMessage("Create new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) throws IdValidException {
        if (userService.existsByEmail(user.getEmail())) {
            throw new IdValidException("Email " + user.getEmail() + "is existed");
        }
        User requestUser = user;
        requestUser.setPassword(passwordEncoder.encode(user.getPassword()));
        User responseUser = userService.handleCreateUser(requestUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.convertUserToResCreateUserDTO(responseUser));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") long userId) throws IdValidException {
        Optional<User> response = userService.getUser(userId);
        if (!response.isPresent()) {
            throw new IdValidException("not found " + userId);
        }
        userService.handleDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("userId") long userId) throws IdValidException {
        Optional<User> response = userService.getUser(userId);
        if (response.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.convertUserToResUserDTO(response.get()));
        } else {
            throw new IdValidException("not found " + userId);
        }
    }

    @GetMapping("users")
    @ApiMessage("fetch list user")
    public ResponseEntity<List<ResUserDTO>> getListUser(
            @Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getListUser(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdValidException {
        User ericUser = this.userService.handleUpdateUser(user);
        if (ericUser == null) {
            throw new IdValidException("User với id = " + user.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(ericUser));
    }
}
