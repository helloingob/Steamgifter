package com.helloingob.gifter.utilities;

public class SharedSettings {

    public class Database {
        public static final String DATASOURCE = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
        public static final String SERVER = "localhost";
        public static final int PORT = 3306;
        public static final String DATABASE = "gifter";
        public static final String USERNAME = "gifter";
        public static final String PASSWORD = "helloingob";
    }
    
    public class Logger {
        public static final String DEFAULT = "file-output";
    }

}