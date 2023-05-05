package com.tpe.security;

import com.tpe.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component  //Class seviye annotqtion yapiyoruz
public class JwtUtils {

    private String jwtSecret = "sboot";

    private long jwtExpirationMs = 86400000;  //24*60*60*1000 (1 gün ms cinsinden)


    //!!! ****************** GENERATE JWT TOKEN *************

    public String generateToken(Authentication authentication) { //Authentication class'i, login olmus kullaniciya SecurityContext üzerinden ulasmamizi saglar

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); //Bu metod ile anlik olarak login olan kullaniciya ulasmis oluyoruz.


        //Jwt tokeni userName field'i, jwtSecret ve jwtExpirationMS bilgilerini kullanarak olusturuyoruz
        return Jwts.builder().
                setSubject(userDetails.getUsername()). //Token icerisinde genel olarak userName ile bilgiler tutariz. Sistem her ne kadar güvenli olsa da password bilgisi dahil edilmez
                        setIssuedAt(new Date()). //üretilecek token tarihi
                        setExpiration(new Date(new Date().getTime() + jwtExpirationMs)). //Burada biz anlik zamani al, daha önce belirledigimiz süreyi (1 gün) üzerine ekle ve gecerlilik süresi olarak kabul et diyoruz
                        signWith(SignatureAlgorithm.HS512, jwtSecret).
                compact();

    }


    //!!! ****************** VALIDATE JWT TOKEN *************

    public boolean validateToken(String token) {

        /* Kisayol : Asagidaki kodu yazip iki satiri sectikten sonra, yukaridaki menülerden Code-->Surround with-->try/catch secilirse otomatik tüm karsilasilacak exception'lar try/catch bloguna alinir.
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        */
        try {
            Jwts.parser().
                    setSigningKey(jwtSecret).
                    parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
        } catch (MalformedJwtException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }


    //!!! ****************** GET UserName from JWT TOKEN *************

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().
                setSigningKey(jwtSecret).
                parseClaimsJws(token).
                getBody().
                getSubject();
    }

}
