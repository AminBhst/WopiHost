package ir.viratech.wopihost.controller;

import ir.viratech.wopihost.config.ConfigHolder;
import ir.viratech.wopihost.entity.WopiFile;
import ir.viratech.wopihost.exception.InvalidFileTypeException;
import ir.viratech.wopihost.repository.WopiFileRepository;
import ir.viratech.wopihost.service.generator.WopiUrlGenerator;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class WopiClientBaseController {

    protected final WopiUrlGenerator urlGenerator;
    protected final String[] validFileTypes;
    protected final WopiFileRepository wopiFileRepository;

    protected WopiClientBaseController(WopiUrlGenerator urlGenerator,
                                       String[] validFileTypes,
                                       WopiFileRepository wopiFileRepository) {
        java.util.UUID.randomUUID().toString();
        this.urlGenerator = urlGenerator;
        this.validFileTypes = validFileTypes;
        this.wopiFileRepository = wopiFileRepository;
    }

    /**
     * Service which generates the URL which is used as a starting point to start an instance of office online
     * to edit the given file.
     * <p>Just as the path suggests, this service is used as an entry point and should be called first in order
     * to start working with office online</p>
     */
    @PostMapping(value = "/entry-point")
    public String generateWopiUrl(@RequestPart("file") MultipartFile file,
                                  @RequestParam String username,
                                  @RequestParam String clientName,
                                  @RequestParam String identifier) throws IOException, InvalidFileTypeException {
        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        validateFileType(originalFilename);
        String fileName = uuid + originalFilename.substring(originalFilename.lastIndexOf("."));
        writeFile(file, fileName);

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("originalFileName", originalFilename);
        params.put("clientName", clientName);

        WopiFile existingWopiFile = wopiFileRepository.getFirstByClientDefinedIdentifierAndClientName(identifier, clientName);
        if (existingWopiFile == null) {
            WopiFile wopiFile = new WopiFile();
            wopiFile.setOwner(username);
            wopiFile.setTempFileName(fileName);
            wopiFile.setOriginalFileName(originalFilename);
            wopiFile.setClientName(clientName);
            wopiFile.setClientDefinedIdentifier(identifier);
            wopiFileRepository.save(wopiFile);
            params.put("tempFileName", fileName);
        } else {
            params.put("tempFileName", existingWopiFile.getTempFileName());
        }


        String base64Json = encodeBase64Json(params);
        return urlGenerator.generate(base64Json);
    }

    private String encodeBase64Json(Map<String, String> params) {
        JSONObject json = new JSONObject();
        params.forEach(json::append);
        return Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeFile(MultipartFile file, String fileName) throws IOException {
        String path = ConfigHolder.getConfig().getFileDirectoryPath();
        Files.copy(file.getInputStream(), Paths.get(path).resolve(fileName));
    }

    private void validateFileType(String fileName) throws InvalidFileTypeException {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        if (!Arrays.asList(validFileTypes).contains(extension))
            throw new InvalidFileTypeException(fileName);
    }
}
