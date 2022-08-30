package ir.viratech.wopihost.service;

import ir.viratech.wopihost.entity.FileInfo;
import ir.viratech.wopihost.entity.WopiRequestHeader;
import ir.viratech.wopihost.entity.WopiStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class WopiHostService {

    @Autowired
    private WopiLockService lockService;

    @Value("${wopi.fileDirectoryPath}")
    private String filePath;


    /**
     * Retrieves a file from a host.
     *
     * @param base64Json a file ID of a file managed by host
     * @param response
     */
    public void getFile(String base64Json, HttpServletResponse response) {
        JSONObject json = decodeBase64ToJson(base64Json);
        Path path = Paths.get(filePath).resolve(json.get("tempFileName").toString());
        File file = path.toFile();
        String filename = file.getName();
        try (InputStream fis = new BufferedInputStream(new FileInputStream(path.toString()));
             OutputStream toClient = new BufferedOutputStream(response.getOutputStream())) {
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            response.addHeader("Content-Length", String.valueOf(file.length()));

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            toClient.write(buffer);
            toClient.flush();
        } catch (IOException e) {
            response.setStatus(WopiStatus.NOT_FOUND.value());
            log.error("getFile failed, errMsg: {}", e.getMessage(), e);
        }
    }

    /**
     * Updates a file’s binary contents.
     *
     * @param base64Json    a file ID of a file managed by host
     * @param content the full binary contents of the file
     * @param request
     * @return
     */
    public ResponseEntity postFile(String base64Json, byte[] content, HttpServletRequest request) {
        JSONObject json = decodeBase64ToJson(base64Json);
        String tempFileName = json.get("tempFileName").toString();

        ResponseEntity response;
        File file = Paths.get(filePath).resolve(tempFileName).toFile();
        String requestLock = request.getHeader(WopiRequestHeader.LOCK);
        if (file.exists()) {
            response = lockService.putFile(request, file, content);
            if (response.getStatusCodeValue() != WopiStatus.OK.value()) {
                log.warn("update {} failed, status: {}", tempFileName, response);
            }
        } else {
            response = ResponseEntity.status(WopiStatus.NOT_FOUND.value()).build();
            log.error("postFile failed, file not found");
        }
        log.info("postFile -- filename: {}, response: {} , requestLock: {}", tempFileName, response, requestLock);
        return response;
    }

    /**
     * Returns information about a file, a user’s permissions on that file,
     * and general information about the capabilities that the WOPI host has on the file.
     *
     * @param base64Json
     */
    public ResponseEntity<FileInfo> getFileInfo(String base64Json) throws Exception {
        JSONObject json = decodeBase64ToJson(base64Json);
        String tempFileName = json.get("tempFileName").toString();
        String originalFileName = json.get("originalFileName").toString();
        String fileOwner = json.get("fileOwner").toString();
        String username = json.get("username").toString();

        FileInfo info = new FileInfo();
        if (tempFileName != null && tempFileName.length() > 0) {
            File file = Paths.get(filePath).resolve(tempFileName).toFile();
            if (file.exists()) {
                info.setBaseFileName(originalFileName);
                info.setSize(file.length());
                info.setUserId(username);
                info.setOwnerId(fileOwner);
                info.setVersion(file.lastModified());
                info.setSha256(getHash256(file));
            } else {
                return ResponseEntity.status(WopiStatus.NOT_FOUND.value()).build();
            }
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(info);
    }

    public ResponseEntity handleLock(String base64Json, HttpServletRequest request) {
        JSONObject json = decodeBase64ToJson(base64Json);
        String fileName = json.get("tempFileName").toString();

        ResponseEntity response;
        String wopiOverride = request.getHeader(WopiRequestHeader.OVERRIDE);
        String requestLock = request.getHeader(WopiRequestHeader.LOCK);
        String oldLock = request.getHeader(WopiRequestHeader.OLD_LOCK);
        switch (wopiOverride) {
            case "LOCK":
                if (oldLock != null) {
                    wopiOverride = "UNLOCK_AND_RELOCK";
                    response = lockService.unlockAndRelock(request, fileName);
                } else {
                    response = lockService.lock(request, fileName);
                }
                break;
            case "GET_LOCK":
                response = lockService.getLock(request, fileName);
                break;
            case "REFRESH_LOCK":
                response = lockService.refreshLock(request, fileName);
                break;
            case "UNLOCK":
                response = lockService.unlock(request, fileName);
                break;
            default:
                response = ResponseEntity.status(WopiStatus.NOT_IMPLEMENTED.value()).build();
                break;
        }
        log.info("handleLock -- filename: {}, override: {}, response: {}, requestLock: {}, oldLock: {}",
                fileName, wopiOverride, response, requestLock, oldLock);
        return response;
    }

    /**
     * Get SHA-256 value of file
     *
     * @param file
     * @return
     */
    private String getHash256(File file) throws IOException, NoSuchAlgorithmException {
        String value = "";
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int numRead;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    digest.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            value = new String(Base64.encodeBase64(digest.digest()));
        }
        return value;
    }


    private JSONObject decodeBase64ToJson(String base64) {
        byte[] jsonBytes = java.util.Base64.getDecoder().decode(base64);
        String jsonString = new String(jsonBytes, StandardCharsets.UTF_8);
        return new JSONObject(jsonString);
    }

    public ResponseEntity<InputStreamResource> downloadFile(String base64Json) throws FileNotFoundException {
        JSONObject json = decodeBase64ToJson(base64Json);
        String tempFileName = json.get("tempFileName").toString();
        String originalFileName = json.get("originalFileName").toString();
        Path path = Paths.get(filePath).resolve(tempFileName);
        final File file = path.toFile();
        final long resourceLength = file.length();
        final long lastModified = file.lastModified();
        final InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + originalFileName)
                .contentLength(resourceLength)
                .lastModified(lastModified)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
