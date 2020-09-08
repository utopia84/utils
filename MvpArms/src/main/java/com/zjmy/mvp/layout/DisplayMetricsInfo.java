package com.zjmy.mvp.layout;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;

class DisplayMetricsInfo implements Parcelable {
    float density;
    int densityDpi;
    float scaledDensity;

    public DisplayMetricsInfo(float density, int densityDpi, float scaledDensity) {
        this.density = density;
        this.densityDpi = densityDpi;
        this.scaledDensity = scaledDensity;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.density);
        dest.writeInt(this.densityDpi);
        dest.writeFloat(this.scaledDensity);
    }

    protected DisplayMetricsInfo(Parcel in) {
        this.density = in.readFloat();
        this.densityDpi = in.readInt();
        this.scaledDensity = in.readFloat();
    }

    public static final Creator<DisplayMetricsInfo> CREATOR = new Creator<DisplayMetricsInfo>() {
        @Override
        public DisplayMetricsInfo createFromParcel(Parcel source) {
            return new DisplayMetricsInfo(source);
        }

        @Override
        public DisplayMetricsInfo[] newArray(int size) {
            return new DisplayMetricsInfo[size];
        }
    };
}
