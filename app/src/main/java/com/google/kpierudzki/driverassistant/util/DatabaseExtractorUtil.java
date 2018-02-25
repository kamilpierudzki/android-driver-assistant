package com.google.kpierudzki.driverassistant.util;

import com.google.kpierudzki.driverassistant.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Kamil on 26.06.2017.
 */

public class DatabaseExtractorUtil {

    public static void extractDB(String srcDatabaseName, String dstDatabaseName) throws IOException {
        File dstFile = App.getAppContext().getExternalFilesDir(null);
        if (!dstFile.exists()) dstFile.mkdirs();

        dstFile = new File(dstFile, dstDatabaseName);
        if (!dstFile.exists()) dstFile.createNewFile();

        InputStream inputStream = new FileInputStream(App.getAppContext().getDatabasePath(srcDatabaseName));
        OutputStream outputStream = new FileOutputStream(dstFile);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
    }
}
