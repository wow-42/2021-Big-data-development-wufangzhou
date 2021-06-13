import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import static java.sql.DriverManager.println;

public class Main extends Application {

    private static Function func = new Function();

    public static void main(String[] args) throws SQLException {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {

        //设置树状结构
        TreeItem<String> rootItem = new TreeItem("tables");
        rootItem.setExpanded(true);



        //设置文本框
        //设置输入文本框
        VBox vb = new VBox();
        Label label = new Label("  输入SQL语句");
        label.setStyle("-fx-font-size:18;");
        label.setStyle("-fx-padding: 12;");
        TextArea input = new TextArea();
        input.setEditable(true);
        input.setStyle("-fx-font-size:18;");


        //设置输出文本框
        Label label1 = new Label("执行结果");
        label1.setStyle("-fx-font-size:18;");
        label1.setStyle("-fx-padding: 12;");
        TableView output = new TableView();
        output.setStyle("-fx-font-size:18;");
        output.setEditable(false);

        //创建菜单栏
        HBox hBox = new HBox();
        Button setBtn = new Button("连接");
        Button doBtn = new Button("执行");

        hBox.getChildren().addAll(setBtn, doBtn);
        vb.getChildren().addAll(label,input,label1,output);

        //连接界面
        setBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage createStage = new Stage();

                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(15);
                grid.setVgap(15);
                grid.setPadding(new Insets(25, 25, 25, 25));

                grid.setStyle("-fx-font-size: 20;");


                Label host = new Label("主机名");
                TextField hostTextField = new TextField();
                grid.add(host,0,2);
                grid.add(hostTextField,1,2);

                Label port= new Label("端口号");
                TextField portTextField = new TextField();
                portTextField.setPrefWidth(2);
                grid.add(port,0,3);
                grid.add(portTextField,1,3);

                Label userName= new Label("用户名");
                TextField userNameTextField = new TextField();
                grid.add(userName,0,4);
                grid.add(userNameTextField,1,4);

                Label passWord= new Label("密码");
                PasswordField passWordTextField = new PasswordField();
                grid.add(passWord,0,5);
                grid.add(passWordTextField,1,5);

                HBox hbBtn = new HBox(10);
                Button confirm = new Button("确认");
                Button cancel  = new Button("取消");

                confirm.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String host= hostTextField.getText();
                        String port= portTextField.getText();
                        String user = userNameTextField.getText();
                        String password = passWordTextField.getText();
                        try {
                            func.connect(host,port,user,password);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        List<String> list = new ArrayList<String>();
                        List<String> subList = new ArrayList<>();
                        try {
                            list = func.bulidTree();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        rootItem.getChildren().clear();
                        for(String s : list){
                            TreeItem<String> item = new TreeItem<>(s);
                            try {
                                subList = func.buildSubTree(s);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            for(String ss : subList){
                                item.getChildren().add(new TreeItem<>(ss));
                            }
                            //item.getChildren().add(new TreeItem<>())
                            rootItem.getChildren().add(item);
                        }
                        createStage.close();
                    }
                });

                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        createStage.close();
                    }
                });

                hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                hbBtn.getChildren().addAll(confirm,cancel);
                grid.add(hbBtn,1,6);

                Scene scene = new Scene(grid, 400, 600);
                createStage.setScene(scene);
                createStage.setTitle("创建连接");
                createStage.show();
            }
        });

        doBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String sql = input.getText();
                try {
                    func.doit(sql,output);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });


        TreeView treeView = new TreeView(rootItem);
        BorderPane bl = new BorderPane();
        bl.setTop(hBox);
        bl.setCenter(vb);
        bl.setLeft(treeView);



        Scene mainScene = new Scene(bl,300,400);
        primaryStage.setTitle("mesql");
        primaryStage.setScene(mainScene);
        //primaryStage.setFullScreen(true);
        primaryStage.setHeight(673);//默认673
        primaryStage.setWidth(1104);//默认1104

        primaryStage.show();

    }
}
