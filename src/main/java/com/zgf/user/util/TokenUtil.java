package com.zgf.user.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenUtil {
    private static final String TOKEN_SECRET="token123";  //密钥盐

     /**
      * 签名生成
      * @return
      */
    public static String sign(String name,String password){
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer("zgf")
                    .withClaim("username", name)
                    .withClaim("password",password)
                    // 使用了HMAC256加密算法。
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 签名验证
     * @param token
     * @return
     */
    public static String verify(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("zgf").build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("password").asString();
        } catch (Exception e){
            return null;
        }
    }
}
