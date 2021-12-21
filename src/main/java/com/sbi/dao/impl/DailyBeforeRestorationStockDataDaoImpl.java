package com.sbi.dao.impl;

import com.sbi.dao.DailyBeforeRestorationStockDataDao;
import com.sbi.model.DailyBeforeRestorationStockData;
import com.sbi.model.KDJAndAverage;
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
public class DailyBeforeRestorationStockDataDaoImpl implements DailyBeforeRestorationStockDataDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void batchInsert(List<DailyBeforeRestorationStockData> dailyBeforeRestorationStockDataList) {
        if(dailyBeforeRestorationStockDataList == null || dailyBeforeRestorationStockDataList.size() == 0){
            return;
        }
        String batchInsertSql = "insert into " + TABLE_NAME
                +" (`stat_date`,`trade_day_rank_no`, `stock_id`, "
                +"  `open`, `close`, `low`, `high`, "
                +"  `vol`, `amount`,`average_5_line`, `average_250_line`,"
                +"  `kdj_k`, `kdj_d`,`kdj_j`) "
                +" values( "
                +"   ?,?,?,"
                +"   ?,?,?,?,"
                +"   ?,?,?,?,"
                +"   ?,?,?)";
        jdbcTemplate.batchUpdate(batchInsertSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int j = 1;

                ps.setString(j++, dailyBeforeRestorationStockDataList.get(i).getStatDate());
                ps.setInt(j++, dailyBeforeRestorationStockDataList.get(i).getTradeDayRankNo());
                ps.setString(j++, dailyBeforeRestorationStockDataList.get(i).getStockId());
                ps.setFloat(j++,dailyBeforeRestorationStockDataList.get(i).getOpen());
                ps.setFloat(j++,dailyBeforeRestorationStockDataList.get(i).getClose());
                ps.setFloat(j++,dailyBeforeRestorationStockDataList.get(i).getLow());
                ps.setFloat(j++,dailyBeforeRestorationStockDataList.get(i).getHigh());
                ps.setFloat(j++, dailyBeforeRestorationStockDataList.get(i).getVol());
                ps.setFloat(j++, dailyBeforeRestorationStockDataList.get(i).getAmount());
                ps.setFloat(j++,dailyBeforeRestorationStockDataList.get(i).getAverage_5_line());
                ps.setFloat(j++,dailyBeforeRestorationStockDataList.get(i).getAverage_250_line());
                ps.setFloat(j++,dailyBeforeRestorationStockDataList.get(i).getKdj_k());
                ps.setFloat(j++,dailyBeforeRestorationStockDataList.get(i).getKdj_d());
                ps.setFloat(j++,dailyBeforeRestorationStockDataList.get(i).getKdj_j());

            }

            @Override
            public int getBatchSize() {
                return dailyBeforeRestorationStockDataList.size();
            }
        });
    }

    @Override
    public int getStockTradeDayCount(String stockId){
        String getStockTradeDayCountSql = "select count(id) from  " + TABLE_NAME + " where stock_id = ?";
        Integer stockTradeDayCount = jdbcTemplate.queryForObject(getStockTradeDayCountSql, new Object[]{stockId}, Integer.class);
        return stockTradeDayCount;
    }

    @Override //todo
    public DailyBeforeRestorationStockData getDailyBeforeRestorationStockData(String statDate){
        String getStockTradeDayCountSql = "select * from  " + TABLE_NAME + " where stat_date = ?";
        DailyBeforeRestorationStockData dailyBeforeRestorationStockData = jdbcTemplate.queryForObject(getStockTradeDayCountSql, new Object[]{statDate}, new BeanPropertyRowMapper<>(DailyBeforeRestorationStockData.class));
        return dailyBeforeRestorationStockData;
    }

    @Override
    public List<String> getAllStockId(){
        String getAllStockTsCodeSql = "select distinct stock_id from  " + TABLE_NAME + "  order by stock_id ASC";
        List<String> stockTsCodeList = jdbcTemplate.queryForList(getAllStockTsCodeSql, new Object[]{}, String.class);
        return stockTsCodeList;
    }

    @Override
    public void batchUpdateKDJ(List<KDJAndAverage> kdjList){
        if(kdjList == null){
            return;
        }
        String sql =
                "UPDATE daily_pre_restoration_stock_data\n" +
                "SET kdj_k = ?, kdj_d = ?, kdj_j = ?\n" +
                "WHERE stock_id= ? AND stat_date = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public int getBatchSize() {
                return kdjList.size();
            }
            public void setValues(PreparedStatement ps, int i)throws SQLException {

                KDJAndAverage kdj = kdjList.get(i);
                ps.setFloat(1,kdj.getKdj_k());
                ps.setFloat(2,kdj.getKdj_d());
                ps.setFloat(3,kdj.getKdj_j());
                ps.setString(4, kdj.getStockId());
                ps.setString(5, kdj.getStatDate());
            }
        });
    }

    @Override
    public void batchUpdateAVG(List<KDJAndAverage> averageList){
        if(averageList == null){
            return;
        }
        String sql =
                "UPDATE daily_pre_restoration_stock_data\n" +
                        "SET average_5_line = ?, average_250_line = ? \n" +
                        "WHERE stock_id= ? AND stat_date = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public int getBatchSize() {
                return averageList.size();
            }
            public void setValues(PreparedStatement ps, int i)throws SQLException {

                KDJAndAverage average = averageList.get(i);
                ps.setFloat(1,average.getAverage_5_line());
                ps.setFloat(2,average.getAverage_250_line());
                ps.setString(3, average.getStockId());
                ps.setString(4, average.getStatDate());
            }
        });
    }

    @Override
    public void batchUpdateAVG5Line(List<KDJAndAverage> averageList){
        if(averageList == null){
            return;
        }
        String sql =
                "UPDATE daily_pre_restoration_stock_data\n" +
                        "SET average_5_line = ? \n" +
                        "WHERE stock_id= ? AND stat_date = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public int getBatchSize() {
                return averageList.size();
            }
            public void setValues(PreparedStatement ps, int i)throws SQLException {

                KDJAndAverage average = averageList.get(i);
                ps.setFloat(1,average.getAverage_5_line());
                ps.setString(2, average.getStockId());
                ps.setString(3, average.getStatDate());
            }
        });
    }

    @Override
    public void batchUpdateAVG250Line(List<KDJAndAverage> averageList){
        if(averageList == null){
            return;
        }
        String sql =
                "UPDATE daily_pre_restoration_stock_data\n" +
                        "SET average_250_line = ? \n" +
                        "WHERE stock_id= ? AND stat_date = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public int getBatchSize() {
                return averageList.size();
            }
            public void setValues(PreparedStatement ps, int i)throws SQLException {

                KDJAndAverage average = averageList.get(i);
                ps.setFloat(1,average.getAverage_250_line());
                ps.setString(2, average.getStockId());
                ps.setString(3, average.getStatDate());
            }
        });
    }
}
