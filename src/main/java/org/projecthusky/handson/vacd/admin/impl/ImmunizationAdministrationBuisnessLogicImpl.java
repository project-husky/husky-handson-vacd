/*
 * This code is made available under the terms of the Eclipse Public License v1.0
 * in the github project https://github.com/project-husky/husky there you also
 * find a list of the contributors and the license information.
 *
 * This project has been developed further and modified by the joined working group Husky
 * on the basis of the eHealth Connector opensource project from June 28, 2021,
 * whereas medshare GmbH is the initial and main contributor/author of the eHealth Connector.
 *
 */
package org.projecthusky.handson.vacd.admin.impl;

import java.util.Date;
import java.util.UUID;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.PositiveIntType;
import org.hl7.fhir.r4.model.Quantity;
import org.projecthusky.fhir.core.ch.resource.r4.ChCorePatientEpr;
import org.projecthusky.fhir.vacd.ch.common.resource.r4.ChVacdImmunizationAdministrationDocument;
import org.projecthusky.handson.vacd.admin.ImmunizationAdministrationBuisnessLogic;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ibm.icu.util.Calendar;

import ca.uhn.fhir.context.FhirContext;

/**
 * 
 */
@Service
public class ImmunizationAdministrationBuisnessLogicImpl
		implements ImmunizationAdministrationBuisnessLogic {

	@Override
	public void createImmunizationAdministrationDocument() {
		ChVacdImmunizationAdministrationDocument chVaccinationAdminDocument = new ChVacdImmunizationAdministrationDocument();

		ChCorePatientEpr patient = new ChCorePatientEpr();
		patient.addAddress(new Address().setCity("Musterhausen").setPostalCode("1234")
				.addLine("Mustergasse 11").setCountry("CH")) //
				.addName(new HumanName().setFamily("Muster").addGiven("Max"));
		patient.setActive(true);
		Calendar dob = Calendar.getInstance();
		dob.set(1968, 6, 15);
		patient.setBirthDate(dob.getTime());
		patient.addIdentifier().setSystem("urn:ietf:rfc:3986")
				.setValue("urn:uuid:" + UUID.randomUUID().toString());
		patient.setId(UUID.randomUUID().toString());

		chVaccinationAdminDocument.setPatient(patient);



		FhirContext ctx = FhirContext.forR4();
		LoggerFactory.getLogger(getClass()).info(ctx.newJsonParser().setPrettyPrint(true)
				.encodeResourceToString(chVaccinationAdminDocument));

	}

}
