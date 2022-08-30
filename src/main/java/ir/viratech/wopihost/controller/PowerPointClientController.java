//package ir.viratech.wopihost.controller;
//
//import ir.viratech.wopihost.repository.WopiFileRepository;
//import ir.viratech.wopihost.service.generator.WopiUrlGenerator;
//import ir.viratech.wopihost.util.ValidFileTypes;
//import ir.viratech.wopihost.util.file.FileType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/wopi/client/powerpoint")
//public class PowerPointClientController extends WopiClientBaseController {
//
//    protected PowerPointClientController(@Qualifier("powerPointUrlGenerator") WopiUrlGenerator urlGenerator,
//                                         @Autowired WopiFileRepository wopiFileRepository) {
//        super(urlGenerator, ValidFileTypes.POWER_POINT, wopiFileRepository);
//    }
//}
//
