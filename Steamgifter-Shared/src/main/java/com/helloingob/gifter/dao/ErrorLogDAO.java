package com.helloingob.gifter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.HikariDatabasePool;
import com.helloingob.gifter.to.ErrorLogTO;
import com.helloingob.gifter.utilities.SharedSettings;
import com.mysql.jdbc.Statement;

public class ErrorLogDAO {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private HikariDatabasePool hikariPool;
    private final static String TABLE_NAME = "error_log ";

    public ErrorLogDAO() {
        hikariPool = HikariDatabasePool.getInstance();
    }

    public boolean save(ErrorLogTO errorLog) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("INSERT INTO ");
            query.append(TABLE_NAME);
            query.append("(message, value, date, user_pk) ");
            query.append("VALUES ");
            query.append("(?, ?, ?, ?);");
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, errorLog.getMessage());
            preparedStatement.setObject(2, errorLog.getValue());
            preparedStatement.setTimestamp(3, errorLog.getDate());
            preparedStatement.setObject(4, errorLog.getUserPk().orElse(null));
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                errorLog.setPk(rs.getInt(1));
                return true;
            }
        } catch (Exception e) {
            new ErrorLogHandler().writeFile(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public boolean update(ErrorLogTO errorLog) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("UPDATE ");
            query.append(TABLE_NAME);
            query.append("SET ");
            query.append("message = ?, ");
            query.append("value = ?, ");
            query.append("date = ?, ");
            query.append("user_pk = ? ");
            query.append("WHERE ");
            query.append("pk = ?;");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, errorLog.getMessage());
            preparedStatement.setObject(2, errorLog.getValue());
            preparedStatement.setTimestamp(3, errorLog.getDate());
            preparedStatement.setObject(4, errorLog.getUserPk());
            preparedStatement.setInt(5, errorLog.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler().writeFile(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public List<ErrorLogTO> get() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<ErrorLogTO> errorLogs = new LinkedList<ErrorLogTO>();
        try {
            connection = hikariPool.getConnection();
            String query = "SELECT * FROM " + TABLE_NAME;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ErrorLogTO errorLog = new ErrorLogTO();

                errorLog.setPk(resultSet.getInt("pk"));
                errorLog.setMessage(resultSet.getString("message"));
                errorLog.setValue((String) resultSet.getObject("value"));
                errorLog.setDate(resultSet.getTimestamp("date"));
                errorLog.setUserPk((Integer) resultSet.getObject("user_pk"));

                errorLogs.add(errorLog);
            }
            return errorLogs;
        } catch (Exception e) {
            new ErrorLogHandler().writeFile(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<ErrorLogTO>();
    }

    public boolean delete(ErrorLogTO errorLog) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("DELETE FROM ");
            query.append(TABLE_NAME);
            query.append("WHERE ");
            query.append("pk = ?;");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setInt(1, errorLog.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler().writeFile(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }
    
    public boolean delete() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("TRUNCATE TABLE ");
            query.append(TABLE_NAME);
            preparedStatement = connection.prepareStatement(query.toString());
            return preparedStatement.executeUpdate() >= 0;
        } catch (Exception e) {
            new ErrorLogHandler().writeFile(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }    
}
