BEGIN TRANSACTION;
ALTER TABLE "my_records" ADD `recordType` TEXT;
ALTER TABLE "my_records" ADD `orderId` TEXT;
ALTER TABLE "my_records" ADD `fileId` TEXT;
ALTER TABLE "addNewPatient" ADD `isDead` INTEGER;
COMMIT;
