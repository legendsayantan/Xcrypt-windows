package com.legendsayantan.xcrypt;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.legendsayantan.xcrypt.HelloApplication.bufferedStage;
import static com.legendsayantan.xcrypt.HelloApplication.hostServices;

public class SingleOperationManager {
    static ArrayList<File> filesToDelete = new ArrayList<>();
    public static Stage stage;
    public static Scene scene;
    static final String Header = "Xcrypt - Single File Operation";
    static boolean run = false;
    public static void main(String args[]) throws IOException {
        stage = new Stage();
        scene = new Scene(FXMLLoader.load(SingleOperationManager.class.getResource("singleoperate.fxml")), 400, 240);
        scene.setFill(Color.TRANSPARENT);
        scene.getRoot().setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
        stage.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("xcrypt.png"))));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setTitle(Header);
        stage.setScene(scene);
        stage.show();
        scene.setOnMousePressed(pressEvent -> {
            scene.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
        stage.setX(bufferedStage.getX()+bufferedStage.getWidth()/2-stage.getWidth()/2);
        stage.setY(bufferedStage.getY()+bufferedStage.getHeight()/2-stage.getHeight()/2);
        stage.setOnCloseRequest(event -> {
            if(run)new CustomDialog(Header,"Wait for the process to finish",null, stage);
            else stage.close();
        });
    }
    public static void processFile(String str, String key, Label title, Button open,Button folder,Boolean delete){
        run=true;
        String dl = new File(System.getProperty("user.home"),"Downloads").getAbsolutePath();
        scene.setCursor(Cursor.WAIT);
        String[] parts = str.split("\\.");
        String ext = parts[parts.length-1].trim();
        File encOut = new File(dl,new File(str.replace("."+ext, ".xcrypt_" + ext)).getName());
        File decOut = new File(dl,new File(str.replace(".xcrypt_", ".")).getName());
        if(ext.startsWith("xcrypt_")){
            title.setText(Header+" [Decrypt]");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CryptoUtils.decrypt(key,new File(str), decOut);
                        if(delete)filesToDelete.add(decOut);
                    } catch (CryptoException e) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                new CustomDialog(Header,"Error - "+e.getLocalizedMessage(),null, stage);
                            }
                        });
                    }
                    scene.setCursor(Cursor.DEFAULT);
                }
            }).start();
            open.setText("Open File");
            open.setOnAction(event -> {
                hostServices.showDocument(decOut.getAbsolutePath());
            });
            folder.setOnAction(event1 -> {
                try {
                    Runtime.getRuntime().exec("explorer.exe "+decOut.getParent());
                } catch (IOException e) {
                    new CustomDialog(Header,"Failed to open file - "+e.getLocalizedMessage(),null, stage);
                }
            });
        }else{
            title.setText(Header+" [Encrypt]");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CryptoUtils.encrypt(key,new File(str),encOut);
                        if(delete)filesToDelete.add(encOut);
                    } catch (CryptoException e) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                new CustomDialog(Header,"Error - "+e.getLocalizedMessage(),null, stage);
                            }
                        });
                    }
                    scene.setCursor(Cursor.DEFAULT);
                }
            }).start();
            open.setText("Copy path");
            open.setOnAction(event -> {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(encOut.getAbsolutePath());
                clipboard.setContent(content);
                new CustomDialog(Header,"Path of the Encrypted File was copied.",null, stage);
            });
            folder.setOnAction(event1 -> {
                try {
                    Runtime.getRuntime().exec("explorer.exe "+encOut.getParent());
                } catch (IOException e) {
                    new CustomDialog(Header,"Failed to open file - "+e.getLocalizedMessage(),null, stage);
                }
            });
        }
        run=false;
    }
}
