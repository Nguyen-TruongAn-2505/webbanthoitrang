package com.cizaya.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({ "com.cizaya.common.entity", "com.cizaya.admin.user" })
public class CizayaBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(CizayaBackEndApplication.class, args);
	}

}
