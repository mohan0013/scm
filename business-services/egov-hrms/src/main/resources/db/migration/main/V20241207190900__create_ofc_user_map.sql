create table eg_user_office_map(
id INTEGER PRIMARY KEY GENERATED ALWAYS AS identity,
user_id integer,
office_id integer,
active boolean,
NAA_code integer,
created_by varchar(64),
created_date timestamp,
last_modified_by varchar(64),
last_modified_date timestamp
);
