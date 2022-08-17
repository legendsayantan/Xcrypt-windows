package com.legendsayantan.xcrypt;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;


public class CustomDialog {
    public CustomDialog(String title, String message, EventHandler<ActionEvent> onYesButtonClicked, Stage bufferedStage){
        Stage stage = new Stage();
        Label label = new Label(title);
        label.setStyle("-fx-font-weight:bold;");
        label.setPrefHeight(Region.USE_COMPUTED_SIZE);
        label.setPrefWidth(250);
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);
        Label label2 = new Label(message);
        label2.setPrefHeight(Region.USE_COMPUTED_SIZE);
        label2.setPrefWidth(250);
        label2.setWrapText(true);
        label2.setAlignment(Pos.CENTER);
        label2.setTextFill(Color.WHITE);
        HBox hBox;
        Button ok = new Button("Yes");
        ok.setTextFill(Color.WHITE);
        ok.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
        ok.requestFocus();
        ok.setOnMouseClicked(event -> stage.close());
        Button btn = new Button("No");
        btn.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
        btn.setTextFill(Color.WHITE);
        btn.setOnMouseClicked(event -> stage.close());
        if(onYesButtonClicked==null){
            btn.setText("Close");
            btn.setDefaultButton(true);
            hBox = new HBox(btn);
        }else{
            ok.setDefaultButton(true);
            ok.setOnAction(onYesButtonClicked);
            Separator separator = new Separator();
            separator.prefWidth(50);
            separator.setOpacity(0);
            hBox = new HBox(ok,separator,btn);
        }
        hBox.setAlignment(Pos.CENTER);
        Separator s1,s2;
        s1=new Separator(Orientation.VERTICAL);
        s2=new Separator(Orientation.VERTICAL);
        s1.setOpacity(0);
        s2.setOpacity(0);
        VBox vBox = new VBox(label,s1,label2,s2,hBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-radius:30 30 30 30;\n"+ "-fx-border-radius:30 30 30 30;\n" +
                "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
        Scene scene = new Scene(vBox,275,125);
        scene.setFill(Color.TRANSPARENT);
        scene.setOnMousePressed(pressEvent -> {
            scene.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("xcrypt.png"))));
        stage.show();
        stage.setX(bufferedStage.getX()+bufferedStage.getWidth()/2-stage.getWidth()/2);
        stage.setY(bufferedStage.getY()+bufferedStage.getHeight()/2-stage.getHeight()/2);
    }
}
