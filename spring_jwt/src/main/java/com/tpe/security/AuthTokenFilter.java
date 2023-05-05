package com.tpe.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired // User'a ulasabilmek icin enjekte edildi.
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Jwt token'i request'in iceerisinden almamiz gerekir
        String jwtToken = parseJwt(request);

        try {
            if (jwtToken != null && jwtUtils.validateToken(jwtToken)) {

                String userName = jwtUtils.getUserNameFromJwtToken(jwtToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                //Buradan itibaren Authenticate edilmis kullaniciyi context'e gönderiyoruz

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);

    }

    //!!! Request'in icindeki JWT token'i cikartan metot
    private String parseJwt(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        // "Bearer lkjghlkjgfhkjghfk.jk43k5h3kgkj.3453kj4h5kjhrkd"
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // !!! alttaki methodun permitAll() dan farki : permitAll() da kimlik kontrolu yapilmayacak
    // end-pointler velirtilirken , shouldNotFilter() da icinde bulundugumuz filtreye
    // girmesini istemedigimiz end-pointleri yaziyoruz
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        AntPathMatcher antMatcher = new AntPathMatcher();
        return  antMatcher.match("/register",request.getServletPath()) ||
                antMatcher.match("/login",request.getServletPath());

    }
}
