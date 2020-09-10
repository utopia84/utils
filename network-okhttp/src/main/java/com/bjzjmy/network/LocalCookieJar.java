package com.bjzjmy.network;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class LocalCookieJar implements CookieJar {
    private static LocalCookieJar cookieJar;
    private List<Cookie> cookies;

    public static LocalCookieJar getInstance() {
        if (cookieJar == null) {
            cookieJar = new LocalCookieJar();
        }
        return cookieJar;
    }

    @Override
    public @NotNull  List<Cookie> loadForRequest(@NotNull HttpUrl arg0) {
        if (cookies != null) {
            return cookies;
        }
        return new ArrayList<>();
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl arg0,@NotNull  List<Cookie> cookie) {
        cookies = cookie;
    }

}
