-- This should be run after initial installation of the Articulate-based webapp

-- add a column to track TinCanAPI packages apart from SCORM packages
ALTER TABLE SCORM_CONTENT_PACKAGE_T ADD IS_TINCANAPI bit(1) NULL;
ALTER TABLE SCORM_CONTENT_PACKAGE_T ALTER IS_TINCANAPI SET DEFAULT 0;

-- update existing rows
UPDATE SCORM_CONTENT_PACKAGE_T SET IS_TINCANAPI = 0 WHERE IS_TINCANAPI IS NULL;