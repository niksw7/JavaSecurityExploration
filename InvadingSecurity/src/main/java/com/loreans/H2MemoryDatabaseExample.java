
package com.loreans;

import java.sql.*;

// H2 In-Memory Database Example shows about storing the database contents into memory. 

public class H2MemoryDatabaseExample {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private static int index = 0;

    public static void main(String[] args) throws Exception {
        try {
            createTable("Person");
            insertWithStatement("Person","Ross","Jackson","Rashmi");
            getPerson("Rashmi");

            //when an injector manipulates the query
            String queryFromHacker = "Rashmi'; update Person set name ='RashmiHacked' where name like '%Rashmi%";
            getPerson(queryFromHacker);

            getPerson("RashmiHacked");

            System.out.println("Getting Person Securely");
            getPersonSecurely("Ross");
            queryFromHacker = "Ross'; update Person set name ='Ross' where name like '%RosHacked%";
            getPersonSecurely(queryFromHacker);
            getPersonSecurely("Ross");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getPersonSecurely(String name) throws SQLException {
        Connection connection = getDBConnection();
        PreparedStatement createPreparedStatement = null;
        PreparedStatement insertPreparedStatement = null;
        PreparedStatement selectPreparedStatement = null;
        String SelectQuery = "select * from PERSON where name=?";

        try {
            connection.setAutoCommit(false);



            selectPreparedStatement = connection.prepareStatement(SelectQuery);
            selectPreparedStatement.setString(1,name);
            ResultSet rs = selectPreparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("name"));
            }
            selectPreparedStatement.close();

            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    private static void insertWithStatement(String tableName,String... names) throws SQLException {
        Connection connection = getDBConnection();
        Statement stmt = connection.createStatement();
        try {
            connection.setAutoCommit(false);
            for (String name: names) {
                String sqlQuery = "INSERT INTO "+tableName+"(id, name) VALUES(" + index++ + ", '" + name + "')";
                stmt.execute(sqlQuery);
            }
            stmt.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    private static void createTable(String tableName) throws SQLException {
        Connection dbConnection = getDBConnection();
        Statement stmt = dbConnection.createStatement();
        String createQuery = "CREATE TABLE "+tableName+"(id int primary key, name varchar(255))";
        stmt.execute(createQuery);
        dbConnection.close();
    }

    private static void getPerson(String name) throws SQLException {

        Connection dbConnection = getDBConnection();
        Statement stmt = dbConnection.createStatement();
        String selectQuery = "select * from PERSON where name='" + name + "'";
        ResultSet rs = stmt.executeQuery(selectQuery);
        while (rs.next()) {
            System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("name"));
        }
        stmt.close();
        dbConnection.close();
    }

    private static void dropTable() throws SQLException {
        Connection dbConnection = getDBConnection();
        Statement stmt = dbConnection.createStatement();
        stmt.execute("DROP TABLE PERSON");
        stmt.close();
        dbConnection.close();
    }
    private static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }
}