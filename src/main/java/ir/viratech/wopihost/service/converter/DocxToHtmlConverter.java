package ir.viratech.wopihost.service.converter;

import ir.viratech.wopihost.service.converter.constants.HTMLConstants;
import ir.viratech.wopihost.util.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static ir.viratech.wopihost.service.converter.constants.Fonts.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Service
@Slf4j
public class DocxToHtmlConverter implements HTMLConstants {

    private static final Map<String, String> fontMap = new HashMap<>();
    static {
        fontMap.put(B_NAZANIN, B_NAZANIN_FILE);
        fontMap.put(B_LOTUS, B_LUTOS_FILE);
        fontMap.put(B_KOODAK, B_KOODAK_FILE);
        fontMap.put(B_YAGUT, B_YAGUT_FILE);
        fontMap.put(B_ZAR, B_ZAR_FILE);
    }


    public String convert(String fileName) throws Docx4JException, IOException {
        WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(FileUtils.getFile(fileName));
        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        htmlSettings.setWmlPackage(mlPackage);
        String htmlFileName = FileUtils.generateHtmlFileName();
        FileOutputStream os = new FileOutputStream(htmlFileName);
        htmlSettings.setFontMapper(createFontMapper());
        htmlSettings.setFontFamilyStack(true);
        Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
        return correctHtml(htmlFileName);
    }


    /**
     *  Adds the Kateb fonts to the html file and some minor corrections are performed
     * @param fileName
     */
    public String correctHtml(String fileName) throws IOException {
        Document document = Jsoup.parse(FileUtils.readFile(fileName));
        Elements head = document.getElementsByTag(HEAD);
        head.get(0).after(HTML_HEAD_REPLACE);
        head.get(0).remove();
        for (Element div : document.getElementsByTag(DIV)) {
            if (div.text().contains("org.docx4j"))
                div.remove();

            if (div.className().equals("document"))
                div.attr("dir", "rtl");
        }
        return document.html();
    }


    private Mapper createFontMapper() {
        Mapper mapper = new IdentityPlusMapper();
        fontMap.forEach((name, file) -> {
            PhysicalFont font = addAndGetPhysicalFont(name, file);
            if (font != null) {
                mapper.put(name, font);
            }
        });
        return mapper;
    }


    private PhysicalFont addAndGetPhysicalFont(String fontName, String fontFile) {
        try {
            PhysicalFonts.addPhysicalFonts(fontName, FileUtils.getFontUrl(fontFile));
            return PhysicalFonts.get(fontName);
        } catch (Throwable t) {
            log.error("could not get font pah! fontName : {}, fontFile : {}", fontName, fontFile, t);
            return null;
        }
    }

}