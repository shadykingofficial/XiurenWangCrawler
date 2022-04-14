package com.tonyqis.xiuren.util;


import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQUtil {


    private static ConnectionFactory mqConnectionFactory ;

    public static void initConnectionFacotory() throws Exception{
        //创建连接工厂
        mqConnectionFactory=new ConnectionFactory();
        //设置参数
        mqConnectionFactory.setHost("127.0.0.1");//主机ip
        mqConnectionFactory.setVirtualHost("/xp1024");//虚拟主机名
        mqConnectionFactory.setUsername("admin");//账号
        mqConnectionFactory.setPassword("admin");//密码

    }

    public static Connection getConnection() throws IOException, TimeoutException {
        return mqConnectionFactory.newConnection();
    }

    public static void initQueue() throws IOException, TimeoutException {
        Connection connection = mqConnectionFactory.newConnection();
        Channel channel =connection.createChannel();
        channel.queueDeclare("test_queue", true, false, false, null);
    }

    public static void sendMsg(Object obj) throws IOException, TimeoutException {
        Connection connection = mqConnectionFactory.newConnection();
        Channel channel =connection.createChannel();

        /*

         *   创建消息队列（如果有可以不用创建，但创建会覆盖之前的）

         *   第一参数：队列名称

         *   第二参数：队列是否持久化（存储到磁盘）

         *   第三参数：队列是否被独占

         *   第四参数：队列是否自动删除

         *   第五参数：

         */



        /*
         *   发送消息
         *   第一参数：交换机名（简单模式不用交换机，但不能用null）
         *   第二参数：队列名称
         *   第三参数：
         *   第四参数：消息（字节流）
         *
         */
        String message = JSONObject.toJSON(obj).toString();
        channel.basicPublish("", "test_queue", null, message.getBytes());

        //关闭资源

        channel.close();
        connection.close();

    }




}
