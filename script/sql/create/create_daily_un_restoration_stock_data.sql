
CREATE TABLE `daily_un_restoration_stock_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trade_day_id` int(11) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8