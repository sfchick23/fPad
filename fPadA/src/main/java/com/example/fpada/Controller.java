package com.example.fpada;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;




public class Controller {


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button nextSound;

    @FXML
    private Button playButton;

    @FXML
    private Button previousSound;

    @FXML
    private Button resetButton;

    @FXML
    private Label soundLabel;

    @FXML
    private ProgressBar soundProgressBar;

    @FXML
    private ComboBox<?> musicsBox;

    @FXML
    private Slider volumeSound;


    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private Media media;
    private MediaPlayer mediaPlayer;

    private int songNumber;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
    private Timer timer;
    private TimerTask timerTask;
    private boolean running;
    private boolean playing;


    @FXML
    void initialize() {
        songs = new ArrayList<File>();
        directory = new File("Musics");

        files = directory.listFiles();


        if (files != null) {
            for (File file : files) {
                songs.add(file);
                System.out.println(file);
            }
        }

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        soundLabel.setText(songs.get(songNumber).getName());

        volumeSound.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSound.getValue() * 0.01);
            }
        });

        soundProgressBar.setStyle("-fx-accent: #00ff00;");
    }

    @FXML
    void playMedia() {
        if (!playing) {
            beginTimer();
            mediaPlayer.play();
            mediaPlayer.setVolume(volumeSound.getValue() * 0.01);
            playButton.setText("PAUSE");

            playing = true;
        }else{
            mediaPlayer.pause();
            playButton.setText("PLAY");
            cancelTimer();
            playing = false;
        }
    }


    @FXML
    void nextMedia(ActionEvent event) {
        if (songNumber < songs.size() - 1) {
            songNumber++;
            mediaPlayer.stop();

            if (running) {
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            soundLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }else {
            songNumber = 0;
            mediaPlayer.stop();

            if (running) {
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            soundLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
    }


    @FXML
    void previousMedia(ActionEvent event) {
        if (songNumber > 0) {
            songNumber--;
            mediaPlayer.stop();

            if (running) {
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            soundLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }else {
            songNumber = songs.size() - 1;
            mediaPlayer.stop();

            if (running) {
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            soundLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
    }

    @FXML
    void resetMedia(ActionEvent event) {
        soundProgressBar.setProgress(0);

        mediaPlayer.seek(Duration.seconds(0));
    }

    public void volumeMedia(MouseEvent mouseEvent) {
    }

    public void beginTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                System.out.println(current / end);
                soundProgressBar.setProgress(current/end);

                if (current/end == 1){
                    cancelTimer();
                }
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    public void cancelTimer(){
        running = false;
        timer.cancel();
    }

}
