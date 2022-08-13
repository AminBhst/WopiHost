package ir.viratech.wopihost.controller;

import ir.viratech.wopihost.dto.EntryPointDTO;
import ir.viratech.wopihost.exception.InvalidFileTypeException;
import ir.viratech.wopihost.service.generator.WopiUrlGenerator;
import ir.viratech.wopihost.util.file.FileType;
import ir.viratech.wopihost.util.file.FileUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class WopiClientBaseController {

    protected final WopiUrlGenerator urlGenerator;
    protected final String[] validFileTypes;

    protected WopiClientBaseController(WopiUrlGenerator urlGenerator, String[] validFileTypes) {
        this.urlGenerator = urlGenerator;
        this.validFileTypes = validFileTypes;
    }

    /**
     * Service which generates the URL which is used as a starting point to start an instance of office online
     * to edit the given file.
     * <p>Just as the path suggests, this service is used as an entry point and should be called first in order
     * to start working with office online</p>
     */
    @PostMapping(value = "/entry-point")
    public EntryPointDTO generateWopiUrl(@RequestPart("file") MultipartFile file) throws IOException, InvalidFileTypeException {
        String uuid = UUID.randomUUID().toString();
        validateFileType(file.getOriginalFilename());
        return new EntryPointDTO(urlGenerator.generate(uuid));
    }

    private void validateFileType(String fileName) throws InvalidFileTypeException {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        if (!Arrays.asList(validFileTypes).contains(extension))
            throw new InvalidFileTypeException(fileName);
    }
}
