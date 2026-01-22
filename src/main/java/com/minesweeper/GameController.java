package com.minesweeper;

import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameController {
	private Stage stage;
	private int gridSize;
	private GameLogic gameLogic;
	private GameTimer gameTimer;
    private Label timerLabel;
    private int savedTime;
    private DBManager dbManager;
    private static String playerName;
    private String difficulty;

	public GameController(Stage stage, int gridSize, String difficulty) {
		super();
		this.stage = stage;
		this.gridSize = gridSize;
		this.gameLogic = new GameLogic(gridSize);
		this.savedTime = -1;
		this.difficulty = difficulty; 
	}

	public void initializedGame() {
		dbManager = new DBManager("data/leaderboard.db");
		if (playerName == null || playerName.isEmpty()) {
		    requestPlayerName();
		}
		
		VBox root = new VBox(20);
		root.setAlignment(Pos.CENTER);
		
		timerLabel = new Label("Time: 0.000s");
        timerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        gameTimer = new GameTimer(timerLabel);

		GridPane grid = new GridPane();
		grid.setHgap(1);
		grid.setVgap(1);

		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				final int row = i;
				final int col = j;
				Button cellButton = new Button();
				cellButton.setPrefSize(30, 30);

				cellButton.setOnMouseClicked(e -> {
	                if (e.getButton() == MouseButton.PRIMARY) {
	                	startTimerIfFirstClick();
	                    handleCellClick(row, col, cellButton);
	                } else if (e.getButton() == MouseButton.SECONDARY) {
	                    handleCellRightClick(row, col, cellButton);
	                }
	            });
				
				grid.add(cellButton, col, row);
			}
		}

		grid.setAlignment(Pos.CENTER);

		Button returnButton = new Button("Main Menu");
		returnButton.setOnAction(e -> {
			Main mainApp = new Main();
			mainApp.start(stage);
		});

		returnButton.getStyleClass().add("button");

		root.getChildren().addAll(timerLabel, grid, returnButton);

		Scene gameScene = new Scene(root, 750, 850);
		stage.setScene(gameScene);
		stage.show();

	}
	
	private void startTimerIfFirstClick() {
        if (!gameLogic.isGameStarted()) {
            gameTimer.start();
            gameLogic.setGameStarted(true);
        }
    }
	
	private void disableGrid(GridPane grid) {
	    for (javafx.scene.Node node : grid.getChildren()) {
	        node.setDisable(true);
	    }
	}
	
	private void showWinMessage() {
	    Label winMessage = new Label("Win!");
	    winMessage.setStyle("-fx-font-size: 30px; -fx-text-fill: green; -fx-font-weight: bold;");
	    winMessage.setAlignment(Pos.CENTER);

	    VBox root = (VBox) stage.getScene().getRoot();
	    root.getChildren().add(0, winMessage);
	}
	
	private void showLoseMessage() {
	    Label loseMessage = new Label("Lose!");
	    loseMessage.setStyle("-fx-font-size: 30px; -fx-text-fill: red; -fx-font-weight: bold;");
	    loseMessage.setAlignment(Pos.CENTER);

	    VBox root = (VBox) stage.getScene().getRoot();
	    root.getChildren().add(0, loseMessage);
	}
	
	private void handleCellClick(int x, int y, Button cellButton) {
		if (gameLogic.isOpened(x, y)) {
			return;
		}
		
		if (gameLogic.isFlagged(x, y)) {
	        return;
	    }
		
		boolean isMine = gameLogic.openCell(x, y, (GridPane) cellButton.getParent());
		
		if (isMine) {
			cellButton.setStyle("-fx-background-color: red;");
			cellButton.setText("X");
			showAllMines((GridPane) cellButton.getParent());
	        disableGrid((GridPane) cellButton.getParent());
	        gameTimer.stop();
	        showLoseMessage();
		} else {
			cellButton.setStyle("-fx-background-color: lightgray;");
			int adjacentMines = gameLogic.getAdjacentMines(x, y);
			
			if(adjacentMines > 0) {
				cellButton.setText(String.valueOf(adjacentMines));
			}
		}
		
		if (gameLogic.checkWin()) {
		    disableGrid((GridPane) cellButton.getParent());
		    gameTimer.stop();
		    savedTime = gameTimer.getElapsedTimeMilliseconds();
		    showWinMessage();
		    
		    if (dbManager != null && playerName != null) {
	            dbManager.updatePlayerScore(playerName, savedTime, difficulty);
	            System.out.println("The result has been saved in the database!");
	        }
		}
		
	}
	
	private void handleCellRightClick(int x, int y, Button cellButton) {		
		if (gameLogic.isOpened(x, y)) {
	        return;
	    }
		
	    gameLogic.toggleFlag(x, y);

	    if (gameLogic.isFlagged(x, y)) {
	        cellButton.setStyle("-fx-background-color: yellow;");
	        cellButton.setText("F");
	    } else {
	        cellButton.setStyle("");
	        cellButton.setText("");
	    }
	}
	
	private void showAllMines(GridPane grid) {
	    for (int i = 0; i < gridSize; i++) {
	        for (int j = 0; j < gridSize; j++) {
	            if (gameLogic.isMine(i, j)) {
	                Button cellButton = getNodeByRowColumnIndex(i, j, grid);
	                cellButton.setStyle("-fx-background-color: red;");
	                cellButton.setText("X");
	            }
	        }
	    }
	}
	
	private Button getNodeByRowColumnIndex(int row, int column, GridPane gridPane) {
	    for (javafx.scene.Node node : gridPane.getChildren()) {
	        if (GridPane.getRowIndex(node) != null && GridPane.getColumnIndex(node) != null &&
	            GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
	            return (Button) node;
	        }
	    }
	    return null;
	}
	
	private void requestPlayerName() {
	    TextInputDialog dialog = new TextInputDialog();
	    dialog.setTitle("Enter player name");
	    dialog.setHeaderText("Please, enter your name:");
	    dialog.setContentText("Name:");

	    while (true) {
	        Optional<String> result = dialog.showAndWait();
	        if (result.isPresent()) {
	            String name = result.get().trim();
	            if (name.isEmpty()) {
	                showError("Name cannot be empty!");
	            } else if (dbManager.playerExists(name)) {
	                showError("Name already taken! Try another one.");
	            } else {
	                playerName = name;
	                break;
	            }
	        } else {
	            return;
	        }
	    }
	}

	private void showError(String message) {
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle("Ошибка");
	    alert.setHeaderText(null);
	    alert.setContentText(message);
	    alert.showAndWait();
	}
	
	
}
