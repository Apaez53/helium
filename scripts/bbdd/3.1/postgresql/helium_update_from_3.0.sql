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
    NEXTVAL('HIBERNATE_SEQUENCE') ID,
    '3.1.0' CODI,
    310 ORDRE,
    current_date DATA_CREACIO,
    false PROCES_EXECUTAT,
    true SCRIPT_EXECUTAT,
    current_date DATA_EXECUCIO_SCRIPT
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 310) = 0;

