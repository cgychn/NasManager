package com.nas.server.controller;

import com.nas.server.config.JwtUtil;
import com.nas.server.entity.db.User;
import com.nas.server.service.SecurityService;
import com.nas.server.util.RespRes;
import com.nas.server.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("security")
public class SecurityController {

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);
    @Autowired
    private SecurityService securityService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("login")
    public Map<String, ?> login(@RequestBody User user, HttpServletResponse httpServletResponse) {
        try {
            boolean b = securityService.verifyUser(user.getUsername(), user.getPassword());
            if (b) {
                // create token
                String token = jwtUtil.createToken(user.getUsername());
                httpServletResponse.setHeader("token", token);
            }
            return RespRes.success(b, "");
        } catch (Exception e) {
            log.error("登录失败", e);
            return RespRes.success(false, e.getMessage());
        }
    }

    @PostMapping("register")
    public Map<String, ?> register(@RequestBody User user) {
        try {
            securityService.createUser(user);
            return RespRes.success(true, "");
        } catch (Exception e) {
            log.error("注册失败", e);
            return RespRes.success(false, e.getMessage());
        }
    }

}
