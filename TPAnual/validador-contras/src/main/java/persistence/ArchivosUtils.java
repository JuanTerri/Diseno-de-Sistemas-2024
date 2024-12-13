package persistence;

import io.javalin.http.UploadedFile;
import views.VistasJavalin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.UUID;

public class ArchivosUtils {
    private static ArchivosUtils instance;
    private String uploadsPath = "";

    private ArchivosUtils(){
        String rootPath = Paths.get("").toAbsolutePath().toString();
        uploadsPath = Paths.get(rootPath, "uploads").toString();
        System.out.println("Path absoluto de los archivos: " + uploadsPath);
    }

    public static ArchivosUtils getInstance(){
        if(instance == null){
            instance = new ArchivosUtils();
        }
        return instance;
    }

    public String guardarArchivo(String pathDentroDeResources, UploadedFile uploadedFile){
        String pathRelativo = "\\" + pathDentroDeResources + "\\" + UUID.randomUUID() + "_" + uploadedFile.filename();

        File uploadDir = new File(uploadsPath + "\\" + pathDentroDeResources);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File file = new File(uploadsPath + pathRelativo);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(uploadedFile.content().readAllBytes());
            System.out.println("Archivo " + pathRelativo);
            System.out.println("En " + uploadsPath + pathRelativo);
        } catch (IOException e) {
            System.out.println("no se pudo guardar el archivo");
        }
        return pathRelativo;
    }
}
