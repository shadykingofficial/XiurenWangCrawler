package com.tonyqis.xiuren.msgrevicer.reciver;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;
import com.tonyqis.xiuren.entity.ImageDownEntity;
import com.tonyqis.xiuren.msgrevicer.util.DownloadUtil;
import com.tonyqis.xiuren.util.MQUtil;
import com.tonyqis.xiuren.util.RedisUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

public class MsgReciver extends Thread {

    private static  final Logger LOGGER = LogManager.getLogger(MsgReciver.class);

    @Override
    public void run() {
        try {
            msgHandler();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void msgHandler() throws IOException, TimeoutException {
        Connection connection = MQUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //body就是从队列中获取的数据
                String msg = new String(body);
                LOGGER.debug("接收到消息："+msg);
                ImageDownEntity entity = JSONObject.parseObject(msg, ImageDownEntity.class);
                try {
                    DownloadUtil.downloadImg(entity.getImageUrl(),entity.getPath());
                    RedisUtil.delete("image:"+entity.getImageUrl());
                    RedisUtil.inrc("totalimagenum");
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }catch (FileNotFoundException e){
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }catch (ConnectException e){
                    String fileName = entity.getImageUrl();
                    int index = fileName.lastIndexOf("/");
                    fileName = fileName.substring(index+1,fileName.length());
                    LOGGER.error("连接超时 "+"wget "+entity.getImageUrl() +" && "+"mv "+fileName+"  "+entity.getPath(),e);
                    channel.basicReject(envelope.getDeliveryTag(),true);
                }catch (Exception e) {
                    LOGGER.error("Error has Exception",e);
                    channel.basicReject(envelope.getDeliveryTag(),true);
                }
            };
        };
        channel.basicConsume("test_queue",consumer);
    }



}
