package com.tauru.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ShopApplication {

	public static void main(String[] args) throws MessagingException { SpringApplication.run(ShopApplication.class, args);
	}
}
