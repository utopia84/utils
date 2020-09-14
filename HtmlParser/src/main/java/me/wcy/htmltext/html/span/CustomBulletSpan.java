package me.wcy.htmltext.html.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

public class CustomBulletSpan implements LeadingMarginSpan {

    private int mBulletRadius = 4;
    private int mGapWidth = 2;
    private boolean mWantColor = true;
    private int mColor = 0;

    @Override
    public int getLeadingMargin(boolean first) {
        return 2 * mBulletRadius + mGapWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas canvas,Paint paint, int x, int dir, int top, int baseline, int bottom,CharSequence text, int start, int end, boolean first, Layout layout) {
        if (((Spanned) text).getSpanStart(this) == start) {
            Paint.Style style = paint.getStyle();
            int oldcolor = 0;

            if (mWantColor) {
                oldcolor = paint.getColor();
                paint.setColor(mColor);
            }

            paint.setStyle(Paint.Style.FILL);

            if (layout != null) {
                // "bottom" position might include extra space_center as a result of line spacing
                // configuration. Subtract extra space_center in order to show bullet in the vertical
                // center of characters.
                final int line = layout.getLineForOffset(start);
                bottom = bottom - layout.getLineDescent(line);
            }

            final float yPosition = (top + bottom) / 2f;
            final float xPosition = x + dir * mBulletRadius;

            canvas.drawCircle(xPosition, yPosition, mBulletRadius, paint);

            if (mWantColor) {
                paint.setColor(oldcolor);
            }

            paint.setStyle(style);
        }
    }
}
