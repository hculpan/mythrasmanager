package org.culpan.mythrasmanager.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.culpan.mythrasmanager.UserConfiguration;
import org.culpan.mythrasmanager.model.MythrasCombatant;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CreatureViewController {
    private MythrasCombatant mythrasCombatant;

    @FXML
    private Button closeButton;

    @FXML
    private WebView webView;

    public CreatureViewController(MythrasCombatant mythrasCombatant) {
        this.mythrasCombatant = mythrasCombatant;
    }

    @FXML
    public void initialize() {
        handleRefresh(null);
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleClose(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleRefresh(ActionEvent actionEvent) {
        try {
            StringBuilder contentBuilder = new StringBuilder();

            String filePath = mythrasCombatant.getRawJsonFile();
            try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) {
                stream.forEach(s -> contentBuilder.append(s).append("\n"));
            }
            String json = contentBuilder.toString();

            Configuration cfg = new Configuration(new Version("2.3.28"));
            cfg.setClassForTemplateLoading(CreatureViewController.class, "/");
            cfg.setDefaultEncoding("UTF-8");

            if (UserConfiguration.getInstance().getExternalCreatureTemplatePath() != null) {
                cfg.setDirectoryForTemplateLoading(new File(UserConfiguration.getInstance().getExternalCreatureTemplatePath()));
            }
            Template template = cfg.getTemplate("creatureview.ftl");

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> templateData = objectMapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});

            try (StringWriter out = new StringWriter()) {
                template.process(templateData, out);
                webView.getEngine().loadContent(out.getBuffer().toString());

                out.flush();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
