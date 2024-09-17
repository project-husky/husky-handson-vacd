package org.projecthusky.handson.vacd;


import org.projecthusky.handson.vacd.admin.ImmunizationAdministrationBuisnessLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class HuskyHandsonVacdApplication implements ApplicationRunner {

	@Autowired
	private ImmunizationAdministrationBuisnessLogic immunizationAdministrationBuisnessLogic;


	public static void main(String[] args) {
		SpringApplication.run(HuskyHandsonVacdApplication.class, args);
	}

	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Hello Husky Hands-on VACD!");

		if (args.containsOption("createia")) {
			log.info("create VACD IMMUNIZATION ADMINISTRATION DOCUMENT");
			immunizationAdministrationBuisnessLogic.createImmunizationAdministrationDocument();
		} else if (args.containsOption("readia")) {
			log.info("read and validate VACD IMMUNIZATION ADMINISTRATION DOCUMENT");

		}

	}
}
