
package com.tonyqis.xiuren.msgrevicer;




import com.tonyqis.xiuren.msgrevicer.reciver.MsgReciver;
import com.tonyqis.xiuren.util.MQUtil;
import com.tonyqis.xiuren.util.RedisUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

public  class MsgReciverMain {

    private static final Logger LOGGER = LogManager.getLogger(MsgReciverMain.class);

    private static final ThreadPoolExecutor MSG_RECIVER_THREAD_POOL = new ThreadPoolExecutor(8,8,60, TimeUnit.SECONDS,new LinkedBlockingDeque<>(10));

    public static  final ExecutorService IMG_DOWNLOAD_THREAD_POOL = Executors.newFixedThreadPool(8);


    public static void main(String[] args) throws Exception {
        LOGGER.info("消息接收器开始启动");
        MQUtil.initConnectionFacotory();
        MQUtil.initQueue();
        RedisUtil.init();
        new MsgReciver().run();
        LOGGER.info("消息接收器启动完成");

    }
}
