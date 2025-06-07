package com.supportportal.supportportal.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import static com.supportportal.supportportal.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.supportportal.supportportal.domain.UserPrincipal;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {

    // Spring will look for jwt.secret in application.yml
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Generates the actual JWT token
     * @param userPrincipal the user that has been authenticated
     * @return JWT token
     */
    public String generateJwtToken(UserPrincipal userPrincipal){

        String[] claims =  getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(GET_ARRAYS_LLC) //the name of the application
                .withAudience(GET_ARRAYS_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())  // username or user id - should be unique
                .withArrayClaim(AUTHORITIES, claims) // user's claims
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    /**
     * Configures the claims for the user
     * @param userPrincipal the user that has been authenticated
     * @return an array of permissions
     */
    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {

        List<String> authorities = new ArrayList<>();
        for(GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
       return authorities.toArray(new String[0]);
    }

    /**
     * Gets the authorities from the token
     * @param token 
     * @return a list of authorities
     */
    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * Configures the claims from the token
     * @param token
     * @return an array of claims
     */
    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier =  getJWTVerifier();
        // Verify the JWT token and then get the claims
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    /**
     * Creates a JWT Verifier passing the Algorithm
     * @return JWT Verifier
     */
    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm)
                            .withIssuer(GET_ARRAYS_LLC)
                            .build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException (TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    /**
     * Gets the authentication of the user
     * @param username
     * @param authorities
     * @param request
     * @return the authentication
     */
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request){
        //  If the token is correct, we need to tell Spring Security to get the authentication of the user
        UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        //  and then set that authentication in the Spring Security context -> this user is authenticated, process their request
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    /**
     * Checks if the token is valid
     * @param username
     * @param token
     * @return
     */
    public boolean isTokenValid(String username, String token){
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    /**
     * Checks if the token is expired
     * @param verifier
     * @param token
     * @return
     */
    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    /**
     * Gets the subject from the token
     * @param token
     * @return
     */
    public String getSubject(String token){
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }
}
