package ir.viratech.wopihost.controller.wopihost.service;

import ir.viratech.wopihost.controller.wopihost.controller.WopiHostController;
import ir.viratech.wopihost.controller.wopihost.entity.FileInfo;
import ir.viratech.wopihost.controller.wopihost.entity.WopiRequestHeader;
import ir.viratech.wopihost.controller.wopihost.entity.WopiStatus;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Handle wopi protocol requests
 *
 * @author ethendev
 * @date 2019/10/27
 */
@Service
public class WopiHostService {

    @Autowired
    private WopiLockService lockService;

    @Value("${wopi.fileDirectoryPath}")
    private String filePath;

    private static final String CHARSET_UTF8 = "UTF-8";

    private Logger logger = LoggerFactory.getLogger(WopiHostController.class);

    /**
     * Retrieves a file from a host.
     * @param name a file ID of a file managed by host
     * @param response
     */
    public void getFile(String name, HttpServletResponse response) {
        String path = filePath + name;
        File file = new File(path);
        String filename = file.getName();
        try (InputStream fis = new BufferedInputStream(new FileInputStream(path));
             OutputStream toClient = new BufferedOutputStream(response.getOutputStream())) {
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    new String(filename.getBytes(CHARSET_UTF8), "ISO-8859-1"));
            response.addHeader("Content-Length", String.valueOf(file.length()));

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            toClient.write(buffer);
            toClient.flush();
        } catch (IOException e) {
            response.setStatus(WopiStatus.NOT_FOUND.value());
            logger.error("getFile failed, errMsg: {}", e);
        }
    }

    /**
     * Updates a file’s binary contents.
     * @param name a file ID of a file managed by host
     * @param content the full binary contents of the file
     * @param request
     * @return
     */
    public ResponseEntity postFile(String name, byte[] content, HttpServletRequest request) {
        ResponseEntity response;
        String requestLock = request.getHeader(WopiRequestHeader.LOCK);
        File file = new File(filePath + name);
        if (file.exists()) {
            response = lockService.putFile(request, file, content);
            if (response.getStatusCodeValue() != WopiStatus.OK.value()) {
                logger.warn("update {} failed, status: {}", name, response);
            }
        } else {
            response = ResponseEntity.status(WopiStatus.NOT_FOUND.value()).build();
            logger.error("postFile failed, file not found");
        }
        logger.info("postFile -- filename: {}, response: {} , requestLock: {}", name, response, requestLock);
        return response;
    }

    /**
     * Returns information about a file, a user’s permissions on that file,
     * and general information about the capabilities that the WOPI host has on the file.
     *
     * @param fileName a file ID of a file managed by host
     * @return
     * @throws Exception
     */
    public ResponseEntity<FileInfo> getFileInfo(String fileName) throws Exception {
        FileInfo info = new FileInfo();
        if (fileName != null && fileName.length() > 0) {
            File file = new File(filePath + fileName);
            if (file.exists()) {
                info.setUserFriendlyName("A RANDOM USER");
                info.setBaseFileName(file.getName());
                info.setSize(file.length());
                info.setOwnerId("admin");
                info.setVersion(file.lastModified());
                info.setSha256(getHash256(file));
            } else {
                return ResponseEntity.status(WopiStatus.NOT_FOUND.value()).build();
            }
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(info);
    }

    public ResponseEntity handleLock(String fileName, HttpServletRequest request) {
        ResponseEntity response = null;
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
        logger.info("handleLock -- filename: {}, override: {}, response: {}, requestLock: {}, oldLock: {}",
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

}
