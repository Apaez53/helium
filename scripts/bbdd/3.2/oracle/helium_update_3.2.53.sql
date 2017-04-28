-------------------------------------------------------------------
-- #1034 Opció de no retrocedir documents
-------------------------------------------------------------------
ALTER TABLE HELIUM.HEL_DOCUMENT ADD (IGNORED NUMBER (1) DEFAULT 0);

-------------------------------------------------------------------
-- #1043 Herència de tipus d’expedient
-------------------------------------------------------------------
ALTER TABLE HELIUM.HEL_EXPEDIENT_TIPUS ADD (HERETABLE NUMBER (1) DEFAULT 0);
ALTER TABLE HELIUM.HEL_EXPEDIENT_TIPUS ADD (EXPEDIENT_TIPUS_PARE_ID NUMBER (19));

ALTER TABLE HEL_EXPEDIENT_TIPUS ADD (
  CONSTRAINT HEL_EXPTIPUS_PARE_EXPTIPUS_FK FOREIGN KEY (EXPEDIENT_TIPUS_PARE_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);
