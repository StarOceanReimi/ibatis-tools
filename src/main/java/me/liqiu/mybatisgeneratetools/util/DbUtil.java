package me.liqiu.mybatisgeneratetools.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariDataSource;
import lombok.val;
import me.liqiu.mybatisgeneratetools.guice.PropertiesPrefix;
import me.liqiu.mybatisgeneratetools.model.TableModel;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static java.util.stream.Collectors.toList;

@Singleton
public final class DbUtil {

    private static final String databasePrefix = "database.";

    @Inject
    public DbUtil(@Named("appConfig") Properties properties) {
        rebuildDataSource(properties);
    }

    public List<String> getTableNamesBySchema(String schemaName) {
        try(Connection conn = getDataSource().getConnection()) {
            List<String> tableNames = new ArrayList<>();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            val result = databaseMetaData.getTables(schemaName, null, null, null);
            while (result.next()) {
                tableNames.add(result.getString("TABLE_NAME"));
            }
            return tableNames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> getColumnInfoBySelectStatement(String sql) {
        try(Connection conn = getDataSource().getConnection()) {
            List<String[]> columnInfo = new ArrayList<>();
            Statement stmt = conn.createStatement();
            val result = stmt.executeQuery(sql);
            ResultSetMetaData metaData = result.getMetaData();
            for(int i=1; i<=metaData.getColumnCount(); i++) {
                columnInfo.add(new String[] {
                    metaData.getColumnLabel(i),
                    metaData.getColumnTypeName(i)
                });
            }
            return columnInfo;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<String[]> getColumnInfoByTableName(String tableName) {
        try(Connection conn = getDataSource().getConnection()) {
            List<String[]> columnInfo = new ArrayList<>();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            val result = databaseMetaData.getColumns(null, null, tableName, null);
            while (result.next()) {
                columnInfo.add(new String[]{
                        result.getString("COLUMN_NAME"),
                        result.getString("TYPE_NAME")
                });
            }
            return columnInfo;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TableModel buildModelBySql(String sqlStatement) {
        TableModel model = new TableModel();
        model.setTableName(null);
        val columnInfo = getColumnInfoBySelectStatement(sqlStatement);
        val columnNames = columnInfo.stream().map(info -> info[0]).collect(toList());
        val columnTypes = columnInfo.stream().map(info -> info[1]).collect(toList());
        model.setColumnNames(columnNames);
        model.setColumnTypes(columnTypes);
        return model;
    }

    public TableModel buildModelByTableName(String name) {
        TableModel model = new TableModel();
        model.setTableName(name);
        val columnInfo = getColumnInfoByTableName(name);
        val columnNames = columnInfo.stream().map(info -> info[0]).collect(toList());
        val columnTypes = columnInfo.stream().map(info -> info[1]).collect(toList());
        model.setColumnNames(columnNames);
        model.setColumnTypes(columnTypes);
        return model;
    }


    public DataSource getDataSource() {
        return dataSource;
    }

    private HikariDataSource dataSource;

    public void rebuildDataSource(Properties properties) {
        if(dataSource != null && !dataSource.isClosed())
            dataSource.close();
        dataSource = new HikariDataSource();
        applyConfiguration(properties);
    }

    private void applyConfiguration(Properties properties) {

        for(String key : properties.stringPropertyNames()) {
            if(key.startsWith(databasePrefix)) {
                String propName = key.substring(databasePrefix.length());
                propName = acceptAlias(propName);
                try {
                    val prop = PropertyUtils.getPropertyDescriptor(dataSource, propName);
                    if(prop == null) continue;
                    val setter = prop.getWriteMethod();
                    val targetType = setter.getParameterTypes()[0];
                    Object target = ConvertUtils.convert(properties.getProperty(key), targetType);
                    setter.invoke(dataSource, target);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String acceptAlias(String propName) {
        switch (propName.toLowerCase()) {
            case "url":
            case "connectionurl":
            case "jdbcurl":
                return "jdbcUrl";
            case "driver":
            case "driverclass":
            case "driverclassname":
                return "driverClassName";
            case "username":
            case "user":
                return "username";
            case "password":
            case "passwd":
            case "pass":
                return "password";
        }
        return propName;
    }

    public static final Map<String, Class<?>> MYSQL_TYPENAME_CLASS_MAPPING = Collections.unmodifiableMap(
        new HashMap<String, Class<?>>() {{
            put("BIT",        Boolean.class);
            put("TINYINT",    Integer.class);
            put("SMALLINT",   Integer.class);
            put("MEDIUMINT",  Integer.class);
            put("INT",        Integer.class);
            put("BIGINT",     Long.class);
            put("FLOAT",      Float.class);
            put("DOUBLE",     Double.class);
            put("DECIMAL",    BigDecimal.class);
            put("DATE",       Date.class);
            put("DATETIME",   Date.class);
            put("TIMESTAMP",  Date.class);
            put("TIME",       Date.class);
            put("CHAR",       String.class);
            put("VARCHAR",    String.class);
            put("TEXT",       String.class);
            put("LONGTEXT",   String.class);
            put("BINARY",     byte[].class);
            put("VARBINARY",  byte[].class);
            put("TINYBLOB",   byte[].class);
            put("BLOB",       byte[].class);
            put("MEDIUMBLOB", byte[].class);
            put("LONGBLOB",   byte[].class);
        }}
    );
}
