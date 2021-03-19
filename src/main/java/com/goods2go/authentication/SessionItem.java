package com.goods2go.authentication;

import lombok.Data;

@Data
public class SessionItem {
    private String  token;
    private String	email;
    private String 	role;
}
