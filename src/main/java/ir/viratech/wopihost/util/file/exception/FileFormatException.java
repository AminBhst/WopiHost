package ir.viratech.wopihost.util.file.exception;

public class FileFormatException extends RuntimeException{
    public FileFormatException(String fileName) {
        super("File " + fileName + "does not contain proper format");
    }
}
