package ir.viratech.wopihost.exception;

public class InvalidFileTypeException extends Exception {

    final private String fileType;

    public InvalidFileTypeException(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }
}
