package converter.configuration.WebSecurity.CRT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigTestAuthorization {

  @Value("${endPointCrt}")
  private String url;

  //    @Bean
//    public SecurityFilterChain tokenBasedSecurity(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(
//                        (s) -> s.requestMatchers(HttpMethod.POST, url).permitAll()
//                                .requestMatchers(HttpMethod.GET, url).permitAll()
//                                .anyRequest().denyAll()
//                )
//                .headers()
//                .cacheControl().disable()
//                .and()
//                .csrf().disable();
//        return http.build();
//    }
  @Bean
  public SecurityFilterChain disableSecurity(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests((authz) -> authz
            .anyRequest().permitAll()
        )
        .csrf().disable();
    return http.build();
  }
}
