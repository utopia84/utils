package me.wcy.htmltext.html;

import android.text.Editable;

import org.xml.sax.XMLReader;

public interface TagHandler {
    void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader);
}
