    //5.给存量前复权数据设置5日均线和250日均线
    @Override
    public void updateHistoryBeforeRestorationAverageData(String statDate){
        //1.获取前复权表中所有stock_id
        List<String> allStockIdList = dailyBeforeRestorationStockDataDao.getAllStockId();
        //更新存量数据5日均线
        String updateHistoryBeforeRestoration5AverageSql =
                "UPDATE daily_pre_restoration_stock_data AS d,(\n" +
                "  SELECT \n" +
                "    dpr1.stat_date AS stat_date,\n" +
                "    dpr1.stock_id AS stock_id,dpr1.`close`,\n" +
                "     SUM(dpr2.`close`)/5  AS average_5_line\n" +
                "  FROM daily_pre_restoration_stock_data AS dpr1,daily_pre_restoration_stock_data AS dpr2\n" +
                "  WHERE \n" +
                "    dpr1.stock_id=dpr2.stock_id\n" +
                "    AND dpr1.trade_day_rank_no - dpr2.trade_day_rank_no < 5\n" +
                "    AND dpr1.trade_day_rank_no - dpr2.trade_day_rank_no >= 0\n" +
                "    AND dpr1.stock_id= ? AND dpr1.stat_date > ? \n" +
                "  GROUP BY dpr1.stat_date,dpr1.stock_id HAVING COUNT(dpr1.stock_id)>=5\n" +
                "  ORDER BY dpr1.stock_id,dpr1.stat_date\n" +
                ") AS g\n" +
                "SET d.average_5_line = g.average_5_line \n" +
                "WHERE d.stat_date=g.stat_date AND d.stock_id=g.stock_id AND d.stock_id= ? ";
        //更新存量数据250日均线
        String updateHistoryBeforeRestoration250AverageSql =
                "UPDATE daily_pre_restoration_stock_data AS d,(\n" +
                "SELECT \n" +
                "    dpr1.stat_date AS stat_date,\n" +
                "    dpr1.stock_id AS stock_id,dpr1.`close`,\n" +
                "     SUM(dpr2.`close`)/250  AS average_250_line\n" +
                "  FROM daily_pre_restoration_stock_data AS dpr1,daily_pre_restoration_stock_data AS dpr2\n" +
                "  WHERE \n" +
                "    dpr1.stock_id=dpr2.stock_id\n" +
                "    AND dpr1.trade_day_rank_no - dpr2.trade_day_rank_no < 250\n" +
                "    AND dpr1.trade_day_rank_no - dpr2.trade_day_rank_no >= 0\n" +
                "    AND dpr1.stock_id= ? AND dpr1.stat_date > ? \n" +
                "  GROUP BY dpr1.stat_date,dpr1.stock_id HAVING COUNT(dpr1.stock_id)>=250\n" +
                "  ORDER BY dpr1.stock_id,dpr1.stat_date\n" +
                ") AS g\n" +
                "SET d.average_250_line = g.average_250_line \n" +
                "WHERE d.stat_date=g.stat_date AND d.stock_id=g.stock_id AND d.stock_id= ? ";
        for (String stockId : allStockIdList){
            jdbcTemplate.update(updateHistoryBeforeRestoration5AverageSql,stockId,statDate,stockId);
            jdbcTemplate.update(updateHistoryBeforeRestoration250AverageSql,stockId,statDate,stockId);
        }
    }