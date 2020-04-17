--创建表
CREATE TABLE `db_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `table_name` varchar(50) NOT NULL COMMENT '表名',
  `table_title` varchar(100) NOT NULL COMMENT '中文名',
  `app` varchar(100) DEFAULT NULL COMMENT '所属应用',
  `module` varchar(100) DEFAULT NULL COMMENT '所属模块',
  `updator` varchar(100) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `partitioned` varchar(10) DEFAULT NULL COMMENT '是否分区',
  `partition_field` varchar(100) DEFAULT NULL COMMENT '分区字段',
  `partition_comment` varchar(100) DEFAULT NULL COMMENT '分区说明',
  `comment` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=606 DEFAULT CHARSET=utf8;

CREATE TABLE `db_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字段ID',
  `table_name` varchar(50) NOT NULL COMMENT '表名',
  `field_name` varchar(50) NOT NULL COMMENT '字段名',
  `field_title` varchar(100) DEFAULT NULL COMMENT '字段说明',
  `data_type` varchar(50) NOT NULL COMMENT '数据类型',
  `data_length` int(11) DEFAULT NULL COMMENT '长度',
  `nullable` varchar(1) DEFAULT NULL COMMENT '允许空',
  `default_value` varchar(100) DEFAULT NULL COMMENT '默认值',
  `comment` varchar(500) DEFAULT NULL COMMENT '备注',
  `sort_order` int(11) DEFAULT NULL COMMENT '排序',
  `updator` varchar(100) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`column_id`),
  KEY `db_column_table_name_IDX` (`table_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8871 DEFAULT CHARSET=utf8 COMMENT='数据库字段';

CREATE TABLE `db_index` (
  `index_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '索引ID',
  `index_name` varchar(100) NOT NULL COMMENT '索引名称',
  `index_type` varchar(20) NOT NULL COMMENT '索引类型',
  `table_name` varchar(50) NOT NULL COMMENT '表名',
  `uniqueness` varchar(20) DEFAULT NULL COMMENT '唯一性',
  `column_name` varchar(100) NOT NULL COMMENT '索引字段',
  `updator` varchar(100) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`index_id`)
) ENGINE=InnoDB AUTO_INCREMENT=792 DEFAULT CHARSET=utf8;

CREATE TABLE `db_change_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `table_name` varchar(50) NOT NULL COMMENT '表名',
  `field_name` varchar(50) DEFAULT NULL COMMENT '字段名',
  `action_type` varchar(100) NOT NULL COMMENT '操作类型',
  `content` varchar(1000) NOT NULL COMMENT '修改内容',
  `sql_log` varchar(1000) DEFAULT NULL COMMENT '生成SQL',
  `updator` varchar(100) NOT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

--更新sort_order
create table column_order as
select column_id, column_id - (select min(column_id) from db_column c1 where c1.table_name =dc.table_name ) as sort_order from db_column dc;

create index idx_column_order on column_order (column_id );
update db_column set sort_order = (select sort_order  from column_order c where db_column .column_id =c.column_id) ;

--创建唯一索引
create unique index uidx_table_name on db_table(table_name);
create unique index uidx_table_column on db_column(table_name, field_name);

--更新APP
update db_table set app='EPSP';
update db_table set app='TMP' where table_name like '%0%' or table_name like '%1%' or table_name like 'HLP%' or table_name like 'FLW%' or table_name like 'QRTZ%' or table_name like 'RECO%';

update db_table set module='ACC' where app='EPSP' and table_name like 'ACC%';
update db_table set module='ACS' where app='EPSP' and table_name like 'ACS%';
update db_table set module='CAS' where app='EPSP' and table_name like 'CAS%';
update db_table set module='CHK' where app='EPSP' and table_name like 'CHK%';
update db_table set module='CLR' where app='EPSP' and table_name like 'CLR%';
update db_table set module='CUM' where app='EPSP' and table_name like 'CUM%';
update db_table set module='CUST' where app='EPSP' and table_name like 'CUST%';
update db_table set module='PAS' where app='EPSP' and table_name like 'PAS%';
update db_table set module='SETT' where app='EPSP' and table_name like 'SETT%';
update db_table set module='PAY' where app='EPSP' and table_name like 'PAY%';
update db_table set module='TXS' where app='EPSP' and table_name like 'TXS%';
update db_table set module='TERM' where app='EPSP' and table_name like 'TERM%';
update db_table set module='RC' where app='EPSP' and table_name like 'RC%';

update db_table set table_title=comment where length(comment) < 30;
update db_column set field_title=comment where length(comment) < 30;

