package com.legendsayantan.xcrypt;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.legendsayantan.xcrypt.HelloApplication.bufferedStage;
import static com.legendsayantan.xcrypt.HelloController.aboutStage;
import static com.legendsayantan.xcrypt.HelloController.listener;

public class AboutController {
    public Hyperlink link;
    public Button back;
    public Button share;
    public Button src;
    public Label version;
    public Button website;

    String versionName = "1.1";
    public void share() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString("https://github.com/legendsayantan/Xcrypt-windows");
        clipboard.setContent(content);
        new CustomDialog("Xcrypt","Share url copied.",null, bufferedStage);
    }
    public void github() {
        try {
            java.awt.Desktop.getDesktop().browse(new URI("https://github.com/legendsayantan/xcrypt-windows"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public void aboutClose() {
        aboutStage.close();
    }
    public void initialize(){
        buttonClickAnimator(src);
        buttonClickAnimator(share);
        buttonClickAnimator(back);
        buttonClickAnimator(website);
        version.setText("Version "+versionName);
        link.setOnAction(event -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URI("https://github.com/legendsayantan"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }
    private void buttonClickAnimator(Button button){
        button.setOnMousePressed(event -> button.setStyle("-fx-background-radius:20 20 20 20;\n"+ "-fx-border-radius:20 20 20 20;\n" +
                "-fx-background-color:#55b9f3;" + "-fx-border-color:#FFFFFF" ));
        button.setOnMouseReleased(event -> button.setStyle(""));
    }
    public void website() {
        try {
            java.awt.Desktop.getDesktop().browse(new URI("https://legendsayantan.github.io/xcrypt"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onKey(KeyEvent keyEvent) {
        if(keyEvent.getCode()== KeyCode.ESCAPE){
            aboutClose();
        }
    }

    public void deleteAll() {
        new CustomDialog("Xcrypt", "Do you want to remove all saved filepaths? This action cannot be undone.",
                event -> {
                    HelloApplication.writeToPreferences("files","");
                    bufferedStage.focusedProperty().removeListener(listener);
                    bufferedStage.focusedProperty().addListener(listener);
                },aboutStage);
    }
}
