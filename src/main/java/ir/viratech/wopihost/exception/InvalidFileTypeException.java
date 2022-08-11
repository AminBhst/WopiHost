package ir.viratech.wopihost.exception;

import ir.viratech.wopihost.util.file.FileType;

public class InvalidFileTypeException extends Exception {

    final private String fileType;

    public InvalidFileTypeException(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }
}
