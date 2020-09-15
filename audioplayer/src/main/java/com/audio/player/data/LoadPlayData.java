package com.audio.player.data;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.audio.player.databases.AudioBookDatabase;
import com.audio.player.databases.DatabaseHolder;
import com.audio.player.databases.table.AudioBookChapter;
import com.audio.player.model.BaseSubscriber;
import com.audio.player.util.UICLog;
import java.util.ArrayList;
import java.util.List;

import eink.yitoa.utils.common.ApplicationUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者 邸昌顺
 * @时间 2019/7/1 17:04
 * @描述 加载播放的数据列表, 这个数据模型只用加载一次
 */
public class LoadPlayData {

    private static LoadPlayData loadPlayData = new LoadPlayData();
    private LoadPlayData(){}

    public static LoadPlayData getDefault(){
        return loadPlayData;
    }

    //数据加载完成回调
    public interface LoadDataCallback {

        void completeLoad(PlayData data);
    }

    private LoadDataCallback mLoadDataCallback;
    private Subscription mSub;

    public final void goGetData(LoadDataCallback loadDataCallback){
        this.mLoadDataCallback = loadDataCallback;

        mSub = Observable.create((Subscriber<? super List<MediaBrowserCompat.MediaItem>> subscriber) -> {
            List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
            List<AudioBookChapter> chapters = DatabaseHolder.getInstance().chapterDao().loadAll();//LitePal.findAll(AudioBookChapter.class);
            for(AudioBookChapter chapter : chapters){
                mediaItems.add(getMediaItem(chapter));
            }
            subscriber.onNext(mediaItems);
        })
        .observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new BaseSubscriber<List<MediaBrowserCompat.MediaItem>>() {
            @Override
            public void onNext(List<MediaBrowserCompat.MediaItem> list) {
                mLoadDataCallback.completeLoad(new PlayData(list));
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                UICLog.e("onError-" + e.toString());
            }
        });
    }

    public final void release(){
        if(mSub != null){
            mSub.unsubscribe();
            mSub = null;
        }
    }

    private MediaBrowserCompat.MediaItem getMediaItem(AudioBookChapter chapter){
        Bundle extras = new Bundle();
        extras.putString(PlayData.CHAPTER_ID, chapter.getChapterId());
        extras.putString(PlayData.CHAPTER_BOOK_ID, chapter.getBookId());
        extras.putString(PlayData.CHAPTER_NAME, chapter.getName());
        extras.putString(PlayData.CHAPTER_URL, chapter.getUri());
        extras.putLong(PlayData.CHAPTER_DURATION, chapter.getDuration());
        extras.putInt(PlayData.CHAPTER_ORDERNO, chapter.getOrderNo());
        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
        builder.setMediaId(chapter.getChapterId());
        builder.setTitle(chapter.getName());
        builder.setExtras(extras);

        return new MediaBrowserCompat.MediaItem(builder.build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    public static MediaMetadataCompat getMediaMetadata(MediaDescriptionCompat mediaDescription){
        Bundle bundle = mediaDescription.getExtras();
        if(bundle == null){
            return null;
        }else {
            MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
            builder.putString(PlayData.CHAPTER_ID, getChapterId(mediaDescription));
            builder.putString(PlayData.CHAPTER_BOOK_ID, getBookId(mediaDescription));
            builder.putString(PlayData.CHAPTER_NAME, getChapterName(mediaDescription));
            builder.putString(PlayData.CHAPTER_URL, getChapterMediaPath(mediaDescription));
            builder.putLong(PlayData.CHAPTER_ORDERNO, getChapterOrderNo(mediaDescription));
            builder.putLong(PlayData.CHAPTER_DURATION, getChapterDuration(mediaDescription));
            builder.putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, getChapterId(mediaDescription));
            builder.putText(MediaMetadataCompat.METADATA_KEY_TITLE, getChapterName(mediaDescription));
            return builder.build();
        }
    }

    public static String getChapterMediaPath(MediaMetadataCompat mediaDescription){
        if(mediaDescription == null){
            return "";
        }
        return mediaDescription.getString(PlayData.CHAPTER_URL);
    }

    public static String getChapterName(MediaMetadataCompat mediaDescription){
        if(mediaDescription == null){
            return "";
        }
        return mediaDescription.getString(PlayData.CHAPTER_NAME);
    }

    public static String getBookId(MediaMetadataCompat mediaDescription){
        if(mediaDescription == null){
            return "";
        }
        return mediaDescription.getString(PlayData.CHAPTER_BOOK_ID);
    }

    public static String getChapterId(MediaMetadataCompat mediaDescription){
        if(mediaDescription == null){
            return "";
        }
        return mediaDescription.getString(PlayData.CHAPTER_ID);
    }

    public static long getChapterDuration(MediaMetadataCompat mediaDescription){
        if(mediaDescription == null){
            return 0;
        }
        return mediaDescription.getLong(PlayData.CHAPTER_DURATION);
    }

    public static int getChapterOrderNo(MediaMetadataCompat mediaDescription){
        if(mediaDescription == null){
            return 0;
        }
        return (int) mediaDescription.getLong(PlayData.CHAPTER_ORDERNO);
    }

    public static String getChapterMediaPath(MediaDescriptionCompat mediaDescription){
        Bundle extras = getBundleForMediaDescription(mediaDescription);
        if(extras == null){
            return "";
        }
        return extras.getString(PlayData.CHAPTER_URL);
    }

    public static String getChapterName(MediaDescriptionCompat mediaDescription){
        Bundle extras = getBundleForMediaDescription(mediaDescription);
        if(extras == null){
            return "";
        }
        return extras.getString(PlayData.CHAPTER_NAME);
    }

    public static String getBookId(MediaDescriptionCompat mediaDescription){
        Bundle extras = getBundleForMediaDescription(mediaDescription);
        if(extras == null){
            return "";
        }
        return extras.getString(PlayData.CHAPTER_BOOK_ID);
    }

    public static String getChapterId(MediaDescriptionCompat mediaDescription){
        Bundle extras = getBundleForMediaDescription(mediaDescription);
        if(extras == null){
            return "";
        }
        return extras.getString(PlayData.CHAPTER_ID);
    }

    public static long getChapterDuration(MediaDescriptionCompat mediaDescription){
        Bundle extras = getBundleForMediaDescription(mediaDescription);
        if(extras == null){
            return 0;
        }
        return extras.getLong(PlayData.CHAPTER_DURATION);
    }

    public static int getChapterOrderNo(MediaDescriptionCompat mediaDescription){
        Bundle extras = getBundleForMediaDescription(mediaDescription);
        if(extras == null){
            return 0;
        }
        return extras.getInt(PlayData.CHAPTER_ORDERNO);
    }

    private static Bundle getBundleForMediaDescription(MediaDescriptionCompat mediaDescription){
        if(mediaDescription == null){
            return null;
        }
        return mediaDescription.getExtras();
    }

    public static class PlayData{

        public static final String CHAPTER_ID = "CHAPTER_ID";
        public static final String CHAPTER_BOOK_ID = "CHAPTER_BOOK_ID";
        public static final String CHAPTER_NAME = "CHAPTER_NAME";
        public static final String CHAPTER_URL = "CHAPTER_URL";
        public static final String CHAPTER_DURATION = "CHAPTER_DURATION";
        public static final String CHAPTER_ORDERNO = "CHAPTER_ORDERNO";

        public List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();

        public PlayData(){}

        public PlayData(List<MediaBrowserCompat.MediaItem> result){
            this.result = result;
        }
    }
}
