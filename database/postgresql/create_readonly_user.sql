CREATE ROLE readonly;
GRANT CONNECT ON DATABASE my_database TO readonly;
GRANT USAGE ON SCHEMA my_schema TO readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA my_schema TO readonly;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA my_schema TO readonly;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA my_schema TO readonly;

-- grant access to all table which will be created in the future
ALTER DEFAULT PRIVILEGES IN SCHEMA my_schema GRANT SELECT ON TABLES TO readonly;
ALTER DEFAULT PRIVILEGES IN SCHEMA my_schema GRANT SELECT ON SEQUENCES TO readonly;
ALTER DEFAULT PRIVILEGES IN SCHEMA my_schema GRANT EXECUTE ON FUNCTIONS TO readonly;

CREATE USER my_user WITH PASSWORD 'my_user_password';
GRANT readonly TO my_user;

