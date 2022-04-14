package com.tonyqis.crawlers.crawler;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.tonyqis.crawlers.contants.Contants;
import com.tonyqis.crawlers.XiurenCrawlerApplication;

import com.tonyqis.xiuren.entity.ImageDownEntity;
import com.tonyqis.xiuren.util.MQUtil;
import com.tonyqis.xiuren.util.RedisUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 秀人网爬虫
 */
@Crawler(name = "xiurenwang")
public class XiurenWangCrawler extends BaseSeimiCrawler {

    private static final Logger LOGGER = LogManager.getLogger(XiurenWangCrawler.class);




    private static int count = 0;


    @Override
    public String[] startUrls() {

        return XiurenCrawlerApplication.START_URLS.split(";");
    }

    boolean urlExist(String url){
        Jedis jedis = RedisUtil.pool.getResource();
        boolean flag = jedis.exists(url);
        jedis.close();
        return  flag;
    }

    public void addUrl(String url){
        Jedis jedis = RedisUtil.pool.getResource();
        jedis.set(url,String.valueOf(System.currentTimeMillis()));
        jedis.close();
    }

    @Override
    public void start(Response response) {
        String url = response.getUrl().toLowerCase();

//        if (urlExist(url)){
//            return;
//        }
//        addUrl(url);
        RedisUtil.delete(Contants.FLOWER_FLAG+url);
        String html = response.getContent();
        Document document = Jsoup.parse(html);
        String pageIndex =  document.getElementsByClass("page").get(0).getElementsByClass("current").get(0).text();
        LOGGER.info("当前读取是第"+pageIndex +"页");
        String nextUrl = document.getElementsByClass("page").get(0).getElementsByClass("current").next().attr("href");
        LOGGER.info("下一页url为："+Contants.ROOT_URL+nextUrl);
        //RedisUtil.set(Contants.FLOWER_FLAG+Contants.ROOT_URL+nextUrl, String.valueOf(System.currentTimeMillis()));
        push(Request.build(Contants.ROOT_URL+nextUrl,XiurenWangCrawler::start));
        Elements eles = document.getElementsByClass("i_list list_n2");
        LOGGER.info("当前第"+pageIndex +"页,一共有"+eles.size()+"条帖子");
        for (Element element : eles) {
            Element ele = element.getElementsByTag("a").get(0);
            String tieziUrl = ele.attributes().get("href");
            LOGGER.info("帖子的url为："+Contants.ROOT_URL+tieziUrl);
            //RedisUtil.set("tiezi:"+Contants.ROOT_URL+tieziUrl, String.valueOf(System.currentTimeMillis()));
            push(Request.build(Contants.ROOT_URL+"/"+tieziUrl,XiurenWangCrawler::parseTiezi));
        }

    }


    public void parseTiezi(Response response){
        String url = response.getUrl().toLowerCase();
        if (urlExist(url)){
            return;
        }
        addUrl(url);
//        RedisUtil.delete("tiezi:"+url);
        String html = response.getContent();
        Document document = Jsoup.parse(html);
        //获取下一页
        Elements next = document.getElementsByClass("page").get(0).getElementsByClass("current").next();
        if (next.size() > 0){
            String nextPageUrl =  Contants.ROOT_URL+next.get(0).attr("href");
            LOGGER.info("帖子下一页为"+nextPageUrl);
            RedisUtil.set("tiezi:"+nextPageUrl, String.valueOf(System.currentTimeMillis()));
            push(Request.build(nextPageUrl,XiurenWangCrawler::parseTiezi));

        }
        //获取模特名
        String modelName = "";
        Elements itemInfos = document.getElementsByClass("item_info").get(0).getElementsByAttribute("rel");
        for (Element element:itemInfos){
            if (element.attr("rel") .equals("author")){
                modelName = element.text();
            }
        }

        modelName = ChineseHelper.convertToSimplifiedChinese(modelName);
        LOGGER.info("模特名字是:"+modelName);
        String tieziName = document.getElementsByClass("item_title").get(0).getElementsByTag("h1").get(0).text();

        LOGGER.info("帖子名字是:"+tieziName);

        Elements contents =  document.getElementsByClass("content");

        for (Element content :contents){
            if (content.getElementsByTag("img").size()<=0){
                continue;
            }
            Elements images = content.getElementsByTag("img");
            for (Element image : images){
                String imageUrl = image.attr("src");
                imageUrl = Contants.ROOT_URL+imageUrl;
                LOGGER.info("image url is:"+imageUrl);
                int index =  imageUrl.toLowerCase().lastIndexOf("/");
                String fileName = null ;
                if (index >0){
                    fileName =  imageUrl.substring(index+1,imageUrl.length());
                }
                String path =  new StringBuilder(Contants.DOWN_PATH).append(File.separator).append(modelName).append(File.separator).append(tieziName).append(File.separator).append(fileName).toString();

                ImageDownEntity entity =  new ImageDownEntity(imageUrl,path);
                //RedisUtil.set("image:"+imageUrl, JSON.toJSONString(entity));
                try {
                    File file = new File(path);
                    if (!file.exists()){
                        MQUtil.sendMsg(entity);
                    }
                } catch (IOException e) {
                    LOGGER.error("消息发送失败",e);
                } catch (TimeoutException e) {
                    LOGGER.error("消息发送失败",e);
                }
            }


        }

    }

}