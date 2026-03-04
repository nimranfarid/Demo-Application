package com.example.LdapSample.security;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.util.NameTransformer.Chained;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfiguration{

	private final String JWT_SECRET = "TestSecretKey";
    private final long JWT_EXPIRATION = 86400000L; // 24 hours
    
    
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .ldapAuthentication()
            .userDnPatterns("uid={0},ou=people")
            .groupSearchBase("ou=groups")
            .contextSource()
            .url("ldap://localhost:8389/dc=example,dc=com")
            .managerDn("uid=admin,ou=system")
            .managerPassword("secret");
    }
    
  
    public String generateToken(Authentication auth) {
        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("authorities", auth.getAuthorities().stream()
                        .map(role -> role.getAuthority())
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }
    class JwtAuthenticationFilter extends OncePerRequestFilter {

        private AuthenticationManager authManager;

        public JwtAuthenticationFilter(AuthenticationManager authManager) {
            this.authManager = authManager;
        }


			String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = Jwts.parser()
                        .setSigningKey(JWT_SECRET)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();

                if (username != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
//            chain.doFilter(request, response);
<<<<<<< HEAD
            
        }

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {
			System.out.println("+++++++++++++++++++++++++++++++");

			System.out.println("+++++++++++++++++++++++++++++++");

			System.out.println("+++++++++++++++++++++++++++++++");

			System.out.println("+++++++++++++++++++++++++++++++");

			System.out.println("+++++++++++++++++++++++++++++++");

			System.out.println("+++++++++++++++++++++++++++++++");

			System.out.println("+++++++++++++++++++++++++++++++");

			// TODO Auto-generated method stub
			
		}			
=======
        }

		
>>>>>>> Demo
		}
}


