CREATE PUBLIC SYNONYM HEL_ACCIO FOR HEL_ACCIO;
CREATE PUBLIC SYNONYM HEL_REDIR FOR HEL_REDIR;
CREATE PUBLIC SYNONYM HEL_ALERTA FOR HEL_ALERTA;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ACCIO TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_REDIR TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ALERTA TO WWW_HELIUM;
