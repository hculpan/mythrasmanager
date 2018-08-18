package org.culpan.mythrasmanager.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.culpan.mythrasmanager.model.MythrasCombatant;

public class AddCombatantController {
    private MythrasCombatant mythrasCombatant;

    @FXML
    private TextField textfieldName;

    @FXML
    private TextField textfieldActionPoints;

    @FXML
    private TextField textfieldInitiative;

    @FXML
    private Spinner<Integer> spinnerInt;

    @FXML
    private Spinner<Integer> spinnerDex;

    @FXML
    private CheckBox checkboxNpc;

    @FXML
    private Label validationError;

    public AddCombatantController() {

    }

    public AddCombatantController(MythrasCombatant mythrasCombatant) {
        this.mythrasCombatant = mythrasCombatant;
    }

    @FXML
    public void initialize() {
        spinnerDex.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 10));
        spinnerDex.valueProperty().addListener( e -> spinnerUpdated());
        spinnerInt.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 10));
        spinnerInt.valueProperty().addListener( e -> spinnerUpdated());

        if (mythrasCombatant != null && mythrasCombatant.name != null) {
            textfieldName.setText(mythrasCombatant.name);
            textfieldActionPoints.setText(Integer.toString(mythrasCombatant.actionPoints));
            textfieldInitiative.setText(Integer.toString(mythrasCombatant.initiative));
            spinnerInt.getValueFactory().setValue(mythrasCombatant.intelligence);
            spinnerDex.getValueFactory().setValue(mythrasCombatant.dexterity);
            checkboxNpc.selectedProperty().setValue(mythrasCombatant.isNpc());
            textfieldName.setEditable(false);
        }
    }

    @FXML
    public void handleOk() {
        if (textfieldName.getText() == null || textfieldName.getText().isEmpty()) {
            validationError.setText("Name cannot be empty");
            Timeline timeline  = new Timeline();
            timeline.setCycleCount(1);
            timeline.getKeyFrames().addAll(new KeyFrame(new Duration(3000)));
            timeline.play();
            timeline.setOnFinished( e -> validationError.setText(""));
            return;
        }

        if (mythrasCombatant == null) {
            mythrasCombatant = new MythrasCombatant();
            mythrasCombatant.name = textfieldName.getText();
            mythrasCombatant.intelligence = spinnerInt.getValue().intValue();
            mythrasCombatant.dexterity = spinnerDex.getValue().intValue();
            mythrasCombatant.setNpc(checkboxNpc.isSelected());
            mythrasCombatant.calculateAttributes();
        } else {
            mythrasCombatant.intelligence = spinnerInt.getValue().intValue();
            mythrasCombatant.dexterity = spinnerDex.getValue().intValue();
            mythrasCombatant.setNpc(checkboxNpc.isSelected());
            mythrasCombatant.calculateAttributes();
        }
        Stage stage = (Stage)spinnerInt.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleCancel() {
        mythrasCombatant = null;
        Stage stage = (Stage)spinnerInt.getScene().getWindow();
        stage.close();
    }

    public void spinnerUpdated() {
        textfieldInitiative.setText(Integer.toString(MythrasCombatant.calculateInitiative(spinnerInt.getValue().intValue(), spinnerDex.getValue().intValue())));
        textfieldActionPoints.setText(Integer.toString(MythrasCombatant.calculateActionPoints(spinnerInt.getValue().intValue(), spinnerDex.getValue().intValue())));
    }

    public MythrasCombatant getMythrasCombatant() {
        return mythrasCombatant;
    }
}
