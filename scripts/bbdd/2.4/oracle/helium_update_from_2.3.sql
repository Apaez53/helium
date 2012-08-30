-- Retrocedir expedients
CREATE TABLE HEL_EXPEDIENT_LOG(
  ID                   NUMBER(19)               NOT NULL,
  ACCIO_TIPUS          NUMBER(10)               NOT NULL,
  ACCIO_PARAMS         VARCHAR2(255 CHAR),
  DATA                 TIMESTAMP(6)             NOT NULL,
  ESTAT                NUMBER(10)               NOT NULL,
  JBPM_LOGID           NUMBER(19),
  PROCESS_INSTANCE_ID  NUMBER(19),
  TARGET_ID            VARCHAR2(255 CHAR)       NOT NULL,
  USUARI               VARCHAR2(255 CHAR)       NOT NULL,
  EXPEDIENT_ID         NUMBER(19)               NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT HEL_EXPEDIENT_LOGS_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES HEL_EXPEDIENT (ID));

-- Permisos per grup
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD RESTRINGIR_GRUP NUMBER(1) DEFAULT 0;
ALTER TABLE HEL_EXPEDIENT ADD GRUP_CODI VARCHAR2(64 CHAR);

-- Tramitació massiva d'expedients
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD TRAM_MASSIVA NUMBER(1) DEFAULT 0;

-- Creació d'índexos
CREATE INDEX IDX_LOG_ACTION ON JBPM_LOG
(ACTION_);
CREATE INDEX IDX_LOG_CHILD ON JBPM_LOG
(CHILD_);
CREATE INDEX IDX_LOG_DESTINATIONNODE ON JBPM_LOG
(DESTINATIONNODE_);
CREATE INDEX IDX_LOG_INDEX_NEWBYTEARRAY ON JBPM_LOG
(NEWBYTEARRAY_);
CREATE INDEX IDX_LOG_NODE ON JBPM_LOG
(NODE_);
CREATE INDEX IDX_LOG_OLDBYTEARRAY ON JBPM_LOG
(OLDBYTEARRAY_);
CREATE INDEX IDX_LOG_PARENT ON JBPM_LOG
(PARENT_);
CREATE INDEX IDX_LOG_SOURCENODE ON JBPM_LOG
(SOURCENODE_);
CREATE INDEX IDX_LOG_SWIMINST ON JBPM_LOG
(SWIMLANEINSTANCE_);
CREATE INDEX IDX_LOG_TASKINSTANCE ON JBPM_LOG
(TASKINSTANCE_);
CREATE INDEX IDX_LOG_TOKEN ON JBPM_LOG
(TOKEN_);
CREATE INDEX IDX_LOG_TRANSITION ON JBPM_LOG
(TRANSITION_);
CREATE INDEX IDX_LOG_VARINSTANCE ON JBPM_LOG
(VARIABLEINSTANCE_);
CREATE INDEX IDX_BYTEARR_FILE ON JBPM_BYTEARRAY
(FILEDEFINITION_);
CREATE INDEX IDX_VARINST_TINSTANCE ON JBPM_VARIABLEINSTANCE
(TASKINSTANCE_);
CREATE INDEX IDX_VARINST_BARR ON JBPM_VARIABLEINSTANCE
(BYTEARRAYVALUE_);
CREATE INDEX IDX_MODINST_DF ON JBPM_MODULEINSTANCE
(TASKMGMTDEFINITION_);

-- Actualització a la nova versió --
INSERT INTO HEL_VERSIO (
    ID,
    CODI,
    ORDRE,
    DATA_CREACIO,
    PROCES_EXECUTAT,
    SCRIPT_EXECUTAT,
    DATA_EXECUCIO_SCRIPT)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL ID,
    '2.4.0' CODI,
    240 ORDRE,
    SYSDATE DATA_CREACIO,
    0 PROCES_EXECUTAT,
    1 SCRIPT_EXECUTAT,
    SYSDATE DATA_EXECUCIO_SCRIPT
FROM DUAL
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 240) = 0;
