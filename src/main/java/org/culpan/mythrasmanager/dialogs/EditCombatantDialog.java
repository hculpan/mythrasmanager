package org.culpan.mythrasmanager.dialogs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.util.Duration;
import org.culpan.mythrasmanager.model.MythrasCombatModel;
import org.culpan.mythrasmanager.model.MythrasCombatant;

import java.util.Optional;

public class EditCombatantDialog extends Dialog {
    public enum EditDialogConfig { ADD, EDIT }

    public MythrasCombatant mythrasCombatant;

    public EditDialogConfig editDialogConfig;

    protected MythrasCombatModel mythrasCombatModel;

    public static EditCombatantDialog addCombatantDialog(MythrasCombatModel mythrasCombatModel) {
        return new EditCombatantDialog(null, mythrasCombatModel, EditDialogConfig.ADD);
    }

    public static EditCombatantDialog editCombatantDialog(MythrasCombatant mythrasCombatant,
                                                          MythrasCombatModel mythrasCombatModel) {
        return new EditCombatantDialog(mythrasCombatant, mythrasCombatModel, EditDialogConfig.EDIT);
    }

    private EditCombatantDialog(MythrasCombatant mythrasCombatant,
                                MythrasCombatModel mythrasCombatModel,
                                EditDialogConfig editDialogConfig) {
        this.mythrasCombatant = mythrasCombatant;
        this.mythrasCombatModel = mythrasCombatModel;
        this.editDialogConfig = editDialogConfig;
    }

    public boolean showDialog() {

        Rectangle rectangle1 = new Rectangle(0, 0, 275, 125);
        rectangle1.setFill(Color.rgb(199, 206, 213));

        Text textName = new Text("Name");
        textName.setLayoutX(18);
        textName.setLayoutY(15);
        textName.setTextOrigin(VPos.TOP);
        textName.setFill(Color.web("#131021"));
        textName.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));

        final Text labelCombatantName = new Text((mythrasCombatant != null ? mythrasCombatant.name : ""));
        labelCombatantName.setTextOrigin(VPos.BOTTOM);
        labelCombatantName.setFill(Color.web("#131021"));
        labelCombatantName.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));

        final TextField textField = new TextField();
        textField.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        if (editDialogConfig == EditDialogConfig.ADD) {
            textField.requestFocus();
        }

        VBox vBox = new VBox();
        vBox.setLayoutX(130);
        vBox.setMinWidth(130);
        vBox.setPrefWidth(130);
        vBox.setAlignment(Pos.BOTTOM_RIGHT);
        if (editDialogConfig == EditDialogConfig.EDIT) {
            vBox.setLayoutY(15);
            vBox.getChildren().add(labelCombatantName);
        } else {
            vBox.setLayoutY(11);
            vBox.getChildren().add(textField);
        }

        Line line1 = new Line(8, 37, 275 - 8, 37);
        line1.setStroke(Color.color(0.66, 0.67, 0.69));

        Text textActionPoints = new Text("Action Points");
        textActionPoints.setLayoutX(18);
        textActionPoints.setLayoutY(46);
        textActionPoints.setTextOrigin(VPos.TOP);
        textActionPoints.setFill(Color.web("#131021"));
        textActionPoints.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));

        Spinner<Integer> spinnerActionPoints = new Spinner<>(0, 99,
                (editDialogConfig == EditDialogConfig.EDIT ? mythrasCombatant.actionPoints : 2), 1);
        spinnerActionPoints.setLayoutX(205);
        spinnerActionPoints.setLayoutY(39);
        spinnerActionPoints.setPrefWidth(60);
        spinnerActionPoints.setPrefHeight(24);

        Line line2 = new Line(8, 68, 275 - 8, 68);
        line2.setStroke(Color.color(0.66, 0.67, 0.69));

        Text textInitiative = new Text("Initiative");
        textInitiative.setLayoutX(18);
        textInitiative.setLayoutY(77);
        textInitiative.setTextOrigin(VPos.TOP);
        textInitiative.setFill(Color.web("#131021"));
        textInitiative.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));

        Spinner<Integer> spinnerInitiative = new Spinner<>(0, 99,
                (editDialogConfig == EditDialogConfig.EDIT ? mythrasCombatant.initiative : 10), 1);
        spinnerInitiative.setLayoutX(205);
        spinnerInitiative.setLayoutY(70);
        spinnerInitiative.setPrefWidth(60);
        spinnerInitiative.setPrefHeight(24);

        final Text validationError = new Text();
        validationError.setLayoutX(18);
        validationError.setLayoutY(120);
        validationError.setTextAlignment(TextAlignment.CENTER);
        validationError.setFill(Color.web("#FF1010"));
        validationError.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        Rectangle rectangle2 = new Rectangle(8, 8, 259, 100);
        rectangle2.setArcHeight(20);
        rectangle2.setArcWidth(20);
        rectangle2.setFill(Color.WHITE);
        rectangle2.setStroke(Color.color(0.66, 0.67, 0.69));

        final ButtonType okButtonType = new ButtonType("Ok",  ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(okButtonType);
        final ButtonType cancelButtonType = new ButtonType("Cancel",  ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(cancelButtonType);

        this.getDialogPane().lookupButton(okButtonType).addEventFilter(ActionEvent.ACTION, ae -> {
            if (editDialogConfig == EditDialogConfig.ADD && textField.getText().isEmpty()) {
                validationError.setText("Name cannot be empty");
                Timeline timeline  = new Timeline();
                timeline.setCycleCount(1);
                timeline.getKeyFrames().addAll(new KeyFrame(new Duration(3000)));
                timeline.play();
                timeline.setOnFinished( e -> validationError.setText(""));
                ae.consume(); //not valid
            } else if (editDialogConfig == EditDialogConfig.ADD && mythrasCombatModel.contains(textField.getText())) {
                validationError.setText("Name is already in combat list");
                Timeline timeline  = new Timeline();
                timeline.setCycleCount(1);
                timeline.getKeyFrames().addAll(new KeyFrame(new Duration(3000)));
                timeline.play();
                timeline.setOnFinished( e -> validationError.setText(""));
                ae.consume(); //not valid
            }
        });

        Group root = new Group(rectangle1, rectangle2, line1, textName, vBox, line2,
                textActionPoints, spinnerActionPoints, textInitiative, spinnerInitiative, validationError);

        this.getDialogPane().setContent(root);
        this.setTitle((editDialogConfig == EditDialogConfig.EDIT ? "Edit Combatant" : "Add Combatant"));
        this.getDialogPane().setPrefSize(275, 125);
        this.getDialogPane().setMaxSize(275, 125);
        this.setResizable(false);
        this.initModality(Modality.APPLICATION_MODAL);
        Optional<ButtonType> result = this.showAndWait();

        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            if (editDialogConfig == EditDialogConfig.ADD) {
                mythrasCombatant = new MythrasCombatant(textField.getText(), spinnerActionPoints.getValue(), spinnerInitiative.getValue());
            } else {
                mythrasCombatant.actionPoints = spinnerActionPoints.getValue();
                mythrasCombatant.initiative = spinnerInitiative.getValue();
            }
            return true;
        } else {
            return false;
        }
    }
}
