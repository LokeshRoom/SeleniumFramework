package dbuttils;

import java.sql.*;

public class DBDriver {
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    Connection connection = null;

    public DBDriver(String filepath,String dbName){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+filepath+"//"+dbName);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
    public void createTable(String createStatement){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(createStatement);
            statement.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public void insertRecords(String insertQuery){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(insertQuery);
            statement.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void executeQuery(String query){
        try{
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.println("Querying: "+query);
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }
            statement.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }

    }

    public void updateRecords(String updateStatement){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(updateStatement);
            statement.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void deleteRecords(String updateStatement){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(updateStatement);
            statement.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void dropTable(String dropTableStatement){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(dropTableStatement);
            statement.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
    }
    public void closeDBDriver(){
        try{
           connection.close();
            System.out.println("DB connection closed.");
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        DBDriver dbDriver= new DBDriver(System.getProperty("user.dir"),"test.db");
        dbDriver.createTable("CREATE TABLE IF NOT EXISTS COMPANY " +
                "(ID INT PRIMARY KEY     NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " AGE            INT     NOT NULL, " +
                " ADDRESS        CHAR(50), " +
                " SALARY         REAL);");
        dbDriver.insertRecords("INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (2, 'Paul', 32, 'California', 20000.00 );");
        dbDriver.executeQuery("select * from COMPANY");
        dbDriver.updateRecords("UPDATE COMPANY set SALARY = 25000.00 where ID=1;");
        dbDriver.deleteRecords("DELETE from COMPANY where ID=1 or ID=2;");
        dbDriver.executeQuery("select * from COMPANY");
        dbDriver.dropTable("DROP TABLE COMPANY");
        dbDriver.closeDBDriver();
    }
}
