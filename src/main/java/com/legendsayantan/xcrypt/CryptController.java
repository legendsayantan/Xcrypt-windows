package com.legendsayantan.xcrypt;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import static com.legendsayantan.xcrypt.HelloApplication.*;

public class CryptController {
    @FXML
    private TextArea log;
    @FXML
    private PasswordField key;
    public static boolean first=false;
    @FXML
    private Button run;
    @FXML
    private Button back;
    @FXML
    private ToggleButton enc;
    @FXML
    private ToggleButton dec;
    @FXML
    private ToggleButton del;
    @FXML
    private void onToggle1(){
        if(enc.isSelected()) {
            enc.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                    "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
            pref.put("encrypt","1");
        } else {
            enc.setStyle("");
            pref.remove("encrypt");
        }

    }
    @FXML
    private void onToggle2(){
        if(dec.isSelected()) {
            dec.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                    "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
            pref.put("decrypt","1");
        } else {
            dec.setStyle("");
            pref.remove("decrypt");
        }
    }
    @FXML
    private void onToggle3(){
        if(del.isSelected()) {
            del.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                    "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
            pref.put("delete","1");
        } else {
            del.setStyle("");
            pref.remove("delete");
        }
    }
    @FXML
    private void initialize(){
        key.setTooltip(new Tooltip("Enter 16 digit Xcrypt key"));
        enc.setTooltip(new Tooltip("Encrypt Mode"));
        dec.setTooltip(new Tooltip("Decrypt Mode"));
        del.setTooltip(new Tooltip("Delete old file after operation"));
        buttonClick(run);
        buttonClick(back);
        HelloApplication.log = log;
        HelloApplication.initCrypt(key,enc,dec,del);
        log.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                log.setScrollTop(Double.MAX_VALUE);
            }
        });
    }
    @FXML
    private void onBack(){
        if(HelloApplication.cryptrun)new CustomDialog("Xcrypt","Xcrypt process is already running. Terminating may cause file corruptions.",null, bufferedStage);
        else {
            HelloController.xView.close();
            HelloController.first=false;
        }
    }
    private void buttonClick(Button button){
        if(HelloApplication.cryptrun)return;
        button.setOnMousePressed(event -> button.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" ));
        button.setOnMouseReleased(event -> button.setStyle(""));
    }


    @FXML
    private void onStart(){
        if(key.getText().length()!=16){
            new CustomDialog("Xcrypt","Please set 16 digit Xcrypt key",null, bufferedStage);
            return;
        }
        if(!(enc.isSelected()||dec.isSelected())){
            new CustomDialog("Xcrypt","Please select at least one operation mode",null, bufferedStage);
            return;
        }
        if(HelloApplication.cryptrun) {
            new CustomDialog("Xcrypt", "A background process is running already.",null, bufferedStage);
            return;
        }
        doOperation(key,enc,dec,del);

    }

    public void onKey(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ESCAPE)onBack();
    }
}
