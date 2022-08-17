package com.legendsayantan.xcrypt;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;


public class HelloApplication extends Application {
    static Stage bufferedStage;
    static Preferences pref;
    static TextArea log;
    static int success;
    static int fail;
    static int skip;
    static long time;
    static long resetTime;
    static boolean cryptrun;
    public static ArrayList<String> files = new ArrayList<>(), results = new ArrayList<>();
    public static HostServices hostServices;

    @Override
    public void start(Stage stage) throws IOException {
        pref = Preferences.userNodeForPackage(HelloApplication.class);
        Scene scene = new Scene(FXMLLoader.load(HelloApplication.class.getResource("/com/legendsayantan/xcrypt/helloview.fxml")), 480, 360);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setTitle("Xcrypt");
        stage.setScene(scene);
        stage.show();
        bufferedStage = stage;
        scene.setOnMousePressed(pressEvent -> {
            scene.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
        hostServices=getHostServices();
        stage.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("xcrypt.png"))));
        stage.setOnCloseRequest(event -> {
            if (cryptrun) {
                new CustomDialog("Xcrypt", "Running Process will be completed in background. Do you want to terminate?", event1 -> {
                    stage.close();
                    System.exit(0);
                }, stage);
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (!cryptrun) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    System.exit(0);
                                }
                            });
                        }
                    }
                }, 1000, 1000);
            } else {
                stage.close();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }

    public static String readFromPreference(String key) {
        return pref.get(key, "");
    }

    public static void writeToPreferences(String key, String value) {
        pref.put(key, value);
    }

    public static void initCrypt(PasswordField pf, ToggleButton en,ToggleButton de,ToggleButton del){
        pf.setText(pref.get("key",""));
        if(!Objects.equals(pref.get("encrypt", ""), "")){
            en.setSelected(true);
            en.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                    "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
        }if(!Objects.equals(pref.get("decrypt", ""), "")){
            de.setSelected(true);
            de.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                    "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
        }if(!Objects.equals(pref.get("delete", ""), "")){
            del.setSelected(true);
            del.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                    "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" );
        }
        pf.setOnKeyTyped(event -> {
            if(pf.getText().length()==0)return;
            if(pf.getText().length() > 16){
                String s = pf.getText().substring(0, 16);
                pf.setText(s);
            }
            pref.put("key",pf.getText());
        });
    }
    public static void Xcrypt(PasswordField key, ToggleButton enc, ToggleButton dec,ToggleButton delete){
        cryptrun=true;
        success=fail=skip=0;
        results=files;
        String fileText = files.size()>1?"files":"file";
        printInfo("Starting Xcrypt for "+files.size()+" "+fileText+"...");
        time=System.currentTimeMillis();
        resetTime=System.currentTimeMillis();
        for(String str : files){
            System.out.println(str);
            String[] parts = str.split("\\.");
            String ext = parts[parts.length-1].trim();
            System.out.println(ext);
            if(ext.contains("xcrypt_")){
                if(dec.isSelected()) {
                    printInfo("Decrypting " + str);
                    try {
                        CryptoUtils.decrypt(key.getText().toString(), new File(str), new File(str.replace("xcrypt_", "")));
                        if (new File(str.replace("xcrypt_", "")).exists()) {
                            printInfo("Decrypted " + str +", took "+getResetTime());
                            success++;
                            if (delete.isSelected()) {
                                if (new File(str).delete()) {
                                    results.set(results.indexOf(str), str.replace("xcrypt_", ""));
                                    printInfo("Deleted file " + str);
                                }
                            }
                        }

                    } catch (CryptoException e) {
                        fail++;
                        printInfo("Error decrypting file " + str + "\n" + e.getMessage() + "-" + e.getCause());
                    }
                }else {
                    printInfo("Skipped decryption "+str);
                    skip++;
                }
            }else {
                if (enc.isSelected()) {
                    printInfo("Encrypting " + str);
                    try {
                        CryptoUtils.encrypt(key.getText().toString(), new File(str), new File(str.replace(ext, "xcrypt_" + ext)));
                        if (new File(str.replace(ext, "xcrypt_" + ext)).exists()) {
                            printInfo("Encrypted " + str +", took "+getResetTime());
                            success++;
                            if (delete.isSelected()) {
                                if (new File(str).delete()) {
                                    results.set(results.indexOf(str), str.replace(ext, "xcrypt_" + ext));
                                    printInfo("Deleted file " + str);
                                }
                            }
                        }
                    } catch (CryptoException e) {
                        fail++;
                        printInfo("Error encrypting file " + str + "\n" + e.getMessage() + "-" + e.getCause());
                    }
                }else {
                    printInfo("Skipped encryption "+str);
                    skip++;
                }
            }
        }
        printInfo("Finished Xcrypt in "+formatTime(System.currentTimeMillis()-time)+"\n("+success+" successful, "+fail+" failed, "+skip+" skipped).");

    }
    public static String formatTime(long ms){
        if(ms>1000){
            ms=ms/1000;
            if(ms>60){
                String s= String.valueOf(ms);
                ms=ms/60;
                if(ms>60){
                    String m= String.valueOf(ms);
                    return ms+" h "+m+" m "+s+" s";
                }else return ms+" m "+s+" s";
            }else return ms+" s";
        }else return ms+" ms";
    }
    public static void printInfo(String str){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                log.appendText("\n\n"+str);
                log.appendText("");
            }
        });
    }
    public static String getResetTime(){
        String ret = formatTime(System.currentTimeMillis()-resetTime);
        resetTime=System.currentTimeMillis();
        return ret;
    }
    public static void doOperation(PasswordField key,ToggleButton en,ToggleButton de,ToggleButton delete){
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.setText("");
                Xcrypt(key,en,de,delete);
                StringBuilder toSave = new StringBuilder();
                for (String s : results) {
                    toSave.append("|").append(s);
                }
                HelloApplication.writeToPreferences("files", toSave.toString());
                cryptrun=false;
            }
        }).start();
    }
}