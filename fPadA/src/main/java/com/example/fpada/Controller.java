package com.example.fpada;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.sound.sampled.*;


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
    private ComboBox<String> musicsList;

   @FXML
   private Text timerClip;

    @FXML
    private Slider volumeSound;


    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private Media media;
    private MediaPlayer mediaPlayer;

    private int songNumber;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
    private int elapsedTime = 0; // Время в секундах
    private Timer timer;
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


        for (File file : songs) {
            musicsList.getItems().add(file.getName());
        }

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        soundLabel.setText(songs.get(songNumber).getName());

        mediaPlayer.setOnEndOfMedia(() -> {
            nextMedia(null);
        });

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
        soundProgressBar.setProgress(0);
        
        if (songNumber < songs.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }

        mediaPlayer.stop();

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        soundLabel.setText(songs.get(songNumber).getName());

        playMedia();
    }


    @FXML
    void previousMedia(ActionEvent event) {
        soundProgressBar.setProgress(0);
        if (songNumber > 0) {
            songNumber--;
        } else {
            songNumber = songs.size() - 1;
        }

        mediaPlayer.stop();

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        soundLabel.setText(songs.get(songNumber).getName());

        playMedia();
    }

    @FXML
    void resetMedia(ActionEvent event) {
        cancelTimer();
        soundProgressBar.setProgress(0);

        mediaPlayer.seek(Duration.seconds(0));
    }

    public void volumeMedia(MouseEvent mouseEvent) {
    }

    public void beginTimer(){
        timer = new Timer();
        elapsedTime = 0;
        TimerTask timerTask = new TimerTask() {
            public void run() {
                running = true;
                elapsedTime++;

                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                System.out.println(current / end);
                soundProgressBar.setProgress(current / end);

                int minutes = elapsedTime / 60;
                int seconds = elapsedTime % 60;

                String timeFormatted = String.format("%d:%02d", minutes, seconds);
                timerClip.setText(timeFormatted);
                if (current / end == 1) {
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

    @FXML
    void handleProgressBarClick(MouseEvent event) {
        double progressBarWidth = soundProgressBar.getWidth();
        double clickX = event.getX();
        double newProgress = clickX / progressBarWidth;
        soundProgressBar.setProgress(newProgress);
        Duration newTime = media.getDuration().multiply(newProgress);
        mediaPlayer.seek(newTime);
    }

    public void handleMusicSelection(ActionEvent event) {
        String selectedSong = (String) musicsList.getSelectionModel().getSelectedItem();

        // Находим индекс выбранного трека
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getName().equals(selectedSong)) {
                songNumber = i; // Устанавливаем выбранный трек
                break;
            }
        }

        // Останавливаем текущий трек, если он проигрывается
        mediaPlayer.stop();

        // Обновляем медиа-плеер для нового трека
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        soundLabel.setText(songs.get(songNumber).getName());

        // Начинаем проигрывание нового трека
        playMedia();
    }
}
