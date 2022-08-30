//package ir.viratech.wopihost.controller;
//
//import ir.viratech.wopihost.config.ConfigProperties;
//import ir.viratech.wopihost.dto.CheckFileInfoDTO;
//import ir.viratech.wopihost.entity.WopiFile;
//import ir.viratech.wopihost.repository.WopiFileRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Base64;
//
///**
// * Services defined in these class should not be called directly and must be called automatically
// * by the running wopi client such as office online
// */
//@Slf4j
//@RestController
//@RequestMapping("/wopi")
//public class WopiController {
//
//    final ConfigProperties config;
//    final WopiFileRepository wopiFileRepository;
//
//    @Autowired
//    public WopiController(ConfigProperties config, WopiFileRepository wopiFileRepository) {
//        this.config = config;
//        this.wopiFileRepository = wopiFileRepository;
//    }
//
//    @GetMapping("/files/{base64Json}")
//    public CheckFileInfoDTO wopi(@PathVariable("base64Json") String base64Json) {
//        byte[] jsonBytes = Base64.getDecoder().decode(base64Json);
//        JSONObject json = new JSONObject(new String(jsonBytes, StandardCharsets.UTF_8));
//        String username = json.get("username").toString();
//        String originalFileName = json.get("originalFileName").toString();
//        String tempFileName = json.get("tempFileName").toString();
//
//        CheckFileInfoDTO cfi = new CheckFileInfoDTO();
//        try {
//            cfi.setBaseFileName(originalFileName);
//            cfi.setVersion("1");
//            cfi.setOwnerId(username);
//            cfi.setUserFriendlyName(username);
//            cfi.setSize(getFileSize(tempFileName));
//        } catch (Throwable t) {
//            log.error("File not created");
//        }
//        return cfi;
//    }
//
//    @PostMapping("/files/{base64Json}/contents")
//    public void updateFileContent(@PathVariable("base64Json") String base64Json, @RequestBody byte[] bytes) {
//        JSONObject json = decodeBase64ToJson(base64Json);
//        String tempFileName = json.get("tempFileName").toString();
//        File file = Paths.get(config.getFileDirectoryPath()).resolve(tempFileName).toFile();
//        try (FileOutputStream fop = new FileOutputStream(file)) {
//            fop.write(bytes);
//            fop.flush();
//        } catch (IOException e) {
//            log.error("postFile failed, errMsg: {}", e.getMessage());
//        }
//    }
//
//    private JSONObject decodeBase64ToJson(String base64) {
//        byte[] json = Base64.getDecoder().decode(base64);
//        return new JSONObject(new String(json, StandardCharsets.UTF_8));
//    }
//
//    @GetMapping("/files/{base64Json}/contents")
//    public byte[] getFileContent(@PathVariable("base64Json") String base64Json) {
//        try {
//            JSONObject json = decodeBase64ToJson(base64Json);
//            String tempFileName = json.get("tempFileName").toString();
//            return IOUtils.toByteArray(new FileInputStream(getFilePathString(tempFileName)));
//        } catch (Throwable t) {
//            log.error("Error occurred while retrieving contents!", t);
//            return null;
//        }
//    }
//
//    @GetMapping("/files/download/{base64Json}")
//    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("base64Json") String base64Json) throws FileNotFoundException {
//        JSONObject json = decodeBase64ToJson(base64Json);
//        String tempFileName = json.get("tempFileName").toString();
//        String originalFileName = json.get("originalFileName").toString();
//        final File iFile = new File(getFilePathString(tempFileName));
//        final long resourceLength = iFile.length();
//        final long lastModified = iFile.lastModified();
//        final InputStreamResource resource = new InputStreamResource(new FileInputStream(iFile));
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=" + originalFileName)
//                .contentLength(resourceLength)
//                .lastModified(lastModified)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
//
//
//    private long getFileSize(String fileName) throws IOException {
//        return Files.size(Paths.get(config.getFileDirectoryPath()).resolve(fileName));
//    }
//
//
//    private String getFilePathString(String fileName) {
//        return Paths.get(config.getFileDirectoryPath()).resolve(fileName).toString();
//    }
//}