-- 3.根据策略生成统计日股票的分析结果 genStockAnalysisByStrategies

select
from  daily_stock_analysis
where
    statDate = ?
    and



-- 统计日每只票的5日和250日线的数据
SELECT
	 d1.stat_date AS stat_date ,d1.stock_id AS stock_id,
	 ROUND(SUM(IF( DATEDIFF(d2.trade_day_id,d1.trade_day_id)<5,d2.close,0 ))/5) AS five_average_line,
	 ROUND(SUM(IF(DATEDIFF(d2.trade_day_id,d1.trade_day_id)<250,d2.close,0 ))/250) AS two_five_zero_average_line,
FROM daily_stock_data AS d1,daily_stock_data AS da2
WHERE
	d1.stat_date='20210716'
	AND d1.stock_id=d2.stock_id
	AND d2.stat_date >= date_format( DATE_ADD('20210716', INTERVAL -250 DAY), '%Y%m%d' )
	AND d2.stat_date <= '20210716'
GROUP BY d1.stat_date,d1.stock_id HAVING count(d1.id)>=250;