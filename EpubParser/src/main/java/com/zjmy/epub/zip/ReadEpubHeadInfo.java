package com.zjmy.epub.zip;

import android.text.TextUtils;
import android.util.Log;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

import com.zjmy.epub.bean.EpubBookInfo;

public class ReadEpubHeadInfo {
    /**
     * 存储content.opf文件路径
     */
    private static final String META_INF_CONTAINER = "META-INF/container.xml";

    public EpubBookInfo getePubBookInfo(String ePubPath) {
        EpubBookInfo book = new EpubBookInfo();
        InputStream metaInfStream = null;
        InputStream bookInfoInputStream = null;

        File file = new File(ePubPath);
        ZipFile zipFile = null;
        if (file.exists()) {
            try {
                zipFile = new ZipFile(file);

                //存储content.opf文件路径信息
                String contentOpfPath = "";
                //1.解压MEAT-INF文件，解析container.xml的rootfile标签，获取content.opf的路径。
                metaInfStream = zipFile.getInputStream(zipFile.getEntry(META_INF_CONTAINER));
                if (metaInfStream != null) {
                    contentOpfPath = dom4jReadXMLFile(metaInfStream, "rootfiles", "rootfile", "full-path");
                    metaInfStream.close();

                    //2.解压获取到的content.opf路径，并用xml解析获取title信息
                    bookInfoInputStream = zipFile.getInputStream(zipFile.getEntry(contentOpfPath));

                    SAXReader reader = new SAXReader();
                    Document document = reader.read(bookInfoInputStream);

                    // 通过document对象获取根节点bookstore
                    Element node = document.getRootElement();
                    Iterator<Element> it = node.element("metadata").elementIterator();
                    //获取element的id属性节点对象
                    if (it != null) {
                        Element element;
                        String coverPath = null;
                        while (it.hasNext()) {
                            // 获取某个子节点对象
                            element = it.next();
                            if ("title".equals(element.getName())) {//解压书名
                                book.setBookName(element.getStringValue());
                            } else if ("creator".equals(element.getName())) {//解压作者
                                book.setAuthor(element.getStringValue());
                            } else if ("publisher".equals(element.getName())) {//解压出版社
                                book.setPublisher(element.getStringValue());
                            } else if ("meta".equals(element.getName()) || "item".equals(element.getName())) {//解压图片路径
                                List<Attribute> list = element.attributes();
                                for (int i = 0; i < list.size() - 1; i++) {
                                    Attribute attr = list.get(i);
                                    if (attr.getName().equals("name") && attr.getValue().equals("cover")) {
                                        if (TextUtils.isEmpty(coverPath)) {
                                            coverPath = list.get(i + 1).getValue();
                                        }
                                    }else  if (attr.getName().equals("id") && attr.getValue().equals("cover")) {
                                        if (TextUtils.isEmpty(coverPath)) {
                                            coverPath = list.get(i + 1).getValue();
                                        }
                                    }
                                }
                            }
                        }


                        if (coverPath != null) {
                            StringBuilder imgPath = new StringBuilder();
                            String[] content = contentOpfPath.split("/");
                            for (int i = 0; i < content.length - 1; i++) {
                                imgPath.append(content[i]).append("/");
                            }

                            String subPath = dom4jReadXMLFile(node, "manifest", "id", coverPath, "href");
                            if (TextUtils.isEmpty(subPath)){
                                subPath = "image/"+coverPath;
                            }
                            imgPath.append(subPath);

                            book.setCoverPath(imgPath.toString());
                            Log.e("test","coverPath:"+imgPath.toString());
                        }


                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (zipFile != null) {
                        zipFile.close();
                    }

                    if (metaInfStream != null) {
                        metaInfStream.close();
                    }

                    if (bookInfoInputStream != null) {
                        bookInfoInputStream.close();
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                return null;
            } finally {
                try {
                    if (zipFile != null) {
                        zipFile.close();
                    }

                    if (metaInfStream != null) {
                        metaInfStream.close();
                    }

                    if (bookInfoInputStream != null) {
                        bookInfoInputStream.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return book;
    }

    /**
     * 找到指定标签的属性值
     * 示例：xml内容为：
     * <metadata xmlns:dc="xxx">
     * <dc:identifier id="Bookid">ts00119215</dc:identifier>
     * </metadata>
     * dom4jReadXMLFile("xx","metadata","identifier","id")：return = "Bookid"
     * dom4jReadXMLFile("xx","metadata","identifier",null)：retrun = "ts00119215"
     *
     * @param fatherNode 父标签名
     * @param zNode      要找的标签名
     * @param Name       属性名 为null时给的是标签所包含的内容
     * @return 属性值
     * @throws DocumentException dom4jReadXMLFile(bookInfoInputStream, "metadata", "name", "cover", "content");
     */
    private static String dom4jReadXMLFile(InputStream inputStream, String fatherNode, String zNode, String Name) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);

        // 通过document对象获取根节点bookstore
        Element node = document.getRootElement();
        Iterator<Element> it = node.element(fatherNode).elementIterator();
        //获取element的id属性节点对象
        if (it != null) {
            while (it.hasNext()) {
                // 获取某个子节点对象
                Element e = it.next();
                List<Attribute> list = e.attributes();
                if (zNode.equals(e.getName())) {
                    if (Name != null) {
                        for (Attribute attr : list) {
                            //System.out.println(attr.getName() + "=" + attr.getValue() + "\n");
                            if (attr.getName().equals(Name)) {
                                return attr.getValue();
                            }
                        }
                        return null;
                    } else {
                        return e.getStringValue();
                    }
                }
            }

        }
        return null;
    }

    //String imgXmlFlag = dom4jReadXMLFile(bookInfoInputStream, "metadata", "name", "cover", "content");
    private static String dom4jReadXMLFile(Element node, String fatherNode, String key, String value, String keydata) {
        Iterator<Element> it = node.element(fatherNode).elementIterator();
        if (it != null) {
            while (it.hasNext()) {
                Element e = it.next();
                List<Attribute> list = e.attributes();
                for (Attribute attr : list) {
                    //System.out.println(attr.getName() + "=" + attr.getValue() + "\n");
                    if (attr.getName().equals(key) && attr.getValue().equals(value)) {
                        for (Attribute attrs : list) {
                            if (attrs.getName().equals(keydata)) {
                                return attrs.getValue();
                            }
                        }
                    }
                }
            }

        }
        return null;
    }
}
