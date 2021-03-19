package com.goods2go.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.goods2go.config.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	
	public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
    	
    	String header = req.getHeader(SecurityConstants.HEADER_STRING);
    	String baerer = req.getParameter(SecurityConstants.HEADER_STRING);
    	UsernamePasswordAuthenticationToken authentication = null;

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
        	if (baerer == null) {
        		chain.doFilter(req, res);
        		return;        		
        	} else {
        		try {
                    authentication = getAuthentication(req, baerer);
                } catch(ExpiredJwtException e) {
                	//Token expired
                	res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                	return;
                }
        	}
        } else {
        	try {
                authentication = getAuthentication(req);
            } catch(ExpiredJwtException e) {
            	//Token expired
            	res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	return;
            }
        }
        
        try {
        	SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
        } catch(ExpiredJwtException e) {
        	//Token expired
        	res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	return;
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, String token) {
    	
        if (token != null) {
            // parse the token.
        	Claims claim = Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET.getBytes())
                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getBody();
        	
        	String user = claim.getSubject();
            String role = (String)claim.get("role");
            
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
            updatedAuthorities.add(authority);

            if (user != null && role != null) {
                return new UsernamePasswordAuthenticationToken(user, null, updatedAuthorities);
            }
            return null;
        }
        return null;
    }
    
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            // parse the token.
        	Claims claim = Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET.getBytes())
                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getBody();
        	
        	String user = claim.getSubject();
            String role = (String)claim.get("role");
            
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
            updatedAuthorities.add(authority);

            if (user != null && role != null) {
                return new UsernamePasswordAuthenticationToken(user, null, updatedAuthorities);
            }
            return null;
        }
        return null;
    }


}
