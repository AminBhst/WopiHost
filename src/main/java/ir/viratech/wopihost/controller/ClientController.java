package ir.viratech.wopihost.controller;

import ir.viratech.wopihost.config.ConfigProperties;
import ir.viratech.wopihost.dto.EntryPointDTO;
import ir.viratech.wopihost.dto.ToHtmlDTO;
import ir.viratech.wopihost.service.converter.DocxToHtmlConverter;
import ir.viratech.wopihost.service.converter.HtmlToDocxConverter;
import ir.viratech.wopihost.service.generator.WopiUrlGenerator;
import ir.viratech.wopihost.util.file.FileType;
import ir.viratech.wopihost.util.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Services to be used by the client such as applications that wish to communicate with office online
 */
@RestController
@RequestMapping("/wopi/client")
@Slf4j
public class ClientController {

    private final DocxToHtmlConverter toHtmlConverter;
    private final HtmlToDocxConverter toDocxConverter;
    private final ConfigProperties config;
    private final WopiUrlGenerator urlGenerator;

    @Autowired
    public ClientController(DocxToHtmlConverter toHtmlConverter,
                            HtmlToDocxConverter toDocxConverter,
                            ConfigProperties config,
                            @Qualifier("officeWordUrlGenerator") WopiUrlGenerator urlGenerator) {
        this.toHtmlConverter = toHtmlConverter;
        this.toDocxConverter = toDocxConverter;
        this.config = config;
        this.urlGenerator = urlGenerator;
    }


    /**
     * Generates the URL which is used as a starting point to start an instance of office online
     * to edit the given file.
     * <p>Just as the path suggests, this service is used as an entry point and should be called first in order
     * to start working with office online</p>
     */
    @PostMapping(value = "/entry-point")
    public EntryPointDTO generateWopiUrl(@RequestPart("file") MultipartFile file) throws IOException, Docx4JException {
        String uuid = UUID.randomUUID().toString();
        String s = new String(file.getBytes(), StandardCharsets.UTF_8);
        FileType fileType = FileUtils.detectFileType(file.getBytes());
        if (true) {
            ByteArrayOutputStream docx = toDocxConverter.convert(file.getBytes());
            FileUtils.writeFile(uuid + ".docx", docx.toByteArray(), config.getHtmlDirectoryPath());
        } else if (fileType == FileType.DOCX) {
            FileUtils.writeFile(uuid, file.getBytes(), config.getWordFileDirectoryPath());
        } else if (fileType == FileType.UNKNOWN) {
            FileUtils.createEmptyWordDocument(uuid);
        }

        return new EntryPointDTO(urlGenerator.generate(uuid + ".docx"));
    }

    @GetMapping(value = "/theFile")
    public byte[] generateWopiUrl() throws IOException, Docx4JException {
        return Files.readAllBytes(Paths.get("/home/amin/Documents/171bb3a9-c5e2-486e-ad6a-084b29416766.docx"));
    }


    /**
     * converts the given docx file to html
     * @param fileName docx fileName
     * @return generated html
     */
    @GetMapping("/files/{fileName}/html")
    public ToHtmlDTO convertDocxToHtml(@PathVariable("fileName") String fileName) {
        ToHtmlDTO html = null;
        try {
            html = new ToHtmlDTO(toHtmlConverter.convert(fileName));
            boolean delete = FileUtils.deleteFile(fileName);
            log.info("Deletion for file {} was {}", fileName, (delete ? "successful" : "unsuccessful"));
        } catch (Throwable t) {
            log.error("Error occurred while converting content to html", t);
        }
        return html;
    }

}
