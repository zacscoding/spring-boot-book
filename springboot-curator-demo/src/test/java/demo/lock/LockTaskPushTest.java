package demo.lock;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.RetryNTimes;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author zacconding
 * @Date 2018-12-12
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class LockTaskPushTest {

    List<String> urls;
    RestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        urls = Arrays.asList(
            "http://127.0.0.1:8081"
            , "http://127.0.0.1:8082"
        );
        restTemplate = new RestTemplate();

        //clearSharedCount();
    }

    @Test
    public void pushTaskTest() throws InterruptedException {
        int startNumber = 1;
        int lastNumber = 2;

        //final CountDownLatch countDownLatch = new CountDownLatch(lastNumber - startNumber + 1);
        for (int taskNumber = startNumber; taskNumber <= lastNumber; taskNumber += 1L) {
            for (String url : urls) {
                pushMasterSlaveTask(url, taskNumber);
            }
        }
    }

    // tag :: lock task

    private void pushLockTask(String url, long taskNumber) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
            .pathSegment("lock", "task", String.valueOf(taskNumber))
            .build()
            .toUri();

        ResponseEntity<Boolean> result = restTemplate.getForEntity(uri, Boolean.class);
        log.info("## push task : {} >> {}", uri, result.getBody());
    }

    private void clearSharedCount() throws Exception {
        RetryPolicy retryPolicy = new RetryNTimes(3, 100);
        CuratorFramework client = CuratorFrameworkFactory
            .newClient("192.168.5.78:2181", retryPolicy);
        client.start();

        SharedCount sharedCount = new SharedCount(client, "/lock/counters/task", 1);
        sharedCount.start();
        sharedCount.setCount(0);

        System.out.println(">> Before shared count : " + sharedCount.getCount());
        client.close();
    }

    // -- tag :: lock task

    // tag :: master slave task

    private void pushMasterSlaveTask(String url, long taskNumber) {
        URI uri = UriComponentsBuilder
            .fromHttpUrl(url)
            .pathSegment("master-slave", "push", "task", String.valueOf(taskNumber))
            .build()
            .toUri();

        ResponseEntity<Boolean> result = restTemplate.getForEntity(uri, Boolean.class);
        log.info("## push task : {} >> {}", uri, result.getBody());
    }

    // -- tag :: master slave task
}
