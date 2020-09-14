package me.wcy.htmltext;

import android.content.Context;
import android.widget.TextView;

public class HtmlUtil {

    public static void format(String txt, Context activity, TextView contentView){
        contentView.setText(Html.fromHtml(txt, null, null));
    }

    public static void format(String txt, TextView contentView, HtmlImageLoader imageLoader){
        HtmlImageGetter imageGetter = new HtmlImageGetter();
        imageGetter.setTextView(contentView);
        imageGetter.setImageLoader(imageLoader);
        imageGetter.getImageSize(txt);
        contentView.setText(Html.fromHtml(txt, imageGetter, null));
    }
}
