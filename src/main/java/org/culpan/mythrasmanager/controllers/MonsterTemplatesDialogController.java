package org.culpan.mythrasmanager.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.culpan.mythrasmanager.UserConfiguration;
import org.culpan.mythrasmanager.model.MythrasCombatant;
import org.culpan.mythrasmanager.model.TemplateEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class MonsterTemplatesDialogController {
    public final static String TEMPLATES_FILE = "index_json.json";

    @FXML
    private Button closeButton;

    @FXML
    private ListView<TemplateEntry> templateListView;

    @FXML
    private TextField filterInput;

    @FXML
    private CheckBox checkCaseSensitivity;

    @FXML
    private CheckBox checkWholeWord;

    @FXML
    private CheckBox checkStartOfName;

    @FXML
    private CheckBox checkRabble;

    @FXML
    private CheckBox checkNovice;

    @FXML
    private CheckBox checkSkilled;

    @FXML
    private CheckBox checkVeteran;

    @FXML
    private CheckBox checkMaster;

    protected ObservableList<TemplateEntry> templateEntries;

    protected FilteredList<TemplateEntry> filteredData;

    protected ListProperty<TemplateEntry> listProperty = new SimpleListProperty<>();

    public List<MythrasCombatant> monsters = new ArrayList<>();

    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        templateListView.itemsProperty().bind(listProperty);

        templateEntries = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(templateEntries, s -> true);

        filterInput.textProperty().addListener(obs-> filterListener());
        checkStartOfName.selectedProperty().addListener( observable -> filterListener());
        checkWholeWord.selectedProperty().addListener( observable -> filterListener());
        checkCaseSensitivity.selectedProperty().addListener( observable -> filterListener());
        checkRabble.selectedProperty().addListener( observable -> filterListener());
        checkNovice.selectedProperty().addListener( observable -> filterListener());
        checkSkilled.selectedProperty().addListener( observable -> filterListener());
        checkVeteran.selectedProperty().addListener( observable -> filterListener());
        checkMaster.selectedProperty().addListener( observable -> filterListener());

        listProperty.set(filteredData);

        loadTemplateList();
    }

    protected void filterListener() {
        String filter = filterInput.getText();
        if(filter == null || filter.length() == 0) {
            filteredData.setPredicate(s -> true);
        } else {
            EnumSet<TemplateEntry.PatternFlags> flags = EnumSet.noneOf(TemplateEntry.PatternFlags.class);
            if (checkCaseSensitivity.isSelected()) flags.add(TemplateEntry.PatternFlags.CASE_SENSITIVE);
            if (checkWholeWord.isSelected()) flags.add(TemplateEntry.PatternFlags.WHOLE_WORD);
            if (checkStartOfName.isSelected()) flags.add(TemplateEntry.PatternFlags.START_WORD);
            if (checkRabble.isSelected()) flags.add(TemplateEntry.PatternFlags.RABBLE);
            if (checkNovice.isSelected()) flags.add(TemplateEntry.PatternFlags.NOVICE);
            if (checkSkilled.isSelected()) flags.add(TemplateEntry.PatternFlags.SKILLED);
            if (checkVeteran.isSelected()) flags.add(TemplateEntry.PatternFlags.VETERAN);
            if (checkMaster.isSelected()) flags.add(TemplateEntry.PatternFlags.MASTER);

            if (TemplateEntry.setPattern(filter, flags)) {
                filteredData.setPredicate(s -> s.matches(filter));
            } else {
                filteredData.setPredicate(s -> false);
            }
        }
    }

    protected void loadTemplateList() {
        if (!new File(UserConfiguration.getInstance().getDataDir(), TEMPLATES_FILE).exists()) return;

        URL url;
        templateEntries.clear();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            url = new File(UserConfiguration.getInstance().getDataDir(), TEMPLATES_FILE).toURI().toURL();
            try {
                JsonNode node = objectMapper.readTree(url);
                if (node.isArray()) {
                    for (int i = 0; i < node.size(); i++) {
                        JsonNode itemNode = node.get(i);
                        Iterator<String> fieldNames = itemNode.fieldNames();
                        TemplateEntry templateEntry = new TemplateEntry();
                        while (fieldNames.hasNext()) {
                            String fieldName = fieldNames.next();
                            if (fieldName.equals("tags")) {

                            } else if (fieldName.equals("name")) {
                                templateEntry.setName(itemNode.findValue(fieldName).asText());
                            } else if (fieldName.equals("rank")) {
                                templateEntry.setRank(itemNode.findValue(fieldName).asInt());
                            } else if (fieldName.equals("race")) {
                                templateEntry.setRace(itemNode.findValue(fieldName).asText());
                            } else if (fieldName.equals("owner")) {
                                templateEntry.setOwner(itemNode.findValue(fieldName).asText());
                            } else if (fieldName.equals("id")) {
                                templateEntry.setId(itemNode.findValue(fieldName).asLong());
                            }
                        }
                        templateEntries.add(templateEntry);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleFetchList(ActionEvent actionEvent) {
        try {
            URL website = new URL("http://skoll.xyz/mythras_eg/index_json/");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = null;
            fos = new FileOutputStream(new File(UserConfiguration.getInstance().getDataDir(), TEMPLATES_FILE));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            loadTemplateList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleGenerate(ActionEvent actionEvent) {
        TemplateEntry templateEntry;
        if ((templateEntry = templateListView.getSelectionModel().getSelectedItem()) == null) return;

        try {
            final FXMLLoader loader = new FXMLLoader(MonsterTemplatesDialogController.class.getResource("/monster_select.fxml"));
            loader.setControllerFactory( f ->
                new MonsterSelectDialogController(templateEntry.getName(), templateEntry.getId().intValue())
            );
            final Parent root = loader.load();
            final Scene scene = new Scene(root); //, 368, 368);
            final Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Generate Monsters");
            stage.setScene(scene);
            stage.showAndWait();
            MonsterSelectDialogController controller = loader.getController();
            if (controller.monsters.size() > 0) {
                monsters.addAll(controller.monsters);
                handleClose(actionEvent);
            } else {
                monsters.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleClose(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
