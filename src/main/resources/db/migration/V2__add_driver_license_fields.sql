ALTER TABLE drivers ADD COLUMN license_number VARCHAR(50) NOT NULL DEFAULT '';
ALTER TABLE drivers ADD COLUMN license_expiry DATE NOT NULL DEFAULT '2030-01-01';
ALTER TABLE drivers ADD COLUMN license_type VARCHAR(20);
ALTER TABLE drivers ADD COLUMN aadhaar_number VARCHAR(20);
ALTER TABLE drivers ADD COLUMN pan_number VARCHAR(15);

-- Remove the temporary defaults after adding
ALTER TABLE drivers ALTER COLUMN license_number DROP DEFAULT;
ALTER TABLE drivers ALTER COLUMN license_expiry DROP DEFAULT;

-- Add unique constraint
ALTER TABLE drivers ADD CONSTRAINT uq_driver_license_number UNIQUE (license_number);