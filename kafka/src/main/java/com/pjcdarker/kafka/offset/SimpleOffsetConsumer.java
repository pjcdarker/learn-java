package com.pjcdarker.kafka.offset;

import com.pjcdarker.kafka.KafkaProps;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * @author pjc
 * @create 10/20/2016
 */
public class SimpleOffsetConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleOffsetConsumer.class);

    private List<String> topics;
    private Consumer consumer;
    private boolean isRebalancedListener;
    private int offset;

    public SimpleOffsetConsumer(List<String> topics, Consumer consumer, int offset) {
        this.topics = topics;
        this.consumer = consumer;
        this.offset = offset;
    }

    void setRebalancedListener(boolean isRebalancedListener) {
        this.isRebalancedListener = isRebalancedListener;
    }

    @Override
    public void run() {
        if (isRebalancedListener) {
            consumer.subscribe(topics, new ConsumerRebalanceListener() {
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    LOG.info("{} topic-partitions are revoked from this consumer\n",
                             Arrays.toString(partitions.toArray()));
                }

                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    switch (offset) {
                        case -1:
                            consumer.seekToEnd(partitions);
                            break;
                        case 0:
                            consumer.seekToBeginning(partitions);
                            break;
                        default:
                            partitions.forEach(partition -> consumer.seek(partition, offset));
                    }
                    LOG.info("{} topic-partitions are assigned to this consumer\n",
                             Arrays.toString(partitions.toArray()));
                }
            });
        } else {
            consumer.subscribe(topics);
        }
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    String key = record.key();
                    String value = record.value();
                    String topic = record.topic();
                    System.out.println(topic + "--> " + key + " --> " + value);
                }
            }
        } catch (WakeupException ex) {
            ex.printStackTrace();
            LOG.error("Exception caught " + ex.getMessage());
        } finally {
            consumer.close();
            LOG.info("After closing KafkaConsumer");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> topics = Arrays.asList("topic01", "topic02");
        Consumer kafkaConsumer = KafkaProps.getConsumer();

        int offset = 0;
        SimpleOffsetConsumer simpleConsumerTask = new SimpleOffsetConsumer(topics, kafkaConsumer, offset);
        simpleConsumerTask.setRebalancedListener(true);

        Thread consumerThread = new Thread(simpleConsumerTask);
        consumerThread.start();
        Scanner scanner = new Scanner(System.in);
        String cmd = "";
        while (!cmd.equals("-1")) {
            cmd = scanner.nextLine();
        }
        scanner.close();
        kafkaConsumer.wakeup();
        LOG.info("Stopping consumer .....");
        consumerThread.join(60000);
    }
}
