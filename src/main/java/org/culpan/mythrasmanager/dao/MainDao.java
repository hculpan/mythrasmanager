package org.culpan.mythrasmanager.dao;

import org.culpan.mythrasmanager.UserConfiguration;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;

import java.io.File;
import java.io.IOException;

public class MainDao {
    private static Nitrite db = null;

    public Nitrite initializeDb() {
        if (db != null) return db;

        try {
            File dbFile = new File(UserConfiguration.getInstance().getDataDir(), "mythrasmanager.db");
            return Nitrite.builder()
                    .compressed()
                    .filePath(dbFile.getCanonicalPath())
                    .openOrCreate("user", "password");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public NitriteCollection getCollection(Nitrite db, String name) {
        initializeDb();
        return db.getCollection(name);
    }
}
