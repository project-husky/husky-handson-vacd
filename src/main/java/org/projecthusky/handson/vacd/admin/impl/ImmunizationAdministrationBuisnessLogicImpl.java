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

import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.PositiveIntType;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.projecthusky.fhir.core.ch.resource.r4.ChCoreOrganizationEpr;
import org.projecthusky.fhir.core.ch.resource.r4.ChCorePatientEpr;
import org.projecthusky.fhir.core.ch.resource.r4.ChCorePractitionerEpr;
import org.projecthusky.fhir.core.ch.resource.r4.ChCorePractitionerRole;
import org.projecthusky.fhir.core.ch.resource.r4.ChCorePractitionerRoleEpr;
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

		{
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
			patient.setGender(AdministrativeGender.MALE);

			chVaccinationAdminDocument.setPatient(patient);
		}
		ChCorePractitionerRoleEpr practitionerRole = new ChCorePractitionerRoleEpr();
		{

			practitionerRole.setId(UUID.randomUUID().toString());
			ChCorePractitionerEpr practitionerEpr = practitionerRole.addPractitioner();
			practitionerEpr.addIdentifier().setSystem("urn:oid:2.51.1.3").setValue("7621020561918");
			practitionerEpr.addName().setFamily("Knochenschlosser").addGiven("Hans");

			ChCoreOrganizationEpr organizationEpr = practitionerRole.addOrganization();
			organizationEpr.addIdentifier().setSystem("urn:oid:2.51.1.3").setValue("7631022371118");
			organizationEpr.setName("Praxis Dr. Knochenschlosser");

			chVaccinationAdminDocument.addAuthor(practitionerRole, new Date());
		}
		{
			var immunization = chVaccinationAdminDocument.addImmunization();
			// immunization.setVaccineCode(new CodeableConcept(new
			// Coding("http://snomed.info/sct",
			// "871878002", "Diphtheria and pertussis and poliomyelitis and
			// tetanus vaccine")))
			// .setLotNumber("A24151");

			immunization.setVaccineCode(new CodeableConcept(
					new Coding("http://fhir.ch/ig/ch-vacd/CodeSystem/ch-vacd-swissmedic-cs", "681",
							"Boostrix Polio")))
					.setLotNumber("A24151");
			immunization.setOccurrence(new DateTimeType(new Date()));
			var immunProtAppl = immunization.getProtocolAppliedFirstRep();
			immunProtAppl.setDoseNumber(new PositiveIntType(1))
					.setSeriesDoses(new PositiveIntType(5));
			immunProtAppl
					.addTargetDisease(new CodeableConcept(
							new Coding("http://snomed.info/sct", "76902006", "Tetanus (disorder)")))//
					.addTargetDisease(new CodeableConcept(new Coding("http://snomed.info/sct",
							"27836007", "Pertussis (disorder)")))//
					.addTargetDisease(new CodeableConcept(
							new Coding("http://snomed.info/sct", "397430003", "Diphtheria")))//
					.addTargetDisease(new CodeableConcept(new Coding("http://snomed.info/sct",
							"398102009", "Acute poliomyelitis")));
		}
		{
			Calendar basicImmunCal = Calendar.getInstance();
			basicImmunCal.set(1978, 5, 5);
			chVaccinationAdminDocument.addBasicImmunization()//
					.setCode(new CodeableConcept(new Coding(
							"http://fhir.ch/ig/ch-vacd/CodeSystem/ch-vacd-basic-immunization-cs",
							"bi-dtpa", "Received basic immunization against DTPa in childhood.")))//
					.setOnset(new DateTimeType(basicImmunCal.getTime()))//
					.setRecordedDate(new Date());
		}
		{
			var allerg = chVaccinationAdminDocument.addAllergyIntolerance();
			// allerg.setCode(new CodeableConcept(new
			// Coding("http://snomed.info/sct", "1303850003",
			// "Adverse reaction to component of vaccine product containing
			// Tick-borne encephalitis virus antigen")));
			allerg.setCode(new CodeableConcept(new Coding("http://snomed.info/sct", "716186003",
					"No known allergy (situation)")));

			Calendar allOcc = Calendar.getInstance();
			allOcc.set(2000, 7, 28);
			allerg.setLastOccurrence(allOcc.getTime());
			allerg.setClinicalStatus(new CodeableConcept(
					new Coding("http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical",
							"active", "Active")));
		}
		{
			chVaccinationAdminDocument.addLaboratoryAndSerology()//
					.setCode(new CodeableConcept(new Coding("http://loinc.org", "22502-9",
							"Measles virus IgG Ab [Titer] in Serum")))//
					.setValue(new Quantity().setCode("[iU]/L").setUnit("[iU]/L")
							.setSystem("http://unitsofmeasure.org").setValue(99))//
					.setStatus(ObservationStatus.FINAL)//
					.setEffective(new DateTimeType(new Date()))//
					.addPerformer(new Reference("urn:uuid:" + practitionerRole.getId()));
		}
		{
			chVaccinationAdminDocument.addMedicalProblem()//
					.setCode(new CodeableConcept(
							new Coding("http://snomed.info/sct", "77386006", "Pregnancy")))//
					.setRecordedDate(new Date());
		}
		{
			chVaccinationAdminDocument.addPastIllness()//
					.setCode(new CodeableConcept(
							new Coding("http://snomed.info/sct", "14189004", "Measles (disorder)")))//
					.setRecordedDate(new Date());
		}
		String jsonString = FhirContext.forR4().newJsonParser().setPrettyPrint(true)
				.encodeResourceToString(chVaccinationAdminDocument);
		LoggerFactory.getLogger(getClass()).info(jsonString);
		try {
			FileOutputStream fos = new FileOutputStream(
					"./target/ImmunizationAdministrationDocument.json");
			fos.write(jsonString.getBytes());
			fos.close();
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error writing JSON to file", e);
		}

	}

}
