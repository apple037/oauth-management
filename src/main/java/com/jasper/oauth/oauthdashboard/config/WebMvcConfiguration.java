package com.jasper.oauth.oauthdashboard.config;

import com.jasper.oauth.oauthdashboard.interceptor.MdcInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@AllArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

  private final MdcInterceptor mdcInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(mdcInterceptor);
  }
}
