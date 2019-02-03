package com.helloingob.gifter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.HikariDatabasePool;
import com.helloingob.gifter.to.InfoLogTO;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.StringConverter;
import com.helloingob.gifter.utilities.TimeHelper;
import com.mysql.jdbc.Statement;

public class InfoLogDAO {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private HikariDatabasePool hikariPool;
    private final static String TABLE_NAME = " info_log ";

    public InfoLogDAO() {
        hikariPool = HikariDatabasePool.getInstance();
    }

    public boolean save(InfoLogTO infoLog) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("INSERT INTO ");
            query.append(TABLE_NAME);
            query.append("(title, points, win_chance, steam_link, giveaway_link, image_link, date, user_asset_pk) ");
            query.append("VALUES ");
            query.append("(?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, StringConverter.removeBadChars(infoLog.getTitle()));
            preparedStatement.setInt(2, infoLog.getPoints());
            preparedStatement.setDouble(3, infoLog.getWinChance());
            preparedStatement.setString(4, infoLog.getSteamLink().orElse(null));
            preparedStatement.setString(5, infoLog.getGiveawayLink());
            preparedStatement.setString(6, infoLog.getImageLink().orElse(null));
            preparedStatement.setTimestamp(7, infoLog.getDate());
            preparedStatement.setInt(8, infoLog.getUserAssetPk());
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                infoLog.setPk(rs.getInt(1));
                return true;
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public boolean update(InfoLogTO infoLog) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append("UPDATE ");
            query.append(TABLE_NAME);
            query.append("SET ");
            query.append("title = ?, ");
            query.append("points = ?, ");
            query.append("win_chance = ?, ");
            query.append("steam_link = ?, ");
            query.append("giveaway_link = ?, ");
            query.append("image_link = ?, ");
            query.append("date = ?, ");
            query.append("user_asset_pk = ? ");
            query.append("WHERE ");
            query.append("pk = ?;");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, infoLog.getTitle());
            preparedStatement.setInt(2, infoLog.getPoints());
            preparedStatement.setDouble(3, infoLog.getWinChance());
            preparedStatement.setString(4, infoLog.getSteamLink().orElse(null));
            preparedStatement.setString(5, infoLog.getGiveawayLink());
            preparedStatement.setString(6, infoLog.getImageLink().orElse(null));
            preparedStatement.setTimestamp(7, infoLog.getDate());
            preparedStatement.setInt(8, infoLog.getUserAssetPk());
            preparedStatement.setInt(9, infoLog.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public List<InfoLogTO> get() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<InfoLogTO> infoLogs = new LinkedList<InfoLogTO>();
        try {
            connection = hikariPool.getConnection();
            String query = "SELECT * FROM " + TABLE_NAME;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                infoLogs.add(createTransferObject(resultSet));
            }
            return infoLogs;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<InfoLogTO>();
    }

    public boolean delete(InfoLogTO infoLog) {
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
            preparedStatement.setInt(1, infoLog.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public List<InfoLogTO> getForUserAsset(Integer pk) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<InfoLogTO> infoLogs = new LinkedList<InfoLogTO>();
        try {
            connection = hikariPool.getConnection();
            String query = "SELECT * FROM " + TABLE_NAME + "WHERE user_asset_pk = ? ORDER BY date ASC";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pk);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                infoLogs.add(createTransferObject(resultSet));
            }
            return infoLogs;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<InfoLogTO>();
    }

    public TreeMap<Long, TreeMap<String, Integer>> getLastTwentyFourHoursEnteredGiveawayCount() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");

        TreeMap<Long, TreeMap<String, Integer>> enteredCount = new TreeMap<Long, TreeMap<String, Integer>>();
        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            String currentDateString = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(TimeHelper.getCurrentTimestamp());

            query.append("SELECT ");
            query.append("DATE_FORMAT(log.DATE, '%Y-%m-%d %H') AS pointintime, ");
            query.append("COUNT(*)                             AS count, ");
            query.append("user.login_name                      AS name ");
            query.append("FROM ");
            query.append("info_log AS log, ");
            query.append("user, ");
            query.append("user_asset AS asset ");
            query.append("WHERE ");
            query.append("log.user_asset_pk = asset.pk ");
            query.append("AND user.pk = asset.user_pk ");
            query.append("AND log.date BETWEEN DATE_SUB('");
            query.append(currentDateString);
            query.append("', INTERVAL 24 HOUR) AND '");
            query.append(currentDateString);
            query.append("' ");
            query.append("GROUP BY ");
            query.append("user.pk, ");
            query.append("pointintime ");
            query.append("ORDER BY ");
            query.append("pointintime");

            preparedStatement = connection.prepareStatement(query.toString());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Date date = sdf.parse(resultSet.getString("pointintime"));
                TreeMap<String, Integer> currentCount;

                //checks if map already exists
                if (enteredCount.containsKey(date.getTime())) {
                    currentCount = enteredCount.get(date.getTime());
                } else {
                    //otherwise create new one
                    currentCount = new TreeMap<String, Integer>();
                    enteredCount.put(date.getTime(), currentCount);
                }
                currentCount.put(resultSet.getString("name"), resultSet.getInt("count"));
            }
            return enteredCount;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return enteredCount;
    }

    private InfoLogTO createTransferObject(ResultSet resultSet) throws Exception {
        InfoLogTO infoLog = new InfoLogTO();

        infoLog.setPk(resultSet.getInt("pk"));
        infoLog.setTitle(resultSet.getString("title"));
        infoLog.setPoints(resultSet.getInt("points"));
        infoLog.setWinChance(resultSet.getDouble("win_chance"));
        infoLog.setSteamLink(resultSet.getString("steam_link"));
        infoLog.setGiveawayLink(resultSet.getString("giveaway_link"));
        infoLog.setImageLink(resultSet.getString("image_link"));
        infoLog.setDate(resultSet.getTimestamp("date"));
        infoLog.setUserAssetPk(resultSet.getInt("user_asset_pk"));

        return infoLog;
    }
}
