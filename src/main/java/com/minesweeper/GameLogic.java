package com.minesweeper;

import java.util.Random;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class GameLogic {
	private int gridSize;
	private Cell[][] field;
	private boolean[][] opened;
	private boolean[][] flagged;
	private int minesCount;
	private boolean gameOver;
	private boolean isFirstClick = true;
	private boolean gameStarted = false;

	public GameLogic(int gridSize) {
		super();
		this.gridSize = gridSize;
		field = new Cell[gridSize][gridSize];
		opened = new boolean[gridSize][gridSize];
		flagged = new boolean[gridSize][gridSize];
	}

	private void initializeField(int firstClickX, int firstClickY) {
		minesCount = (int) (gridSize * gridSize * 0.15);

		// Place mines randomly

		Random random = new Random();

		for (int i = 0; i < minesCount; i++) {
			int x, y;
			do {
				x = random.nextInt(gridSize);
				y = random.nextInt(gridSize);
			} while ((x == firstClickX && y == firstClickY) || (field[x][y] != null && field[x][y].isMine()));

			field[x][y] = new Cell(true);
		}

		// Initialize the remaining cells

		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (field[i][j] == null) {
					field[i][j] = new Cell(false);
				}
				field[i][j].setAdjacentMines(countAdjacentMines(i, j));
			}
		}

	}

	// Counting mines around a specific cell.
	private int countAdjacentMines(int x, int y) {
		int count = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				int nx = x + dx, ny = y + dy;
				if (nx >= 0 && ny >= 0 && nx < gridSize && ny < gridSize && field[nx][ny] != null && field[nx][ny].isMine()) {
					count++;
				}
			}
		}
		return count;
	}

	public boolean openCell(int x, int y, GridPane grid) {
		if (isFirstClick) {
	        isFirstClick = false;
	        initializeField(x, y);
	    }
		
	    if (x < 0 || y < 0 || x >= gridSize || y >= gridSize || opened[x][y] || flagged[x][y]) {
	        return false;
	    }

	    opened[x][y] = true;

	    Button cellButton = (Button) grid.getChildren().get(x * gridSize + y);
	    cellButton.setStyle("-fx-background-color: lightgray;");

	    int adjacentMines = field[x][y].getAdjacentMines();
	    if (adjacentMines > 0) {
	        cellButton.setText(String.valueOf(adjacentMines));
	    }

	    if (field[x][y].isMine()) {
	        gameOver = true;
	        return true;
	    }

	    if (adjacentMines == 0) {
	        for (int dx = -1; dx <= 1; dx++) {
	            for (int dy = -1; dy <= 1; dy++) {
	                if (dx != 0 || dy != 0) {
	                    int nx = x + dx, ny = y + dy;
	                    if (nx >= 0 && ny >= 0 && nx < gridSize && ny < gridSize && !opened[nx][ny]) {
	                        openCell(nx, ny, grid);
	                    }
	                }
	            }
	        }
	    }

	    return false;
	}

	public void toggleFlag(int x, int y) {
		if (!opened[x][y]) {
			flagged[x][y] = !flagged[x][y];
		}
	}

	public boolean checkWin() {
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (!field[i][j].isMine() && !opened[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public int getAdjacentMines(int x, int y) {
	    if (field[x][y] != null) {
	        return field[x][y].getAdjacentMines();
	    }
	    return 0;
	}
	
	public boolean isMine(int x, int y) {
	    if (field[x][y] != null) {
	        return field[x][y].isMine();
	    }
	    return false;
	}
	
	public Cell[][] getField() {
	    return field;
	}

	public boolean isOpened(int x, int y) {
		return opened[x][y];
	}

	public boolean isFlagged(int x, int y) {
		return flagged[x][y];
	}

	public boolean isGameOver() {
		return gameOver;
	}
	
	public boolean isGameStarted() {
	    return gameStarted;
	}

	public void setGameStarted(boolean started) {
	    this.gameStarted = started;
	}


}
