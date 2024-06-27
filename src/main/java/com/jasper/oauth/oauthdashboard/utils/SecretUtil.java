package com.jasper.oauth.oauthdashboard.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Slf4j
public class SecretUtil {
  // Bcrypt password encoder
  public static String encodePassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  // Bcrypt password matcher
  public static boolean matchPassword(String password, String hashedPassword) {
    return BCrypt.checkpw(password, hashedPassword);
  }
}
