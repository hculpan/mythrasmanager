package org.culpan.mythrasmanager.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.culpan.mythrasmanager.model.MythrasCombatant;

import java.io.IOException;
import java.net.URL;

@SuppressWarnings("unused")
public class MonsterSelectDialogController {
    private String monsterName;

    private int monsterId;

    ObservableList<MythrasCombatant> monsters = FXCollections.observableArrayList();

    @FXML
    private Label labelMonsterName;

    @FXML
    private Spinner generateNumber;

    @FXML
    private Button buttonGenerate;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    @FXML
    private TableView<MythrasCombatant> tableMonsters;

    @FXML
    private TableColumn<MythrasCombatant, String> colName;

    @FXML
    private TableColumn<MythrasCombatant, String> colActionPoints;

    @FXML
    private TableColumn<MythrasCombatant, String> colInitiative;

    public MonsterSelectDialogController() {

    }

    public MonsterSelectDialogController(String monsterName, int monsterId) {
        this.monsterName = monsterName;
        this.monsterId = monsterId;
    }

    @FXML
    public void handleCancel() {
        monsters.clear();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleOk() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void generateMonsters() {
        try {
            URL website = new URL("http://skoll.xyz/mythras_eg/generate_enemies_json/?id=" + monsterId + "&amount=" + generateNumber.getValue().toString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(website);
            if (root.isArray()) {
                monsters.clear();
                for (JsonNode node : root) {
                    MythrasCombatant m = new MythrasCombatant();
                    m.name = node.path("name").asText();
                    m.actionPoints = node.path("attributes").path("action_points").asInt();
                    m.currentActionPoints = m.actionPoints;
                    m.setNpc(true);
                    JsonNode stats = node.path("stats");
                    for (JsonNode stat : stats) {
                        if (stat.get("INT") != null) {
                            m.intelligence = stat.get("INT").asInt();
                        } else if (stat.get("DEX") != null) {
                            m.dexterity = stat.get("DEX").asInt();
                        }
                    }
                    JsonNode hitLocations = node.path("hit_locations");
                    for (JsonNode location : hitLocations) {
                        MythrasCombatant.HitLocation hitLocation = new MythrasCombatant.HitLocation();
                        hitLocation.setArmorPoints(location.get("ap").asInt());
                        hitLocation.setRange(location.get("range").asText());
                        hitLocation.setHitPoints(location.get("hp").asInt());
                        hitLocation.setCurrentHitPoints(hitLocation.getHitPoints());
                        hitLocation.setName(location.get("name").asText());
                        m.hitLocations.add(hitLocation);
                    }
                    m.initiative = MythrasCombatant.calculateInitiative(m.intelligence, m.dexterity);
                    m.currentInitiative = m.initiative;
                    monsters.add(m);
                }
                tableMonsters.setItems(monsters);
            }
        } catch (IOException e) {

        }
    }

    @FXML
    public void handleGenerate() {
        generateMonsters();
        okButton.setDisable(false);
    }

    @FXML
    public void initialize() {
        labelMonsterName.setText(monsterName);

        generateNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99));

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colActionPoints.setCellValueFactory(new PropertyValueFactory<>("actionPoints"));
        colInitiative.setCellValueFactory(new PropertyValueFactory<>("initiative"));
    }
}
