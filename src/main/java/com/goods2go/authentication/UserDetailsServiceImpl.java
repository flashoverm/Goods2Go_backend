package com.goods2go.authentication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.goods2go.models.User;
import com.goods2go.repositories.UserDao;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
	@Autowired
	private UserDao userDao;

    public UserDetailsServiceImpl(UserDao applicationUserRepository) {
        this.userDao = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.findOneByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        
        if(!user.isActive() || !user.isMailvalidated()) {
            throw new UsernameNotFoundException(email);
        }
       
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
        updatedAuthorities.add(authority);
        
        return new org.springframework.security.core.userdetails.User(
        		user.getEmail(), user.getPassword(), updatedAuthorities);
    }
}
