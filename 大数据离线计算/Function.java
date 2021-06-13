import java.sql.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import java.util.*;

import static java.sql.DriverManager.*;

public class Function {
    private static Connection conn;


    public static void connect(String host,String port,String user,String password) throws SQLException {

        String url = "jdbc:hive2://"+host+":"+port+"/"+user+"_db";
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conn = getConnection(url,user,password);
    }

    public static void doit(String sql, TableView output) throws SQLException {
        output.refresh();
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);


        ResultSetMetaData md = res.getMetaData();
        int columnCount = md.getColumnCount();//获取列的数量

        List<List<Object>> rows = new ArrayList<>();
        ArrayList<String> keys=new ArrayList();

        for (int i = 1; i <= columnCount; i++) {
            keys.add(md.getColumnName(i));//获取键名
        }
        while (res.next()) {
            List<Object> rowData = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.add(res.getObject(i));//获取一行值
            }
            rows.add(rowData);//获取每行值
        }


        for(int i =0; i < columnCount; i++ ){
            TableColumn<List<Object>,Object> column = new TableColumn<>(keys.get(i));
            int columnIndex = i;
            column.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().get(columnIndex)));
            output.getColumns().add(column);
        }
        output.getItems().setAll(rows);



        System.out.println(rows);
        System.out.println(keys);
        System.out.println(columnCount);
        System.out.println(md);



    }

    //树结构
    public static List<String> bulidTree() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("show tables");
        List<String> temp = new ArrayList<String>();
        while(res.next()){
            temp.add(res.getString(1));
        }
        return temp;
    }
    public static List<String> buildSubTree(String s) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("show columns FROM " + s);
        List<String> list = new ArrayList<>();
        while(res.next()) {
            list.add(res.getString(1));
        }
        return list;
    }
}
