package ru.i_novus;

import net.n2oapp.platform.security.autoconfigure.N2oPlatformResourceServerConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends N2oPlatformResourceServerConfigurerAdapter {
    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry requests) {
        requests.mvcMatchers("/api/**").permitAll();
    }
}
