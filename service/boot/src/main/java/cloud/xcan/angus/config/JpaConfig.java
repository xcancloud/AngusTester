package cloud.xcan.angus.config;

import cloud.xcan.angus.core.jpa.repository.BaseRepositoryImpl;
import cloud.xcan.angus.core.spring.condition.MySqlEnvCondition;
import cloud.xcan.angus.core.spring.condition.PostgresEnvCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration class providing database-specific configurations for the Angus Tester platform.
 * <p>
 * This configuration supports multiple database environments (MySQL and PostgreSQL) with
 * conditional activation based on the runtime environment. It establishes the foundation
 * for data persistence, transaction management, and audit tracking across the application.
 * <p>
 * Key features configured:
 * - Multi-database support with environment-specific activation
 * - Custom repository base class for enhanced functionality
 * - Automatic entity auditing for creation and modification tracking
 * - Transaction management for data consistency
 * - Package scanning for repository interfaces
 * <p>
 * The configuration uses conditional beans to activate the appropriate database
 * configuration based on environment variables or application properties,
 * enabling seamless deployment across different infrastructure environments.
 *
 * @author Angus Team
 * @since 1.0.0
 */
@Configuration
public class JpaConfig {

  /**
   * MySQL-specific JPA configuration activated when MySQL environment is detected.
   * <p>
   * This configuration provides optimized settings for MySQL database environments,
   * including MySQL-specific repository packages and connection configurations.
   * It enables comprehensive data management capabilities including:
   * - Entity auditing for tracking creation and modification timestamps
   * - Transaction management with MySQL-optimized settings
   * - Custom repository implementations with enhanced query capabilities
   * - ID generation service integration
   * <p>
   * The configuration is conditionally activated only when the MySQL environment
   * condition is satisfied, ensuring proper database-specific optimizations.
   */
  @EnableJpaAuditing              // Enable automatic auditing of entity creation and modification
  @EnableTransactionManagement    // Enable declarative transaction management
  @EnableJpaRepositories(
      repositoryBaseClass = BaseRepositoryImpl.class,  // Custom base repository with enhanced functionality
      basePackages = {
          "cloud.xcan.angus.idgen.dao",                                        // ID generation service repositories
          "cloud.xcan.angus.core.tester.infra.persistence.mysql.master"       // MySQL-specific repository implementations
      })
  @Conditional(MySqlEnvCondition.class)  // Activate only in MySQL environments
  protected static class JpaEnableMysqlConfiguration {
    // Configuration class for MySQL-specific JPA settings
    // Bean definitions and customizations can be added here as needed
  }

  /**
   * PostgreSQL-specific JPA configuration activated when PostgreSQL environment is detected.
   * <p>
   * This configuration provides optimized settings for PostgreSQL database environments,
   * including PostgreSQL-specific repository packages and connection configurations.
   * It enables comprehensive data management capabilities including:
   * - Entity auditing for tracking creation and modification timestamps
   * - Transaction management with PostgreSQL-optimized settings
   * - Custom repository implementations with enhanced query capabilities
   * - ID generation service integration
   * <p>
   * The configuration is conditionally activated only when the PostgreSQL environment
   * condition is satisfied, ensuring proper database-specific optimizations and
   * leveraging PostgreSQL's advanced features.
   */
  @EnableJpaAuditing              // Enable automatic auditing of entity creation and modification
  @EnableTransactionManagement    // Enable declarative transaction management
  @EnableJpaRepositories(
      repositoryBaseClass = BaseRepositoryImpl.class,  // Custom base repository with enhanced functionality
      basePackages = {
          "cloud.xcan.angus.idgen.dao",                                           // ID generation service repositories
          "cloud.xcan.angus.core.tester.infra.persistence.postgres.master"       // PostgreSQL-specific repository implementations
      })
  @Conditional(PostgresEnvCondition.class)  // Activate only in PostgreSQL environments
  protected static class JpaEnablePostgresConfiguration {
    // Configuration class for PostgreSQL-specific JPA settings
    // Bean definitions and customizations can be added here as needed
  }
}
