create table t_config_category(
	category_id bigint primary key,
	code varchar(50),
	name varchar(50),
	remarks varchar(400)
);

create table t_config(
	config_id bigint primary key,
	category_code varchar(50),
	config_key varchar(50),
	config_value varchar(4000),
	name varchar(50),
	remarks varchar(400)
);

create table t_sequence(
	category varchar(100) primary key,
	next_val bigint,
	version bigint,
	step int default 10,
	remarks varchar(400),
	create_date datetime,
	update_date datetime
);