create database pas character set utf8 collate utf8_general_ci;

create table t_user (
	user_id bigint primary key,
	username varchar(40),
	password varchar(50),
	org_name varchar(200),
	real_name varchar(50),
	create_date datetime,
	update_date datetime
);

create table t_role(
	role_id bigint primary key,
	name varchar(50),
	remarks varchar(200)
);

create table t_perm(
	perm_id bigint primary key,
	name varchar(50),
	tag varchar(50),
	remarks varchar(200)
);

create table t_role_user(
	role_id bigint,
	user_id bigint,
	primary key(role_id,user_id)
);

create table t_role_perm(
	role_id bigint,
	perm_id bigint,
	primary key(role_id,perm_id)
);

create table t_menu(
	menu_id bigint primary key,
	parent_id bigint,
	perm_id bigint,
	sort_id bigint,
	name varchar(50),
	url varchar(300),
	remarks varchar(500)
);

