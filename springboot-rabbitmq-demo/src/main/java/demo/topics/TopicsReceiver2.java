package demo.topics;

import demo.model.DefaultMessage;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author zacconding
 * @Date 2018-08-07
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@RabbitListener(queues = "#{autoDeleteQueue2.name}")
public class TopicsReceiver2 {

    @Value("#{autoDeleteQueue2.name}")
    private String queName;

    @PostConstruct
    private void setUp() {
        log.info("## TopicsReceiver2`s queue name : " + queName);
    }

    @RabbitHandler
    private void receiveMessage(DefaultMessage receiveMessage) {
        StringBuilder sb = new StringBuilder("\n// ==================================================\n")
            .append("[[ Topics Receiver2 ]]\n")
            .append("Thread : ").append(Thread.currentThread().getName()).append("(").append(Thread.currentThread().getId()).append(")\n")
            .append("Receive message : ").append(receiveMessage.toString()).append("\n")
            .append("===================================================== //\n");

        log.info(sb.toString());
    }
}