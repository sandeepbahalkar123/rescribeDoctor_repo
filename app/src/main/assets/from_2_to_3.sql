BEGIN TRANSACTION;

ALTER TABLE "addNewPatient" ADD `patientAddressLine` TEXT;
ALTER TABLE "addNewPatient" ADD `patientAddressState` TEXT;
ALTER TABLE "addNewPatient" ADD `patientAddressCity` TEXT;
ALTER TABLE "addNewPatient" ADD `patientAddressArea` TEXT;
ALTER TABLE "addNewPatient" ADD `referenceTypeID` INTEGER;
ALTER TABLE "addNewPatient" ADD `referenceName` TEXT;
ALTER TABLE "addNewPatient" ADD `referencePhone` INTEGER;
ALTER TABLE "addNewPatient" ADD `referenceEmail` TEXT;


COMMIT;
