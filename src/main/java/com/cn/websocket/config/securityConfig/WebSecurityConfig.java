package com.cn.websocket.config.securityConfig;

import com.cn.websocket.entity.AnonConfig;
import com.cn.websocket.security.filter.CorsFilter;
import com.cn.websocket.security.filter.JwtAuthenticationFilter;
import com.cn.websocket.security.provider.JwtAuthenticationProvider;
import com.cn.websocket.server.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationEntryPoint unauthenticationEntryPoint;

	@Autowired
	private AnonConfig anonConfig;
	
	@Bean(name = "jwtAuthenticationProvider")
	public JwtAuthenticationProvider jwtAuthenticationProvider() {
		final JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider();
		authenticationProvider.setJwtService(jwtService);
		return authenticationProvider;
	}
	
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}
	
	@Bean
    public Converter<String, String> StringConvert() {
        return new Converter<String, String>() {
            @Override
            public String convert(String source) {
                return StringUtils.trimToNull(source);
            }
        };
    }
 
    @Bean
    public Converter<String, LocalDate> LocalDateConvert() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                if (StringUtils.isBlank(source)) {
                    return null;
                }
                return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
 
        };
    }
 
    @Bean
    public Converter<String, LocalDateTime> LocalDateTimeConvert() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                if (StringUtils.isBlank(source)) {
                    return null;
                }
                return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
 
        };
    }
	
	@Bean
	public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(JwtAuthenticationFilter filter) {
		FilterRegistrationBean<JwtAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.setEnabled(false);
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}

	@Bean
	public CorsFilter corsFilterFilter() {
		return new CorsFilter();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider());
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}
    
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.exceptionHandling().authenticationEntryPoint(this.unauthenticationEntryPoint);
		http.addFilterBefore(jwtAuthenticationFilter(), BasicAuthenticationFilter.class);
		http.csrf().disable();
		anonymousRequest(http);
		http.authorizeRequests().antMatchers("/**").authenticated();
	}

	//白名单
	protected void anonymousRequest(HttpSecurity http) throws Exception {
		List<String> anonList = anonConfig.getAnon();
		for(String anon : anonList) {
			http.authorizeRequests().antMatchers(anon).permitAll();
		}
	}
	
}
