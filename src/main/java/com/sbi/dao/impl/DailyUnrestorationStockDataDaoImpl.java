package com.sbi.dao.impl;

import com.sbi.dao.DailyUnrestorationStockDataDao;
import com.sbi.model.DailyUnrestorationStockData;
import com.sbi.model.StockCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DailyUnrestorationStockDataDaoImpl implements DailyUnrestorationStockDataDao {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<DailyUnrestorationStockData> dailyUnrestorationStockDataList) {

        String batchInsertSql = "insert into " + TABLE_NAME
                +" (`stat_date`,`stock_id`,`adj_factor`,`restoration_status`, "
                +"  `open`, `close`, `low`, `high`, "
                +"  `pre_close`, `change`, `vol`, `amount`) "
                +" values( "
                +"   ?,?,?,?,"
                +"   ?,?,?,?,"
                +"   ?,?,?,?)";
        jdbcTemplate.batchUpdate(batchInsertSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int j = 1;

                ps.setString(j++, dailyUnrestorationStockDataList.get(i).getStatDate());
                ps.setString(j++, dailyUnrestorationStockDataList.get(i).getStockId());
                ps.setFloat(j++, dailyUnrestorationStockDataList.get(i).getAdjFactor());
                ps.setInt(j++, dailyUnrestorationStockDataList.get(i).getRestorationStatus());
                ps.setFloat(j++,dailyUnrestorationStockDataList.get(i).getOpen());
                ps.setFloat(j++,dailyUnrestorationStockDataList.get(i).getClose());
                ps.setFloat(j++,dailyUnrestorationStockDataList.get(i).getLow());
                ps.setFloat(j++,dailyUnrestorationStockDataList.get(i).getHigh());
                ps.setFloat(j++,dailyUnrestorationStockDataList.get(i).getPreClose());
                ps.setFloat(j++,dailyUnrestorationStockDataList.get(i).getChange());
                ps.setFloat(j++, dailyUnrestorationStockDataList.get(i).getVol());
                ps.setFloat(j++, dailyUnrestorationStockDataList.get(i).getAmount());

            }

            @Override
            public int getBatchSize() {
                return dailyUnrestorationStockDataList.size();
            }
        });
    }

    @Override
    public void updateTradeDayRankNo(String tsStockId){
        String updateTradeDayRankNoSql =
                "UPDATE daily_un_restoration_stock_data AS t,( \n" +
                "  SELECT a.stat_date AS stat_date,@rank:=@rank + 1 AS rank_no \n" +
                "  FROM ( \n" +
                "        SELECT stat_date \n" +
                "        FROM daily_un_restoration_stock_data \n" +
                "        WHERE stock_id= ? \n" +
                "        ORDER BY stat_date ASC \n" +
                "     ) a, (SELECT @rank:= 0) b \n" +
                ") AS g \n" +
                "SET t.trade_day_rank_no=g.rank_no \n" +
                "WHERE t.stat_date = g.stat_date AND t.stock_id= ? ";
        jdbcTemplate.update(updateTradeDayRankNoSql,tsStockId,tsStockId);
    }


    @Override
    public List<String> getAllStockId(){
        String getAllStockIdSql = "select distinct stock_id from daily_un_restoration_stock_data ";

        List<String> stockIdList = jdbcTemplate.queryForList(getAllStockIdSql,String.class);
        return stockIdList;
    }

    @Override
    public DailyUnrestorationStockData getDailyUnrestorationStockData(String stockId,String statDate){
        String sql = "select * from daily_un_restoration_stock_data where stock_id = ? and stat_date = ? ";
        DailyUnrestorationStockData dailyUnrestorationStockData = jdbcTemplate.queryForObject(sql, new Object[]{stockId, statDate}, new BeanPropertyRowMapper<>(DailyUnrestorationStockData.class));
        return dailyUnrestorationStockData;
    }

    @Override
    public List<DailyUnrestorationStockData> getDailyUnrestorationStockDataByStatDate(String statDate){
        String sql = "select * from daily_un_restoration_stock_data where stat_date = ? ";
        List<DailyUnrestorationStockData> dailyUnrestorationStockDataList = jdbcTemplate.query(sql, new Object[]{statDate}, new BeanPropertyRowMapper<>(DailyUnrestorationStockData.class));
        return dailyUnrestorationStockDataList;
    }

}
