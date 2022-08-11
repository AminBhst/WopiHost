package ir.viratech.wopihost.service.generator;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public interface WopiUrlGenerator {

    String generate(String fileName);

    String urlFormat = "%s?WOPISrc=" + ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + "/wopi/files/%s";
}
