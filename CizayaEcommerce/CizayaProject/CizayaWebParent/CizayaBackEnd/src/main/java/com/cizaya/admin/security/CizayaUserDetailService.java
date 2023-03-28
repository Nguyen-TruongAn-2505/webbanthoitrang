package com.cizaya.admin.security;

import com.cizaya.admin.user.controller.UserRepository;
import com.cizaya.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class CizayaUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    //tham chieu den repo de lay ham timkiem user theo Email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =  userRepo.getUserByEmail(email);
        if(user != null){
            return new CizayaUserDetail(user);
        }
        throw new UsernameNotFoundException("count not find user with email :" + email);
    }
}
