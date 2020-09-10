package com.hxz.test.login.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hxz.test.login.bean.Login;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtSignUtils {

    public static final Long ACCESS_EXPIRE_TIME = 30 * 1000L;
    public static final Long REFRESH_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    private static final String TOKEN_SECRET = "123456";

    public static Login sign(String userId) {
        return new Login(getToken(userId,ACCESS_EXPIRE_TIME),getToken(userId,REFRESH_EXPIRE_TIME));
    }

    public static String getToken(String userId,Long time) {
        String token = null;
        Date accessDate = new Date(System.currentTimeMillis() + time);  // 过期时间
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            Map<String,Object> header = new HashMap<>();
            header.put("typ","JWT");
            header.put("alg","HS256");

            token = JWT.create().withHeader(header).withClaim("userId",userId).withExpiresAt(accessDate).sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
//            String userId = jwt.getClaim("userId").asString();
//            System.out.println("userId = " + userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getUserId(String token) {
        DecodedJWT decode = JWT.decode(token);
        return decode.getClaim("userId").asString();
    }
}
