package com.tonyqis.crawlers;

import cn.wanghaomiao.seimi.core.Seimi;
import com.tonyqis.xiuren.util.MQUtil;
import com.tonyqis.xiuren.util.RedisUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class XiurenCrawlerApplication {


    private static final Logger LOGGER = LogManager.getLogger(XiurenCrawlerApplication.class);


    public static final String START_URLS = "https://www.xiurenb.net/MyGirl/;" +
            "https://www.xiurenb.net/XiuRen/;" +
            "https://www.xiurenb.net/MFStar/;" +
            "https://www.xiurenb.net/MiStar/;" +
            "https://www.xiurenb.net/IMiss/;" +
            "https://www.xiurenb.net/BoLoli/;" +
            "https://www.xiurenb.net/YouWu/;" +
            "https://www.xiurenb.net/Uxing/;" +
            "https://www.xiurenb.net/MiiTao/;" +
            "https://www.xiurenb.net/FeiLin/;" +
            "https://www.xiurenb.net/WingS/;" +
            "https://www.xiurenb.net/Taste/;" +
            "https://www.xiurenb.net/LeYuan/;" +
            "https://www.xiurenb.net/HuaYan/;" +
            "https://www.xiurenb.net/DKGirl/;" +
            "https://www.xiurenb.net/MintYe/;" +
            "https://www.xiurenb.net/YouMi/;" +
            "https://www.xiurenb.net/Candy/;" +
            "https://www.xiurenb.net/MTMeng/;" +
            "https://www.xiurenb.net/Micat/;" +
            "https://www.xiurenb.net/HuaYang/;" +
            "https://www.xiurenb.net/XingYan/;" +
            "https://www.xiurenb.net/XiaoYu/";

    public static void main(String[] args) throws Exception {
        System.setProperty("log4j.configurationFile",Thread.currentThread().getContextClassLoader().getResource("").getFile()+"log4j2.xml");
        LOGGER.info("爬虫开始启动");
        MQUtil.initConnectionFacotory();
        MQUtil.initQueue();
        RedisUtil.init();
        Seimi s = new Seimi();
        s.goRun("xiurenwang");
        LOGGER.info("爬虫启动完成");
    }
}
