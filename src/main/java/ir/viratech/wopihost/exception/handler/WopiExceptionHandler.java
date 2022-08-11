package ir.viratech.wopihost.exception.handler;

import ir.viratech.wopihost.dto.MessageDTO;
import ir.viratech.wopihost.dto.UiMetadataDTO;
import ir.viratech.wopihost.exception.InvalidFileTypeException;
import ir.viratech.wopihost.util.I18N;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.util.Locale;

@RestControllerAdvice
public class WopiExceptionHandler extends ResponseEntityExceptionHandler {

    final MessageSource messageSource;

    @Autowired
    public WopiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected UiMetadataDTO handleInvalidFileType(InvalidFileTypeException ex) {
        String message = messageSource.getMessage(I18N.invalidFileType, new Object[]{ex.getFileType().toString()}, new Locale("fa"));
        MessageDTO messageDto = new MessageDTO(message, MessageDTO.MessageType.ERROR);
        return new UiMetadataDTO(messageDto);
    }

}
