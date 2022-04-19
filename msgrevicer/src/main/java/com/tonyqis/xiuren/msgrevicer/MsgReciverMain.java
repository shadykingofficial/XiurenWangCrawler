
package com.tonyqis.xiuren.msgrevicer;




import com.tonyqis.xiuren.msgrevicer.reciver.MsgReciver;
import com.tonyqis.xiuren.util.MQUtil;
import com.tonyqis.xiuren.util.RedisUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

public  class MsgReciverMain {

    private static final Logger LOGGER = LogManager.getLogger(MsgReciverMain.class);
    
    private static final Executor  IMAGE_DOWNLOADER = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        LOGGER.info("消息接收器开始启动");
        MQUtil.initConnectionFacotory();
        MQUtil.initQueue();
        RedisUtil.init();
        for (int i=0;i<4;i++){
            IMAGE_DOWNLOADER.execute(new MsgReciver());
        }
        LOGGER.info("消息接收器启动完成");
    }
}
