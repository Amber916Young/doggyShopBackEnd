package com.doggy.utils;

import com.doggy.entity.CustomerInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
public class TokenUtils {
    static private String key = "wx702d1ebb3c60dca3"; //密钥，名称要与application.yml中配的一样
    static private long expiration = 86400000; //过期时间，名称要与application.yml中配的一样

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    //生成token
    static public  String genToken(CustomerInfo customer) {
        long curTime = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                .setId(customer.getId() +"").setSubject(customer.getUsername()) //使用用户ID与手机号
                .setIssuedAt(new Date(curTime)) //设置token创建时间
                .signWith(SignatureAlgorithm.HS256, key); //进行签名，HS256方式，密钥为key
        if (expiration > 0) {
            builder.setExpiration(new Date(curTime + expiration));  //设置token过期时间
        }
        builder.claim("uuid", customer.getUuid());  //添加自定义key:value对
        return builder.compact(); //生成token
    }

    //解析token
    public CustomerInfo parser(String token) {
        Claims c = Jwts.parser().setSigningKey(key) //设置签名密钥为yyh
                .parseClaimsJws(token).getBody(); //解析token
        CustomerInfo customer = new CustomerInfo();
        String id = c.getId(); //获取token中的用户ID
        String username = c.getSubject(); //获取token中的手机号
        Date createDate = c.getIssuedAt(); //获取token的创建时间
        Date expirationDate = c.getExpiration(); //获取token的过期时间
        String uuid = (String) c.get("uuid"); //获取token中的自定义key:value
//        String phone = (String) c.get("phone"); //获取token中的自定义key:value
        customer.setId(Integer.parseInt(id));
        customer.setUsername(username);
        customer.setUuid(uuid);
        return customer;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

}
