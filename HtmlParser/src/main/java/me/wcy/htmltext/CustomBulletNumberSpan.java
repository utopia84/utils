package me.wcy.htmltext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.BulletSpan;

public class CustomBulletNumberSpan extends BulletSpan {

    private static final int STANDARD_BULLET_RADIUS = 8;
    public static final int STANDARD_GAP_WIDTH = 16;
    private static final int STANDARD_COLOR = 0;

    private int liIndex;
    private String liType;

    public static final String OL = "OL";
    public static final String UL = "UL";

    private int left;

    public CustomBulletNumberSpan(int liIndex, String liType) {
        this.liIndex = liIndex;
        this.liType = liType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int getLeadingMargin(boolean first) {
//        if(first){
//            return left;
//        }
        if(OL.equalsIgnoreCase(liType)){
            return 50;
        }
        return STANDARD_BULLET_RADIUS * 2 + STANDARD_GAP_WIDTH;
    }

    @Override
    public void drawLeadingMargin(Canvas canvas,Paint paint, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        if (((Spanned) text).getSpanStart(this) == start) {

//            Paint.Style style = paint.getStyle();
//            paint.setStyle(Paint.Style.FILL);
//            canvas.drawCircle(STANDARD_BULLET_RADIUS, ((top + bottom) / 2 + baseline) / 2, STANDARD_BULLET_RADIUS, paint);
//            paint.setStyle(style);



            Paint.Style style = paint.getStyle();
            if(OL.equalsIgnoreCase(liType)){
                Paint paint1 = new Paint();
                paint1.setAntiAlias(true);
                paint1.setColor(Color.parseColor("#333333"));
                paint1.setTextSize(UICDisplayTool.sp2Px(12));
                paint1.setFakeBoldText(true);
                canvas.drawText("" + liIndex, 0, ((top + bottom) / 2 + baseline) / 2 + 4, paint1);

                Paint paint3 = new Paint();
                paint3.setAntiAlias(true);
                paint3.setColor(Color.parseColor("#333333"));
                paint3.setTextSize(UICDisplayTool.sp2Px(12));
                paint3.setStyle(Paint.Style.FILL);
                float yPosition = ((top + bottom) / 2 + baseline) / 2;
                canvas.drawCircle(paint3.measureText("" + liIndex) + 8, yPosition, 4, paint3);
            }else {
                Paint paint3 = new Paint();
                paint3.setAntiAlias(true);
                paint3.setColor(Color.parseColor("#333333"));
                paint.setStyle(Paint.Style.FILL);
                float yPosition = ((top + bottom) / 2 + baseline) / 2;
                canvas.drawCircle(STANDARD_BULLET_RADIUS, yPosition, STANDARD_BULLET_RADIUS, paint3);
            }
            paint.setStyle(style);
        }
    }

    @Override
    public int getSpanTypeId() {
        return 8888;
    }
}
