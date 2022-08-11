package ir.viratech.wopihost.controller;

import ir.viratech.wopihost.dto.CheckFileInfoDTO;
import ir.viratech.wopihost.util.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Services defined in these class should not be called directly and must be called automatically
 * by the running wopi client such as office online
 */
@RestController
@RequestMapping("/wopi")
@Slf4j
public class WopiController {


    @GetMapping("/files/{fileName}")
    public CheckFileInfoDTO wopi(@PathVariable("fileName") String fileName) {
        try {
            CheckFileInfoDTO cfi = new CheckFileInfoDTO();
            cfi.setBaseFileName(fileName);
            cfi.setVersion("1");
            cfi.setOwnerId("KATEB");
            cfi.setUserFriendlyName("KATEB");
            cfi.setSize(FileUtils.getFileSize(fileName));
            return cfi;
        } catch (Throwable t) {
            log.error("Error write", t);
        }
        log.error("File not created");
        return null;
    }



    @PostMapping("/files/{fileName}/contents")
    public void updateFileContent(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        FileInputStream fis = new FileInputStream(FileUtils.getPathString(fileName));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        response.reset();
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        URL url = new URL(FileUtils.getPathString(fileName));
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        response.addHeader("Content-Length", String.valueOf(uc.getContentLengthLong()));
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setStatus(200);
        toClient.write(buffer);
        toClient.flush();
    }

    @GetMapping("/files/{fileName}/contents")
    public byte[] getFileContent(@PathVariable("fileName") String fileName) {
        try {
            return IOUtils.toByteArray(new FileInputStream(FileUtils.getPathString(fileName)));
        } catch (Throwable t) {
            log.error("Error occurred while retrieving contents!", t);
            return null;
        }
    }
}