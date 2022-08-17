package com.legendsayantan.xcrypt;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.*;
import javafx.stage.FileChooser;

import java.io.File;

import static com.legendsayantan.xcrypt.HelloApplication.*;
import static com.legendsayantan.xcrypt.SingleOperationManager.*;

public class SingleController {
    @FXML
    public CheckBox delete;
    public PasswordField key;
    public Button opener;
    public Button folder;
    public Button close;
    public Button choose;
    public Label title;
    String filepath = "";
    String xcryptKey = "";
    @FXML
    public void onChoose() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose file for "+Header);
        File file = chooser.showOpenDialog(bufferedStage);
        if(file==null){
            new CustomDialog(Header, "No files selected",null, stage);
        }else{
            filepath = file.getAbsolutePath();
            choose.setText(file.getName());
            opener.setText("Xcrypt");
            folder.setOnAction(event -> {});
            opener.setOnAction(event -> {
                if(filepath.equals("")) {
                    new CustomDialog(SingleOperationManager.Header,"Please Choose a file first.",null, stage);
                    return;
                }if(xcryptKey.length()!=16) {
                    new CustomDialog(SingleOperationManager.Header,"Please Enter 16 digit Xcrypt Key.",null, stage);
                    return;
                }
                SingleOperationManager.processFile(filepath,xcryptKey,title,opener,folder,delete.isSelected());
            });
        }
    }
    @FXML
    public void onKey() {
        if(key.getText().length()>16) {
            key.setText(xcryptKey);
            return;
        }
        xcryptKey=key.getText();
    }
    @FXML
    public void onExit() {
        if(SingleOperationManager.run){
            new CustomDialog(Header,"Wait for the processing to finish.",null, stage);
        }else{
            for(File f:filesToDelete)f.delete();
            SingleOperationManager.stage.close();
            SingleOperationManager.stage=null;
        }
    }
    private void buttonClickAnimator(Button button){
        button.setOnMousePressed(event -> button.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" ));
        button.setOnMouseReleased(event -> button.setStyle(""));
    }
    public void initialize(){
        buttonClickAnimator(opener);
        buttonClickAnimator(choose);
        buttonClickAnimator(folder);
        buttonClickAnimator(close);
        folder.setOnAction(event -> {});
        opener.setOnAction(event -> {
            if(filepath.equals("")) {
                new CustomDialog(SingleOperationManager.Header,"Please Choose a file first.",null, stage);
                return;
            }if(xcryptKey.length()!=16) {
                new CustomDialog(SingleOperationManager.Header,"Please Enter 16 digit Xcrypt Key.",null, stage);
                return;
            }
            SingleOperationManager.processFile(filepath,xcryptKey,title,opener,folder,delete.isSelected());

        });
    }
    public void drop(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if(db.hasFiles()){
            File f = db.getFiles().get(0);
            if(db.getFiles().size()>1){
                new CustomDialog(Header, "Multiple files were dragged in. Choose the last selected file only?", new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        addFile(f);
                    }
                },stage);
            }else addFile(f);
        }
    }
    public void drag(DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.COPY);
    }
    public void addFile(File f){
        filepath = f.getAbsolutePath();
        choose.setText(f.getName());
        opener.setText("Xcrypt");
        folder.setOnAction(event -> {});
        opener.setOnAction(event -> {
            if(filepath.equals("")) {
                new CustomDialog(SingleOperationManager.Header,"Please Choose a file first.",null, stage);
                return;
            }if(xcryptKey.length()!=16) {
                new CustomDialog(SingleOperationManager.Header,"Please Enter 16 digit Xcrypt Key.",null, stage);
                return;
            }
            SingleOperationManager.processFile(filepath,xcryptKey,title,opener,folder,delete.isSelected());
        });
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ESCAPE)onExit();
    }
}
