package com.utopia.logan.test;

import android.text.TextUtils;
import android.util.Log;

import com.utopia.logan.GsonUtils;
import com.utopia.logan.Util;
import com.utopia.logan.gzip.ReadDetailBean;
import com.utopia.logan.gzip.StatisticsDayByDeviceEntity;
import com.utopia.logan.gzip.StatisticsDayEntity;
import com.utopia.logan.gzip.StatisticsUserDataEntity;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class LoganParser {
    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_TYPE = "AES/CBC/NoPadding";
    private Cipher mDecryptCipher;
    private byte[] mEncryptKey16; //128位ase加密Key
    private byte[] mEncryptIv16; //128位aes加密IV

    public LoganParser(byte[] encryptKey16, byte[] encryptIv16) {
        mEncryptKey16 = encryptKey16;
        mEncryptIv16 = encryptIv16;
        initEncrypt();
    }

    private void initEncrypt() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(mEncryptKey16, ALGORITHM);
        try {
            mDecryptCipher = Cipher.getInstance(ALGORITHM_TYPE);
            mDecryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(mEncryptIv16));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public void printLogFile(File logFile) {
        byte[] content = Util.readFile2BytesByMap(logFile);
        int dataLength = 0;
        if (content != null) {
            dataLength = content.length;
        }

        int lineLength;
        int lineEndPosition;
        ByteArrayOutputStream uncompressBytesArray = new ByteArrayOutputStream();

        for (int i = 0; i < dataLength - 4; i++) {
            //非行开头标识，进入下一行解析
            if (content[i] != '\1') {
                continue;
            }

            //开始计算行数据长度
            lineLength = (content[i + 1] & 0xFF) << 24 | (content[i + 2] & 0xFF) << 16 | (content[i + 3] & 0xFF) << 8 | (content[i + 4] & 0xFF);
            i += 4;

            //行数据大于0时，才解析具体数据
            if (lineLength > 0) {
                boolean skipStartTag = false;
                lineEndPosition = lineLength + i + 1;

                if (dataLength > lineEndPosition) {
                    if ('\0' == content[lineEndPosition]) {
                        skipStartTag = true;
                    } else if ('\1' != content[lineEndPosition]) {
                        i -= 4;
                        continue;
                    }
                } else if (dataLength < lineEndPosition) {
                    i -= 4;
                    continue;
                }


                InflaterInputStream inflaterOs = null;
                ByteArrayInputStream inputStream = new ByteArrayInputStream(content, i + 1, lineLength);
                CipherInputStream cipherInputStream = new CipherInputStream(inputStream, mDecryptCipher);

                try {
                    uncompressBytesArray.reset();
                    inflaterOs = new GZIPInputStream(cipherInputStream);
                    int e = 0;
                    byte[] buffer = new byte[1024];

                    while ((e = inflaterOs.read(buffer)) >= 0) {
                        uncompressBytesArray.write(buffer, 0, e);
                    }
                    //String tmpStr = uncompressBytesArray.toString();
                    Log.e("test", "日志数据：" + uncompressBytesArray.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Util.ioClose(cipherInputStream);
                    Util.ioClose(inputStream);
                    Util.ioClose(inflaterOs);
                }

                i = skipStartTag ? lineLength + i + 1 : lineLength + i;
            }
        }
        Util.ioClose(uncompressBytesArray);
    }

    public String parse(File logFile) {
        byte[] content = Util.readFile2BytesByMap(logFile);
        int dataLength = 0;
        if (content != null) {
            dataLength = content.length;
        }

        int lineLength;
        int lineEndPosition;
        ByteArrayOutputStream uncompressBytesArray = new ByteArrayOutputStream();
        List<ReadDetailBean> readDetailBeanList = new ArrayList<>();

        for (int i = 0; i < dataLength - 4; i++) {
            //非行开头标识，进入下一行解析
            if (content[i] != '\1') {
                continue;
            }

            //开始计算行数据长度
            lineLength = (content[i + 1] & 0xFF) << 24 | (content[i + 2] & 0xFF) << 16 | (content[i + 3] & 0xFF) << 8 | (content[i + 4] & 0xFF);
            i += 4;

            //行数据大于0时，才解析具体数据
            if (lineLength > 0) {
                boolean skipStartTag = false;
                lineEndPosition = lineLength + i + 1;

                if (dataLength > lineEndPosition) {
                    if ('\0' == content[lineEndPosition]) {
                        skipStartTag = true;
                    } else if ('\1' != content[lineEndPosition]) {
                        i -= 4;
                        continue;
                    }
                } else if (dataLength < lineEndPosition) {
                    i -= 4;
                    continue;
                }


                InflaterInputStream inflaterOs = null;
                ByteArrayInputStream inputStream = new ByteArrayInputStream(content, i + 1, lineLength);
                CipherInputStream cipherInputStream = new CipherInputStream(inputStream, mDecryptCipher);

                try {
                    uncompressBytesArray.reset();
                    inflaterOs = new GZIPInputStream(cipherInputStream);
                    int e = 0;
                    byte[] buffer = new byte[1024];

                    while ((e = inflaterOs.read(buffer)) >= 0) {
                        uncompressBytesArray.write(buffer, 0, e);
                    }
                    String tmpStr = uncompressBytesArray.toString();
                    if (tmpStr.contains("read_detail")) {
                        readDetailBeanList.add(GsonUtils.toObject(tmpStr, ReadDetailBean.class));
                    }

                    //Log.e("test", "日志数据：" + uncompressBytesArray.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Util.ioClose(cipherInputStream);
                    Util.ioClose(inputStream);
                    Util.ioClose(inflaterOs);
                }

                i = skipStartTag ? lineLength + i + 1 : lineLength + i;
            }
        }
        Util.ioClose(uncompressBytesArray);

        String result1 = statisticsAndInsertToFile(readDetailBeanList, true);
        String result2 = statisticsAndInsertToFile(readDetailBeanList, false);
        String result3 = statisticsDayOrBook(readDetailBeanList, true);
        String result4 = statisticsDayOrBook(readDetailBeanList, false);
        String result5 = statisticsUserData(readDetailBeanList);
        readDetailBeanList.clear();
        return result1 + result2 + result3 + result4 + result5;
    }

    private String statisticsAndInsertToFile(List<ReadDetailBean> datas, boolean useRules) {
        StringBuilder result = new StringBuilder();
        String tab;
        if (useRules) {
            tab = "statistics_day_mult";
        } else {
            tab = "statistics_day_notfilter";
        }
        Map<String, StatisticsDayByDeviceEntity> dayNotfilterMap = new HashMap<>();//无规则全部统计
        StatisticsDayByDeviceEntity currentData = null;
        String currentPrimaryKey = "";
        int page_words = 0;
        long readTimeSecond = 0;
        for (ReadDetailBean bean : datas) {//list数据肯定是按日期正序排列的，跟随写入日志数据的日期
            page_words = Integer.valueOf(bean.page_words);
            readTimeSecond = bean.getReadSeconds();
            if (!currentPrimaryKey.equals(bean.user_id + bean.book_id + bean.getDate()) || currentData == null) {
                currentPrimaryKey = bean.user_id + bean.book_id + bean.getDate();
                currentData = dayNotfilterMap.get(currentPrimaryKey);
                if (currentData == null) {
                    currentData = new StatisticsDayByDeviceEntity(bean.user_id, bean.book_id, Integer.valueOf(bean.total_words), bean.getDate(), bean.device_num, bean.eink_version);
                    currentData.setPrimaryKey(currentPrimaryKey);
                    currentData.setTab(tab);
                    dayNotfilterMap.put(currentPrimaryKey, currentData);
                }
            }

            if (readTimeSecond > 0) {
                if (useRules && (page_words * 6 / readTimeSecond > 100)) {
                    //阅读速度 > 1000字/分钟，字数、时间记为0；
                    page_words = 0;
                    readTimeSecond = 0;
                }
            }

            //需要更新的数据
            currentData.setBegin_time(bean.begin_time);
            currentData.setPage_num(bean.page_num);
            currentData.setRead_words(Integer.valueOf(bean.read_words));
            currentData.addTimes(readTimeSecond, useRules);//阅读时间 > 15分钟(900秒)，阅读时间记为15分钟，字数使用日志文件的数据。
            currentData.addReadWordsNum(page_words);
        }


        Log.e("test", "-----------------------------------");
        //value
        for (StatisticsDayByDeviceEntity value : dayNotfilterMap.values()) {
            result.append(value.toJsonString()).append("\n");
        }
        return result.toString();
    }


    private String statisticsDayOrBook(List<ReadDetailBean> datas, boolean byDate) {
        StringBuilder result = new StringBuilder();
        String tab;
        if (byDate) {
            tab = "statistics_day";
        } else {
            tab = "statistics_user_book";
        }
        Map<String, StatisticsDayEntity> dataEntityMap = new HashMap<>();//无规则全部统计
        StatisticsDayEntity currentData = null;
        String currentPrimaryKey = "";
        String tmpKey = "";
        int page_words = 0;
        long readTimeSecond = 0;

        for (ReadDetailBean bean : datas) {//list数据肯定是按日期正序排列的，跟随写入日志数据的日期
            page_words = Integer.valueOf(bean.page_words);
            readTimeSecond = bean.getReadSeconds();
            tmpKey = bean.user_id + bean.book_id;
            if (byDate) {
                tmpKey += bean.getDate();
            }

            if (!currentPrimaryKey.equals(tmpKey) || currentData == null) {
                currentPrimaryKey = tmpKey;
                currentData = dataEntityMap.get(currentPrimaryKey);
                if (currentData == null) {
                    currentData = new StatisticsDayEntity(bean.user_id, bean.book_id, Integer.valueOf(bean.total_words), bean.getDate());
                    currentData.setPrimaryKey(currentPrimaryKey);
                    currentData.setTab(tab);
                    dataEntityMap.put(currentPrimaryKey, currentData);
                }
            }

            if (readTimeSecond > 0) {
                if (page_words * 60 / readTimeSecond > 100) {
                    //阅读速度 > 1000字/分钟，字数、时间记为0；
                    page_words = 0;
                    readTimeSecond = 0;
                }
            }
            //需要更新的数据
            currentData.setBegin_time(bean.begin_time);
            currentData.setPage_num(bean.page_num);
            currentData.setRead_words(Integer.valueOf(bean.read_words));
            currentData.addTimes(readTimeSecond, true);//阅读时间 > 15分钟(900秒)，阅读时间记为15分钟，字数使用日志文件的数据。
            currentData.addReadWordsNum(page_words);
        }


        Log.e("test", "-----------------------------------");
        for (StatisticsDayEntity value : dataEntityMap.values()) {
            result.append(value.toJsonString()).append("\n");
        }
        return result.toString();

    }

    private String statisticsUserData(List<ReadDetailBean> datas) {
        StringBuilder result = new StringBuilder();
        Map<String, StatisticsUserDataEntity> dataEntityMap = new HashMap<>();//无规则全部统计
        StatisticsUserDataEntity currentData = null;
        String currentPrimaryKey = "";
        String tmpKey = "";
        int page_words = 0;
        long readTimeSecond = 0;

        for (ReadDetailBean bean : datas) {//list数据肯定是按日期正序排列的，跟随写入日志数据的日期
            page_words = Integer.valueOf(bean.page_words);
            readTimeSecond = bean.getReadSeconds();
            tmpKey = bean.user_id + bean.getDate();
            if (!currentPrimaryKey.equals(tmpKey) || currentData == null) {
                currentPrimaryKey = tmpKey;
                currentData = dataEntityMap.get(currentPrimaryKey);
                if (currentData == null) {
                    currentData = new StatisticsUserDataEntity(bean.user_id, bean.getDate());
                    currentData.setPrimaryKey(currentPrimaryKey);
                    dataEntityMap.put(currentPrimaryKey, currentData);
                }
            }

            if (readTimeSecond > 0) {
                if (page_words * 60 / readTimeSecond > 100) {
                    //阅读速度 > 1000字/分钟，字数、时间记为0；
                    page_words = 0;
                    readTimeSecond = 0;
                }
            }
            //需要更新的数据
            currentData.setBegin_time(bean.begin_time);
            currentData.addTimes(readTimeSecond, true);//阅读时间 > 15分钟(900秒)，阅读时间记为15分钟，字数使用日志文件的数据。
            currentData.addReadWordsNum(page_words);
        }


        Log.e("test", "-----------------------------------");
        for (StatisticsUserDataEntity value : dataEntityMap.values()) {
            result.append(value.toJsonString()).append("\n");
        }
        return result.toString();
    }

}
