package vn.hoidanit.jobhunter.controller;

import org.hibernate.grammars.hql.HqlParser.SecondContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO.UserLogin;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {

        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUserName(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // // create token
        String accessToken = securityUtil.createAccessToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User userDB = userService.getUserByEmail(loginDTO.getUserName());
        ResLoginDTO.UserLogin userLogin = new UserLogin(userDB.getId(), userDB.getEmail(), userDB.getName());
        resLoginDTO.setUserLogin(userLogin);
        resLoginDTO.setAccessToken(accessToken);

        String refreshToken = securityUtil.createRefreshToken(loginDTO.getUserName(), resLoginDTO);
        userService.updateUserToken(refreshToken, loginDTO.getUserName());
        return ResponseEntity.ok().body(resLoginDTO);
    }
}
