DROP TABLE trade_day;

CREATE TABLE `trade_day` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trade_day` varchar(20) NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_trade_day` (`trade_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table daily_un_restoration_stock_data;
CREATE TABLE `daily_un_restoration_stock_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trade_day_rank_no` int(11) NOT NULL DEFAULT '0',
  `stat_date` varchar(20) NOT NULL COMMENT '交易日期',
  `stock_id` varchar(20) NOT NULL,
  `adj_factor` float NOT NULL COMMENT '复权因子',
  `restoration_status` int(2) NOT NULL DEFAULT '0' COMMENT '1为需要调整历史前复权数据，0不需要',
  `open` float NOT NULL,
  `close` float NOT NULL,
  `low` float NOT NULL,
  `high` float NOT NULL,
  `pre_close` float NOT NULL,
  `change` float NOT NULL COMMENT '涨跌额',
  `vol` float NOT NULL COMMENT '成交量 （手）',
  `amount` float NOT NULL COMMENT '成交额 （千元）',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_stat_date_stock_id` (`stat_date`,`stock_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table daily_pre_restoration_stock_data;
CREATE TABLE `daily_pre_restoration_stock_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trade_day_rank_no` int(11) NOT NULL DEFAULT '0',
  `stat_date` varchar(20) NOT NULL COMMENT '交易日期',
  `stock_id` varchar(20) NOT NULL,
  `open` float NOT NULL,
  `close` float NOT NULL,
  `low` float NOT NULL,
  `high` float NOT NULL,
  `vol` float NOT NULL COMMENT '成交量 （手）',
  `amount` float NOT NULL COMMENT '成交额 （千元）',
  `average_5_line` float NOT NULL DEFAULT '0',
  `average_250_line` float NOT NULL DEFAULT '0',
  `kdj_k` float NOT NULL DEFAULT '0',
  `kdj_d` float NOT NULL DEFAULT '0',
  `kdj_j` float NOT NULL DEFAULT '0',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_stat_date_stock_id` (`stat_date`,`stock_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8