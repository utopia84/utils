package me.wcy.htmltext.html;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.wcy.htmltext.html.span.ImageClickSpan;

public class HtmlUtil {

    public static void format(String txt, TextView contentView, HtmlImageLoader imageLoader, OnTagClickListener onTagClickListener){
        HtmlImageGetter imageGetter = new HtmlImageGetter();
        imageGetter.setTextView(contentView);
        imageGetter.setImageLoader(imageLoader);
        imageGetter.getImageSize(txt);

        Spanned spanned = HtmlParser.fromHtml(txt, imageGetter, null);
        SpannableStringBuilder ssb;
        if(spanned instanceof SpannableStringBuilder){
            ssb = (SpannableStringBuilder) spanned;
        }else {
            ssb = new SpannableStringBuilder(spanned);
        }

        List<String> imageUrls = new ArrayList<>();

        ImageSpan[] imageSpans = ssb.getSpans(0, ssb.length(), ImageSpan.class);

        for (int i = 0; i < imageSpans.length; i++) {
            ImageSpan imageSpan = imageSpans[i];
            String imageUrl = imageSpan.getSource();
            int start = ssb.getSpanStart(imageSpan);
            int end = ssb.getSpanEnd(imageSpan);
            imageUrls.add(imageUrl);

            ImageClickSpan imageClickSpan = new ImageClickSpan(contentView.getContext(), imageUrls, i);
            imageClickSpan.setListener(onTagClickListener);
            ClickableSpan[] clickableSpans = ssb.getSpans(start, end, ClickableSpan.class);
            if (clickableSpans != null) {
                for (ClickableSpan cs : clickableSpans) {
                    ssb.removeSpan(cs);
                }
            }
            ssb.setSpan(imageClickSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        contentView.setMovementMethod(LinkMovementMethod.getInstance());
        contentView.setText(ssb);
    }
}
