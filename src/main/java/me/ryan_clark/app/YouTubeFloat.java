package me.ryan_clark.app;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class YouTubeFloat extends Application {

	// Constants
	private final int BUTTON_DIM = 20;
	private final int INIT_DIM_X = 640;
	private final int INIT_DIM_Y = 360;
	private final int TEXT_FIELD_WIDTH = 400;
	private final int MIN_DIM_X = 160;
	private final int MIN_DIM_Y = 90;

	// Member variables
	private int xOffset;
	private int yOffset;
	private int originalWidth;
	private int originalHeight;
	private String url = "";
	private boolean mouseClicked = false;
	private boolean mouseIn = false;
	private boolean videoLoaded = false;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		// Creating a group
		Group root = new Group();

		// "Invisible" background pane (alpha of 0.001) to detect mouse events
		Pane paneBackground = new Pane();
		paneBackground.setStyle("-fx-background-color: rgba(255,255,255,0.001)");
		paneBackground.prefWidthProperty().bind(stage.widthProperty());
		paneBackground.prefHeightProperty().bind(stage.heightProperty());

		// Draggable button to move the window
		Pane paneMove = new Pane();
		paneMove.setStyle("-fx-background-color: rgba(255,255,255,0.001)");
		paneMove.setPrefSize(BUTTON_DIM, BUTTON_DIM);

		ImageView moveIcon = new ImageView();
		moveIcon.setImage(new Image("move.png"));
		moveIcon.setVisible(false);

		// Draggable button to resize the window
		Pane paneResize = new Pane();
		paneResize.setStyle("-fx-background-color: rgba(255,255,255,0.001)");
		paneResize.setPrefSize(BUTTON_DIM, BUTTON_DIM);

		ImageView resizeIcon = new ImageView();
		resizeIcon.setImage(new Image("corner.png"));
		resizeIcon.setVisible(false);

		// Button to close the window
		Pane paneClose = new Pane();
		paneClose.setStyle("-fx-background-color: rgba(255,255,255,0.001)");
		paneClose.setPrefSize(BUTTON_DIM, BUTTON_DIM);

		ImageView closeIcon = new ImageView();
		closeIcon.setImage(new Image("close.png"));
		closeIcon.setVisible(false);

		// Text prompt to enter YouTube URL
		Text prompt = new Text();
		prompt.setTranslateX((INIT_DIM_X - TEXT_FIELD_WIDTH) / 2);
		prompt.setTranslateY((INIT_DIM_Y - BUTTON_DIM) / 2 - 10);
		prompt.setText("Enter a valid YouTube URL");
		prompt.setFont(Font.font("Verdana", 16));
		prompt.setFill(Color.WHITE);

		// Text field to enter YouTube URL
		TextField urlField = new TextField();
		urlField.setTranslateX((INIT_DIM_X - TEXT_FIELD_WIDTH) / 2);
		urlField.setTranslateY((INIT_DIM_Y - BUTTON_DIM) / 2);
		urlField.setPrefWidth(TEXT_FIELD_WIDTH);
		urlField.setPrefHeight(BUTTON_DIM);

		// Webview to display a given YouTube embed URL
		WebView webview = new WebView();
		webview.setPrefSize(INIT_DIM_X, INIT_DIM_Y);
		webview.prefWidthProperty().bind(stage.widthProperty());
		webview.prefHeightProperty().bind(stage.heightProperty());

		// When the mouse enters the window, display the buttons
		root.setOnMouseEntered((MouseEvent event) -> {
			mouseIn = true;
			moveIcon.setVisible(true);
			resizeIcon.setVisible(true);
			closeIcon.setVisible(true);
		});

		// When the mouse exits the window, hide the buttons
		root.setOnMouseExited((MouseEvent event) -> {
			mouseIn = false;
			if (!mouseClicked) {
				moveIcon.setVisible(false);
				resizeIcon.setVisible(false);
				closeIcon.setVisible(false);
			}
		});

		// On mouse click, record mouse position and window dimensions.
		// If the top right corner is clicked, close the window.
		// Sets mouseClicked flag.
		root.setOnMousePressed((MouseEvent event) -> {
			mouseClicked = true;
			xOffset = (int) event.getSceneX();
			yOffset = (int) event.getSceneY();
			originalWidth = (int) stage.getWidth();
			originalHeight = (int) stage.getHeight();

			if (xOffset >= originalWidth - BUTTON_DIM && yOffset <= BUTTON_DIM) {
				// Left clicking the X closes the window, right clicking closes currently
				// playing video
				if (event.isPrimaryButtonDown()) {
					stage.close();
				} else if (event.isSecondaryButtonDown() && videoLoaded) {
					videoLoaded = false;
					for (int i = root.getChildren().size() - 1; i > -1; i--)
						root.getChildren().remove(i);
					root.getChildren().addAll(paneBackground, prompt, urlField, paneMove, paneResize, paneClose,
							moveIcon, resizeIcon, closeIcon);
					prompt.setText("Enter a valid YouTube URL");
					prompt.setFill(Color.WHITE);
					urlField.clear();
					webview.getEngine().load(null);

				}
			}
		});

		// On mouse release, change mouseClicked flag.
		root.setOnMouseReleased((MouseEvent event) -> {
			mouseClicked = false;
			if(!mouseIn) {
				moveIcon.setVisible(false);
				resizeIcon.setVisible(false);
				closeIcon.setVisible(false);
			}
		});

		// Do stuff if the mouse is being dragged after a click
		root.setOnMouseDragged((MouseEvent event) -> {

			// If the mouse was clicked at the top left, dragging moves the window
			// If the mouse was clicked at the bottom right, dragging resizes the window
			if (xOffset <= BUTTON_DIM && yOffset <= BUTTON_DIM) {
				stage.setX(event.getScreenX() - xOffset);
				stage.setY(event.getScreenY() - yOffset);
			} else if (xOffset >= originalWidth - BUTTON_DIM && yOffset >= originalHeight - BUTTON_DIM) {
				double width = event.getScreenX() - stage.getX() + (originalWidth - xOffset);
				double height = event.getScreenY() - stage.getY() + (originalHeight - yOffset);

				// Enforce minimum window width
				if (width > MIN_DIM_X) {
					stage.setWidth(width);
				} else {
					stage.setWidth(MIN_DIM_X);
				}

				// Enforce minimum window height
				if (height > MIN_DIM_Y) {
					stage.setHeight(height);
				} else {
					stage.setHeight(MIN_DIM_Y);
				}
			}
		});

		// Listener to move buttons if the window width changes
		stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			resizeIcon.setLayoutX((double) newVal - BUTTON_DIM);
			paneResize.setLayoutX((double) newVal - BUTTON_DIM);
			closeIcon.setLayoutX((double) newVal - BUTTON_DIM);
			paneClose.setLayoutX((double) newVal - BUTTON_DIM);
			
			prompt.setTranslateX(((double) newVal - TEXT_FIELD_WIDTH) / 2);
			if(prompt.getTranslateX() < 10) {
				prompt.setTranslateX(10);
			}
			
			if((double) newVal < 235 && prompt.getFont().getSize() != 10) {
				prompt.setFont(Font.font("Verdana", 10));
			} else if ((double) newVal >= 235 && prompt.getFont().getSize() != 16) {
				prompt.setFont(Font.font("Verdana", 16));
			}
			
			urlField.setTranslateX(((double) newVal - TEXT_FIELD_WIDTH) / 2);
			if(urlField.getTranslateX() < 10) {
				urlField.setTranslateX(10);
				urlField.setPrefWidth((double) newVal - 20);
			} else if(urlField.getPrefWidth() != TEXT_FIELD_WIDTH){
				urlField.setPrefWidth(TEXT_FIELD_WIDTH);
			}
		});

		// Listener to move buttons if the window height changes
		stage.heightProperty().addListener((obs, oldVal, newVal) -> {
			resizeIcon.setLayoutY((double) newVal - BUTTON_DIM);
			paneResize.setLayoutY((double) newVal - BUTTON_DIM);
			
			prompt.setTranslateY(((double) newVal - BUTTON_DIM) / 2 - 10);
			urlField.setTranslateY(((double) newVal - BUTTON_DIM) / 2);
		});

		// When the enter key is pressed while typing in the URL field, parse the
		// YouTube URL into an embed URL. If invalid, clear the field and error prompt.
		urlField.setOnKeyPressed((KeyEvent event) -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				String urlText = parseURL(urlField.getText());
				if (!urlText.equals("")) {
					url = "http://www.youtube.com/embed/" + urlText + "?autoplay=1" + "&modestbranding=1" + "&fs=0"
							+ "&rel=0";
					webview.getEngine().load(url);

					for (int i = root.getChildren().size() - 1; i > -1; i--)
						root.getChildren().remove(i);

					root.getChildren().addAll(webview, paneMove, paneResize, paneClose, moveIcon, resizeIcon,
							closeIcon);
					videoLoaded = true;
					System.out.println("Opened Video");
				} else {
					prompt.setFill(Color.RED);
					prompt.setText("Invalid URL");
					urlField.clear();
				}
			}
		});

		// Add everything to the root group
		ObservableList<Node> list = root.getChildren();
		list.addAll(paneBackground, prompt, urlField, paneMove, paneResize, paneClose, moveIcon, resizeIcon, closeIcon);

		// Create a scene using the root group
		Scene scene = new Scene(root, INIT_DIM_X, INIT_DIM_Y);
		scene.setFill(Color.BLACK);

		// Setup the stage, and display it
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);	
		stage.titleProperty().bind(webview.getEngine().titleProperty());
		stage.setAlwaysOnTop(true);
		stage.show();
	}

	
	// Hardcoding :^)
	private String parseURL(String s) {
		// Standard YouTube URL (e.g. http://youtube.com/watch?v=0123456789a)
		if (s.contains("youtube.com/watch?v=")) {
			try {
				return s.substring(s.indexOf('=') + 1, s.indexOf('=') + 12);
			} catch (Exception e) {
				return "";
			}
		}

		// Shortened YouTube URL (e.g. http://youtu.be/0123456789a)
		if (s.contains("youtu.be")) {
			try {
				return s.substring(s.indexOf('e') + 2, s.indexOf('e') + 13);
			} catch (Exception e) {
				return "";
			}
		}

		// YouTube embed URL (e.g. http://www.youtube.com/embed/0123456789a)
		if (s.contains("youtube.com/embed")) {
			try {
				return s.substring(s.indexOf("embed") + 6, s.indexOf("embed") + 17);
			} catch (Exception e) {
				return "";
			}
		}

		// Invalid YouTube URL
		return "";
	}

}