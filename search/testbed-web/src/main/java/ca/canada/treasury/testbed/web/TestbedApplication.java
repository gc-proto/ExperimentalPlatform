package ca.canada.treasury.testbed.web;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestbedApplication {

    private static final Logger LOG =
            LoggerFactory.getLogger(TestbedApplication.class);

	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TestbedApplication.class);
        app.setBannerMode(Mode.OFF);
        app.run(args);
	}

    @Bean
    public ApplicationRunner applicationRunner(ApplicationContext ctx) {
        return args -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Beans provided by Spring Boot:");
                String[] beanNames = ctx.getBeanDefinitionNames();
                Arrays.sort(beanNames);
                for (String beanName : beanNames) {
                    LOG.debug("  - {}", beanName);
                }
            }
        };
    }
}
