package com.legendsayantan.xcrypt;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.prefs.Preferences;

import static com.legendsayantan.xcrypt.HelloApplication.*;
import static com.legendsayantan.xcrypt.SingleOperationManager.stage;

public class HelloController {
    static ListProperty<String> property = new SimpleListProperty<>();
    static boolean first = false;
    ArrayList<String> strings = new ArrayList<>();
    public static Stage xView;
    public static Stage aboutStage;
    @FXML
    private ListView<String> listView;
    @FXML
    private Button single;
    @FXML
    private Button info;
    @FXML
    private Button b1;
    @FXML
    private Button b2;
    @FXML
    private Button b3;
    @FXML
    protected void onFileAdd() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Add files for Xcrypt");
        List<File> file = chooser.showOpenMultipleDialog(bufferedStage);
        if(file==null){
            new CustomDialog("Xcrypt", "No files selected",null, bufferedStage);
            updateList();
        }else{
            String str = file.size()>1?"s":"";
            new CustomDialog("Xcrypt", "Add selected " + file.size() + " file" + str + " ?", event -> {
                ArrayList<String> allPaths = new ArrayList<>();
                for (File f : file) {
                    allPaths.add(f.getAbsolutePath());
                }
                String saved = readFromPreference("files");
                String[] savedFiles = saved.split("\\|");
                allPaths.addAll(Arrays.asList(savedFiles));
                allPaths.removeIf(s -> s.trim().equals(""));
                StringBuilder toSave = new StringBuilder();
                for (String s : allPaths) {
                    toSave.append("|").append(s);
                }
                HelloApplication.writeToPreferences("files", toSave.toString());
                updateList();
            }, bufferedStage);
        }
    }
    @FXML
    protected void onExitPressed(){
        if (cryptrun) {
            new CustomDialog("Xcrypt", "A background process is running, exiting may corrupt files. Terminate anyway?",event ->{
                bufferedStage.close();
                System.exit(0);
            }, bufferedStage);
            return;
        }
        new CustomDialog("Xcrypt", "Do you want to close Xcrypt?", event -> {
            bufferedStage.close();
            System.exit(0);
        }, bufferedStage);
    }
    public void updateList(){
        listView.itemsProperty().bind(property);
        strings.clear();
        files.clear();
        String items = readFromPreference("files");
        String[] listItems = items.split("\\|");
        for(String s:listItems){
            if(!s.trim().equals("")) {
                strings.add(new File(s).getName());
                files.add(s);
            }
        }
        property.set(FXCollections.observableArrayList(strings));
    }
    @FXML
    private void onlist(){
        if(listView.getSelectionModel().getSelectedItem()==null)return;
        new CustomDialog("Xcrypt", "Are you sure to remove "+listView.getSelectionModel().getSelectedItem().toString()+" ?", event -> {
            String saved = readFromPreference("files");
            String[] savedFiles = saved.split("\\|");
            ArrayList<String> allPaths = new ArrayList<>(Arrays.asList(savedFiles));
            allPaths.removeIf(s -> s.trim().equals(""));
            allPaths.remove(strings.indexOf(listView.getSelectionModel().getSelectedItem().toString()));
            StringBuilder toSave = new StringBuilder();
            for(String s:allPaths){
                toSave.append("|").append(s);
            }
            writeToPreferences("files", toSave.toString());
            updateList();
        }, bufferedStage);
    }
    @FXML
    private void initialize(){
        single.setTooltip(new Tooltip("Single file operation"));
        info.setTooltip(new Tooltip("App Info"));
        updateList();
        buttonClickAnimator(b1);
        buttonClickAnimator(b2);
        buttonClickAnimator(b3);
        buttonClickAnimator(single);
        buttonClickAnimator(info);
        single.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if(SingleOperationManager.stage==null){
                        SingleOperationManager.main(null);
                    }else{
                        SingleOperationManager.stage.requestFocus();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        info.setOnAction(event -> {
            aboutStage = new Stage();
            Scene scene = null;
            try {
                scene = new Scene(FXMLLoader.load(HelloController.class.getResource("about.fxml")), 480, 360);
            } catch (IOException e) {
                e.printStackTrace();
            }
            pref = Preferences.userNodeForPackage(HelloApplication.class);
            scene.setFill(Color.TRANSPARENT);
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.initStyle(StageStyle.TRANSPARENT);
            aboutStage.setResizable(false);
            aboutStage.setTitle("Xcrypt - About");
            aboutStage.setScene(scene);
            aboutStage.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("xcrypt.png"))));
            aboutStage.show();
            aboutStage.setX(bufferedStage.getX());
            aboutStage.setY(bufferedStage.getY());
            Scene finalScene = scene;
            scene.setOnMousePressed(pressEvent -> {
                finalScene.setOnMouseDragged(dragEvent -> {
                    bufferedStage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                    bufferedStage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
                    aboutStage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                    aboutStage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
                });
            });

        });
    }
    @FXML
    private void onXcrypt(){
        if(HelloApplication.readFromPreference("files").replace("|","").trim().equals("")){
            new CustomDialog("Xcrypt","Please add files first",null, bufferedStage);
            return;
        }
        if (HelloApplication.cryptrun) {
            new CustomDialog("Xcrypt", "A background process is running already.",null, bufferedStage);
            return;
        }
        xView = new Stage();
        Scene scene = null;
        try {
            scene = new Scene(FXMLLoader.load(HelloController.class.getResource("cryptview.fxml")), 480, 360);
            pref = Preferences.userNodeForPackage(HelloApplication.class);
            scene.setFill(Color.TRANSPARENT);
            xView.initModality(Modality.APPLICATION_MODAL);
            xView.initStyle(StageStyle.TRANSPARENT);
            xView.setResizable(false);
            xView.setTitle("Xcrypt");
            xView.setScene(scene);
            xView.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("xcrypt.png"))));
            xView.show();
            CryptController.first=false;
            xView.setX(bufferedStage.getX());
            xView.setY(bufferedStage.getY());
            Scene finalScene = scene;
            scene.setOnMousePressed(pressEvent -> {
                finalScene.setOnMouseDragged(dragEvent -> {
                    bufferedStage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                    bufferedStage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
                    xView.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                    xView.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
                });
            });
            xView.setOnCloseRequest(event -> {
                if (cryptrun) {
                    xView.close();
                    new CustomDialog("Xcrypt", "Process is still running in background.",null
                    , xView);
                } else {
                    xView.close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void buttonClickAnimator(Button button){
        button.setOnMousePressed(event -> button.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" ));
        button.setOnMouseReleased(event -> button.setStyle(""));
    }
    public void drop(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if(db.hasFiles()){
            List<File> file = db.getFiles();
            String str = file.size()>1?"s":"";
            new CustomDialog("Xcrypt", "Add selected " + file.size() + " file" + str + " ?", event -> {
                ArrayList<String> allPaths = new ArrayList<>();
                for (File f : file) {
                    allPaths.add(f.getAbsolutePath());
                }
                String saved = readFromPreference("files");
                String[] savedFiles = saved.split("\\|");
                allPaths.addAll(Arrays.asList(savedFiles));
                allPaths.removeIf(s -> s.trim().equals(""));
                StringBuilder toSave = new StringBuilder();
                for (String s : allPaths) {
                    toSave.append("|").append(s);
                }
                HelloApplication.writeToPreferences("files", toSave.toString());
                updateList();
            }, bufferedStage);
        }
    }

    public void drag(DragEvent dragEvent) {dragEvent.acceptTransferModes(TransferMode.COPY);}

    public void onKey(KeyEvent keyEvent) {
        if(keyEvent.getCode()==KeyCode.ESCAPE)onExitPressed();
    }
}