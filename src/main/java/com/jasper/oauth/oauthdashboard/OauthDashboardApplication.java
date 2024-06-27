package com.jasper.oauth.oauthdashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class OauthDashboardApplication {

  public static void main(String[] args) {
    SpringApplication.run(OauthDashboardApplication.class, args);
  }
}
