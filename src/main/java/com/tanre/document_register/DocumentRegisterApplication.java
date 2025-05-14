package com.tanre.document_register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication (
		exclude = { SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class
		}


)
public class DocumentRegisterApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentRegisterApplication.class, args);
	}

}
