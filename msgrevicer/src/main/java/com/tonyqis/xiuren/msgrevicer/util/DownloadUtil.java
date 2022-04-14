package com.tonyqis.xiuren.msgrevicer.util;


import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public  class  DownloadUtil {

    private static  final Logger LOGGER = LogManager.getLogger(DownloadUtil.class);

    public static void downloadImg(String url, String path) throws Exception {
        try{
            InputStream inputStream = getInputStream(url);
            download(inputStream,path);
            LOGGER.info("下载文件："+path);
        } catch (FileNotFoundException e){
            LOGGER.error("文件未找到"+url+"         "+path);
            throw e;
        }catch (IOException e) {
            LOGGER.error("连接超时",  "   wget "+url +" && "+"mv "+path);
            throw  e;
        }

    }


    private static InputStream getInputStream(String imgUrl) throws IOException {
        InputStream inputStream = null;
        try{
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(imgUrl).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
            httpURLConnection.setRequestProperty("Accept-Encoding", "gzip");
            httpURLConnection.setRequestProperty("Referer","no-referrer");
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            httpURLConnection.setConnectTimeout(150000);
            httpURLConnection.setReadTimeout(200000);
            inputStream = httpURLConnection.getInputStream();
        }catch (IOException e){
            throw e;
        }
        return inputStream;
    }



    private static boolean download(InputStream inputStream,String path) throws Exception {
        boolean flag = true;
        File file = new File(path);
        if (file.exists()){
            return flag;
        }
        File fileParent = file.getParentFile();
        if (!fileParent.exists()){
            fileParent.mkdirs();//创建路径
        }
        try {
            FileUtils.copyToFile(inputStream,file);
        }catch (Exception e) {
            flag = false;
            throw e;
        }
        return flag;
    }
}
