package com.sbi.dao.impl;

import com.sbi.dao.StockCodeDao;
import com.sbi.model.StockCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StockCodeDaoImpl implements StockCodeDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public void batchInsert(List<StockCode> stockCodeList) {


        String batchInsertSql = " insert into " + TABLE_NAME +
                                " (stock_id,ts_stock_code,stock_name,list_date," +
                                " industry,market,list_status) " +
                                " values( ?,?,?,?,?,?,?)";

        jdbcTemplate.batchUpdate(batchInsertSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, stockCodeList.get(i).getStockId());
                ps.setString(2, stockCodeList.get(i).getTsStockCode());
                ps.setString(3, stockCodeList.get(i).getStockName());
                ps.setString(4, stockCodeList.get(i).getListDate());
                ps.setString(5, stockCodeList.get(i).getIndustry());
                ps.setString(6, stockCodeList.get(i).getMarket());
                ps.setString(7, stockCodeList.get(i).getListStatus());
            }

            @Override
            public int getBatchSize() {
                return stockCodeList.size();
            }
        });
    }

    @Override
    public List<StockCode> getAllListStockTsCode(){
        String getAllStockTsCodeSql = "select * from stock_code where list_status = 'L' ";

        List<StockCode> stockTsCodeList = jdbcTemplate.query(getAllStockTsCodeSql,new Object[]{},new BeanPropertyRowMapper(StockCode.class));
        return stockTsCodeList;
    }

    @Override
    public void insertOrUpdate(StockCode stockCode){
        String sql = " INSERT INTO " + TABLE_NAME +
                     " (stock_id,ts_stock_code,stock_name,list_date," +
                     " industry,market,list_status) " +
                     " VALUES (?,?,?,?," +
                             "?,?,?) " +
                     " ON DUPLICATE KEY UPDATE industry = ?,market = ?,list_status = ?";
        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                int i = 1;


                preparedStatement.setString(i++, stockCode.getStockId());
                preparedStatement.setString(i++, stockCode.getTsStockCode());
                preparedStatement.setString(i++, stockCode.getStockName());
                preparedStatement.setString(i++, stockCode.getListDate());

                for (int j = 0; j < 2; j++){
                    preparedStatement.setString(i++, stockCode.getIndustry());
                    preparedStatement.setString(i++, stockCode.getMarket());
                    preparedStatement.setString(i++, stockCode.getListStatus());
                }
            }
        });
    }

    public StockCode getStockCode(String stockId){
        StockCode result = jdbcTemplate.queryForObject("select * from stock_code where stock_id = ? or ts_stock_code = ? ",new Object[]{stockId,stockId}, new BeanPropertyRowMapper<>(StockCode.class));
        return result;
    }
}
