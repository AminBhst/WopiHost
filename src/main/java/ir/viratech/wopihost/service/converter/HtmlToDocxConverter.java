package ir.viratech.wopihost.service.converter;

import ir.viratech.wopihost.service.converter.constants.HTMLConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;


@Component
public class HtmlToDocxConverter implements HTMLConstants {


    /**
     * Converts a html file to docx
     * <p>an empty RTL template file is used as the initial docx file, an iteration will be performed on the paragraphs and
     * they will be copied to the initial RTL docx file to preserve the RTL text direction
     * @see HtmlToDocxConverter#getRTLEnabledDocxTemplate()
     */
    public ByteArrayOutputStream convert(byte[] html) throws Docx4JException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ByteArrayOutputStream convertedFromHtml = simpleConvert(html);
        XWPFDocument convertedFromHtmlDocument = new XWPFDocument(new ByteArrayInputStream(convertedFromHtml.toByteArray()));
        XWPFDocument rtlEnabledTemplate = new XWPFDocument(new ByteArrayInputStream(getRTLEnabledDocxTemplate()));
        copyTextToDocument(convertedFromHtmlDocument, rtlEnabledTemplate);
        rtlEnabledTemplate.write(outputStream);
        outputStream.flush();
        return outputStream;
    }


    /**
     * Converts a html file to docx without using the RTL template
     */
    private ByteArrayOutputStream simpleConvert(byte[] html) throws Docx4JException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
        String formattedHtml = formatHtmlFonts(html);
        wordMLPackage.getMainDocumentPart().getContent().addAll(XHTMLImporter.convert(correctHtml(formattedHtml), null));
        wordMLPackage.save(outputStream);
        return outputStream;
    }


    /**
     * Appends the font family and font size of each span to the text value of it in the following format : ${FONT_FAMILY, FONT_SIZE}$
     * <p>used in order to get the font family and font size from the text itself and set it as the font family and size property of the {@link XWPFRun} in the process of converting the html to docx
     * <p> e.g. a paragraph which has a font family of 'BNazanin' and a font size of '14', this method will extract both values from
     * the html tag and append ${BNazanin,14}$ to the text value of each paragraph. then this value will be extracted and set for
     * the paragraphs in the docx</p>
     *
     * @return formatted html
     */
    private String formatHtmlFonts(byte[] html) {
        Document document = Jsoup.parse(new String(html, StandardCharsets.UTF_8));
        Elements paragraphs = document.body().getElementsByTag("p");
        for (Element paragraph : paragraphs) {
            String fontFamily;
            String fontSize;
            Elements spans = paragraph.getElementsByTag(SPAN);
            for (Element span : spans) {
                Elements childSpans = span.getElementsByTag(SPAN);
                if (childSpans.size() == 2) {
                    Element childSpan = childSpans.get(1);
                    fontSize = extractFontSize(span.attr(STYLE));
                    fontFamily = extractFontFamily(childSpan.attr(STYLE));

                    if (StringUtils.isEmpty(fontSize))
                        fontSize = extractFontSize(childSpan.attr(STYLE));

                    if (StringUtils.isEmpty(fontFamily))
                        fontFamily = extractFontFamily(span.attr(STYLE));

                    childSpan.text(formatFontStyle(span.text(), fontFamily, fontSize));
                } else if (childSpans.size() == 1) {
                    Element orgSpan = childSpans.get(0);
                    String style = orgSpan.attr(STYLE);
                    fontSize = extractFontSize(style);
                    fontFamily = extractFontFamily(style);
                    if (!span.text().contains(STYLE_FORMAT_OPEN)) {
                        orgSpan.text(formatFontStyle(orgSpan.text(), fontFamily, fontSize));
                    }
                }
            }
        }

        return document.html();
    }

    /**
     * Extracts the font family from the html attributes
     */
    private String extractFontFamily(String attribute) {
        return attribute.contains(FONT_FAMILY) ? attribute.substring(attribute.indexOf(":") + 2, attribute.indexOf(";") - 1) : "";
    }

    /**
     * Extracts the font size from the html attributes
     */
    private String extractFontSize(String attribute) {
        return attribute.contains(FONT_SIZE) ? attribute.substring(attribute.indexOf(":") + 1, attribute.indexOf(";") - 2) : "";
    }


    /**
     * Similar to Kateb, A Custom docx with RTL is created and will be used as the initial pdf, then
     * all the texts will be appended to this docx file to preserve the RTL text direction
     */
    private byte[] getRTLEnabledDocxTemplate() throws IOException {
        String path = this.getClass().getClassLoader().getResource("RTLEnabledTemplate.docx").getFile();

        if (SystemUtils.IS_OS_WINDOWS) {
            path = path.replaceFirst("/", "");
        }

        return Files.readAllBytes(Paths.get(path));
    }


    /**
     * Copies the paragraphs from one document to another
     */
    private void copyTextToDocument(XWPFDocument originDocument, XWPFDocument destDocument) {
        List<XWPFParagraph> paragraphs = originDocument.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            if (!paragraph.getParagraphText().isEmpty()) {
                XWPFParagraph newParagraph = destDocument.createParagraph();
                copyAllRunsToAnotherParagraph(paragraph, newParagraph);
            }
        }
    }

    /**
     * Copies the content of a paragraph to another. font family and font size are extracted from the formatted text
     * @see HtmlToDocxConverter#formatHtmlFonts(byte[])
     */
    private void copyAllRunsToAnotherParagraph(XWPFParagraph oldParagraph, XWPFParagraph newParagraph) {
        for (XWPFRun run : oldParagraph.getRuns()) {
            String text = run.getText(0);
            if (StringUtils.isEmpty(text)) {
                continue;
            }

            XWPFRun newRun = newParagraph.createRun();
            newRun.setBold(run.isBold());
            newRun.setItalic(run.isItalic());
            newRun.setDoubleStrikethrough(run.isDoubleStrikeThrough());
            newRun.setStrikeThrough(run.isStrikeThrough());
            newRun.setColor(run.getColor());
            newRun.setFontSize(extractFontSizeValue(text));
            newRun.setFontFamily(extractFontFamilyValue(text));
            newRun.setText(removeStyleFormat(text));
        }
    }


    /**
     * Removes the style format ( ${FONT_FAMILY,FONT_SIZE}$ ) from the text after the extraction process is done
     */
    private String removeStyleFormat(String text) {
        return text.replaceAll((STYLE_FORMAT_REGEX), "");
    }


    /**
     * Extracts the Style format ( ${FONT_FAMILY,FONT_SIZE}$ ) from the text using a regex
     * @return The raw style format
     */
    private String extractStyleFormat(String text) {
        if (!Pattern.compile(STYLE_FORMAT_REGEX).matcher(text).find()) {
            return text;
        }
        return text.substring(text.indexOf(STYLE_FORMAT_OPEN) + 2, text.indexOf(STYLE_FORMAT_CLOSE));
    }

    /**
     * Extracts the fontSize from the entire text using the style format
     * @return fontSize
     */
    private Integer extractFontSizeValue(String text) {
        String[] tokens = extractStyleFormat(text).split(",");

        if (tokens.length < 2)
            return DEFAULT_FONT_SIZE;

        String fontSize = tokens[1];
        return StringUtils.isEmpty(fontSize) ? DEFAULT_FONT_SIZE : Integer.parseInt(fontSize);
    }

    /**
     * Extracts the fontFamily from the entire text using style fromat
     * @return fontFamily
     */
    private String extractFontFamilyValue(String text) {
        String[] tokens = extractStyleFormat(text).split(",");

        if (tokens.length == 0)
            return DEFAULT_FONT_FAMILY;

        String fontFamily = tokens[0];
        return StringUtils.isEmpty(fontFamily) ? DEFAULT_FONT_FAMILY : fontFamily;
    }

    /**
     * Creates and appends the style format to the end of the text
     * @param text paragraph text
     * @param fontFamily extracted fontFamily
     * @param fontSize extracted fontSize
     * @return the whole text containing the style format
     */
    private String formatFontStyle(String text, String fontFamily, String fontSize) {
        return String.format("%s${%s,%s}$", text, fontFamily, fontSize);
    }

    /**
     * Used to resolve some bugs
     */
    private String correctHtml(String html) {
        return html.replaceAll("&nbsp;", "")
                .replaceAll("<br>", "<div></div>")
                .replaceAll(BAD_META, CORRECTED_META);
    }


}
