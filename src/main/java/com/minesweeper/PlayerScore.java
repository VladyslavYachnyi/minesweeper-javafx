package com.minesweeper;

public class PlayerScore {
	private String name;
    private int time;
    private String difficulty;

    public PlayerScore(String name, int time, String difficulty) {
        this.name = name;
        this.time = time;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
    	double timeInSeconds = time / 1000.0;
        return String.format("%.3g", timeInSeconds);
    }

    public String getDifficulty() {
        return difficulty;
    }
}
