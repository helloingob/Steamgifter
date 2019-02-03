package com.helloingob.gifter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.HikariDatabasePool;
import com.helloingob.gifter.to.UserAssetTO;
import com.helloingob.gifter.utilities.SharedSettings;
import com.mysql.jdbc.Statement;

public class UserAssetDAO {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private HikariDatabasePool hikariPool;

    public UserAssetDAO() {
        hikariPool = HikariDatabasePool.getInstance();
    }

    public boolean save(UserAssetTO userAsset) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append(" INSERT INTO ");
            query.append(" user_asset ");
            query.append(" (points, level, synced, date, user_pk) ");
            query.append(" VALUES ");
            query.append(" (?, ?, ?, ?, ?); ");
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userAsset.getPoints());
            preparedStatement.setDouble(2, userAsset.getLevel());
            preparedStatement.setBoolean(3, userAsset.getSynced());
            preparedStatement.setTimestamp(4, userAsset.getDate());
            preparedStatement.setInt(5, userAsset.getUserPk());
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                userAsset.setPk(rs.getInt(1));
                return true;
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public boolean update(UserAssetTO userAsset) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append(" UPDATE ");
            query.append(" user_asset ");
            query.append(" SET ");
            query.append(" points = ?, ");
            query.append(" level = ?, ");
            query.append(" synced = ?, ");
            query.append(" date = ?, ");
            query.append(" user_pk = ? ");
            query.append(" WHERE ");
            query.append(" pk = ?; ");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setInt(1, userAsset.getPoints());
            preparedStatement.setDouble(2, userAsset.getLevel());
            preparedStatement.setBoolean(3, userAsset.getSynced());
            preparedStatement.setTimestamp(4, userAsset.getDate());
            preparedStatement.setInt(5, userAsset.getUserPk());
            preparedStatement.setInt(6, userAsset.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public List<UserAssetTO> get() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<UserAssetTO> userAssets = new LinkedList<UserAssetTO>();
        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append(" SELECT *, sum(info_log.points) AS spent_points, count(info_log.pk) AS entered_giveaways ");
            query.append(" FROM user_asset, info_log ");
            query.append(" WHERE user_asset.pk = info_log.user_asset_pk ");
            query.append(" GROUP BY user_asset.pk ");
            query.append(" ORDER BY user_asset.date ASC ");
            preparedStatement = connection.prepareStatement(query.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userAssets.add(createTransferObject(resultSet));
            }
            return userAssets;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<UserAssetTO>();
    }

    public boolean delete(UserAssetTO userAsset) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append(" DELETE FROM ");
            query.append(" user_asset ");
            query.append(" WHERE ");
            query.append(" pk = ?;");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setInt(1, userAsset.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return false;
    }

    public List<UserAssetTO> getForUser(Integer pk) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<UserAssetTO> userAssets = new LinkedList<UserAssetTO>();
        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append(" SELECT *, sum(info_log.points) AS spent_points, count(info_log.pk) AS entered_giveaways ");
            query.append(" FROM user_asset, info_log ");
            query.append(" WHERE user_asset.user_pk = ? AND user_asset.pk = info_log.user_asset_pk ");
            query.append(" GROUP BY user_asset.pk ");
            query.append(" ORDER BY user_asset.date ASC ");
            preparedStatement = connection.prepareStatement(query.toString());

            preparedStatement.setInt(1, pk);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userAssets.add(createTransferObject(resultSet));
            }
            return userAssets;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return new LinkedList<UserAssetTO>();
    }

    public Optional<Timestamp> getLastSyncedDateForUser(Integer pk) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append(" SELECT max(date) AS last_synced_date ");
            query.append(" FROM user_asset ");
            query.append(" WHERE user_asset.user_pk = ? ");
            query.append(" AND synced = 1 ");
            preparedStatement = connection.prepareStatement(query.toString());

            preparedStatement.setInt(1, pk);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                return Optional.ofNullable(resultSet.getTimestamp("last_synced_date"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return Optional.empty();
    }

    public int getEntryCountForUser(Integer pk) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        StringBuilder query = new StringBuilder();
        try {
            connection = hikariPool.getConnection();
            query.append(" SELECT count(1) AS all_entries, ua.user_pk ");
            query.append(" FROM user_asset ua, info_log il ");
            query.append(" WHERE ua.pk = il.user_asset_pk AND ua.user_pk = ? ");
            query.append(" GROUP BY ua.user_pk ");
            preparedStatement = connection.prepareStatement(query.toString());

            preparedStatement.setInt(1, pk);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                return resultSet.getInt("all_entries");
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return 0;
    }

    public TreeMap<Long, Double> getUserLevelChanges(Integer pk) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        TreeMap<Long, Double> map = null;
        try {
            connection = hikariPool.getConnection();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" SELECT date, level  ");
            stringBuilder.append(" FROM user_asset ");
            stringBuilder.append(" WHERE user_pk = ? ");
            stringBuilder.append(" GROUP BY level ");
            stringBuilder.append(" ORDER BY date ");
            preparedStatement = connection.prepareStatement(stringBuilder.toString());

            preparedStatement.setInt(1, pk);

            resultSet = preparedStatement.executeQuery();
            map = new TreeMap<Long, Double>();
            while (resultSet.next()) {
                map.put(resultSet.getTimestamp("date").getTime(), resultSet.getDouble("level"));
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        } finally {
            hikariPool.close(preparedStatement, resultSet, connection);
        }
        return map;
    }

    private UserAssetTO createTransferObject(ResultSet resultSet) throws Exception {
        UserAssetTO userAsset = new UserAssetTO();

        userAsset.setPk(resultSet.getInt("pk"));
        userAsset.setPoints(resultSet.getInt("points"));
        userAsset.setLevel(resultSet.getDouble("level"));
        userAsset.setSynced(resultSet.getBoolean("synced"));
        userAsset.setDate(resultSet.getTimestamp("date"));
        userAsset.setUserPk(resultSet.getInt("user_pk"));
        userAsset.setSpentPoints(resultSet.getInt("spent_points"));
        userAsset.setEnteredGiveaways(resultSet.getInt("entered_giveaways"));

        return userAsset;
    }
}