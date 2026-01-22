package com.minesweeper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);

		Label title = new Label("MineSweeper");
		title.getStyleClass().add("title");

		Button startGameButton = new Button("New Game");
		Button leaderboardButton = new Button("Leaderboard");
		Button exitButton = new Button("Exit");

		startGameButton.getStyleClass().add("button");
		leaderboardButton.getStyleClass().add("button");
		exitButton.getStyleClass().add("button");

		startGameButton.setOnAction(e -> showDifficultySelection(primaryStage));
		leaderboardButton.setOnAction(e -> showLeaderboard(primaryStage));
		exitButton.setOnAction(e -> primaryStage.close());

		root.getChildren().addAll(title, startGameButton, leaderboardButton, exitButton);

		Scene scene = new Scene(root, 750, 850);
		scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
		primaryStage.setTitle("MineSweeper");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void showDifficultySelection(Stage primaryStage) {
	    VBox root = new VBox(10);
	    root.setAlignment(Pos.CENTER);

	    Label title = new Label("Select Difficulty:");
	    title.getStyleClass().add("title-diff");

	    Button easyButton = new Button("Easy");
	    Button mediumButton = new Button("Medium");
	    Button hardButton = new Button("Hard");
	    Button returnButton = new Button("Back");

	    Pane spacer = new Pane();
	    spacer.setMinHeight(30);

	    easyButton.getStyleClass().add("button");
	    mediumButton.getStyleClass().add("button");
	    hardButton.getStyleClass().add("button");
	    returnButton.getStyleClass().add("button");

	    easyButton.setOnAction(e -> startGame(primaryStage, 10, "Easy"));
	    mediumButton.setOnAction(e -> startGame(primaryStage, 15, "Medium"));
	    hardButton.setOnAction(e -> startGame(primaryStage, 20, "Hard"));
	    returnButton.setOnAction(e -> showMainMenu(primaryStage));

	    root.getChildren().addAll(title, easyButton, mediumButton, hardButton, spacer, returnButton);

	    Scene scene = new Scene(root, 750, 850);
	    scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
	    primaryStage.setScene(scene);
	}

	private void startGame(Stage primaryStage, int gridSize, String difficulty) {
		GameController gameController = new GameController(primaryStage, gridSize, difficulty);
		gameController.initializedGame();
	}

	private void showMainMenu(Stage primaryStage) {
	    VBox root = new VBox(10);
	    root.setAlignment(Pos.CENTER);

	    Label title = new Label("MineSweeper");
	    title.getStyleClass().add("title");

	    Button startGameButton = new Button("New Game");
	    Button leaderboardButton = new Button("Leaderboard");
	    Button exitButton = new Button("Exit");

	    startGameButton.getStyleClass().add("button");
	    leaderboardButton.getStyleClass().add("button");
	    exitButton.getStyleClass().add("button");

	    startGameButton.setOnAction(e -> showDifficultySelection(primaryStage));
	    leaderboardButton.setOnAction(e -> showLeaderboard(primaryStage));
	    exitButton.setOnAction(e -> primaryStage.close());

	    root.getChildren().addAll(title, startGameButton, leaderboardButton, exitButton);

	    Scene scene = new Scene(root, 750, 850);
	    scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
	    primaryStage.setScene(scene);
	}
	
	private void showLeaderboard(Stage primaryStage) {
	    VBox root = new VBox(10);
	    root.setAlignment(Pos.CENTER);

	    Label title = new Label("Leaderboard");
	    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

	    TableView<PlayerScore> table = new TableView<>();
	    TableColumn<PlayerScore, String> nameColumn = new TableColumn<>("Name");
	    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

	    TableColumn<PlayerScore, String> timeColumn = new TableColumn<>("Time (s)");
	    timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

	    TableColumn<PlayerScore, String> difficultyColumn = new TableColumn<>("Difficulty");
	    difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));

	    table.getColumns().add(nameColumn);
	    table.getColumns().add(timeColumn);
	    table.getColumns().add(difficultyColumn);

	    HBox buttonBox = new HBox(10);
	    buttonBox.setAlignment(Pos.CENTER);

	    Button easyButton = new Button("Easy");
	    Button mediumButton = new Button("Medium");
	    Button hardButton = new Button("Hard");
	    Button backButton = new Button("Back");

	    easyButton.setOnAction(e -> loadScores(table, "Easy"));
	    mediumButton.setOnAction(e -> loadScores(table, "Medium"));
	    hardButton.setOnAction(e -> loadScores(table, "Hard"));
	    backButton.setOnAction(e -> showMainMenu(primaryStage));

	    buttonBox.getChildren().addAll(easyButton, mediumButton, hardButton, backButton);

	    root.getChildren().addAll(title, buttonBox, table);

	    Scene leaderboardScene = new Scene(root, 750, 850);
	    leaderboardScene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
	    primaryStage.setScene(leaderboardScene);

	    loadScores(table, "Easy");
	}
	
	private void loadScores(TableView<PlayerScore> table, String difficulty) {
        ObservableList<PlayerScore> scores = FXCollections.observableArrayList();

        String query = "SELECT name, time, difficulty FROM leaderboard WHERE difficulty = ? ORDER BY time ASC";
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:data/leaderboard.db");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, difficulty);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int time = resultSet.getInt("time");
                String diff = resultSet.getString("difficulty");

                scores.add(new PlayerScore(name, time, diff));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setItems(scores);
    }
	
	public static void main(String[] args) {
        launch(args);
    }

}
