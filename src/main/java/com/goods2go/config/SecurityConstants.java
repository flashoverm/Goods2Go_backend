package com.goods2go.config;

public class SecurityConstants {
	
	public static final String SECRET = "g2gftw";
	//private static final long VALIDITY_TIME_MS = 10 * 24 * 60 * 60 * 1000;// 10 days Validity
	public static final long VALIDITY_TIME_MS =  2 * 60 * 60 * 1000; // 2 hours  validity
	//public static final long VALIDITY_TIME_MS =  20 * 1000; // 20 sec  validity
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/user/signup";

}
