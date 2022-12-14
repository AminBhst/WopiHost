package ir.viratech.wopihost.controller;

import ir.viratech.wopihost.repository.WopiFileRepository;
import ir.viratech.wopihost.service.generator.WopiUrlGenerator;
import ir.viratech.wopihost.util.ValidFileTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wopi/client/word")
public class OfficeWordClientController extends WopiClientBaseController {

    protected OfficeWordClientController(@Qualifier("officeWordUrlGenerator") WopiUrlGenerator urlGenerator,
                                         @Autowired WopiFileRepository wopiFileRepository) {
        super(urlGenerator, ValidFileTypes.OFFICE_WORD, wopiFileRepository);
    }
}
