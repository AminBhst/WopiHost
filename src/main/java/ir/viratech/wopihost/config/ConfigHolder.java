package ir.viratech.wopihost.config;

import ir.viratech.wopihost.spring.ApplicationContextHolder;

public class ConfigHolder {

    public static ConfigProperties getConfig() {
        return ApplicationContextHolder.getContext().getBean(ConfigProperties.class);
    }
}
