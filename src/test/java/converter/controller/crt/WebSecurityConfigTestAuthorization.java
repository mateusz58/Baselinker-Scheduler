package converter.controller.crt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@TestConfiguration
@EnableWebSecurity
public class WebSecurityConfigTestAuthorization implements WebMvcConfigurer {

  @Value("${endPointCrt}")
  private String url;

  @Bean
  public SecurityFilterChain tokenBasedSecurity(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(
            (s) -> s.requestMatchers(HttpMethod.POST, url).permitAll()
                .anyRequest().denyAll()
        )
        .headers()
        .cacheControl().disable()
        .and()
        .csrf().disable();
    return http.build();
  }
}
