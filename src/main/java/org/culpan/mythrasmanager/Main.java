package org.culpan.mythrasmanager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.culpan.mythrasmanager.controllers.AddCombatantController;
import org.culpan.mythrasmanager.controllers.CreatureViewController;
import org.culpan.mythrasmanager.controllers.DamageHitLocationController;
import org.culpan.mythrasmanager.controllers.MonsterTemplatesDialogController;
import org.culpan.mythrasmanager.model.MythrasCombatModel;
import org.culpan.mythrasmanager.model.MythrasCombatant;
import org.culpan.mythrasmanager.utils.DiceRoller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main extends Application {
    final private Color color = Color.color(0.66, 0.67, 0.69);

    private MythrasCombatModel mythrasCombatModel = new MythrasCombatModel();

    private Stage stage;

    private Text statusText;

    private Text roundText;

    private ListView<MythrasCombatant> listView;

    private Button nextRoundButton;

    private Button startCombatButton;

    private Button resetCombatButton;

    private TableView<MythrasCombatant.HitLocation> hitLocationTableView;

    private Random random = new Random();

    private int askForInitiative(MythrasCombatant m) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Initiative Roll");
        dialog.setHeaderText("Add initiative for " + m.getName());
        dialog.setContentText("Enter the initiative roll:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                return Integer.parseInt(result.get());
            } catch (NumberFormatException n) {
                return 0;
            }
        };
        return 0;
    }

    @SuppressWarnings("unused")
    private Parent leftPane(int formWidth, int formHeight) {
        listView = new ListView<>();
        listView.setLayoutX(13);
        listView.setLayoutY(42);
        listView.setPrefWidth(488);
        listView.setPrefHeight(446);
        listView.setStyle("-fx-background-insets: 0; -fx-padding: 0;");
        listView.setCellFactory(listView1 -> {
            ListCell<MythrasCombatant> result = new CombatListCellItem(mythrasCombatModel);
            return result;
        });

        final ContextMenu tableContextMenu = new ContextMenu();
        MenuItem damageMenuItem = new MenuItem("Add to Initiative");
        damageMenuItem.setOnAction( e -> {
            MythrasCombatant m = listView.getSelectionModel().getSelectedItem();
            if (m != null) {
                int n = askForInitiative(m);
                m.setCurrentInitiative(m.getInitiative() + n);
                mythrasCombatModel.sortByInitiative();
                listView.refresh();
            }
        });
        MenuItem healMenuItem = new MenuItem("Make Initiative Roll");
        healMenuItem.setOnAction( e -> {
            MythrasCombatant m = listView.getSelectionModel().getSelectedItem();
            if (m != null) {
                m.setCurrentInitiative(m.getInitiative() + random.nextInt(10) + 1);
                mythrasCombatModel.sortByInitiative();
                listView.refresh();
            }
        });
        tableContextMenu.getItems().addAll(damageMenuItem, healMenuItem);
        listView.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if(t.getButton() == MouseButton.SECONDARY && listView.getSelectionModel().getSelectedItem() != null) {
                tableContextMenu.show(listView, t.getScreenX(), t.getScreenY());
            }
        });

        Button addButton = new Button();
        addButton.setLayoutX(8);
        addButton.setLayoutY(5);
        addButton.setPrefHeight(25);
        addButton.setPrefWidth(75);
        addButton.setText("Add");
        addButton.setOnAction( (ActionEvent actionEvent) -> {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddCombatant.fxml"));
            final Parent root;
            try {
                root = loader.load();
                final Scene scene = new Scene(root); //, 368, 368);
                final Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle("Add Combatant");
                stage.setScene(scene);
                stage.showAndWait();
                AddCombatantController controller = loader.getController();
                if (controller.getMythrasCombatant() != null) {
                    mythrasCombatModel.combatants.add(controller.getMythrasCombatant());
                    mythrasCombatModel.sortByInitiative();
                    listView.refresh();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button editButton = new Button();
        editButton.setLayoutX(212);
        editButton.setLayoutY(5);
        editButton.setPrefHeight(25);
        editButton.setPrefWidth(75);
        editButton.setText("Edit");
        editButton.setOnAction( (ActionEvent actionEvent) -> {
            MythrasCombatant mythrasCombatant = null;
            if ((mythrasCombatant = (MythrasCombatant)listView.getSelectionModel().getSelectedItem()) != null) {
                MythrasCombatant temp = (MythrasCombatant)mythrasCombatant.clone();
                final FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddCombatant.fxml"));
                loader.setControllerFactory( f -> new AddCombatantController(temp));
                final Parent root;
                try {
                    root = loader.load();
                    final Scene scene = new Scene(root); //, 368, 368);
                    final Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.setTitle("Edit Combatant");
                    stage.setScene(scene);
                    stage.showAndWait();
                    AddCombatantController controller = loader.getController();
                    if (controller.getMythrasCombatant() != null) {
                        mythrasCombatModel.combatants.remove(mythrasCombatant);
                        mythrasCombatModel.combatants.add(temp);
                        mythrasCombatModel.sortByInitiative();
                        listView.refresh();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button removeButton = new Button();
        removeButton.setLayoutX(425);
        removeButton.setLayoutY(5);
        removeButton.setPrefHeight(25);
        removeButton.setPrefWidth(75);
        removeButton.setText("Remove");
        removeButton.setOnAction( (ActionEvent actionEvent) -> {
            MythrasCombatant mythrasCombatant;
            if ((mythrasCombatant = listView.getSelectionModel().getSelectedItem()) != null) {
                deleteCombatant(mythrasCombatant);
            }
        });

        mythrasCombatModel.sortByInitiative();
        listView.setItems(mythrasCombatModel.combatants);

        Rectangle rectangle3 = new Rectangle(8, 38, 500, 454);
        rectangle3.setArcHeight(20);
        rectangle3.setArcWidth(20);
        rectangle3.setFill(Color.WHITE);
        rectangle3.setStroke(color);

        startCombatButton = new Button("Start Combat");
        startCombatButton.setLayoutX(8);
        startCombatButton.setLayoutY(500);
        startCombatButton.setPrefHeight(25);
        startCombatButton.setPrefWidth(100);
        startCombatButton.setOnAction( (ActionEvent actionEvent) -> {
            if (mythrasCombatModel.combatants.size() == 0) return;
            nextRoundButton.setDisable(false);
            startCombatButton.setDisable(true);
            resetCombatButton.setDisable(false);
            initiativeForAll();
            mythrasCombatModel.startCombat();
            roundText.setText("Round " + mythrasCombatModel.getCombatRound());
        });

        nextRoundButton = new Button("Next Round");
        nextRoundButton.setLayoutX(118);
        nextRoundButton.setLayoutY(500);
        nextRoundButton.setPrefHeight(25);
        nextRoundButton.setPrefWidth(100);
        nextRoundButton.setDisable(true);
        nextRoundButton.setOnAction( (ActionEvent actionEvent) -> {
            mythrasCombatModel.nextRound();
            listView.refresh();
            roundText.setText("Round " + mythrasCombatModel.getCombatRound());
        });

        resetCombatButton = new Button("Stop Combat");
        resetCombatButton.setLayoutX(228);
        resetCombatButton.setLayoutY(500);
        resetCombatButton.setPrefHeight(25);
        resetCombatButton.setPrefWidth(100);
        resetCombatButton.setDisable(true);
        resetCombatButton.setOnAction( (ActionEvent actionEvent) -> {
            mythrasCombatModel.stopCombat();
            startCombatButton.setDisable(false);
            nextRoundButton.setDisable(true);
            resetCombatButton.setDisable(true);
            resetInitiative();
            roundText.setText("");
        });

        roundText = new Text();
        roundText.setLayoutX(378);
        roundText.setLayoutY(520);
        roundText.setTextAlignment(TextAlignment.CENTER);
        roundText.setFill(Color.BLACK);
        roundText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        statusText = new Text();
        statusText.setLayoutX(8);
        statusText.setLayoutY(502);
        statusText.setTextAlignment(TextAlignment.CENTER);
        statusText.setFill(Color.BLACK);
        statusText.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        FlowPane topButtons = new FlowPane();
        topButtons.setPadding(new Insets(5, 10, 10, 23));
        topButtons.setHgap(120);
        topButtons.getChildren().addAll(addButton, editButton, removeButton);
        topButtons.setPrefWidth(540);

        return new Group(rectangle3, topButtons, listView, statusText, startCombatButton, nextRoundButton, resetCombatButton, roundText);
    }

    protected void drawSkillText(GraphicsContext gc, int y, String level, int value, int roll) {
        gc.setFont(Font.font("Arial", 12));
        if (roll > 0) {
            gc.setFill(Color.BLACK);
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(level, 75, y);
            gc.setTextAlign(TextAlignment.CENTER);
            if ((roll == 100) || (roll == 99 && value < 100)) {
                gc.setFill(Color.RED);
                gc.fillText(String.format("%d > %d", roll, value), 135, y);
            } else if (roll <= value) {
                gc.setFill(Color.BLACK);
                gc.fillText(String.format("%d <= %d", roll, value), 135, y);
            } else {
                gc.setFill(Color.DARKGRAY);
                gc.fillText(String.format("%d > %d", roll, value), 135, y);
            }
            int crit = (int)Math.ceil((double)value * 0.1);
            if ((roll == 100) || (roll == 99 && value < 100)) {
                gc.setFill(Color.RED);
                gc.fillText(String.format("%d > %d", roll, value), 205, y);
            } else if (roll <= crit) {
                gc.setFill(Color.BLACK);
                gc.fillText(String.format("%d <= %d", roll, crit), 205, y);
            } else {
                gc.setFill(Color.DARKGRAY);
                gc.fillText(String.format("%d > %d", roll, crit), 205, y);
            }
        } else {
            gc.setFill(Color.BLACK);
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(level, 75, y);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.format("%d", value), 135, y);
            gc.fillText(String.format("%d", (int)Math.ceil((double)value * 0.1)), 205, y);
        }
    }

    protected void drawSkillPercentage(String value, int roll, Canvas skillPercentCanvas) {
        GraphicsContext gc = skillPercentCanvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.setStroke(color);
        gc.fillRoundRect(0, 0, 254, 125, 20, 20);
        gc.strokeRoundRect(0, 0, 254, 125, 20, 20);
        gc.setFill(Color.LIGHTCYAN);
        gc.fillRoundRect(2, 3, 250, 15, 20, 20);
        gc.fillRect(2, 13, 250, 8);
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(2, 22, 250, 19);
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(2, 42, 250, 20);
        gc.setFill(Color.YELLOW);
        gc.fillRect(2, 63, 250, 19);
        gc.setFill(Color.LIGHTSALMON);
        gc.fillRect(2, 84, 250, 19);
        gc.setFill(Color.LIGHTPINK);
        gc.fillRect(2, 104, 250, 12);
        gc.fillRoundRect(2, 111, 250, 11, 20, 20);
        try {
            int number = Integer.parseInt(value);
            drawSkillText(gc, 16, "Very Easy", number * 2, roll);
            drawSkillText(gc, 37, "Easy", (int)Math.ceil((double)number * 1.5), roll);
            drawSkillText(gc, 57, "Standard", number, roll);
            drawSkillText(gc, 77, "Hard", (int)Math.ceil((double)number * .66), roll);
            drawSkillText(gc, 97, "Formidable", (int)Math.ceil((double)number / 2), roll);
            drawSkillText(gc, 117, "Herculean", (int)Math.ceil((double)number * .1), roll);
        } catch (NumberFormatException e) {
            gc.setFill(Color.BLACK);
            gc.fillText("Not a number", 105, 57);
        }
    }

    protected Parent buildSkillPercentage() {
        HBox hBoxSkillPercent = new HBox();
        hBoxSkillPercent.setSpacing(15);

        Label textSkillPercent = new Label("Skill %:");
        textSkillPercent.setPadding(new Insets(5, 0, 0, 0));
        textSkillPercent.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        final TextField textFieldSkillPercent = new TextField("50");
        textFieldSkillPercent.setPrefWidth(75);
        final Button buttonSkillPercent = new Button("Roll");
        buttonSkillPercent.setPrefWidth(75);
        final Canvas canvasSkillPercent = new Canvas();
        canvasSkillPercent.setWidth(254);
        canvasSkillPercent.setHeight(125);
        drawSkillPercentage(textFieldSkillPercent.getText(), 0, canvasSkillPercent);

        buttonSkillPercent.setOnAction( e -> {
            drawSkillPercentage(textFieldSkillPercent.getText(), random.nextInt(100) + 1, canvasSkillPercent);
        });

        textFieldSkillPercent.textProperty().addListener(e -> {
            drawSkillPercentage(textFieldSkillPercent.getText(), 0, canvasSkillPercent);
        });

        hBoxSkillPercent.getChildren().addAll(textSkillPercent, textFieldSkillPercent, buttonSkillPercent);

        VBox result = new VBox();
        result.setSpacing(6);
        result.getChildren().addAll(hBoxSkillPercent, canvasSkillPercent);

        return result;
    }

    protected Parent rightPane(int formWidth, int formHeight) {
        GridPane result = new GridPane();
        result.setPadding(new Insets(6, 0, 0, 0));
        result.setHgap(10);
        result.setVgap(10);

        hitLocationTableView = new TableView<>();

        listView.getSelectionModel().selectedItemProperty().addListener( e -> {
            MythrasCombatant m = listView.getSelectionModel().getSelectedItem();
            if (m != null) {
                hitLocationTableView.setItems(FXCollections.observableArrayList(m.hitLocations));
            }
        } );

        TableColumn<MythrasCombatant.HitLocation, String> colRange = new TableColumn<>("Range");
        colRange.setCellValueFactory( new PropertyValueFactory<>("range"));
        colRange.setStyle( "-fx-alignment: CENTER;");
        TableColumn<MythrasCombatant.HitLocation, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory( new PropertyValueFactory<>("name"));
        colName.setStyle( "-fx-alignment: CENTER;");
        TableColumn<MythrasCombatant.HitLocation, Integer> colAp = new TableColumn<>("AP");
        colAp.setCellValueFactory( new PropertyValueFactory<>("armorPoints"));
        colAp.setPrefWidth(43);
        colAp.setStyle( "-fx-alignment: CENTER;");
        TableColumn<MythrasCombatant.HitLocation, Integer> colHp = new TableColumn<>("HP");
        colHp.setCellValueFactory( new PropertyValueFactory<>("hitPoints"));
        colHp.setPrefWidth(43);
        colHp.setStyle( "-fx-alignment: CENTER;");
        TableColumn<MythrasCombatant.HitLocation, Integer> colCurrentHp = new TableColumn<>("Current");
        colCurrentHp.setCellValueFactory( new PropertyValueFactory<>("currentHitPoints"));
        colCurrentHp.setPrefWidth(55);
        colCurrentHp.setStyle( "-fx-alignment: CENTER;");
        TableColumn<MythrasCombatant.HitLocation, Integer> colWound = new TableColumn<>("Wound");
        colWound.setCellValueFactory( new PropertyValueFactory<>("wound"));
        colWound.setPrefWidth(55);
        colWound.setStyle( "-fx-alignment: CENTER;");
        TableColumn<MythrasCombatant.HitLocation, Integer> colEffect = new TableColumn<>("Effect");
        colEffect.setCellValueFactory( new PropertyValueFactory<>("effect"));
        colEffect.setPrefWidth(145);
        colEffect.setStyle( "-fx-alignment: CENTER;");
        hitLocationTableView.getColumns().addAll(colRange, colName, colAp, colHp, colCurrentHp, colWound, colEffect);
        hitLocationTableView.setEditable(true);

        final ContextMenu tableContextMenu = new ContextMenu();
        MenuItem damageMenuItem = new MenuItem("Damage");
        damageMenuItem.setOnAction( e -> {
            MythrasCombatant.HitLocation hitLocation = hitLocationTableView.getSelectionModel().getSelectedItem();
            if (hitLocation != null) {
                int damage = hitLocationDamageDialog(hitLocation);
                hitLocation.setCurrentHitPoints(hitLocation.getCurrentHitPoints() - damage);
                hitLocation.calculateWound();
                hitLocationTableView.refresh();
            }
        });
        MenuItem healMenuItem = new MenuItem("Restore");
        healMenuItem.setOnAction( e -> {
            MythrasCombatant.HitLocation hitLocation = hitLocationTableView.getSelectionModel().getSelectedItem();
            if (hitLocation != null) {
                hitLocation.setCurrentHitPoints(hitLocation.getHitPoints());
                hitLocation.calculateWound();
                hitLocationTableView.refresh();
            }
        });
        tableContextMenu.getItems().addAll(damageMenuItem, healMenuItem);
        hitLocationTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if(t.getButton() == MouseButton.SECONDARY || t.isControlDown()) {
                tableContextMenu.show(hitLocationTableView, t.getScreenX(), t.getScreenY());
            }
        });

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setPrefWidth(304);
        tabPane.setMaxWidth(504);
        tabPane.setMinHeight(317);
        tabPane.setPrefHeight(317);

        Tab hitLocationsTab = new Tab("Hit Locations");
        hitLocationsTab.setContent(hitLocationTableView);
        tabPane.getTabs().add(hitLocationsTab);

        result.add(buildSkillPercentage(), 0, 0);
        result.add(tabPane, 0, 1, 2, 1);

        GridPane diceGridPane = new GridPane();
        diceGridPane.setVgap(2);
        diceGridPane.getRowConstraints().addAll(
            new RowConstraints(28),
            new RowConstraints(28),
            new RowConstraints(28),
            new RowConstraints(28),
            new RowConstraints(28)
        );

        Rectangle rectangle3 = new Rectangle(0, 0, 237, 157);
        rectangle3.setArcHeight(20);
        rectangle3.setArcWidth(20);
        rectangle3.setFill(Color.WHITE);
        rectangle3.setStroke(color);
        rectangle3.toBack();
        diceGridPane.add(rectangle3, 0, 0, 1, 5);

        buildDiceRoller(diceGridPane, 0);
        buildDiceRoller(diceGridPane, 1);
        buildDiceRoller(diceGridPane, 2);
        buildDiceRoller(diceGridPane, 3);
        buildDiceRoller(diceGridPane, 4);

        result.add(diceGridPane, 1, 0);

        result.getColumnConstraints().add(new ColumnConstraints(254));
        result.getColumnConstraints().add(new ColumnConstraints(245));
        GridPane.setMargin(diceGridPane, new Insets(10, 0, 0, 0));

        return result;
    }

    private void buildDiceRoller(GridPane gridPane, int rowIndex) {
        HBox result = new HBox();
        result.setSpacing(15);

        final TextField diceTextField = new TextField();
        diceTextField.setMaxWidth(100);
        final Text diceResultText = new Text();
        HBox.setMargin(diceResultText, new Insets(5, 0, 0, 0));

        Button rollButton = new Button("->");

        diceTextField.setOnAction( e -> rollDice(diceTextField, diceResultText));
        rollButton.setOnAction(e -> rollDice(diceTextField, diceResultText));

        result.getChildren().addAll(diceTextField, rollButton, diceResultText);

        gridPane.add(result, 0, rowIndex);
        GridPane.setMargin(result, new Insets(0, 0, 0, 5));
    }

    private void rollDice(TextField diceTextField, Text diceResultText) {
        if (!diceTextField.getText().isEmpty()) {
            DiceRoller diceRoller = new DiceRoller(diceTextField.getText());
            try {
                diceResultText.setText(Integer.toString(diceRoller.roll()));
            } catch (Exception ex) {
                diceResultText.setText(ex.getLocalizedMessage());
            }
        }
    }

    protected int hitLocationDamageDialog(MythrasCombatant.HitLocation hitLocation) {
        int result = 0;

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/DamageHitLocation.fxml"));
        final Parent root;
        try {
            loader.setControllerFactory( f -> new DamageHitLocationController(hitLocation.getName(), hitLocation.getHitPoints(), hitLocation.getArmorPoints()));
            root = loader.load();
            DamageHitLocationController controller = loader.getController();
            final Scene scene = new Scene(root); //, 368, 368);
            final Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Damage Hit Location");
            stage.setScene(scene);
            stage.showAndWait();
            result = controller.damageAmount;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_LEFT);
        gridPane.add(leftPane(520, 540), 0, 0);
        gridPane.add(rightPane(520, 540), 1, 0);
        gridPane.getColumnConstraints().add(new ColumnConstraints(520));
        gridPane.getColumnConstraints().add(new ColumnConstraints(520));

        Rectangle rectangle2 = new Rectangle(0, 0, 1040, 540);
        rectangle2.setFill(Color.rgb(199, 206, 213));

        Group masterGroup = new Group(addMenu(), rectangle2, gridPane);

        Scene scene = new Scene(masterGroup, 1040, 540);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Mythras Manager");
        primaryStage.resizableProperty().setValue(false);
        primaryStage.show();
    }

    protected void saveAsCombatData() {
        UserConfiguration.getInstance().setLastSavedPath(null);
        saveCombatData();
    }

    protected void saveCombatData() {
        if (mythrasCombatModel.combatants.size() > 0) {
            String savePath = UserConfiguration.getInstance().getLastSavedPath();
            if (savePath == null || savePath.isEmpty() || !new File(savePath).exists()) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Combat Information");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Mythras Combat Files", "*.myci"),
                        new FileChooser.ExtensionFilter("JSON", "*.json"),
                        new FileChooser.ExtensionFilter("All Files", "*.*")
                );
                File file = fileChooser.showSaveDialog(stage);
                if (file != null) {
                    try {
                        savePath = file.getCanonicalPath();
                    } catch (IOException e) {
                        savePath = null;
                    }
                }
            }

            if (savePath != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(mythrasCombatModel.combatants);
                    try (FileWriter out = new FileWriter(new File(savePath))) {
                        out.write(json);
                    }
                    UserConfiguration.getInstance().setLastSavedPath(savePath);
                    statusText.setText("Saved");
                    Timeline timeline = new Timeline();
                    timeline.setCycleCount(1);
                    timeline.getKeyFrames().addAll(new KeyFrame(new Duration(4000)));
                    timeline.play();
                    timeline.setOnFinished(e -> statusText.setText(""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void openCombatData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Combat Information");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Mythras Combat Files", "*.myci"),
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            mythrasCombatModel.reset();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<MythrasCombatant> combatants = objectMapper.readValue(file, new TypeReference<List<MythrasCombatant>>(){});
                mythrasCombatModel.combatants.addAll(combatants);
                UserConfiguration.getInstance().setLastSavedPath(file.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected MenuBar addMenu() {
        final String os = System.getProperty("os.name");
        final Menu menuFile = new Menu("File");
        final Menu menuCombatant = new Menu("Combatant");

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menuFile);
        menuBar.getMenus().add(menuCombatant);

        KeyCombination.Modifier systemKey = KeyCombination.CONTROL_DOWN;
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
            systemKey = KeyCombination.META_DOWN;
        }

        MenuItem saveCombat = new MenuItem("Save...");
        saveCombat.setAccelerator(new KeyCodeCombination(KeyCode.S, systemKey));
        saveCombat.setOnAction( event -> {
            saveCombatData();
        });

        MenuItem saveAsCombat = new MenuItem("Save As...");
        saveAsCombat.setAccelerator(new KeyCodeCombination(KeyCode.A, systemKey));
        saveAsCombat.setOnAction( event -> {
            saveAsCombatData();
        });

        MenuItem openCombat = new MenuItem("Open...");
        openCombat.setAccelerator(new KeyCodeCombination(KeyCode.O, systemKey));
        openCombat.setOnAction( event -> {
            openCombatData();
        });

        MenuItem menuTemplates = new MenuItem("Monster Templates");
        menuTemplates.setAccelerator(new KeyCodeCombination(KeyCode.M, systemKey));
        menuTemplates.setOnAction( event -> {
            try {
                final FXMLLoader loader = new FXMLLoader(getClass().getResource("/monster_templates.fxml"));
                final Parent root = loader.load();
                final Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle("Monster Templates");
                stage.setScene(scene);
                stage.showAndWait();
                MonsterTemplatesDialogController controller = loader.getController();
                if (!controller.monsters.isEmpty()) {
                    mythrasCombatModel.combatants.addAll(controller.monsters);
                    mythrasCombatModel.sortByInitiative();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        MenuItem menuResetActionPoints = new MenuItem("Reset Action Points");
        menuResetActionPoints.setAccelerator(new KeyCodeCombination(KeyCode.R, systemKey));
        menuResetActionPoints.setOnAction( event -> {
            if (listView.getSelectionModel().getSelectedItem() != null) {
                MythrasCombatant m = listView.getSelectionModel().getSelectedItem();
                m.currentActionPoints = m.actionPoints;
                listView.refresh();
            }
        });

        MenuItem menuDeleteCombatant = new MenuItem("Delete Combatant");
        menuDeleteCombatant.setAccelerator(new KeyCodeCombination(KeyCode.D, systemKey));
        menuDeleteCombatant.setOnAction( event -> {
            if (listView.getSelectionModel().getSelectedItem() != null) {
                MythrasCombatant m = listView.getSelectionModel().getSelectedItem();
                deleteCombatant(m);
            }
        });

        MenuItem menuDeleteNpcs = new MenuItem("Delete NPCs");
        menuDeleteNpcs.setAccelerator(new KeyCodeCombination(KeyCode.N, systemKey));
        menuDeleteNpcs.setOnAction( event -> {
            Iterator<MythrasCombatant> i = mythrasCombatModel.combatants.iterator();
            while (i.hasNext()) {
                MythrasCombatant m = i.next();
                if (m.isNpc()) {
                    i.remove();
                }
            }
            listView.refresh();
        });

        MenuItem menuResetInitForAll = new MenuItem("Reset All Initiatives");
        menuResetInitForAll.setOnAction( event -> {
            resetInitiative();
        });

        MenuItem menuRollInitForNpcs = new MenuItem("Roll Initiative for All NPCs");
        menuRollInitForNpcs.setOnAction( event -> {
            mythrasCombatModel.combatants.filtered(m -> m.isNpc()).forEach(m -> m.setCurrentInitiative(m.getInitiative() + random.nextInt(10) + 1));
            mythrasCombatModel.sortByInitiative();
            listView.refresh();
        });

        MenuItem menuInitForAll = new MenuItem("Initiative for All Combatants");
        menuInitForAll.setOnAction( event -> {
            initiativeForAll();
        });

        CheckMenuItem menuShowInitiative = new CheckMenuItem("Show Initiative");
        menuShowInitiative.setSelected(UserConfiguration.getInstance().isShowInitiative());
        menuShowInitiative.setOnAction( event -> {
            UserConfiguration.getInstance().setShowInitiative(!UserConfiguration.getInstance().isShowInitiative());
            menuShowInitiative.setSelected(UserConfiguration.getInstance().isShowInitiative());
            listView.refresh();
        });

        MenuItem menuCreatureView = new MenuItem("View creature");
        menuCreatureView.setAccelerator(new KeyCodeCombination(KeyCode.V, systemKey));
        menuCreatureView.setOnAction( event -> {
            MythrasCombatant m = listView.getSelectionModel().getSelectedItem();
            if (m != null && m.getRawJsonFile() != null) {
                try {
                    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreatureView.fxml"));
                    final Parent root;
                    loader.setControllerFactory( f -> new CreatureViewController(m));
                    root = loader.load();
                    final Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.setTitle("View " + m.getName());
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        menuFile.getItems().addAll(openCombat, saveCombat, saveAsCombat, new SeparatorMenuItem(), menuTemplates);

        menuCombatant.getItems().addAll(menuResetActionPoints, menuDeleteCombatant, menuDeleteNpcs,
                new SeparatorMenuItem(), menuCreatureView,
                new SeparatorMenuItem(), menuInitForAll, menuRollInitForNpcs, menuResetInitForAll, menuShowInitiative);

        return menuBar;
    }

    private void deleteCombatant(MythrasCombatant m) {
        if (m.isActing()) {
            mythrasCombatModel.nextCombatantToAct();
        }
        mythrasCombatModel.combatants.remove(m);
        listView.refresh();
    }

    private void resetInitiative() {
        mythrasCombatModel.combatants.forEach(m -> m.setCurrentInitiative(m.getInitiative()));
        mythrasCombatModel.sortByInitiative();
        listView.refresh();
    }

    private void initiativeForAll() {
        for (MythrasCombatant m : mythrasCombatModel.combatants) {
            if (m.isNpc()) {
                m.setCurrentInitiative(m.getInitiative() + random.nextInt(10) + 1);
            } else {
                int n = askForInitiative(m);
                m.setCurrentInitiative(m.getInitiative() + n);
            }
        }
        mythrasCombatModel.sortByInitiative();
        listView.refresh();
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            UserConfiguration.getInstance().setExternalCreatureTemplatePath(args[0]);
        }
        launch(args);
    }
}
