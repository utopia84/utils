package me.wcy.htmltext.html;

import android.text.SpannableString;
import android.text.Spanned;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class HtmlParser {

    private HtmlParser() { }

    public static Spanned fromHtml(String source) {
        return fromHtml(source, Html.FROM_HTML_MODE_LEGACY, null, null);
    }

    public static Spanned fromHtml(String source, int flags) {
        return fromHtml(source, flags, null, null);
    }

    public static Spanned fromHtml(String source, ImageGetter imageGetter, TagHandler tagHandler) {
        return fromHtml(source, Html.FROM_HTML_MODE_LEGACY, imageGetter, tagHandler);
    }

    public static Spanned fromHtml(String source, int flags, ImageGetter imageGetter, TagHandler tagHandler) {
        try {
            SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
            XMLReader parser = sp.getXMLReader();
            return new HtmlToSpannedConverter(source, imageGetter, tagHandler, parser, flags).convert();
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return new SpannableString(source);
    }
}
