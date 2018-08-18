package org.culpan.mythrasmanager;

import org.dizitart.no2.Nitrite;

import java.io.File;

public class UserConfiguration {
    private File dataDir = new File(System.getProperty("user.home"), ".mythrasmanager");

    protected static UserConfiguration instance;

    protected String lastSavedPath;

    protected boolean showInitiative = false;

    public static UserConfiguration getInstance() {
        if (instance == null) {
            instance = new UserConfiguration();
        }
        return instance;
    }

    private UserConfiguration() {
    }

    public File getDataDir() {
        return dataDir;
    }

    public String getLastSavedPath() {
        return lastSavedPath;
    }

    public void setLastSavedPath(String lastSavedPath) {
        this.lastSavedPath = lastSavedPath;
    }

    public boolean isShowInitiative() {
        return showInitiative;
    }

    public void setShowInitiative(boolean showInitiative) {
        this.showInitiative = showInitiative;
    }
}
