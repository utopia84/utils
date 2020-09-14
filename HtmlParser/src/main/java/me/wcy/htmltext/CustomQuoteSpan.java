package me.wcy.htmltext;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.QuoteSpan;

public class CustomQuoteSpan extends QuoteSpan {

    int fMargin = 0;

    @Override
    public int getLeadingMargin(boolean first) {
        return fMargin;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        fMargin = (int) p.measureText("        ");
    }
}
