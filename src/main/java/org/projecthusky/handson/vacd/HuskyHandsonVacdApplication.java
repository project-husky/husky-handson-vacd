package org.projecthusky.handson.vacd;


import org.projecthusky.handson.vacd.admin.ImmunizationAdministrationBuisnessLogic;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class HuskyHandsonVacdApplication implements ApplicationRunner {

	private ImmunizationAdministrationBuisnessLogic immunizationAdministrationBuisnessLogic;


	public static void main(String[] args) {
		SpringApplication.run(HuskyHandsonVacdApplication.class, args);
	}

	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Hello Husky Hands-on Emed!");

		if (args.containsOption("createdis")) {
			log.info("create EMED DISPENSE DOCUMENT");
			immunizationAdministrationBuisnessLogic.createImmunizationAdministrationDocument();
		} else if (args.containsOption("readdis")) {
			log.info("read and validate EMED DISPENSE DOCUMENT");

		}

	}
}
