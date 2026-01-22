package com.minesweeper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Label;

public class GameTimer {
    private int elapsedTimeMilliseconds;
    private Timeline timer;
    private Label timerLabel;

    public GameTimer(Label timerLabel) {
        this.timerLabel = timerLabel;
        this.elapsedTimeMilliseconds = 0;
        this.timer = new Timeline(new KeyFrame(Duration.millis(10), e -> updateTimer()));
        this.timer.setCycleCount(Timeline.INDEFINITE);
    }

    private void updateTimer() {
        elapsedTimeMilliseconds += 10;
        timerLabel.setText(String.format("Time: %.3gs", elapsedTimeMilliseconds / 1000.0));
    }

    public void start() {
        elapsedTimeMilliseconds = 0;
        timer.play();
    }

    public void stop() {
        timer.stop();
    }

    public int getElapsedTimeMilliseconds() {
        return elapsedTimeMilliseconds;
    }
}
