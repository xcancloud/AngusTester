package cloud.xcan.angus.config;

import static cloud.xcan.angus.core.spring.env.EnvHelper.getBoolean;
import static cloud.xcan.angus.core.spring.env.EnvKeys.PROXY_STARTUP_IN_TESTER;
import static cloud.xcan.angus.core.utils.CoreUtils.exitApp;

import cloud.xcan.angus.core.tester.infra.agent.AgentServerProperties;
import cloud.xcan.angus.core.tester.infra.agent.AngusAgentServer;
import cloud.xcan.angus.proxy.AngusProxy;
import cloud.xcan.angus.remoting.server.RemotingServer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Service configuration class for initializing core infrastructure services.
 * <p>
 * This configuration manages the startup and lifecycle of essential platform services
 * including the remoting server for distributed communication and the optional proxy
 * service for traffic management. It coordinates the initialization sequence to ensure
 * proper service dependencies and graceful startup/shutdown procedures.
 * <p>
 * Key services configured:
 * - Remoting server for agent communication and distributed test execution
 * - Proxy service for traffic routing and load balancing (optional)
 * - Configuration properties binding for external configuration management
 * <p>
 * The configuration uses Spring's event-driven architecture to coordinate service
 * startup timing and handles failures gracefully with appropriate error handling
 * and application lifecycle management.
 *
 * @author Angus Team
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)  // Disable proxy bean methods for performance optimization
@EnableConfigurationProperties(AgentServerProperties.class)  // Enable binding of agent server properties
public class ServiceConfig {

  /**
   * Creates and configures the remoting server for distributed agent communication.
   * <p>
   * The remoting server serves as the central communication hub for distributed
   * test execution, enabling coordination between the main tester application
   * and remote test execution agents. It provides:
   * - Bidirectional communication channels with test agents
   * - Command distribution and result collection
   * - Health monitoring and agent status tracking
   * - Load balancing across available execution nodes
   * <p>
   * The server is configured using externalized properties and includes
   * automatic shutdown handling for graceful application termination.
   *
   * @param properties configuration properties for the agent server including
   *                  port settings, thread pool configurations, and security settings
   * @return configured RemotingServer instance ready for agent communication
   */
  @Bean(destroyMethod = "shutdown")  // Ensure graceful shutdown when application context closes
  public RemotingServer remotingServer(AgentServerProperties properties) {
    return AngusAgentServer.start(properties);
  }

  /**
   * Conditionally starts the Angus Proxy service after application initialization.
   * <p>
   * The proxy service provides advanced traffic management capabilities including:
   * - Request routing and load balancing
   * - Traffic shaping and rate limiting
   * - Service discovery integration
   * - Health check and circuit breaker functionality
   * <p>
   * The proxy startup is controlled by the PROXY_STARTUP_IN_TESTER environment
   * variable, allowing flexible deployment configurations. If proxy startup fails,
   * the application will terminate gracefully to prevent inconsistent states.
   * <p>
   * This method is triggered by the ApplicationReadyEvent to ensure all other
   * application components are fully initialized before proxy startup.
   */
  @EventListener(ApplicationReadyEvent.class)  // Execute after application is fully ready
  public void startProxy() {
    try {
      // Check if proxy should be started in this tester instance
      if (getBoolean(PROXY_STARTUP_IN_TESTER, false)) {
        AngusProxy.start();
      }
    } catch (Exception e) {
      // If proxy startup fails, terminate application to prevent inconsistent state
      // This ensures fail-fast behavior and prevents partial initialization issues
      exitApp();
    }
  }
}
