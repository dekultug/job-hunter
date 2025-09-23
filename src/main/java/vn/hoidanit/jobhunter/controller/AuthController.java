package vn.hoidanit.jobhunter.controller;

import java.net.http.HttpHeaders;

import org.hibernate.grammars.hql.HqlParser.SecondContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;
import vn.hoidanit.jobhunter.domain.response.RestResponse;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO.UserGetAccount;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO.UserLogin;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.error.IdValidException;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpire;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {

        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUserName(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // // create token
        // set thông tin vào sercurity holder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User userDB = userService.getUserByEmail(loginDTO.getUserName());
        ResLoginDTO.UserLogin userLogin = new UserLogin(userDB.getId(), userDB.getEmail(), userDB.getName());
        resLoginDTO.setUserLogin(userLogin);
        String accessToken = securityUtil.createAccessToken(authentication.getName(), userLogin);
        resLoginDTO.setAccessToken(accessToken);

        // refresh
        String refreshToken = securityUtil.createRefreshToken(loginDTO.getUserName(), resLoginDTO);
        userService.updateUserToken(refreshToken, loginDTO.getUserName());

        // set cookies
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpire)
                .build();

        return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    public ResLoginDTO.UserGetAccount getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User user = userService.getUserByEmail(email);
        UserLogin userLogin = new UserLogin();
        UserGetAccount userGetAccount = new UserGetAccount();
        userLogin.setName(user.getName());
        userLogin.setId(user.getId());
        userLogin.setEmail(email);
        userGetAccount.setUserLogin(userLogin);
        return userGetAccount;
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token") String refreshToken) throws IdValidException {
        // check valid
        Jwt decodeToken = securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodeToken.getSubject();
        User currentUser = userService.getUserByTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdValidException("refresh token ko hop le");
        }

        // new token
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User userDB = userService.getUserByEmail(email);
        ResLoginDTO.UserLogin userLogin = new UserLogin(userDB.getId(), userDB.getEmail(), userDB.getName());
        resLoginDTO.setUserLogin(userLogin);
        String accessToken = securityUtil.createAccessToken(email, userLogin);
        resLoginDTO.setAccessToken(accessToken);

        // refresh
        String newRefreshToken = securityUtil.createRefreshToken(email, resLoginDTO);
        userService.updateUserToken(newRefreshToken, email);

        // set cookies
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpire)
                .build();

        return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/account/logout")
    public ResponseEntity<Void> logOut() throws IdValidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.isEmpty()) {
            throw new IdValidException("log out email error");
        }
        userService.updateUserToken(null, email);
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpire)
                .build();
        return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(null);
    }

}
