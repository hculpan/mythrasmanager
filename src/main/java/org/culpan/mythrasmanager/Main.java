package org.culpan.mythrasmanager;

import javafx.application.Application;
import javafx.stage.Stage;
import org.culpan.mythrasmanager.dialogs.MainDialog;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        MainDialog mainDialog = new MainDialog();
        mainDialog.show(primaryStage);
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            UserConfiguration.getInstance().setExternalCreatureTemplatePath(args[0]);
        }
        launch(args);
    }
}
