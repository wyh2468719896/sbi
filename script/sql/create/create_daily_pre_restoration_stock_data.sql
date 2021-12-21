-- 每日前复权数据
CREATE TABLE `daily_pre_restoration_stock_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trade_day_id` int(11) NOT NULL,
  `stat_date` varchar(20) NOT NULL COMMENT '交易日期',
  `stock_id` varchar(20) NOT NULL,
  `open` float NOT NULL,
  `close` float NOT NULL,
  `low` float NOT NULL,
  `high` float NOT NULL,
  `vol` float NOT NULL COMMENT '成交量 （手）',
  `amount` float NOT NULL COMMENT '成交额 （千元）',
  `average_5_line` float,
  `average_250_line` float ,
  `kdj_k` float NOT NULL ,
  `kdj_d` float NOT NULL ,
  `kdj_j` float NOT NULL ,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_stat_date_stock_id` (`stat_date`,`stock_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8