package com.sbi.dao.impl;

import com.sbi.dao.TradeDayDao;
import com.sbi.model.TradeDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TradeDayDaoIml implements TradeDayDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;  //这个是系统自带的


    @Override
    public void batchInsert(List<TradeDay> tradeDayList) {
        String batchInsertSql = "insert into " + TABLE_NAME +" (trade_day) values( ? )";

        jdbcTemplate.batchUpdate(batchInsertSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, tradeDayList.get(i).getTradeDay());
            }

            @Override
            public int getBatchSize() {
                return tradeDayList.size();
            }
        });

    }

    @Override
    public List<TradeDay> queryList(){
        List<TradeDay> resultList = jdbcTemplate.query("select * from trade_day where id > ?", new Object[]{200}, new BeanPropertyRowMapper(TradeDay.class));
        if(resultList!=null && resultList.size()>0){
            return resultList;
        }else{
            return null;
        }
    }
    @Override
    public String getTradeDay(String tradeDay){
        String result = jdbcTemplate.queryForObject("select trade_day from trade_day where trade_day = ?", new Object[]{tradeDay}, String.class);
        if(result!=null ){
            return result;
        }else{
            return null;
        }
    }

//    //补入trade_day数据后，重排rank_no
//    @Override
//    public void updateTradeDayRankNo(){
//        String updateTradeDayRankNoSql = "UPDATE trade_day AS t,(\n" +
//                "  SELECT a.trade_day AS trade_day,@rank:=@rank + 1 AS rank_no\n" +
//                "  FROM (\n" +
//                "        SELECT trade_day\n" +
//                "        FROM trade_day\n" +
//                "        ORDER BY trade_day ASC\n" +
//                "     ) a, (SELECT @rank:= 0) b\n" +
//                ") AS g\n" +
//                "SET t.rank_no=g.rank_no\n" +
//                "WHERE t.trade_day = g.trade_day";
//        jdbcTemplate.execute(updateTradeDayRankNoSql);
//
//    }
}
