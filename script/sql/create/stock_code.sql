CREATE TABLE `stock_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock_id` varchar(20) NOT NULL,
  `ts_stock_code` varchar(20) NOT NULL,
  `stock_name` varchar(20) NOT NULL,
  `industry` varchar(40) NOT NULL COMMENT ''所属行业'',
  `market` varchar(20) NOT NULL COMMENT ''市场类型（主板/创业板/科创板/CDR）'',
  `list_status` varchar(20) NOT NULL COMMENT ''上市状态 L上市 D退市 P暂停上市'',
  `list_date` varchar(20) NOT NULL COMMENT ''上市日期'',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_stock_id` (`stock_id`),
  UNIQUE KEY `UK_ts_stock_code` (`ts_stock_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8