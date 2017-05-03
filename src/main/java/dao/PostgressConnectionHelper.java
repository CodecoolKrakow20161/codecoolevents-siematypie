package dao;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import utils.FileReader;

public class PostgressConnectionHelper {
    private static Sql2o dbCon;
    private static FileReader reader = new FileReader();

    public static Sql2o getDb(){
        return dbCon;
    }

    public static void setDbCon(String dbAddress, String dbName, String user, String pass){
        dbCon = new Sql2o("jdbc:postgresql://" + dbAddress + "/" + dbName, user, pass);

        String[] createTableQueries = reader.getStringFromFile("/sqls/createTables.sql").split(";");
        try (Connection con = dbCon.beginTransaction()) {
            for (String query: createTableQueries){
                con.createQuery(query).executeUpdate();
            }
            con.commit();
        }
    }

}
