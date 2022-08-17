package com.legendsayantan.xcrypt;

import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.legendsayantan.xcrypt.HelloApplication.bufferedStage;
import static com.legendsayantan.xcrypt.HelloController.aboutStage;

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
}
