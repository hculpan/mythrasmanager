package org.culpan.mythrasmanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DamageHitLocationController {
    @FXML
    private CheckBox checkboxBypassArmor;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    @FXML
    private Label name;

    @FXML
    private TextField textfieldDamage;

    @FXML
    private TextField textfieldArmor;

    @FXML
    private TextField textfieldFinal;

    @FXML
    private TextField textfieldCurrentHp;

    public int damageAmount = 0;

    public String hitLocationName;

    public int hitPoints;

    public int armor;

    public DamageHitLocationController(String hitLocatioName, int hitPoints, int armor) {
        this.hitLocationName = hitLocatioName;
        this.hitPoints = hitPoints;
        this.armor = armor;
    }

    private void calcFinalDamage() {
        try {
            int damage = Integer.parseInt(textfieldDamage.getText());
            if (!checkboxBypassArmor.isSelected()) {
                damage = (damage - armor > 0 ? damage - armor : 0);
            }
            damageAmount = damage;
            textfieldFinal.setText(Integer.toString(damageAmount));
        } catch (NumberFormatException e) {

        }
    }

    @FXML
    public void initialize() {
        name.setText(hitLocationName);
        textfieldDamage.textProperty().addListener( e-> calcFinalDamage());
        checkboxBypassArmor.selectedProperty().addListener( e -> calcFinalDamage());
        textfieldArmor.setText(Integer.toString(armor));
        textfieldCurrentHp.setText(Integer.toString(hitPoints));
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleOk() {
        try {
            damageAmount = Integer.parseInt(textfieldFinal.getText());
        } catch (NumberFormatException e) {
            return;
        }
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }
}
