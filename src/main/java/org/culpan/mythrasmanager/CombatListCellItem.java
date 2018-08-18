package org.culpan.mythrasmanager;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.culpan.mythrasmanager.model.MythrasCombatModel;
import org.culpan.mythrasmanager.model.MythrasCombatant;

import java.util.Optional;

public class CombatListCellItem extends ListCell {

    protected MythrasCombatModel mythrasCombatModel;

    public CombatListCellItem(MythrasCombatModel mythrasCombatModel) {
        this.mythrasCombatModel = mythrasCombatModel;
    }

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            final MythrasCombatant m = (MythrasCombatant)item;

            String combatantText = m.name;
            if (UserConfiguration.getInstance().isShowInitiative()) {
                combatantText += " [" + m.initiative + "]";
            }
            Text text = new Text(combatantText);
            if (m.currentActionPoints > 0) {
                text.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                if (isSelected()) {
                    text.setFill(Color.WHITE);
                } else {
                    text.setFill(Color.BLACK);
                }
            } else {
                text.setFont(Font.font("Arial", FontPosture.ITALIC, 14));
                if (isSelected()) {
                    text.setFill(Color.LIGHTGRAY);
                } else {
                    text.setFill(Color.GRAY);
                }
            }

            Canvas actingCanvas = new Canvas();
            actingCanvas.setHeight(30);
            actingCanvas.setWidth(10);
            if (m.isActing()) {
                GraphicsContext gc = actingCanvas.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.fillOval(0, 10, 8, 8);
            }

            Button actButton = new Button("A");
            actButton.setDisable(!(m.currentActionPoints > 0));
            actionTaken(m, actButton, this);
            actButton.setPrefWidth(40);

            Button reactButton = new Button("R");
            reactButton.setDisable(!(m.currentActionPoints > 0));
            reactButton.setOnAction(reactionTaken(m));
            reactButton.setPrefWidth(40);

            Button holdButton = new Button("H");
            holdButton.setDisable(!(m.currentActionPoints > 0));
            holdButton.setOnAction(e -> {
                if (m.isActing()) {
                    mythrasCombatModel.nextCombatantToAct();
                }
                this.getListView().refresh();
            });
            holdButton.setPrefWidth(40);

            Canvas canvas = new Canvas();
            canvas.setHeight(30);
            canvas.setWidth(40);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            if (m.hasActionPoints()) {
                gc.setFill(Color.LIGHTGREEN);
                gc.setStroke(Color.BLACK);
            } else {
                gc.setFill(Color.LIGHTGRAY);
                gc.setStroke(Color.DARKGRAY);
            }
            gc.fillOval(1, 0, 27, 27);
            gc.strokeText(Integer.toString(m.currentActionPoints), 11, 18);

            GridPane group = new GridPane();
            group.add(actingCanvas, 0, 0);
            group.add(canvas, 1, 0);
            group.add(text, 2, 0);
            group.add(actButton, 3, 0);
            group.add(reactButton, 4, 0);
            group.add(holdButton, 5, 0);
            group.getColumnConstraints().add(new ColumnConstraints(10));
            group.getColumnConstraints().add(new ColumnConstraints(30));
            group.getColumnConstraints().add(new ColumnConstraints(145));
            group.getColumnConstraints().add(new ColumnConstraints(25));
            group.getColumnConstraints().add(new ColumnConstraints(25));
            group.getColumnConstraints().add(new ColumnConstraints(25));
            setGraphic(group);
        }
    }

    private EventHandler<ActionEvent> reactionTaken(MythrasCombatant m) {
        return e -> {
            if (mythrasCombatModel.hasCombatStarted() && m.hasActionPoints()) {
                m.currentActionPoints--;
                this.getListView().refresh();
                if (m.currentActionPoints == 0 && m.isActing() && mythrasCombatModel.nextCombatantToAct() == null && autoMoveToNextRound()) {
                    mythrasCombatModel.nextRound();
                } else if (mythrasCombatModel.findFirstThatCanAct() == null && autoMoveToNextRound()) {
                    mythrasCombatModel.nextRound();
                }
            }
            this.getListView().refresh();
        };
    }

    private boolean autoMoveToNextRound() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Next Round");
        alert.setContentText("All combatants have used their Action Points.\nAdvance to next round?");

        ButtonType buttonTypeOne = new ButtonType("Next Round");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            return true;
        }

        return false;
    }

    private void actionTaken(MythrasCombatant m, Button reactButton, CombatListCellItem cell) {
        reactButton.setOnAction( e -> {
            if (!mythrasCombatModel.hasCombatStarted()) return;

            if (m.currentActionPoints > 0) m.currentActionPoints--;
            if (m.isActing()) {
                cell.getListView().refresh();
                if (mythrasCombatModel.nextCombatantToAct() == null && autoMoveToNextRound()) {
                    mythrasCombatModel.nextRound();
                }
            }
            cell.getListView().refresh();
        });
    }
}
