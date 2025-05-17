package cloud.xcan.angus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients(basePackages = {
    "cloud.xcan.angus.api",
    "cloud.xcan.angus.security"
})
@EnableDiscoveryClient
@SpringBootApplication
public class XCanAngusTesterApplication {

  public static void main(String[] args) {
    SpringApplication.run(XCanAngusTesterApplication.class, args);
  }

}
