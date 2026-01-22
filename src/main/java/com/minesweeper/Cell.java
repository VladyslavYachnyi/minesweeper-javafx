package com.minesweeper;

public class Cell {
	private boolean isMine;
	private int adjacentMines;

	public Cell(boolean isMine) {
		super();
		this.isMine = isMine;
	}

	public boolean isMine() {
		return isMine;
	}

	public int getAdjacentMines() {
		return adjacentMines;
	}

	public void setAdjacentMines(int adjacentMines) {
		this.adjacentMines = adjacentMines;
	}

}