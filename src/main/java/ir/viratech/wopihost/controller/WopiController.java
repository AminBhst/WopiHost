package ir.viratech.wopihost.controller;

import ir.viratech.wopihost.config.ConfigProperties;
import ir.viratech.wopihost.dto.CheckFileInfoDTO;
import ir.viratech.wopihost.util.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Services defined in these class should not be called directly and must be called automatically
 * by the running wopi client such as office online
 */
@Slf4j
@RestController
@RequestMapping("/wopi")
public class WopiController {

    final ConfigProperties config;

    @Autowired
    public WopiController(ConfigProperties config) {
        this.config = config;
    }

    @GetMapping("/files/{fileName}")
    public CheckFileInfoDTO wopi(@PathVariable("fileName") String fileName) {
        try {
            CheckFileInfoDTO cfi = new CheckFileInfoDTO();
            cfi.setBaseFileName(fileName);
            cfi.setVersion("1");
            cfi.setOwnerId("KATEB");
            cfi.setUserFriendlyName("KATEB");
            cfi.setSize(getFileSize(fileName));
            return cfi;
        } catch (Throwable t) {
            log.error("Error write", t);
        }
        log.error("File not created");
        return null;
    }

    private long getFileSize(String fileName) throws IOException {
        return Files.size(Paths.get(config.getFileDirectoryPath()).resolve(fileName));
    }


    private String getFilePathString(String fileName) {
        return Paths.get(config.getFileDirectoryPath()).resolve(fileName).toString();
    }

    @PostMapping("/files/{fileName}/contents")
    public void updateFileContent(@PathVariable("fileName") String fileName, @RequestBody byte[] bytes) {
        File file = Paths.get(config.getFileDirectoryPath()).resolve(fileName).toFile();
        try (FileOutputStream fop = new FileOutputStream(file)) {
            fop.write(bytes);
            fop.flush();
        } catch (IOException e) {
            log.error("postFile failed, errMsg: {}", e.getMessage());
        }
    }


    @GetMapping("/files/{fileName}/contents")
    public byte[] getFileContent(@PathVariable("fileName") String fileName) {
        try {
            return IOUtils.toByteArray(new FileInputStream(getFilePathString(fileName)));
        } catch (Throwable t) {
            log.error("Error occurred while retrieving contents!", t);
            return null;
        }
    }
}