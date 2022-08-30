package ir.viratech.wopihost.service.generator;

import ir.viratech.wopihost.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExcelUrlGenerator implements WopiUrlGenerator {

    private final ConfigProperties config;

    @Autowired
    public ExcelUrlGenerator(ConfigProperties config) {
        this.config = config;
    }


    @Override
    public String generate(String base64Json) {
        return String.format(urlFormat, config.getExcelEditorFrameUrl(), base64Json);
    }
}
