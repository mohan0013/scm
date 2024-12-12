ALTER TABLE eg_user_office_map ALTER COLUMN created_date TYPE bigint USING (to_char(created_date,'yyyymmddhh24miss')::bigint);
ALTER TABLE eg_user_office_map ALTER COLUMN last_modified_date TYPE bigint USING (to_char(last_modified_date,'yyyymmddhh24miss')::bigint);
