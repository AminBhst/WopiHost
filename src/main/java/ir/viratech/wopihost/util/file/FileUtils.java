package ir.viratech.wopihost.util.file;

import ir.viratech.wopihost.config.ConfigHolder;
import ir.viratech.wopihost.util.file.exception.FileFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class FileUtils {

    public static Path getFilePath(String fileName) {
        if (fileName.toLowerCase().endsWith(".docx")) {
            return Paths.get(ConfigHolder.getConfig().getWordFileDirectoryPath()).resolve(fileName);
        } else if (fileName.toLowerCase().endsWith(".html")) {
            return Paths.get(ConfigHolder.getConfig().getHtmlDirectoryPath()).resolve(fileName);
        } else throw new FileFormatException(fileName);
    }

    public static File getFile(String fileName) {
        return Objects.requireNonNull(getFilePath(fileName)).toFile();
    }

    public static String generateHtmlFileName() {
        return UUID.randomUUID() + ".html";
    }

    public static String getPathString(String fileName) {
        return getFilePath(fileName).toString();
    }

    public static URL getFontUrl(String fontFile) throws MalformedURLException {
        return Paths.get(ConfigHolder.getConfig().getFontPath()).resolve(fontFile).toUri().toURL();
    }

    public static String readFile(String fileName) throws IOException {
        byte[] bytes = Files.readAllBytes(getFilePath(fileName));
        return new String(bytes, StandardCharsets.UTF_8);
    }

//    public static Long getFileSize(String fileName) {
//        Long fileSize = null;
//        try {
//            return Files.size(config(fileName));
//        } catch (IOException e) {
//            log.warn("Could not get file size for {}", fileName);
//        }
//        return fileSize;
//    }

    public static boolean deleteFile(String fileName) {
        return getFile(fileName).delete();
    }

    public static void writeFile(String fileName, byte[] bytes, String dirPath) throws IOException {
        log.info("Writing file {}", fileName);
        Path path = Paths.get(dirPath).resolve(fileName);
        Files.write(path, bytes);
    }


    public static FileType detectFileType(byte[] file) {
        Tika tika = new Tika();
        if (tika.detect(file).equals("application/x-tika-ooxml")) {
            return FileType.DOCX;
        } else if (tika.detect(file).equals(MediaType.TEXT_HTML_VALUE)) {
            return FileType.HTML;
        } else return FileType.UNKNOWN;
    }


    public static void createEmptyWordDocument(String fileName) throws IOException {
        if (!fileName.endsWith(".docx"))
            fileName += ".docx";

        XWPFDocument document = new XWPFDocument();
        FileOutputStream fos = new FileOutputStream(FileUtils.getPathString(fileName));
        log.info("Writing {}", fileName);
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun run = paragraph.createRun();
        run.setText("");
        document.write(fos);
        fos.close();
        document.close();
    }
}
