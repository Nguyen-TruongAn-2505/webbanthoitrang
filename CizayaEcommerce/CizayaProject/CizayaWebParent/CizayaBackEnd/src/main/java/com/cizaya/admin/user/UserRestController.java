package com.cizaya.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
	@Autowired
	private UserService service;

	// nhan request /users/check_email kem duong link co gan email, va chuoi email
	@PostMapping("/users/check_email")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
		System.out.println(email);
		return service.isEmailUnique(id, email) ? "OK" : "Duplicate";
	}
}
