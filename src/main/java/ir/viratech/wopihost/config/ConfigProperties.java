package ir.viratech.wopihost.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ConfigurationProperties(prefix = "wopi")
@Configuration
public class ConfigProperties {
    private String password;
    private String username;
    private String serverEditorFrameUrl;
    private String wordFileDirectoryPath;
    private String htmlDirectoryPath;
    private String fontPath;
    private String officeWordEditorFrameUrl;
    private String powerPointEditorFrameUrl;
    private String excelEditorFrameUrl;
}
