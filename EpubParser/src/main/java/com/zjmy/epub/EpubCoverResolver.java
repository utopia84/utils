package com.zjmy.epub;

import com.zjmy.epub.callback.OnCompressListener;
import com.zjmy.epub.cover.task.ResolverTask;
import com.zjmy.epub.utils.Logger;
import com.zjmy.epub.utils.ResolveThreadPool;

@SuppressWarnings("unused")
public class EpubCoverResolver {
    private static final String TAG = "EpubCoverResolver";

    private volatile static EpubCoverResolver resolver;

    public static EpubCoverResolver getInstance() {
        if (resolver == null) {
            synchronized (EpubCoverResolver.class) {
                if (resolver == null) {
                    resolver = new EpubCoverResolver();
                }
            }
        }
        return resolver;
    }

    private EpubCoverResolver() {
    }


    public void load(String path , String coverName,OnCompressListener listener) {
        ResolveThreadPool.getInstance().getThreadPoolExecutor().execute(
                new ResolverTask(path,coverName,listener)
        );
    }

    public static void debug(boolean isDebug) {
        Logger.debug(isDebug);
    }

}