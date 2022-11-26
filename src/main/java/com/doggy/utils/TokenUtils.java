package com.doggy.utils;

import com.doggy.entity.CustomerInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * @ClassName:TokenUtils
 * @Auther: yyj
 * @Description:
 * @Date: 25/11/2022 15:20
 * @Version: v1.0
 */

//token工具类
@Data
public class TokenUtils {
    static private String SECRET = "yang"; //密钥，名称要与application.yml中配的一样
//    private static final long EXPIRE_TIME = 10;
    private static final long EXPIRE_TIME = 72 * 60 * 60 * 1000;
    /**
     * 用户名称
     */
    private static final String USERNAME = Claims.SUBJECT;
    private static final String ID = Claims.ID;
    private static final String UUID = "UUID";
    private static final String OPENID = "OPENID";
    /**
     * 创建时间
     */
    private static final String CREATED = "created";
    /**
     * 权限列表
     */
    private static final String AUTHORITIES = "authorities";
    /**
     * 密钥
     */

    //生成token
    static public  String genToken(CustomerInfo customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USERNAME, customer.getUsername());
        claims.put(ID, customer.getId());
        claims.put(UUID,  customer.getUuid());
        claims.put(OPENID,  customer.getOpenid());
        claims.put(CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private static String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

    /**
     * 刷新令牌
     * @param token
     * @return
     */
    public static String refreshToken(String token) {

        String refreshedToken;
        try {

            Claims claims = getClaimsFromToken(token);
            claims.put(CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }
    public static String refreshToken(CustomerInfo customer) {
        if(!isTokenExpired(customer.getToken())){
           return genToken(customer);
        }
        return refreshToken(customer.getToken());
    }




    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    /**
     * 从令牌中获取数据声明
     * @param token 令牌
     * @return 数据声明
     */
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return !expiration.before(new Date());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            claims = null;
        }
        return claims;
    }

}
