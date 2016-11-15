
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
    '3.2.0' CODI,
    310 ORDRE,
    SYSDATE DATA_CREACIO,
    0 PROCES_EXECUTAT,
    1 SCRIPT_EXECUTAT,
    SYSDATE DATA_EXECUCIO_SCRIPT
FROM DUAL
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 310) = 0;

--------------------------------------------------------
-- 820 Simplificació de la configuració en les definicions de procés
--------------------------------------------------------

-- Crea les noves columnes
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD AMB_INFO_PROPIA NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_CAMP 		ADD EXPEDIENT_TIPUS_ID NUMBER(19,0);
ALTER TABLE HEL_CAMP		ADD DEFPROC_JBPMKEY  VARCHAR2(255 CHAR);
ALTER TABLE HEL_CAMP_AGRUP 	ADD EXPEDIENT_TIPUS_ID NUMBER(19,0);
ALTER TABLE HEL_DOCUMENT 	ADD EXPEDIENT_TIPUS_ID NUMBER(19,0);
ALTER TABLE HEL_TERMINI 	ADD EXPEDIENT_TIPUS_ID NUMBER(19,0);
ALTER TABLE HEL_ACCIO 		ADD EXPEDIENT_TIPUS_ID NUMBER(19,0);
ALTER TABLE HEL_ACCIO 		ADD DEFPROC_JBPMKEY  VARCHAR2(255 CHAR);

-- Modifica les columnes existents per a que puguin ser nules
ALTER TABLE HEL_CAMP MODIFY 		(DEFINICIO_PROCES_ID NULL);
ALTER TABLE HEL_CAMP_AGRUP MODIFY	(DEFINICIO_PROCES_ID NULL);
ALTER TABLE HEL_DOCUMENT MODIFY		(DEFINICIO_PROCES_ID NULL);
ALTER TABLE HEL_TERMINI MODIFY		(DEFINICIO_PROCES_ID NULL);
ALTER TABLE HEL_ACCIO MODIFY		(DEFINICIO_PROCES_ID NULL);

-- Distribució de camps de formulari
ALTER TABLE HEL_CAMP_TASCA ADD AMPLE_COLS NUMBER(10) DEFAULT 12;
ALTER TABLE HEL_CAMP_TASCA ADD BUIT_COLS NUMBER(10) DEFAULT 0;
ALTER TABLE HEL_CONSULTA_CAMP ADD AMPLE_COLS NUMBER(10) DEFAULT 12;
ALTER TABLE HEL_CONSULTA_CAMP ADD BUIT_COLS NUMBER(10) DEFAULT 0;

-- Crea els índexos per a les noves columnes
CREATE INDEX HEL_CAMP_EXPTIP_I 		ON HEL_CAMP 		(EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_CAMPAGRUP_EXPTIP_I ON HEL_CAMP_AGRUP 	(EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_DOCUMENT_EXPTIP_I 	ON HEL_DOCUMENT 	(EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_TERMINI_EXPTIP_I 	ON HEL_TERMINI 		(EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_ACCIO_EXPTIP_I 	ON HEL_ACCIO		(EXPEDIENT_TIPUS_ID);


-- Esborra les constraints actuals de les taules hel_camp i hel_camp_agrup sobre les columnes codi, definicio_proces_id
DECLARE
 PROCEDURE Borra_restr(p_tab VARCHAR2) IS
  src_cur pls_integer;
  src_rows  pls_integer;
  v_sql VARCHAR2(1000);
  CURSOR cur_rest IS
           
	SELECT UQ.CONSTRAINT_NAME nom
	FROM USER_CONSTRAINTS UQ
	    INNER JOIN USER_CONS_COLUMNS C1 ON C1.CONSTRAINT_NAME = UQ.CONSTRAINT_NAME
	    INNER JOIN USER_CONS_COLUMNS C2 ON C2.CONSTRAINT_NAME = UQ.CONSTRAINT_NAME
	WHERE
	    UQ.TABLE_NAME = UPPER(p_tab)    
	    AND C1.column_name LIKE UPPER('CODI')
	    AND C2.column_name LIKE UPPER('DEFINICIO_PROCES_ID');
    
 BEGIN
  FOR i IN cur_rest LOOP
      src_cur := DBMS_SQL.OPEN_CURSOR;
       v_sql := 'ALTER TABLE ' || p_tab || ' DROP CONSTRAINT ' || i.nom ;
       DBMS_SQL.PARSE(src_cur, v_sql, 2 );     -- Desactivar la restricción
       src_rows := DBMS_SQL.EXECUTE(src_cur);
       DBMS_SQL.CLOSE_CURSOR(src_cur); -- Cerrar el cursor
  END LOOP;
 END;
BEGIN
 Borra_restr('HEL_CAMP');
 Borra_restr('HEL_CAMP_AGRUP');
 Borra_restr('HEL_DOCUMENT');
 Borra_restr('HEL_ACCIO');
 Borra_restr('HEL_TERMINI');
END;

-- Afegeix les constraints úniques de 3 camps
ALTER TABLE HEL_CAMP 		ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_CAMP_AGRUP 	ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_DOCUMENT 	ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_TERMINI 	ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_ACCIO 		ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);

ALTER TABLE HEL_CAMP 		ADD ( CONSTRAINT HEL_EXPTIP_CAMP_FK 		FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID));
ALTER TABLE HEL_CAMP_AGRUP 	ADD ( CONSTRAINT HEL_EXPTIP_CAMPAGRUP_FK 	FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID));
ALTER TABLE HEL_DOCUMENT 	ADD ( CONSTRAINT HEL_EXPTIP_DOC_FK 			FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID));
ALTER TABLE HEL_TERMINI		ADD ( CONSTRAINT HEL_EXPTIP_TERMINI_FK 		FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID));
ALTER TABLE HEL_ACCIO 		ADD ( CONSTRAINT HEL_EXPTIP_ACCIO_FK 		FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID));

--------------------------------------------------------
-- 8889 Millores en el procés d'importació de tipus d'expedients
--------------------------------------------------------

-- Esborra la restricció de clau única [id entorn - codi] del comini i hi afegeix la clau [id entorn, id tipus expedient, codi]
DECLARE
 PROCEDURE Borra_unique(p_tab VARCHAR2) IS
  src_cur pls_integer;
  src_rows  pls_integer;
  v_sql VARCHAR2(1000);
  CURSOR cur_rest IS
           
	SELECT UQ.CONSTRAINT_NAME nom
	FROM USER_CONSTRAINTS UQ
	    INNER JOIN USER_CONS_COLUMNS C1 ON C1.CONSTRAINT_NAME = UQ.CONSTRAINT_NAME
	    INNER JOIN USER_CONS_COLUMNS C2 ON C2.CONSTRAINT_NAME = UQ.CONSTRAINT_NAME
	WHERE
	    UQ.TABLE_NAME = UPPER(p_tab)    
	    AND C1.column_name LIKE UPPER('CODI')
	    AND C2.column_name LIKE UPPER('ENTORN_ID');
    
 BEGIN
  FOR i IN cur_rest LOOP
      src_cur := DBMS_SQL.OPEN_CURSOR;
       v_sql := 'ALTER TABLE ' || p_tab || ' DROP CONSTRAINT ' || i.nom ;
       DBMS_SQL.PARSE(src_cur, v_sql, 2 );     -- Desactivar la restricción
       src_rows := DBMS_SQL.EXECUTE(src_cur);
       DBMS_SQL.CLOSE_CURSOR(src_cur); -- Cerrar el cursor
  END LOOP;
 END;
BEGIN
 Borra_unique('HEL_DOMINI');
 Borra_unique('HEL_CONSULTA');
END;

-- Afegeix les constraints úniques de 3 camps
ALTER TABLE HEL_DOMINI 		ADD UNIQUE (CODI, ENTORN_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_CONSULTA		ADD UNIQUE (CODI, ENTORN_ID, EXPEDIENT_TIPUS_ID);
