package com.nas.server.config;

import com.nas.server.service.SecurityService;
import com.nas.server.util.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.header}")
    private String header;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;
    @Value("${jwt.expiration}")
    private String expiration;
    @Autowired
    private SecurityService securityService;

    public String createToken(String username) {
        if (StringUtil.isEmpty(username)) {
            return null;
        }
        Date date = new Date();
        return tokenPrefix + Jwts.builder()
                .setSubject(username)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + Long.parseLong(expiration)))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getHeader() {
        return this.header;
    }

    public boolean verifyToken(String token) {
        if (StringUtil.isEmpty(token)) {
            return false;
        }
        token = token.replace(tokenPrefix, "");
        Claims body = Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        if (body.getExpiration().getTime() > System.currentTimeMillis()) {
            return false;
        }
        return securityService.findUserByUsername(body.getSubject()) != null;
    }

    public boolean needUpdate(String token) {
        if (StringUtil.isEmpty(token)) {
            return false;
        }
        token = token.replace(tokenPrefix, "");
        Claims body = Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return body.getExpiration().getTime() + (60 * 1000) > System.currentTimeMillis();
    }

    public String getUsername(String token) {
        if (StringUtil.isEmpty(token)) {
            return null;
        }
        token = token.replace(tokenPrefix, "");
        Claims body = Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return body.getSubject();
    }
}
