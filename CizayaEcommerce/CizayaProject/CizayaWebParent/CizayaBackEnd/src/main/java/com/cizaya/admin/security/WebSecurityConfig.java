package com.cizaya.admin.security;

import com.cizaya.common.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // tre ve 1 phien ban moi cua BCryptPasswordEncoder
    @Bean
    public UserDetailsService userDetailsService() {
        return new CizayaUserDetailService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    //su dung nha cung cap DaoAuthenticationProvider
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    //can ghi de mot phuong thuc khac de dinh cau hinh nha cung cap xac thuc
    //authenticationProvider() tạo một thể hiện của DaoAuthenticationProvider và định cấu hình nó,
    // trong khi phương thức configure(AuthenticationManagerBuilder auth) báo cho
    // Spring Security sử dụng authenticationProvider của bạn để xác thực.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    // cho phep cong khai den moi class.
    //phuong thuc ghi de configure co
    // tham so HttpSecurity bieu thi cau hinh muon thiet lap
    //.anyRequest().authenticated() chi dinh trang dang nhap tuy chinh- phan quyen
    // .formLogin()
    //                .loginPage("/login")
    //                .usernameParameter("email")//
    //                .permitAll()
    // formLogin() cho phep moi nguoi su dung - dang nhap thanh cong voi email
    //.and().logout().permitAll() chp phep moi nguoi deu co the logout
    //.and().rememberMe(); thiết lập sơ đồ xác thực và ủy quyền
    //.key("AbcDefgHijKlmnOpqrs_1234567890") nhu 1 khoa co dinh de tu dong dang nhap khi tat chuong trinh
    //.tokenValiditySeconds(7 * 24 * 60 * 60);dinh cau hinh de ghi nho trong 7 ngay
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/users/**","/settings/**").hasAuthority("Admin")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")//
                .permitAll()
                .and().logout().permitAll()
                .and()
                    .rememberMe()
                        .key("AbcDefgHijKlmnOpqrs_1234567890")
                         .tokenValiditySeconds(7 * 24 * 60 * 60);

    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }
}
