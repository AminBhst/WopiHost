package ir.viratech.wopihost.service.generator;

import ir.viratech.wopihost.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PowerPointUrlGenerator implements WopiUrlGenerator {

    private final ConfigProperties config;

    @Autowired
    public PowerPointUrlGenerator(ConfigProperties config) {
        this.config = config;
    }

    @Override
    public String generate(String base64) {
        return String.format(urlFormat, config.getPowerPointEditorFrameUrl(), base64);
    }
}
