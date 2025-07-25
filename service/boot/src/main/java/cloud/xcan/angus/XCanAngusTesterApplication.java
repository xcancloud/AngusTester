package cloud.xcan.angus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the XCan Angus Tester distributed testing platform.
 * <p>
 * This Spring Boot application serves as the entry point for the comprehensive
 * API testing and quality assurance platform. The application provides a unified
 * environment for managing APIs, executing tests, monitoring performance, and
 * ensuring quality across distributed systems.
 * <p>
 * Key platform capabilities:
 * - API lifecycle management and documentation
 * - Automated functional, performance, and stability testing
 * - Distributed test execution across multiple nodes
 * - Real-time monitoring and reporting
 * - Mock service integration
 * - Multi-tenant support with role-based access control
 * <p>
 * The application integrates with cloud-native infrastructure including
 * service discovery, distributed configuration, and microservice communication
 * through Feign clients. Scheduled tasks handle background operations such as
 * test execution, data synchronization, and system maintenance.
 * <p>
 * Configuration highlights:
 * - Service discovery enabled for dynamic service registration
 * - Feign clients configured for inter-service communication
 * - Scheduling enabled for automated background tasks
 * - Auto-configuration for Spring Boot ecosystem integration
 *
 * @author Angus Team
 * @since 1.0.0
 */
@EnableScheduling  // Enable scheduled task execution for background operations
@EnableFeignClients(basePackages = {
    "cloud.xcan.angus.api",        // API client definitions for external services
    "cloud.xcan.angus.security"    // Security-related service clients
})
@EnableDiscoveryClient  // Enable service discovery for microservice registration
@SpringBootApplication  // Enable Spring Boot auto-configuration and component scanning
public class XCanAngusTesterApplication {

  /**
   * Application entry point that initializes the Spring Boot application context.
   * <p>
   * This method bootstraps the entire testing platform, including:
   * - Spring application context initialization
   * - Auto-configuration of components and services
   * - Database connection and JPA entity management
   * - Web server startup and endpoint registration
   * - Service discovery registration
   * - Background task scheduler initialization
   * <p>
   * The application startup process includes validation of critical configurations,
   * database schema verification, and health check endpoint activation.
   *
   * @param args command-line arguments passed to the application
   *             Common arguments include profile activation, port configuration,
   *             and environment-specific settings
   */
  public static void main(String[] args) {
    SpringApplication.run(XCanAngusTesterApplication.class, args);
  }
}
