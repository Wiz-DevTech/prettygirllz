ALTER TABLE users ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT true;
COMMENT ON COLUMN users.enabled IS 'Indicates if the user account is enabled';