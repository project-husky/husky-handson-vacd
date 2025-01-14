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
package org.projecthusky.handson.vacd.vacrec.impl;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.PositiveIntType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.projecthusky.fhir.core.ch.resource.r4.ChCoreOrganizationEpr;
import org.projecthusky.fhir.core.ch.resource.r4.ChCorePatientEpr;
import org.projecthusky.fhir.core.ch.resource.r4.ChCorePractitionerEpr;
import org.projecthusky.fhir.core.ch.resource.r4.ChCorePractitionerRoleEpr;
import org.projecthusky.fhir.vacd.ch.common.resource.r4.ChVacdVaccinationRecordDocument;
import org.projecthusky.fhir.vacd.ch.common.utils.ChVacdImmunizationUtils;
import org.projecthusky.handson.vacd.vacrec.VaccinationRecordBuisnessLogic;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ibm.icu.util.Calendar;

import ca.uhn.fhir.context.FhirContext;

/**
 * 
 */
@Service
public class VaccinationRecordBuisnessLogicImpl implements VaccinationRecordBuisnessLogic {

	@Override
	public void createVaccinationRecordDocument() {
		ChVacdVaccinationRecordDocument chVaccinationRecordDocument = new ChVacdVaccinationRecordDocument();

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

			chVaccinationRecordDocument.setPatient(patient);
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

			chVaccinationRecordDocument.addAuthor(practitionerRole, new Date());
		}
		{
			var immunization = chVaccinationRecordDocument.addImmunization();
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
			immunProtAppl.setTargetDisease(ChVacdImmunizationUtils
					.getTargetDiseaseFromVaccineCode(immunization.getVaccineCode()));
		}
		{
			var immunization = chVaccinationRecordDocument.addImmunization();
			immunization.setVaccineCode(new CodeableConcept(new Coding("http://snomed.info/sct",
					"871878002", "Diphtheria and pertussis and poliomyelitis and tetanus vaccine")))
					.setLotNumber("A24151");

//			immunization.setVaccineCode(new CodeableConcept(
//					new Coding("http://fhir.ch/ig/ch-vacd/CodeSystem/ch-vacd-swissmedic-cs", "681",
//							"Boostrix Polio")))
//					.setLotNumber("A24151");
			immunization.setOccurrence(new DateTimeType(new Date()));
			var immunProtAppl = immunization.getProtocolAppliedFirstRep();
			immunProtAppl.setDoseNumber(new PositiveIntType(1))
					.setSeriesDoses(new PositiveIntType(5));
			immunProtAppl.setTargetDisease(ChVacdImmunizationUtils
					.getTargetDiseaseFromVaccineCode(immunization.getVaccineCode()));
		}
		{
			Calendar basicImmunCal = Calendar.getInstance();
			basicImmunCal.set(1978, 5, 5);
			chVaccinationRecordDocument.addBasicImmunization()//
					.setCode(new CodeableConcept(new Coding(
							"http://fhir.ch/ig/ch-vacd/CodeSystem/ch-vacd-basic-immunization-cs",
							"bi-dtpa", "Received basic immunization against DTPa in childhood.")))//
					.setOnset(new DateTimeType(basicImmunCal.getTime()))//
					.setRecordedDate(new Date());
		}
		{
			var allerg = chVaccinationRecordDocument.addAllergyIntolerance();
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
			chVaccinationRecordDocument.addLaboratoryAndSerology()//
					.setCode(new CodeableConcept(new Coding("http://loinc.org", "22502-9",
							"Measles virus IgG Ab [Titer] in Serum")))//
					.setValue(new Quantity().setCode("[iU]/L").setUnit("[iU]/L")
							.setSystem("http://unitsofmeasure.org").setValue(99))//
					.setStatus(ObservationStatus.FINAL)//
					.setEffective(new DateTimeType(new Date()))//
					.addPerformer(new Reference("urn:uuid:" + practitionerRole.getId()));
		}
		{
			chVaccinationRecordDocument.addMedicalProblem()//
					.setCode(new CodeableConcept(
							new Coding("http://snomed.info/sct", "77386006", "Pregnancy")))//
					.setRecordedDate(new Date());
		}
		{
			chVaccinationRecordDocument.addPastIllness()//
					.setCode(new CodeableConcept(
							new Coding("http://snomed.info/sct", "14189004", "Measles (disorder)")))//
					.setRecordedDate(new Date());
		}
		String jsonString = FhirContext.forR4().newJsonParser().setPrettyPrint(true)
				.encodeResourceToString(chVaccinationRecordDocument);
		LoggerFactory.getLogger(getClass()).info(jsonString);
		try {
			FileOutputStream fos = new FileOutputStream("./target/VaccinationRecordDocument.json");
			fos.write(jsonString.getBytes());
			fos.close();
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error writing JSON to file", e);
		}
	}

}
