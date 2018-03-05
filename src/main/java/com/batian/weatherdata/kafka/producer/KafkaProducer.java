package com.batian.weatherdata.kafka.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;
import java.util.Random;

/**
 * Created by Ricky on 2018/3/3
 *
 * @author Tomcox
 */
public class KafkaProducer {
    public static void main(String[] args) {
        final String TOPIC_NAME = "WeatherTopic";
        final char[] chars = "abdsawsdfsdf1234567809".toCharArray();
        final int charLength = chars.length;
        //1.create producer object
        //1.1 save connect kafka of about args
        Properties props = new Properties();
        props.put("metadata.broker.list", "");
        props.put("request.required.acks", "0");
        props.put("producer.type", "sync");
        //序列化机制一般都需要给定，原因是默认的defaultEncoder要求传入的是key/value数据是字节数组
        //而我们一般用到的多是String，很少是字节数组
        props.put("serializer.class", "kafka.serializer.StringEncoder");

        //1.2 use props building Producer context
        ProducerConfig conf = new ProducerConfig(props);
        //1.3基于上下文构建连接对象，构建对象完成后，生产者即完成和Kafka的连接
        final Producer<String, String> producer = new Producer<String, String>(conf);

        //2.多线程发送数据
        final Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //初始化一个要发送多少条数据的值
                    //发送的数据量是100~1099中随机的一个数据量
                    int events = random.nextInt(1000) + 100;
                    String threadName = Thread.currentThread().getName();
                    for (int j = 0; j < events; j++) {
                        //1.获取需要发送的数据
                        String key = "key" + random.nextInt(100);
                        //message/value是字符串，由单词构成
                        StringBuffer sb = new StringBuffer();
                        for (int k = 0; k < random.nextInt(10) + 1; k++) {

                            StringBuffer sb2 = new StringBuffer();
                            for (int l = 0; l < random.nextInt(10) + 5; l++) {
                                sb2.append(chars[random.nextInt(charLength)]);
                            }
                            sb.append(sb2.toString()).append("  ");
                        }
                        String value = sb.toString().trim();
                        KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(TOPIC_NAME, key, value);

                        //2.调用Producer的生产者发送数据
                        producer.send(keyedMessage);

                        //3.不可能一直发，设置休眠一下
                        try {
                            Thread.sleep(random.nextInt(1000) + 10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("线程" + threadName + "总共发送数据：" + events);
                }
            }, "Thread-" + i).start();
        }
        //3.关闭Producer对象
        //一般的情况下把关闭的操作添加一个jvm钩子，当jvm退出的时候进行关闭操作
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                producer.close();
            }
        }));
    }
}
