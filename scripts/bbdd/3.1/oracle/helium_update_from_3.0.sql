
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
    '3.1.0' CODI,
    310 ORDRE,
    SYSDATE DATA_CREACIO,
    0 PROCES_EXECUTAT,
    1 SCRIPT_EXECUTAT,
    SYSDATE DATA_EXECUCIO_SCRIPT
FROM DUAL
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 310) = 0;


--------------------------------------------------------
-- TASQUES EN SEGÓN PLA
--------------------------------------------------------
ALTER TABLE
	JBPM_TASKINSTANCE 
ADD (
	MARCADAFINALITZAR_ TIMESTAMP(6),
	INICIFINALITZACIO_ TIMESTAMP(6),
	ERRORFINALITZACIO_ VARCHAR2(255 CHAR),
	TITOLACTUALITZAT_ NUMBER(1),
	SELECTEDOUTCOME_ VARCHAR2(255 CHAR)
);

UPDATE JBPM_TASKINSTANCE
SET 
	TITOLACTUALITZAT_ = 0
;

ALTER TABLE
	JBPM_TASKINSTANCE
MODIFY (
	TITOLACTUALITZAT_ NUMBER(1) DEFAULT 0 NOT NULL
);


ALTER TABLE
	HEL_TASCA
ADD (
	FIN_SEGON_PLA NUMBER(1)
);

UPDATE HEL_TASCA
SET FIN_SEGON_PLA = 1;

ALTER TABLE
	HEL_TASCA
MODIFY (
	FIN_SEGON_PLA NUMBER(1) DEFAULT 1 NOT NULL
);
