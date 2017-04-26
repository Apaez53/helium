-------------------------------------------------------------------
-- #1034 Opció de no retrocedir documents
-------------------------------------------------------------------
ALTER TABLE HELIUM.HEL_DOCUMENT ADD COLUMN "IGNORED" BOOLEAN DEFAULT FALSE;

-------------------------------------------------------------------
-- #1043 Herència de tipus d’expedient
-------------------------------------------------------------------
ALTER TABLE HELIUM.HEL_EXPEDIENT_TIPUS ADD COLUMN "HERETABLE" BOOLEAN DEFAULT FALSE;
ALTER TABLE HELIUM.HEL_EXPEDIENT_TIPUS ADD COLUMN "HERETAT_ID" BIGINT;

ALTER TABLE HEL_EXPEDIENT_TIPUS ADD 
  CONSTRAINT HEL_HERETAT_EXPTIPUS_FK 
 FOREIGN KEY (HERETAT_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

