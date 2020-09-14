package me.wcy.htmltext;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HtmlTxt {

    private HtmlImageLoader imageLoader;
    private After after;
    private String source;

    public interface After {
        CharSequence after(SpannableStringBuilder ssb);
    }

    private HtmlTxt(String source){
        this.source = source;
    }

    public static HtmlTxt from(String source){
        return new HtmlTxt(source);
    }

    public HtmlTxt setImageLoader(HtmlImageLoader imageLoader){
        this.imageLoader = imageLoader;
        return this;
    }

    public HtmlTxt after(After after){
        this.after = after;
        return this;
    }

    /**
     * 注入TextView
     * */
    public void into(TextView tv){
        if(TextUtils.isEmpty(source)){
            tv.setText("");
            return;
        }

        HtmlImageGetter imageGetter = new HtmlImageGetter();
        HtmlTagHandler tagHandler = new HtmlTagHandler();
        List<String> imageUrls = new ArrayList<>();

        imageGetter.setTextView(tv);
        imageGetter.setImageLoader(imageLoader);
        imageGetter.getImageSize(source);

        tagHandler.setTextView(tv);
        source = tagHandler.overrideTags(source);

        Spanned spanned = Html.fromHtml(source, imageGetter, tagHandler);
        SpannableStringBuilder ssb;
        if(spanned instanceof SpannableStringBuilder){
            ssb = (SpannableStringBuilder) spanned;
        }else {
            ssb = new SpannableStringBuilder(spanned);
        }

        imageUrls.clear();
        ImageSpan[] imageSpans = ssb.getSpans(0, ssb.length(), ImageSpan.class);
        for (int i = 0; i < imageSpans.length; i++) {
            ImageSpan imageSpan = imageSpans[i];
            String imageUrl = imageSpan.getSource();
            int start = ssb.getSpanStart(imageSpan);
            int end = ssb.getSpanEnd(imageSpan);
            imageUrls.add(imageUrl);

            ImageClickSpan imageClickSpan = new ImageClickSpan(tv.getContext(), imageUrls, i);
//            imageClickSpan.setListener(onTagClickListener);
            ClickableSpan[] clickableSpans = ssb.getSpans(start, end, ClickableSpan.class);
            if (clickableSpans != null) {
                for (ClickableSpan cs : clickableSpans) {
                    ssb.removeSpan(cs);
                }
            }
            ssb.setSpan(imageClickSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Hold text url link
        URLSpan[] urlSpans = ssb.getSpans(0, ssb.length(), URLSpan.class);
        if (urlSpans != null) {
            for (URLSpan urlSpan : urlSpans) {
                int start = ssb.getSpanStart(urlSpan);
                int end = ssb.getSpanEnd(urlSpan);
                ssb.removeSpan(urlSpan);
                LinkClickSpan linkClickSpan = new LinkClickSpan(tv.getContext(), urlSpan.getURL());
//                linkClickSpan.setListener(onTagClickListener);
                ssb.setSpan(linkClickSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }

        CharSequence charSequence = ssb;
        if (after != null) {
            charSequence = after.after(ssb);
        }

        tv.setText(charSequence);
    }
}
