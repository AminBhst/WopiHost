package ir.viratech.wopihost.controller;

import ir.viratech.wopihost.entity.FileInfo;
import ir.viratech.wopihost.service.WopiHostService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Services defined in these class should not be called directly and must be called automatically
 * by the running wopi client such as office online
 */
@Slf4j
@RestController
@RequestMapping("/wopi")
public class WopiController {

    private final WopiHostService wopiHostService;

    @Autowired
    public WopiController(WopiHostService wopiHostService) {
        this.wopiHostService = wopiHostService;
    }


    /**
     * search a file from the host, return a file’s binary contents
     */
    @GetMapping("/files/{base64Json}/contents")
    public void getFile(@PathVariable String base64Json, HttpServletResponse response) {
        wopiHostService.getFile(base64Json, response);
    }

    /**
     * The postFile operation updates a file’s binary contents.
     */
    @PostMapping("/files/{base64Json}/contents")
    public void postFile(@PathVariable(name = "base64Json") String base64Json, @RequestBody byte[] content,
                         HttpServletRequest request) {
        wopiHostService.postFile(base64Json, content, request);
    }

    /**
     * returns information about a file, a user’s permissions on that file,
     * and general information about the capabilities that the WOPI host has on the file.
     */
    @GetMapping("/files/{base64Json}")
    public ResponseEntity<FileInfo> checkFileInfo(@PathVariable(name = "base64Json") String base64Json) throws Exception {
        return wopiHostService.getFileInfo(base64Json);
    }

    /**
     * Handling lock related operations
     */
    @PostMapping("/files/{base64Json}")
    public ResponseEntity handleLock(@PathVariable(name = "base64Json") String base64Json, HttpServletRequest request) {
        return wopiHostService.handleLock(base64Json, request);
    }


    @GetMapping("/files/{base64Json}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("base64Json") String base64Json) throws FileNotFoundException, FileNotFoundException {
        return wopiHostService.downloadFile(base64Json);
    }

}