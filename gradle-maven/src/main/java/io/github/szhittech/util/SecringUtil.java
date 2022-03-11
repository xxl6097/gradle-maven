package io.github.szhittech.util;

import org.gradle.api.Project;
import org.gradle.api.logging.Logging;
import org.gradle.internal.extensibility.DefaultExtraPropertiesExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SecringUtil {

    public static DefaultExtraPropertiesExtension loadDefaultSecring(Project project){
        Object obj = project.getProperties().get("ext");
        if (obj == null)
            return null;
        if (obj instanceof DefaultExtraPropertiesExtension){
            DefaultExtraPropertiesExtension etx = (DefaultExtraPropertiesExtension) obj;
            if (!etx.has("signing.keyId")){
                //Logging.getLogger(SecringUtil.class).error("loadDefaultSecring");
                etx.setProperty("signing.keyId","FB58CB54");
                etx.setProperty("signing.password","2475431305");
                InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("secring.gpg");
                File gradleUserDir = project.getGradle().getGradleUserHomeDir();
                String choMengDir = gradleUserDir.getAbsolutePath() + File.separator + "chomeng";
                File chFile = new File(choMengDir);
                if (!chFile.exists()){
                    chFile.mkdirs();
                }
                String secring = choMengDir + File.separator + "secring.gpg";
                try {
                    File sfile = new File(secring);
                    sfile.deleteOnExit();
                    writeToLocal(secring,inputStream);
                    etx.setProperty("signing.secretKeyRingFile",secring);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return etx;
        }
        return null;
    }

    public static void writeToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        input.close();
        downloadFile.close();

    }
}
