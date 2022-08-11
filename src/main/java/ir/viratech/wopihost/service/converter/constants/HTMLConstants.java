package ir.viratech.wopihost.service.converter.constants;

public interface HTMLConstants {
    String FONT_FAMILY = "font-family";
    String FONT_SIZE = "font-size";

    String DEFAULT_FONT_FAMILY = "B Yagut";
    Integer DEFAULT_FONT_SIZE = 14;
    String STYLE = "style";
    String SPAN = "span";
    String DIV = "div";
    String HEAD = "head";

    String BAD_META = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
    String CORRECTED_META = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>";

    String STYLE_FORMAT_REGEX = "\\$\\{.*}\\$";
    String STYLE_FORMAT_OPEN = "${";
    String STYLE_FORMAT_CLOSE = "}$";

    String HTML_HEAD_REPLACE = "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
            "    <style>* {\n" +
            "        font-family: 'B Yagut'\n" +
            "    }\n" +
            "\n" +
            "    p, div {\n" +
            "        line-height: 2;\n" +
            "    }\n" +
            "\n" +
            "    span {\n" +
            "        font-family: inherit;\n" +
            "    }</style>\n" +
            "    <style>.kateb_template_delivery_bcc {\n" +
            "        display: none\n" +
            "    }\n" +
            "\n" +
            "    .kateb_template_delivery_cc {\n" +
            "        display: none\n" +
            "    }</style>\n" +
            "    <style>.delivery_hide_cc {\n" +
            "        display: none\n" +
            "    }</style>\n" +
            "    <style>.delivery_hide_bcc {\n" +
            "        display: none\n" +
            "    }</style>\n" +
            "    <style>.kateb_font_yagut {\n" +
            "        font-family: 'B Yagut'\n" +
            "    }\n" +
            "\n" +
            "    .kateb_font_titr {\n" +
            "        font-family: 'B Titr'\n" +
            "    }\n" +
            "\n" +
            "    .kateb_font_nazanin {\n" +
            "        font-family: 'B Nazanin'\n" +
            "    }\n" +
            "\n" +
            "    .kateb_font_lotus {\n" +
            "        font-family: 'B Lotus'\n" +
            "    }\n" +
            "\n" +
            "    .kateb_font_koodak {\n" +
            "        font-family: 'B Koodak'\n" +
            "    }\n" +
            "\n" +
            "    .kateb_font_zar {\n" +
            "        font-family: 'B Zar'\n" +
            "    }</style>\n" +
            "</head>";
}
