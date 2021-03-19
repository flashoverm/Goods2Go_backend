package com.goods2go.authentication;

import org.springframework.security.core.authority.AuthorityUtils;

import com.goods2go.models.User;

public class TokenUser extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = 1L;
	private User user;

    public TokenUser(User user) {
        super( String.valueOf(user.getId()), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()  )  );
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getRole() {
        return user.getRole().toString();
    }
}
