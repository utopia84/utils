package me.wcy.htmltext.html;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.wcy.htmltext.UICDisplayTool;

public class HtmlImageGetter implements ImageGetter {
    private static final String IMAGE_TAG_REGULAR = "<(img|IMG)\\s+([^>]*)>";
    private static final Pattern IMAGE_TAG_PATTERN = Pattern.compile(IMAGE_TAG_REGULAR);
    private static final Pattern IMAGE_WIDTH_PATTERN = Pattern.compile("(width|WIDTH)\\s*=\\s*\"?(\\w+)\"?");
    private static final Pattern IMAGE_HEIGHT_PATTERN = Pattern.compile("(height|HEIGHT)\\s*=\\s*\"?(\\w+)\"?");

    private List<ImageSize> imageSizeList;
    private TextView textView;
    private HtmlImageLoader imageLoader;

    private int index;

    public HtmlImageGetter() {
        imageSizeList = new ArrayList<>();
    }

    public void setTextView(TextView tv) {
        this.textView = tv;
    }

    public void setImageLoader(HtmlImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void getImageSize(String source) {
        Matcher imageMatcher = IMAGE_TAG_PATTERN.matcher(source);
        while (imageMatcher.find()) {
            String attrs = imageMatcher.group(2).trim();
            int width = -1;
            int height = -1;
            Matcher widthMatcher = IMAGE_WIDTH_PATTERN.matcher(attrs);
            if (widthMatcher.find()) {
                width = parseSize(widthMatcher.group(2).trim());
            }
            Matcher heightMatcher = IMAGE_HEIGHT_PATTERN.matcher(attrs);
            if (heightMatcher.find()) {
                height = parseSize(heightMatcher.group(2).trim());
            }
            ImageSize imageSize = new ImageSize(width, height);
            imageSizeList.add(imageSize);
        }
    }

    private static int parseSize(String size) {
        try {
            return Integer.valueOf(size);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public Drawable getDrawable(String source) {
        final ImageDrawable imageDrawable =
                new ImageDrawable(textView.getResources(),
                        index++,
                        imageLoader);

        if (imageLoader != null) {
            imageDrawable.setDrawable(imageLoader.getPlaceHolderDrawable(), true);
            imageLoader.loadImage(source, new HtmlImageLoader.LoadCallback() {
                @Override
                public void onPrepare() {

                }

                @Override
                public void onComplete(final Bitmap bitmap) {
                    runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(textView.getResources(), bitmap);
                            imageDrawable.setDrawable(drawable, true);
                            textView.setText(textView.getText());
                        }
                    });
                }

                @Override
                public void onFailed(Throwable e) {
                    runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            imageDrawable.setDrawable(imageLoader.getFailureDrawable(), true);
                            textView.setText(textView.getText());
                        }
                    });
                }
            });
        }
        return imageDrawable;
    }

    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            textView.post(r);
        }
    }

    /**
     * 图片的宽高
     */
    static class ImageSize {

        private final int width;
        private final int height;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        /**
         * 数值是否有效
         */
        public boolean valid() {
            return width >= 0 && height >= 0;
        }
    }

    class ImageDrawable extends BitmapDrawable {

        // img 标签出现的位置
        private Resources resources;
        private final int position;
        private final HtmlImageLoader imageLoader;
        private Drawable mDrawable;

        public ImageDrawable(Resources resources, int position, HtmlImageLoader imageLoader) {
            super(null, (Bitmap) null);
            this.position = position;
            this.imageLoader = imageLoader;
            this.resources = resources;
        }

        public void setDrawable(Drawable drawable, boolean fitSize) {
            mDrawable = drawable;
            if (mDrawable == null) {
                setBounds(0, 0, 0, 0);
                return;
            }

            int width, height;

            if (fitSize) {
                ImageSize imageSize = (imageSizeList.size() > position) ? imageSizeList.get(position) : null;
                if (imageSize != null && imageSize.valid()) {
                    width = UICDisplayTool.dp2Px(imageSize.width);
                    height = UICDisplayTool.dp2Px(imageSize.height);
                } else {
                    width = mDrawable.getIntrinsicWidth();
                    height = mDrawable.getIntrinsicHeight();
                }
            } else {
                width = mDrawable.getIntrinsicWidth();
                height = mDrawable.getIntrinsicHeight();
            }

            if (width > 0 && height > 0) {
                int maxWidth = (imageLoader == null) ? 0 : imageLoader.getMaxWidth();
                boolean fitWidth = imageLoader != null && imageLoader.fitWidth();
                if (maxWidth > 0 && (width > maxWidth || fitWidth)) {
                    height = (int) ((float) height / width * maxWidth);
                    width = maxWidth;
                }
            }

            mDrawable.setBounds(0, 0, width, height);
            setBounds(0, 0, width, height);
        }

        @Override
        public void draw(Canvas canvas) {
            if (mDrawable != null) {
                if (mDrawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) mDrawable;
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                }
                mDrawable.draw(canvas);
            }
        }
    }
}
