ALTER TABLE refresh_token
    ADD COLUMN device_id varchar(64);

UPDATE refresh_token
SET device_id = 'unknown'
WHERE device_id IS NULL;

-- garde 1 token max par (user_id, device_id) en gardant le plus récent
DELETE FROM refresh_token rt
    USING refresh_token rt2
WHERE rt.user_id = rt2.user_id
  AND rt.device_id = rt2.device_id
  AND rt.created_at < rt2.created_at;

ALTER TABLE refresh_token
    ALTER COLUMN device_id SET NOT NULL;

CREATE UNIQUE INDEX ux_refresh_token_user_device
    ON refresh_token(user_id, device_id);

CREATE INDEX ix_refresh_token_token
    ON refresh_token(token);
