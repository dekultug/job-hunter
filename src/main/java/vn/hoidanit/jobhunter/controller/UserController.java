package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

@RestController
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

    @PostMapping("/user/create")
    public ResponseEntity<User> createNewUser(@RequestBody User user) {
        User requestUser = user;
        requestUser.setPassword(passwordEncoder.encode(user.getPassword()));
        User responseUser = userService.handleCreateUser(requestUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") long userId) {
        userService.handleDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") long userId) {
        Optional<User> response = userService.getUser(userId);
        if (response.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(response.get());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }

    @GetMapping("user/getList")
    public ResponseEntity<List<User>> getListUser(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {

        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "1";
        String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "1";

        int current = Integer.parseInt(sCurrent);
        int pageSize = Integer.parseInt(sPageSize);

        Pageable pageable = PageRequest.of(current - 1, pageSize);

        return ResponseEntity.status(HttpStatus.OK).body(userService.getListUser(pageable));
    }
}
